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

import de.novanic.hydradar.io.data.symbol.Symbol;
import de.novanic.hydradar.view.results.TreeCategoryItem;
import de.novanic.hydradar.view.results.content.category.TreeCategory;
import org.eclipse.jface.viewers.Viewer;

import java.util.Collection;

/**
 * @author sstrohschein
 *         <br>Date: 04.09.2016
 *         <br>Time: 15:07
 */
abstract class AbstractTreeContentProvider implements SymbolTreeContentProvider
{
    private final boolean isEmpty;

    public AbstractTreeContentProvider(boolean aIsEmpty) {
        isEmpty = aIsEmpty;
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
    }

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

    protected TreeCategoryItem[] createArray(Collection<? extends Symbol> aSymbols, TreeCategory aTreeCategory) {
        TreeCategoryItem[] theTreeCategoryItems = new TreeCategoryItem[aSymbols.size()];
        int i = 0;
        for(Symbol theSymbol: aSymbols) {
            theTreeCategoryItems[i] = new TreeCategoryItem(theSymbol.getSymbolName(), aTreeCategory);
            i++;
        }
        return theTreeCategoryItems;
    }
}