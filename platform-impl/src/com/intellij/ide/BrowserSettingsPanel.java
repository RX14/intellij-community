package com.intellij.ide;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author spleaner
 */
public class BrowserSettingsPanel extends JPanel {
  private JRadioButton myUseDefaultBrowser;
  private JRadioButton myUseAlternativeBrowser;
  private BrowserSettingsProvider[] mySettingsProviders;
  private TextFieldWithBrowseButton myBrowserPathField;
  private JCheckBox myConfirmExtractFiles;
  private JButton myClearExtractedFiles;

  public BrowserSettingsPanel() {
    setLayout(new BorderLayout());

    final JPanel outerPanel = new JPanel();
    outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));

    // generic system browser
    final JPanel genericPanel = new JPanel();
    genericPanel.setBorder(BorderFactory.createTitledBorder("Default Web Browser"));
    genericPanel.setLayout(new BoxLayout(genericPanel, BoxLayout.Y_AXIS));
    final JPanel innerPanel1 = new JPanel(new BorderLayout());

    final ButtonGroup group = new ButtonGroup();
    myUseDefaultBrowser = new JRadioButton("Use system default browser");
    group.add(myUseDefaultBrowser);

    innerPanel1.add(myUseDefaultBrowser, BorderLayout.WEST);
    genericPanel.add(innerPanel1);
    final JPanel innerPanel2 = new JPanel(new BorderLayout());

    myUseAlternativeBrowser = new JRadioButton("Use");
    group.add(myUseAlternativeBrowser);

    innerPanel2.add(myUseAlternativeBrowser, BorderLayout.WEST);
    myBrowserPathField = new TextFieldWithBrowseButton();
    innerPanel2.add(myBrowserPathField, BorderLayout.CENTER);
    genericPanel.add(innerPanel2);

    JPanel innerPanel3 = new JPanel(new BorderLayout());
    myConfirmExtractFiles = new JCheckBox("Show confirmation before extracting files");
    myClearExtractedFiles = new JButton("Clear extracted files");
    myClearExtractedFiles.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        BrowserUtil.clearExtractedFiles();
      }
    });
    innerPanel3.add(myConfirmExtractFiles, BorderLayout.CENTER);
    innerPanel3.add(myClearExtractedFiles, BorderLayout.EAST);
    genericPanel.add(innerPanel3);

    outerPanel.add(genericPanel);

    FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor();
    myBrowserPathField.addBrowseFolderListener(IdeBundle.message("title.select.path.to.browser"), null, null, descriptor);

    if (BrowserUtil.canStartDefaultBrowser()) {
      ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          updateBrowserField();
        }
      };
      myUseDefaultBrowser.addActionListener(actionListener);
      myUseAlternativeBrowser.addActionListener(actionListener);
    }
    else {
      myUseDefaultBrowser.setVisible(false);
      myUseAlternativeBrowser.setVisible(false);
    }

    mySettingsProviders = BrowserSettingsProvider.EP_NAME.getExtensions();
    for (BrowserSettingsProvider settingsProvider : mySettingsProviders) {
      outerPanel.add(settingsProvider.createComponent());
    }

    add(outerPanel, BorderLayout.NORTH);
  }

  public boolean isModified() {
    boolean isModified = false;
    GeneralSettings settings = GeneralSettings.getInstance();
    isModified |= !Comparing.strEqual(settings.getBrowserPath(), myBrowserPathField.getText());
    isModified |= settings.isUseDefaultBrowser() != myUseDefaultBrowser.isSelected();
    isModified |= settings.isConfirmExtractFiles() != myConfirmExtractFiles.isSelected();

    if (isModified) {
      return true;
    }

    for (BrowserSettingsProvider provider : mySettingsProviders) {
      if (provider.isModified()) {
        return true;
      }
    }

    return false;
  }

  private void updateBrowserField() {
    if (!BrowserUtil.canStartDefaultBrowser()) {
      return;
    }

    myBrowserPathField.getTextField().setEnabled(myUseAlternativeBrowser.isSelected());
    myBrowserPathField.getButton().setEnabled(myUseAlternativeBrowser.isSelected());
  }

  public void apply() throws ConfigurationException {
    GeneralSettings settings = GeneralSettings.getInstance();

    settings.setBrowserPath(myBrowserPathField.getText());
    settings.setUseDefaultBrowser(myUseDefaultBrowser.isSelected());
    settings.setConfirmExtractFiles(myConfirmExtractFiles.isSelected());

    for (BrowserSettingsProvider provider : mySettingsProviders) {
      provider.apply();
    }
  }

  public void reset() {
    GeneralSettings settings = GeneralSettings.getInstance();
    myBrowserPathField.setText(settings.getBrowserPath());

    if (settings.isUseDefaultBrowser()) {
      myUseDefaultBrowser.setSelected(true);
    }
    else {
      myUseAlternativeBrowser.setSelected(true);
    }
    myConfirmExtractFiles.setSelected(settings.isConfirmExtractFiles());

    updateBrowserField();

    for (BrowserSettingsProvider provider : mySettingsProviders) {
      provider.reset();
    }
  }
}
