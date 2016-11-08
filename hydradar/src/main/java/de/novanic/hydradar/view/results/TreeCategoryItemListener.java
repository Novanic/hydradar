/*
 * Copyright (c) 2016 and beyond, Hydradar committers.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 */
package de.novanic.hydradar.view.results;

import de.novanic.hydradar.HydradarRuntimeException;
import de.novanic.hydradar.util.ClassNameExtractor;
import de.novanic.hydradar.view.results.content.category.*;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.part.ISetSelectionTarget;

/**
 * @author sstrohschein
 *         <br>Date: 04.09.2016
 *         <br>Time: 14:29
 */
public class TreeCategoryItemListener
{
    public void onSelect(TreeCategoryItem aTreeCategoryItem) {
        TreeCategory theTreeCategory = aTreeCategoryItem.getTreeCategory();

        String theSymbolId = aTreeCategoryItem.toString();
        if(theTreeCategory instanceof PackageTreeCategory) {
            jumpToPackage(theSymbolId);
        } else {
            int theTypeMethodDivisorPos = theSymbolId.indexOf('#');
            int theMethodParameterDivisorPos = theSymbolId.indexOf('(');
            boolean isConstructor = false;
            if(theTypeMethodDivisorPos < 0) {
                isConstructor = true;
                theTypeMethodDivisorPos = theMethodParameterDivisorPos;
            }

            boolean isMethod = theTreeCategory instanceof MethodsTreeCategory || theTreeCategory instanceof UselessMethodsTreeCategory;
            boolean isVariable = theTreeCategory instanceof VariablesTreeCategory;
            String theClassName = ClassNameExtractor.extractClassName(theSymbolId, isMethod, isVariable, theTypeMethodDivisorPos);

            try {
                IType theType = OpenTypeAction.findTypeInWorkspace(theClassName, true);

                IDebugModelPresentation thePresentation = JDIDebugUIPlugin.getDefault().getModelPresentation();
                IEditorInput theEditorInput = thePresentation.getEditorInput(theType);
                if(theEditorInput != null) {
                    String theEditorId = thePresentation.getEditorId(theEditorInput, theType);
                    if(theEditorId != null) {
                        IEditorPart theEditor = JDIDebugUIPlugin.getActivePage().openEditor(theEditorInput, theEditorId);
                        boolean isLocalType = theSymbolId.contains("$");
                        if(!isLocalType) {
                            JavaEditor theJavaEditor = (JavaEditor)theEditor;
                            if(theTreeCategory instanceof TypesTreeCategory) {
                                jumpToType(theJavaEditor, theType);
                            } else if(isMethod) {
                                jumpToMethod(theJavaEditor, theSymbolId, theMethodParameterDivisorPos, theType, theClassName, isConstructor);
                            } else if(isVariable) {
                                jumpToVariable(theJavaEditor, theSymbolId, theTypeMethodDivisorPos, theMethodParameterDivisorPos, theType, theClassName, isConstructor);
                            }
                        }
                    }
                }
            } catch(CoreException e) {
                throw new HydradarRuntimeException("Error on opening class \"" + theClassName + "\"!", e);
            }
        }
    }

    private void jumpToPackage(String aSymbolId) {
        ISetSelectionTarget thePackageExplorer = PackageExplorerPart.getFromActivePerspective();
        ISetSelectionTarget theProjectExplorer = (ISetSelectionTarget)JavaPlugin.getActivePage().findView(ProjectExplorer.VIEW_ID);
        if(thePackageExplorer != null || theProjectExplorer != null) {
            ISelection thePackageSelection = findPackageSelection(aSymbolId);
            if(thePackageSelection != null) {
                if(thePackageExplorer != null) {
                    thePackageExplorer.selectReveal(thePackageSelection);
                }
                if(theProjectExplorer != null) {
                    theProjectExplorer.selectReveal(thePackageSelection);
                }
            }
        }
    }

    private static ISelection findPackageSelection(String aSymbolId) {
        try {
            String[] thePackageElements = aSymbolId.split("\\.");
            PackageResourceSearcher thePackageResourceSearcher = new PackageResourceSearcher(thePackageElements);
            ResourcesPlugin.getWorkspace().getRoot().accept(thePackageResourceSearcher);
            IResource theFoundResource = thePackageResourceSearcher.getFoundResource();
            if(theFoundResource != null) {
                return new StructuredSelection(theFoundResource);
            }
            return null;
        } catch(CoreException e) {
            throw new HydradarRuntimeException("Error on opening package \"" + aSymbolId + "\"!", e);
        }
    }

