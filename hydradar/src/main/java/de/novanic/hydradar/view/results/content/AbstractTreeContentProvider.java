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

import de.novanic.hydradar.view.results.TreeCategoryItem;
import de.novanic.hydradar.view.results.content.category.TreeCategory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.List;

/**
 * @author sstrohschein
 *         <br>Date: 04.09.2016
 *         <br>Time: 15:07
 */
abstract class AbstractTreeContentProvider implements ITreeContentProvider
{
    @Override
    public Object[] getElements(Object aObject) {
        return getChildren(aObject);
    }

    @Override
    public boolean hasChildren(Object aObject) {
        return getChildren(aObject).length > 0;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void inputChanged(Viewer aViewer, Object aO, Object aO1) {

    }

    protected TreeCategoryItem[] createArray(List<String> aIDs, TreeCategory aTreeCategory) {
        TreeCategoryItem[] theTreeCategoryItems = new TreeCategoryItem[aIDs.size()];
        int i = 0;
        for(String theID: aIDs) {
            theTreeCategoryItems[i] = new TreeCategoryItem(theID, aTreeCategory);
            i++;
        }
        return theTreeCategoryItems;
    }
}