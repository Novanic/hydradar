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
package de.novanic.hydradar.view.results.content.category;

import de.novanic.hydradar.io.data.ResultModuleData;
import org.eclipse.swt.graphics.Image;

/**
 * @author sstrohschein
 *         <br>Date: 31.08.2016
 *         <br>Time: 09:26
 */
public abstract class TreeCategory
{
    private final String myName;
    private final ResultModuleData myParentModuleData;

    protected TreeCategory(String aName, ResultModuleData aParentModuleData) {
        myName = aName;
        myParentModuleData = aParentModuleData;
    }

    public abstract Image getIcon();

    public ResultModuleData getParentModuleData() {
        return myParentModuleData;
    }

    @Override
    public String toString() {
        return myName;
    }

    @Override
    public boolean equals(Object aObject) {
        if(this == aObject) return true;
        if(aObject == null || getClass() != aObject.getClass()) return false;

        TreeCategory theObject = (TreeCategory)aObject;
        return myName.equals(theObject.myName) && (myParentModuleData != null ? myParentModuleData.equals(theObject.myParentModuleData) : theObject.myParentModuleData == null);
    }

    @Override
    public int hashCode() {
        int theResult = myName.hashCode();
        theResult = 31 * theResult + (myParentModuleData != null ? myParentModuleData.hashCode() : 0);
        return theResult;
    }
}