    private void jumpToType(JavaEditor aJavaEditor, IType aType) {
        aJavaEditor.setSelection(aType);
    }

    private void jumpToMethod(JavaEditor aJavaEditor, String aSymbolId, int aMethodParameterDivisorPos, IType aType, String aClassName, boolean isConstructor) {
        IMethod theMethod = findMethod(aSymbolId, aMethodParameterDivisorPos, aType, aClassName, isConstructor);
        if(theMethod != null) {
            aJavaEditor.setSelection(theMethod);
        }
    }

    private void jumpToVariable(JavaEditor aJavaEditor, String aSymbolId, int aTypeMethodDivisorPos, int aMethodParameterDivisorPos, IType aType, String aClassName, boolean isConstructor) {
        boolean isField = aTypeMethodDivisorPos < 0;
        if(isField) {
            String theVariableName = aSymbolId.substring(aClassName.length() + 1, aSymbolId.length());
            IField theField = aType.getField(theVariableName);
            aJavaEditor.setSelection(theField);
        } else {
            String theParameterPosDivisor = ";Pos:";
            int theParameterPosDivisorPos = aSymbolId.indexOf(theParameterPosDivisor);
            if(theParameterPosDivisorPos > -1) {
                IMethod theMethod = findMethod(aSymbolId, aMethodParameterDivisorPos, aType, aClassName, isConstructor);
                if(theMethod != null) {
                    int theParameterNumber = Integer.valueOf(aSymbolId.substring(theParameterPosDivisorPos + theParameterPosDivisor.length()));
                    try {
                        ILocalVariable theParameter = theMethod.getParameters()[theParameterNumber];
                        aJavaEditor.setSelection(theParameter);
                    } catch(JavaModelException e) {
                        throw new HydradarRuntimeException("Error on parsing parameters of class \"" + aClassName + "\"!", e);
                    }
                }
            } else {
                jumpToMethod(aJavaEditor, aSymbolId, aMethodParameterDivisorPos, aType, aClassName, isConstructor);
            }
        }
    }

    private IMethod findMethod(String aSymbolId, int aMethodParameterDivisorPos, IType aType, String aClassName, boolean isConstructor) {
        IMethod theMatchingMethod = null;
        String theMethodName;
        if(isConstructor) {
            int thePackageTypeNameDivisorPos = aClassName.lastIndexOf('.');
            if(thePackageTypeNameDivisorPos > -1) {
                theMethodName = aClassName.substring(thePackageTypeNameDivisorPos + 1, aClassName.length());
            } else {
                theMethodName = aClassName;
            }
        } else {
            theMethodName = aSymbolId.substring(aClassName.length() + 1, aMethodParameterDivisorPos);
        }

        int theParameterCount;
        String theParametersString = aSymbolId.substring(aMethodParameterDivisorPos, aSymbolId.length());
        if(theParametersString.contains("()")) {
            theParameterCount = 0;
        } else {
            theParameterCount = 1;
        }
        for(char theChar: theParametersString.toCharArray()) {
            if(theChar == ',') {
                theParameterCount++;
            }
        }

        try {
            for(IMethod theMethod: aType.getMethods()) {
                if(theMethodName.equals(theMethod.getElementName())
                        && theParameterCount == theMethod.getParameterTypes().length) {

                    if(theMatchingMethod != null) {
                        return null; //similar method found (the result isn't precise so don't use it)
                    }
                    theMatchingMethod = theMethod;
                }
            }
            return theMatchingMethod;
        } catch(JavaModelException e) {
            throw new HydradarRuntimeException("Error on parsing methods of class \"" + aClassName + "\"!", e);
        }
    }

    private static class PackageResourceSearcher implements IResourceVisitor
    {
        private final String[] mySearchedPackageElements;
        private IResource myFoundResource;

        public PackageResourceSearcher(String[] aPackageElements) {
            mySearchedPackageElements = aPackageElements;
        }

        @Override
        public boolean visit(IResource aResource) throws CoreException {
            if(myFoundResource != null) {
                return false;
            }

            IResource theCurrentResource = aResource;
            int i = mySearchedPackageElements.length - 1;
            for(; i >= 0; i--) {
                if(mySearchedPackageElements[i].equals(theCurrentResource.getName())) {
                    theCurrentResource = theCurrentResource.getParent();
                } else {
                    return true;
                }
            }

            if(i < 0) {
                myFoundResource = aResource;
                return false;
            }
            return true;
        }

        public IResource getFoundResource() {
            return myFoundResource;
        }
    }
}