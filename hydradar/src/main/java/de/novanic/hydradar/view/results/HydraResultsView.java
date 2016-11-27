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

import de.novanic.hydradar.io.HydraResultsImporter;
import de.novanic.hydradar.io.data.ResultData;
import de.novanic.hydradar.io.data.ResultModuleData;
import de.novanic.hydradar.io.data.ResultSystemData;
import de.novanic.hydradar.io.data.symbol.MethodSymbol;
import de.novanic.hydradar.io.data.symbol.PackageSymbol;
import de.novanic.hydradar.io.data.symbol.TypeSymbol;
import de.novanic.hydradar.io.data.symbol.VariableSymbol;
import de.novanic.hydradar.view.results.content.TreeContentProviderGrouped;
import de.novanic.hydradar.view.results.content.TreeContentProviderUngrouped;
import de.novanic.hydradar.view.results.content.category.TreeCategory;
import de.novanic.hydradar.view.util.IconLoader;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.*;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class HydraResultsView extends ViewPart {

    private final Image IMAGE_ICON = IconLoader.loadImage("obj16/brkpi_obj.png");
    private final Image IMAGE_IDEA = IconLoader.loadImage("elcl16/smartmode_co.png");
    private final Image IMAGE_MODULE = IconLoader.loadImage("eview16/packages.png");

    private ResultData myResultData;
    private FilteredTree myResultsTree;
    private HydraResultsToolbar myResultsToolbar;

    public void createPartControl(Composite aParent) {
        myResultsTree = createResultsTree(aParent, getSite());
        myResultsToolbar = attachToolbar();

        reload();

        registerTreeCategoryItemListener(myResultsTree);
        registerActiveWindowSwitchListener();
    }

    private FilteredTree createResultsTree(Composite aParent, IWorkbenchPartSite aSite) {
        PatternFilter theFilter = new PatternFilter();
        theFilter.setIncludeLeadingWildcard(true);

        FilteredTree theResultsTree = new FilteredTree(aParent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL, theFilter, true);
        TreeViewer theTreeViewer = theResultsTree.getViewer();

        MenuManager theMenuManager = new MenuManager();
        Menu theTreeContextMenu = theMenuManager.createContextMenu(theTreeViewer.getTree());
        theTreeViewer.getTree().setMenu(theTreeContextMenu);
        aSite.registerContextMenu(theMenuManager, theTreeViewer);
        aSite.setSelectionProvider(theTreeViewer);

        theTreeViewer.setLabelProvider(new TreeCategoryLabelProvider());

        return theResultsTree;
    }

    private void registerTreeCategoryItemListener(FilteredTree aTree) {
        final TreeCategoryItemListener theTreeCategoryItemListener = new TreeCategoryItemListener();

        aTree.getViewer().addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent aDoubleClickEvent) {
                //Open the selected source code elements
                List<TreeCategoryItem> theSelectedTreeCategoryItems = getSelectedTreeCategoryItems();
                if(!theSelectedTreeCategoryItems.isEmpty()) {
                    TreeCategoryItem theTreeCategoryItem = theSelectedTreeCategoryItems.iterator().next();
                    theTreeCategoryItemListener.onSelect(theTreeCategoryItem);
                }

                //Expand/collapse the selected tree node
                TreePath[] theSelectedTreePaths = ((TreeSelection)aDoubleClickEvent.getSelection()).getPaths();
                if(theSelectedTreePaths != null && theSelectedTreePaths.length > 0) {
                    TreePath theFirstTreePath = theSelectedTreePaths[0];
                    if(myResultsTree.getViewer().getExpandedState(theFirstTreePath)) {
                        myResultsTree.getViewer().collapseToLevel(theFirstTreePath, AbstractTreeViewer.ALL_LEVELS);
                    } else {
                        myResultsTree.getViewer().expandToLevel(theFirstTreePath, 1);
                    }
                    myResultsTree.getViewer().refresh();
                }
            }
        });
    }

    private void registerActiveWindowSwitchListener() {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(new IPartListener() {
            @Override
            public void partActivated(IWorkbenchPart aIWorkbenchPart) {}

            @Override
            public void partBroughtToTop(IWorkbenchPart aWorkbenchPart) {
                if(myResultsToolbar.isShowCurrentTypeActionChecked() && aWorkbenchPart instanceof IEditorPart) {
                    if(aWorkbenchPart instanceof CompilationUnitEditor) {
                        attachUnusedSymbolsOfActiveType((IEditorPart)aWorkbenchPart);
                    } else {
                        attachUnusedSymbolsEmpty(myResultsTree);
                    }
                }
            }

            @Override
            public void partClosed(IWorkbenchPart aIWorkbenchPart) {}

            @Override
            public void partDeactivated(IWorkbenchPart aIWorkbenchPart) {}

            @Override
            public void partOpened(IWorkbenchPart aIWorkbenchPart) {}
        });
    }

    public void reload() {
        myResultData = new HydraResultsImporter().load();
        if(myResultData.getResultFile() != null) {
            setTitleToolTip(myResultData.getResultFile().getName());

            attachUnusedSymbols(myResultData, myResultsTree);
        } else {
            setTitleToolTip("");
        }
    }

    private void attachUnusedSymbols(ResultData aResultData, FilteredTree aResultsTree) {

        displayUnusedSymbols(aResultsTree,
                aResultData.getUnusedPackages(),
                aResultData.getUnusedTypes(),
                aResultData.getUnusedMethods(),
                aResultData.getUselessMethods(),
                aResultData.getUnusedVariables());
    }

    private void attachUnusedSymbolsGroupedBySystemModule(ResultData aResultData, FilteredTree aResultsTree) {
        displayUnusedSymbols(aResultsTree, aResultData);
    }

    private void attachUnusedSymbolsOfActiveType(IEditorPart aWorkbenchPart) {
        ITypeRoot theType = EditorUtility.getEditorInputJavaElement(aWorkbenchPart, false);
        if(theType != null) {
            IType thePrimaryType = theType.findPrimaryType();
            if(thePrimaryType != null) {
                String theTypeName = thePrimaryType.getFullyQualifiedName();
                attachUnusedSymbolsOfType(theTypeName, myResultData, myResultsTree);
            } else {
                attachUnusedSymbolsEmpty(myResultsTree);
            }
        } else {
            attachUnusedSymbolsEmpty(myResultsTree);
        }
    }

    private void attachUnusedSymbolsOfType(String aTypeName, ResultData aResultData, FilteredTree aResultsTree) {
        SortedSet<PackageSymbol> theUnusedPackages = new TreeSet<>();
        SortedSet<TypeSymbol> theUnusedTypes = new TreeSet<>();
        SortedSet<MethodSymbol> theUnusedMethods = new TreeSet<>();
        SortedSet<MethodSymbol> theUselessMethods = new TreeSet<>();
        SortedSet<VariableSymbol> theUnusedVariables = new TreeSet<>();

        int thePackageEndPos = aTypeName.lastIndexOf('.');
        if(thePackageEndPos >= 0) {
            String thePackage = aTypeName.substring(0, thePackageEndPos + 1);
            for(PackageSymbol theUnusedPackage: aResultData.getUnusedPackages()) {
                if(thePackage.startsWith(theUnusedPackage.getSymbolName())) {
                    theUnusedPackages.add(theUnusedPackage);
                }
            }
        }
        for(TypeSymbol theUnusedType: aResultData.getUnusedTypes()) {
            if(aTypeName.equals(theUnusedType.getEnclosingTypeName())) {
                theUnusedTypes.add(theUnusedType);
            }
        }
        for(MethodSymbol theUnusedMethod: aResultData.getUnusedMethods()) {
            if(aTypeName.equals(theUnusedMethod.getTypeName())) {
                theUnusedMethods.add(theUnusedMethod);
            }
        }
        for(MethodSymbol theUselessMethod: aResultData.getUselessMethods()) {
            if(aTypeName.equals(theUselessMethod.getTypeName())) {
                theUselessMethods.add(theUselessMethod);
            }
        }
        for(VariableSymbol theUnusedVariable: aResultData.getUnusedVariables()) {
            if(aTypeName.equals(theUnusedVariable.getTypeName())) {
                theUnusedVariables.add(theUnusedVariable);
            }
        }

        displayUnusedSymbols(aResultsTree,
                theUnusedPackages,
                theUnusedTypes,
                theUnusedMethods,
                theUselessMethods,
                theUnusedVariables);

        aResultsTree.getViewer().expandAll();
    }

    private void attachUnusedSymbolsEmpty(FilteredTree aResultsTree) {
        displayUnusedSymbols(aResultsTree, new TreeSet<PackageSymbol>(), new TreeSet<TypeSymbol>(), new TreeSet<MethodSymbol>(), new TreeSet<MethodSymbol>(), new TreeSet<VariableSymbol>());
    }

    private void displayUnusedSymbols(FilteredTree aResultsTree, ResultData aResultData) {

        setTitleImage(IMAGE_ICON);

        aResultsTree.getViewer().setContentProvider(new TreeContentProviderGrouped(aResultData));
        aResultsTree.getViewer().setInput(getViewSite());
    }

    private void displayUnusedSymbols(FilteredTree aResultsTree,
                                      SortedSet<PackageSymbol> aUnusedPackages,
                                      SortedSet<TypeSymbol> aUnusedTypes,
                                      SortedSet<MethodSymbol> aUnusedMethods,
                                      SortedSet<MethodSymbol> aUselessMethods,
                                      SortedSet<VariableSymbol> aUnusedVariables) {

        if(myResultsToolbar.isShowCurrentTypeActionChecked()
                && (!aUnusedPackages.isEmpty() || !aUnusedTypes.isEmpty() || !aUnusedMethods.isEmpty() || !aUnusedVariables.isEmpty())) {
            setTitleImage(IMAGE_IDEA);
        } else {
            setTitleImage(IMAGE_ICON);
        }

        aResultsTree.getViewer().setContentProvider(new TreeContentProviderUngrouped(aUnusedPackages, aUnusedTypes, aUnusedMethods, aUselessMethods, aUnusedVariables));
        aResultsTree.getViewer().setInput(getViewSite());
    }

    private HydraResultsToolbar attachToolbar() {
        IToolBarManager theToolBarManager = getViewSite().getActionBars().getToolBarManager();
        HydraResultsToolbar theResultsToolbar = new HydraResultsToolbar(theToolBarManager);

        theResultsToolbar.addListener(new HydraResultsToolbarListener() {
            @Override
            public void onToggleShowCurrentType(boolean isShowCurrentType) {
                if(isShowCurrentType) {
                    IEditorPart theActiveEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                    if(theActiveEditor instanceof CompilationUnitEditor) {
                        attachUnusedSymbolsOfActiveType(theActiveEditor);
                    }
                } else {
                    attachUnusedSymbols(myResultData, myResultsTree);
                }
            }

            @Override
            public void onToggleSystemModuleGroup(boolean isShowSystemGroup) {
                if(isShowSystemGroup) {
                    attachUnusedSymbolsGroupedBySystemModule(myResultData, myResultsTree);
                } else {
                    attachUnusedSymbols(myResultData, myResultsTree);
                }
            }
        });
        return theResultsToolbar;
    }

    public void setFocus() {

    }

    public List<TreeCategoryItem> getSelectedTreeCategoryItems() {
        TreeItem[] theSelection = myResultsTree.getViewer().getTree().getSelection();
        if(theSelection == null || theSelection.length <= 0) {
            return new ArrayList<>();
        }

        List<TreeCategoryItem> theTreeCategoryItems = new ArrayList<>(theSelection.length);
        for(TreeItem theTreeItem: theSelection) {
            Object theSelectedObject = theTreeItem.getData();
            if(theSelectedObject instanceof TreeCategoryItem) {
                theTreeCategoryItems.add((TreeCategoryItem)theSelectedObject);
            }
        }
        return theTreeCategoryItems;
    }

    private class TreeCategoryLabelProvider extends LabelProvider
    {
        @Override
        public Image getImage(Object aElement) {
            if(aElement instanceof TreeCategoryItem) {
                return getImage(((TreeCategoryItem)aElement).getTreeCategory());
            } else if(aElement instanceof TreeCategory) {
                return ((TreeCategory)aElement).getIcon();
            } else if(aElement instanceof ResultSystemData || aElement instanceof ResultModuleData) {
                return IMAGE_MODULE;
            }
            return super.getImage(aElement);
        }
    }
}