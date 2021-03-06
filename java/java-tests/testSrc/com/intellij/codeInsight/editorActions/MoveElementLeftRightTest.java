/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.codeInsight.editorActions;

import com.intellij.testFramework.TestFileType;

public class MoveElementLeftRightTest extends AbstractMoveElementLeftRightTest {
  public void testMoveLiteralMethodParameter() throws Exception {
    doTestFromLeftToRight("class C { void m() { System.setProperty(<caret>\"a\", \"b\"); } }", 
                          "class C { void m() { System.setProperty(\"b\", <caret>\"a\"); } }");
  }
  
  public void testMoveAtLiteralMethodParameterEnd() throws Exception {
    doTestFromLeftToRight("class C { void m() { System.setProperty(\"a\"<caret>, \"b\"); } }", 
                          "class C { void m() { System.setProperty(\"b\", \"a\"<caret>); } }");
  }
  
  public void testMoveComplexMethodParameter() throws Exception {
    doTestFromLeftToRight("class C { void m() { System.setProperty(<caret>System.getEnv(null), \"b\"); } }", 
                          "class C { void m() { System.setProperty(\"b\", <caret>System.getEnv(null)); } }");
  }
  
  public void testMoveOuterLevelOutsideParenthesis() throws Exception {
    doTestFromLeftToRight("class C { void m() { System.setProperty(System.setProperty<caret>(\"c\", \"d\"), \"b\"); } }", 
                          "class C { void m() { System.setProperty(\"b\", System.setProperty<caret>(\"c\", \"d\")); } }");
  }
  
  public void testMoveInnerLevelInsideParenthesis() throws Exception {
    doTestFromLeftToRight("class C { void m() { System.setProperty(System.setProperty(<caret>\"c\", \"d\"), \"b\"); } }",
                          "class C { void m() { System.setProperty(System.setProperty(\"d\", <caret>\"c\"), \"b\"); } }");
  }
  
  public void testMoveOuterLevelInsideParenthesisWithSingleParameter() throws Exception {
    doTestFromLeftToRight("class C { void m() { System.setProperty(System.getEnv(<caret>null), \"b\"); } }",
                          "class C { void m() { System.setProperty(\"b\", System.getEnv(<caret>null)); } }");
  }

  public void testMoveSingleParameterWithSelection() throws Exception {
    doTestFromLeftToRight("class C { void m() { System.setProperty(\"<selection>a<caret></selection>\", \"b\"); } }",
                          "class C { void m() { System.setProperty(\"b\", \"<selection>a<caret></selection>\"); } }");
  }
  
  public void testMoveTwoParametersWithSelection() throws Exception {
    doTestFromLeftToRight("class C { void m() { java.util.Arrays.asList(\"<selection><caret>a\", \"b</selection>\", \"c\"); } }",
                          "class C { void m() { java.util.Arrays.asList(\"c\", \"<selection><caret>a\", \"b</selection>\"); } }");
  }
  
  public void testMovingMethodDeclaredParameters() throws Exception {
    doTestFromLeftToRight("class C { void m(i<caret>nt a, String b, @NotNull List c) {} }",
                          "class C { void m(String b, i<caret>nt a, @NotNull List c) {} }",
                          "class C { void m(String b, @NotNull List c, i<caret>nt a) {} }");
  }
  
  public void testMovingArrayInitializerComponents() throws Exception {
    doTestFromLeftToRight("class C { int[] a = {1<caret>, 2, 3}; }",
                          "class C { int[] a = {2, 1<caret>, 3}; }",
                          "class C { int[] a = {2, 3, 1<caret>}; }");
  }
  
  public void testMoveEnumConstants() throws Exception {
    doTestFromLeftToRight("enum E { AA<caret>, BB, }",
                          "enum E { BB, AA<caret>, }");
  }

  @Override
  protected void configureEditor(String contents) throws Exception {
    init(contents, TestFileType.JAVA);
  }
}
