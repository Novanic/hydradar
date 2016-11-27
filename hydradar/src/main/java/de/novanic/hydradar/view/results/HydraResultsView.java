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
import de.novanic.hydradar.view.results.content.SymbolTreeContentProvider;
import de.novanic.hydradar.view.results.content.SymbolTreeContentProviderFactory;
import de.novanic.hydradar.view.util.IconLoader;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;

import java.util.ArrayList;
import java.util.List;

public class HydraResultsView extends ViewPart {

    private final Image IMAGE_ICON = IconLoader.loadImage("obj16/brkpi_obj.png");
    private final Image IMAGE_IDEA = IconLoader.loadImage("elcl16/smartmode_co.png");

    private ResultData myResultData;
    private FilteredTree myResultsTree;
    private HydraResultsToolbar myResultsToolbar;
    private SymbolTreeContentProviderFactory mySymbolTreeContentProviderFactory;

    public void createPartControl(Composite aParent) {
        mySymbolTreeContentProviderFactory = SymbolTreeContentProviderFactory.getInstance();

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
                if(myResultsToolbar.isShowCurrentTypeActionChecked()) {
                    SymbolTreeContentProvider theContentProvider = mySymbolTreeContentProviderFactory.createSymbolTreeContentProvider(
                            myResultData,
                            true,
                            myResultsToolbar.isSystemGroupActionChecked());
                    refreshView(theContentProvider);
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

        final SymbolTreeContentProvider theContentProvider;
        if(myResultData.getResultFile() != null) {
            setTitleToolTip(myResultData.getResultFile().getName());

            theContentProvider = mySymbolTreeContentProviderFactory.createSymbolTreeContentProvider(
                    myResultData,
                    myResultsToolbar.isShowCurrentTypeActionChecked(),
                    myResultsToolbar.isSystemGroupActionChecked());
        } else {
            setTitleToolTip("");

            theContentProvider = mySymbolTreeContentProviderFactory.createEmptyContentProvider();
        }
        refreshView(theContentProvider);
    }

    private void refreshView(SymbolTreeContentProvider aContentProvider) {
        myResultsTree.getViewer().setContentProvider(aContentProvider);
        myResultsTree.getViewer().setInput(getViewSite());
        if(myResultsToolbar.isShowCurrentTypeActionChecked() && !aContentProvider.isEmpty()) {
            setTitleImage(IMAGE_IDEA);
            myResultsTree.getViewer().expandAll();
        } else {
            setTitleImage(IMAGE_ICON);
        }
    }

    private HydraResultsToolbar attachToolbar() {
        IToolBarManager theToolBarManager = getViewSite().getActionBars().getToolBarManager();
        HydraResultsToolbar theResultsToolbar = new HydraResultsToolbar(theToolBarManager);

        theResultsToolbar.addListener(new HydraResultsToolbarListener() {
            @Override
            public void onToggleShowCurrentType(boolean isShowCurrentType) {
                SymbolTreeContentProvider theContentProvider = mySymbolTreeContentProviderFactory.createSymbolTreeContentProvider(myResultData, isShowCurrentType, false);
                refreshView(theContentProvider);
            }

            @Override
            public void onToggleSystemModuleGroup(boolean isShowSystemGroup) {
                SymbolTreeContentProvider theContentProvider = mySymbolTreeContentProviderFactory.createSymbolTreeContentProvider(myResultData, false, isShowSystemGroup);
                refreshView(theContentProvider);
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
}