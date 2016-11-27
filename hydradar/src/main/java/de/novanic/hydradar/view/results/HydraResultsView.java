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

import de.novanic.hydradar.HydradarPlugin;
import de.novanic.hydradar.HydradarRuntimeException;
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
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.*;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.prefs.BackingStoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class HydraResultsView extends ViewPart {

    private final Image IMAGE_ICON = IconLoader.loadImage("obj16/brkpi_obj.png");
    private final Image IMAGE_IDEA = IconLoader.loadImage("elcl16/smartmode_co.png");
    private final Image IMAGE_SYSTEM_MODULE_GROUP = IconLoader.loadImage("elcl16/hierarchicalLayout.png");
    private final Image IMAGE_SHOW_ONLY_CURRENT_TYPE = IconLoader.loadImage("elcl16/synced.png");
    private final Image IMAGE_MODULE = IconLoader.loadImage("eview16/packages.png");

    private ResultData myResultData;
    private FilteredTree myResultsTree;
    private Action myShowCurrentTypeAction;
    private Action mySystemModuleGroupToggleAction;

    public void createPartControl(Composite aParent) {
        myResultsTree = createResultsTree(aParent, getSite());

        reload();
        attachToolbar();

        registerTreeCategoryItemListener(myResultsTree);
        registerActiveWindowSwitchListener();
    }

    private static FilteredTree createResultsTree(Composite aParent, IWorkbenchPartSite aSite) {
        PatternFilter theFilter = new PatternFilter();
        theFilter.setIncludeLeadingWildcard(true);
        FilteredTree theResultsTree = new FilteredTree(aParent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL, theFilter, true);

        MenuManager theMenuManager = new MenuManager();
        Menu theTreeContextMenu = theMenuManager.createContextMenu(theResultsTree.getViewer().getTree());
        theResultsTree.getViewer().getTree().setMenu(theTreeContextMenu);
        aSite.registerContextMenu(theMenuManager, theResultsTree.getViewer());
        aSite.setSelectionProvider(theResultsTree.getViewer());

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
                if(myShowCurrentTypeAction.isChecked() && aWorkbenchPart instanceof IEditorPart) {
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
                new ArrayList<>(theUnusedPackages),
                new ArrayList<>(theUnusedTypes),
                new ArrayList<>(theUnusedMethods),
                new ArrayList<>(theUselessMethods),
                new ArrayList<>(theUnusedVariables));

        aResultsTree.getViewer().expandAll();
    }

    private void attachUnusedSymbolsEmpty(FilteredTree aResultsTree) {
        displayUnusedSymbols(aResultsTree, new ArrayList<PackageSymbol>(), new ArrayList<TypeSymbol>(), new ArrayList<MethodSymbol>(), new ArrayList<MethodSymbol>(), new ArrayList<VariableSymbol>());
    }

    private void displayUnusedSymbols(FilteredTree aResultsTree, ResultData aResultData) {

        setTitleImage(IMAGE_ICON);

        aResultsTree.getViewer().setContentProvider(new TreeContentProviderGrouped(aResultData));
        aResultsTree.getViewer().setLabelProvider(new TreeCategoryLabelProvider());
        aResultsTree.getViewer().setInput(getViewSite());
    }

    private void displayUnusedSymbols(FilteredTree aResultsTree,
                                      List<PackageSymbol> aUnusedPackages,
                                      List<TypeSymbol> aUnusedTypes,
                                      List<MethodSymbol> aUnusedMethods,
                                      List<MethodSymbol> aUselessMethods,
                                      List<VariableSymbol> aUnusedVariables) {

        if(myShowCurrentTypeAction != null && myShowCurrentTypeAction.isChecked()
                && (!aUnusedPackages.isEmpty() || !aUnusedTypes.isEmpty() || !aUnusedMethods.isEmpty() || !aUnusedVariables.isEmpty())) {
            setTitleImage(IMAGE_IDEA);
        } else {
            setTitleImage(IMAGE_ICON);
        }

        aResultsTree.getViewer().setContentProvider(new TreeContentProviderUngrouped(aUnusedPackages, aUnusedTypes, aUnusedMethods, aUselessMethods, aUnusedVariables));
        aResultsTree.getViewer().setLabelProvider(new TreeCategoryLabelProvider());
        aResultsTree.getViewer().setInput(getViewSite());
    }

    private void attachToolbar() {
        myShowCurrentTypeAction = new Action("Show only current type", Action.AS_CHECK_BOX) {};
        myShowCurrentTypeAction.setImageDescriptor(new ImageDescriptor() {
            @Override
            public ImageData getImageData() {
                return IMAGE_SHOW_ONLY_CURRENT_TYPE.getImageData();
            }
        });

        mySystemModuleGroupToggleAction = new Action("Group by system/module", Action.AS_CHECK_BOX) {};
        mySystemModuleGroupToggleAction.setImageDescriptor(new ImageDescriptor() {
            @Override
            public ImageData getImageData() {
                return IMAGE_SYSTEM_MODULE_GROUP.getImageData();
            }
        });

        myShowCurrentTypeAction.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent aPropertyChangeEvent) {
                if(Action.CHECKED.equals(aPropertyChangeEvent.getProperty())) {
                    boolean theSetting = (boolean)aPropertyChangeEvent.getNewValue();
                    if(theSetting) {
                        mySystemModuleGroupToggleAction.setEnabled(false);
                        mySystemModuleGroupToggleAction.setChecked(false);
                        IEditorPart theActiveEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                        if(theActiveEditor instanceof CompilationUnitEditor) {
                            attachUnusedSymbolsOfActiveType(theActiveEditor);
                        }
                    } else {
                        attachUnusedSymbols(myResultData, myResultsTree);
                        mySystemModuleGroupToggleAction.setEnabled(true);
                    }
                    IEclipsePreferences thePreferences = InstanceScope.INSTANCE.getNode(HydraResultsView.class.getName());
                    thePreferences.putBoolean(HydradarPlugin.PROPERTY_SHOW_ONLY_CURRENT_TYPE, theSetting);
                    thePreferences.putBoolean(HydradarPlugin.PROPERTY_SYSTEM_MODULE_GROUP, mySystemModuleGroupToggleAction.isChecked()); //it is changed above
                    savePreferences(thePreferences);
                }
            }
        });
        mySystemModuleGroupToggleAction.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent aPropertyChangeEvent) {
                if(Action.CHECKED.equals(aPropertyChangeEvent.getProperty()) && mySystemModuleGroupToggleAction.isEnabled()) {
                    boolean theSetting = (boolean)aPropertyChangeEvent.getNewValue();
                    if(theSetting) {
                        attachUnusedSymbolsGroupedBySystemModule(myResultData, myResultsTree);
                    } else {
                        attachUnusedSymbols(myResultData, myResultsTree);
                    }
                    IEclipsePreferences thePreferences = InstanceScope.INSTANCE.getNode(HydraResultsView.class.getName());
                    thePreferences.putBoolean(HydradarPlugin.PROPERTY_SYSTEM_MODULE_GROUP, theSetting);
                    savePreferences(thePreferences);
                }
            }
        });

        IEclipsePreferences thePreferences = InstanceScope.INSTANCE.getNode(HydraResultsView.class.getName());
        myShowCurrentTypeAction.setChecked(thePreferences.getBoolean(HydradarPlugin.PROPERTY_SHOW_ONLY_CURRENT_TYPE, false));
        mySystemModuleGroupToggleAction.setChecked(thePreferences.getBoolean(HydradarPlugin.PROPERTY_SYSTEM_MODULE_GROUP, false));
        savePreferences(thePreferences);

        IToolBarManager theToolBarManager = getViewSite().getActionBars().getToolBarManager();
        theToolBarManager.add(myShowCurrentTypeAction);
        theToolBarManager.add(mySystemModuleGroupToggleAction);
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

    private void savePreferences(IEclipsePreferences aPreferences) {
        try {
            aPreferences.flush();
        } catch(BackingStoreException e) {
            throw new HydradarRuntimeException("Error on saving preferences!", e);
        }
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