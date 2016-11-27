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
package de.novanic.hydradar.view.results.content;

import de.novanic.hydradar.io.data.symbol.MethodSymbol;
import de.novanic.hydradar.io.data.symbol.PackageSymbol;
import de.novanic.hydradar.io.data.symbol.TypeSymbol;
import de.novanic.hydradar.io.data.symbol.VariableSymbol;
import de.novanic.hydradar.view.results.TreeCategoryItem;
import de.novanic.hydradar.view.results.content.category.*;
import org.eclipse.ui.internal.ViewSite;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sstrohschein
 *         <br>Date: 04.09.2016
 *         <br>Time: 15:07
 */
public class TreeContentProviderUngrouped extends AbstractTreeContentProvider
{
    private final PackageTreeCategory myPackageTreeCategory;
    private final TypesTreeCategory myTypesTreeCategory;
    private final MethodsTreeCategory myMethodsTreeCategory;
    private final UselessMethodsTreeCategory myUselessMethodsTreeCategory;
    private final VariablesTreeCategory myVariablesTreeCategory;
    private final List<PackageSymbol> myUnusedPackages;
    private final List<TypeSymbol> myUnusedTypes;
    private final List<MethodSymbol> myUnusedMethods;
    private final List<MethodSymbol> myUselessMethods;
    private final List<VariableSymbol> myUnusedVariables;

    public TreeContentProviderUngrouped(List<PackageSymbol> aUnusedPackages,
                                         List<TypeSymbol> aUnusedTypes,
                                         List<MethodSymbol> aUnusedMethods,
                                         List<MethodSymbol> aUselessMethods,
                                         List<VariableSymbol> aUnusedVariables) {
        myUnusedPackages = aUnusedPackages;
        myUnusedTypes = aUnusedTypes;
        myUnusedMethods = aUnusedMethods;
        myUselessMethods = aUselessMethods;
        myUnusedVariables = aUnusedVariables;
        myPackageTreeCategory = new PackageTreeCategory(null);
        myTypesTreeCategory = new TypesTreeCategory(null);
        myMethodsTreeCategory = new MethodsTreeCategory(null);
        myUselessMethodsTreeCategory = new UselessMethodsTreeCategory(null);
        myVariablesTreeCategory = new VariablesTreeCategory(null);
    }

    @Override
    public Object[] getChildren(Object aObject) {
        if(aObject instanceof ViewSite) {
            List<TreeCategory> theCategories = new ArrayList<>(4);
            if(!myUnusedPackages.isEmpty()) {
                theCategories.add(myPackageTreeCategory);
            }
            if(!myUnusedTypes.isEmpty()) {
                theCategories.add(myTypesTreeCategory);
            }
            if(!myUnusedMethods.isEmpty()) {
                theCategories.add(myMethodsTreeCategory);
            }
            if(!myUselessMethods.isEmpty()) {
                theCategories.add(myUselessMethodsTreeCategory);
            }
            if(!myUnusedVariables.isEmpty()) {
                theCategories.add(myVariablesTreeCategory);
            }
            return theCategories.toArray();
        } else if(aObject instanceof PackageTreeCategory) {
            return createArray(myUnusedPackages, myPackageTreeCategory);
        } else if(aObject instanceof TypesTreeCategory) {
            return createArray(myUnusedTypes, myTypesTreeCategory);
        } else if(aObject instanceof MethodsTreeCategory) {
            return createArray(myUnusedMethods, myMethodsTreeCategory);
        } else if(aObject instanceof UselessMethodsTreeCategory) {
            return createArray(myUselessMethods, myUselessMethodsTreeCategory);
        } else if(aObject instanceof VariablesTreeCategory) {
            return createArray(myUnusedVariables, myVariablesTreeCategory);
        }
        return new Object[0];
    }

    @Override
    public Object getParent(Object aObject) {
        if(aObject instanceof TreeCategoryItem) {
            return ((TreeCategoryItem)aObject).getTreeCategory();
        }
        return null;
    }
}