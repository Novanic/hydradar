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

import de.novanic.hydradar.view.results.content.category.TreeCategory;

/**
 * @author sstrohschein
 *         <br>Date: 31.08.2016
 *         <br>Time: 09:28
 */
public class TreeCategoryItem
{
    private String myId;
    private TreeCategory myTreeCategory;

    public TreeCategoryItem(String aId, TreeCategory aTreeCategory) {
        myId = aId;
        myTreeCategory = aTreeCategory;
    }

    public TreeCategory getTreeCategory() {
        return myTreeCategory;
    }

    @Override
    public String toString() {
        return myId;
    }

    @Override
    public boolean equals(Object aObject) {
        if(this == aObject) return true;
        if(aObject == null || getClass() != aObject.getClass()) return false;

        TreeCategoryItem theObject = (TreeCategoryItem)aObject;
        return myId.equals(theObject.myId) && myTreeCategory.equals(theObject.myTreeCategory);
    }

    @Override
    public int hashCode() {
        int theResult = myId.hashCode();
        theResult = 31 * theResult + myTreeCategory.hashCode();
        return theResult;
    }
}