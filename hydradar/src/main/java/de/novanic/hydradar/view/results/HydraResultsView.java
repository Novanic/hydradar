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
import org.eclipse.e4.core.services.events.IEventBroker;
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
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class HydraResultsView extends ViewPart implements EventHandler {

    private static final String EVENT_GRAPH_REFRESH = "de/novanic/hydradar/graphRefresh";

    private final Image IMAGE_ICON = IconLoader.loadImage("obj16/brkpi_obj.png");
    private final Image IMAGE_IDEA = IconLoader.loadImage("elcl16/smartmode_co.png");

    private ResultData myResultData;
    private FilteredTree myResultsTree;
    private HydraResultsToolbar myResultsToolbar;
    private SymbolTreeContentProviderFactory mySymbolTreeContentProviderFactory;

    private IEventBroker myEventBroker;

    public void createPartControl(Composite aParent) {
        myEventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
        myEventBroker.subscribe(EVENT_GRAPH_REFRESH, this);

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
                    final IType theCurrentType = determineCurrentType(true);

                    startThread(new Runnable() {
                        @Override
                        public void run() {
                            SymbolTreeContentProvider theContentProvider = mySymbolTreeContentProviderFactory.createSymbolTreeContentProvider(
                                    myResultData,
                                    true,
                                    theCurrentType,
                                    myResultsToolbar.isSystemGroupActionChecked());
                            myEventBroker.send(EVENT_GRAPH_REFRESH, theContentProvider);
                        }
                    });
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
        myResultsToolbar.disable();

        final boolean isShowCurrentType = myResultsToolbar.isShowCurrentTypeActionChecked();
        final IType theCurrentType = determineCurrentType(isShowCurrentType);

        startThread(new Runnable() {
            @Override
            public void run() {
                myResultData = new HydraResultsImporter().load();

                final SymbolTreeContentProvider theContentProvider;
                if(myResultData.getResultFile() != null) {
                    setTitleToolTip(myResultData.getResultFile().getName());

                    theContentProvider = mySymbolTreeContentProviderFactory.createSymbolTreeContentProvider(
                            myResultData,
                            isShowCurrentType,
                            theCurrentType,
                            myResultsToolbar.isSystemGroupActionChecked());
                } else {
                    setTitleToolTip("");

                    theContentProvider = mySymbolTreeContentProviderFactory.createEmptyContentProvider();
                }
                myResultsTree.getViewer().setContentProvider(theContentProvider);

                myEventBroker.send(EVENT_GRAPH_REFRESH, theContentProvider);
            }
        });
    }

    @Override
    public void handleEvent(Event aEvent) {
        if(EVENT_GRAPH_REFRESH.equals(aEvent.getTopic())) {
            SymbolTreeContentProvider theContentProvider = (SymbolTreeContentProvider)aEvent.getProperty(IEventBroker.DATA);
            refreshView(theContentProvider);
            myResultsToolbar.enable();
        }
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
            public void onToggleShowCurrentType(final boolean isShowCurrentType) {
                //When the toggle is deactivated all levels should get collapsed, because all levels were expanded before, by default.
                // This makes the switch faster within the UI and looks more smooth.
                if(!isShowCurrentType) {
                    myResultsTree.getViewer().collapseAll();
                }

                final IType theCurrentType = determineCurrentType(isShowCurrentType);

                startThread(new Runnable() {
                    @Override
                    public void run() {
                        SymbolTreeContentProvider theContentProvider = mySymbolTreeContentProviderFactory.createSymbolTreeContentProvider(myResultData, isShowCurrentType, theCurrentType, false);
                        myEventBroker.send(EVENT_GRAPH_REFRESH, theContentProvider);
                    }
                });
            }

            @Override
            public void onToggleSystemModuleGroup(final boolean isShowSystemGroup) {
                startThread(new Runnable() {
                    @Override
                    public void run() {
                        SymbolTreeContentProvider theContentProvider = mySymbolTreeContentProviderFactory.createSymbolTreeContentProvider(myResultData, false, null, isShowSystemGroup);
                        myEventBroker.send(EVENT_GRAPH_REFRESH, theContentProvider);
                    }
                });
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

    private IType determineCurrentType(boolean isShowCurrentType) {
        if(isShowCurrentType) {
            IEditorPart theActiveEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            if(theActiveEditor instanceof CompilationUnitEditor) {
                ITypeRoot theType = EditorUtility.getEditorInputJavaElement(theActiveEditor, false);
                if(theType != null) {
                    return theType.findPrimaryType();
                }
            }
        }
        return null;
    }

    private void startThread(Runnable aRunnable) {
        //The user should not toggle between modes when a thread is currently running.
        myResultsToolbar.disable();

        new Thread(aRunnable).start();
    }
}