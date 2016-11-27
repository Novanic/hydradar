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

import de.novanic.hydradar.io.data.ResultData;
import de.novanic.hydradar.io.data.ResultModuleData;
import de.novanic.hydradar.io.data.ResultSystemData;
import de.novanic.hydradar.view.results.TreeCategoryItem;
import de.novanic.hydradar.view.results.content.category.*;
import org.eclipse.ui.internal.ViewSite;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sstrohschein
 *         <br>Date: 04.09.2016
 *         <br>Time: 15:09
 */
class TreeContentProviderGrouped extends AbstractTreeContentProvider
{
    private final ResultData myResultData;

    public TreeContentProviderGrouped(ResultData aResultData) {
        super(aResultData.getUnusedPackages().isEmpty()
                && aResultData.getUnusedTypes().isEmpty()
                && aResultData.getUnusedMethods().isEmpty()
                && aResultData.getUselessMethods().isEmpty()
                && aResultData.getUnusedVariables().isEmpty());
        myResultData = aResultData;
    }

    @Override
    public Object[] getChildren(Object aObject) {
        if(aObject instanceof ViewSite) {
            return myResultData.getSystemDataEntries().toArray();
        } else if(aObject instanceof ResultSystemData) {
            return ((ResultSystemData)aObject).getModuleDataEntries().toArray();
        } else if(aObject instanceof ResultModuleData) {
            ResultModuleData theResultModuleData = (ResultModuleData)aObject;
            List<TreeCategory> theCategories = new ArrayList<>(4);

            if(!theResultModuleData.getUnusedPackages().isEmpty()) {
                theCategories.add(new PackageTreeCategory(theResultModuleData));
            }
            if(!theResultModuleData.getUnusedTypes().isEmpty()) {
                theCategories.add(new TypesTreeCategory(theResultModuleData));
            }
            if(!theResultModuleData.getUnusedMethods().isEmpty()) {
                theCategories.add(new MethodsTreeCategory(theResultModuleData));
            }
            if(!theResultModuleData.getUselessMethods().isEmpty()) {
                theCategories.add(new UselessMethodsTreeCategory(theResultModuleData));
            }
            if(!theResultModuleData.getUnusedVariables().isEmpty()) {
                theCategories.add(new VariablesTreeCategory(theResultModuleData));
            }

            return theCategories.toArray();
        } else if(aObject instanceof PackageTreeCategory) {
            ResultModuleData theResultModuleData = (ResultModuleData)getParent(aObject);
            return createArray(theResultModuleData.getUnusedPackages(), (PackageTreeCategory)aObject);
        } else if(aObject instanceof TypesTreeCategory) {
            ResultModuleData theResultModuleData = (ResultModuleData)getParent(aObject);
            return createArray(theResultModuleData.getUnusedTypes(), (TypesTreeCategory)aObject);
        } else if(aObject instanceof MethodsTreeCategory) {
            ResultModuleData theResultModuleData = (ResultModuleData)getParent(aObject);
            return createArray(theResultModuleData.getUnusedMethods(), (MethodsTreeCategory)aObject);
        } else if(aObject instanceof UselessMethodsTreeCategory) {
            ResultModuleData theResultModuleData = (ResultModuleData)getParent(aObject);
            return createArray(theResultModuleData.getUselessMethods(), (UselessMethodsTreeCategory)aObject);
        } else if(aObject instanceof VariablesTreeCategory) {
            ResultModuleData theResultModuleData = (ResultModuleData)getParent(aObject);
            return createArray(theResultModuleData.getUnusedVariables(), (VariablesTreeCategory)aObject);
        }
        return new Object[0];
    }

    @Override
    public Object getParent(Object aObject) {
        if(aObject instanceof TreeCategory) {
            return ((TreeCategory)aObject).getParentModuleData();
        } else if(aObject instanceof TreeCategoryItem) {
            return ((TreeCategoryItem)aObject).getTreeCategory();
        } else if(aObject instanceof ResultModuleData) {
            return ((ResultModuleData)aObject).getResultSystemData();
        }
        return null;
    }
}