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
package de.novanic.hydradar.view.results.command;

import de.novanic.hydradar.view.results.TreeCategoryItem;
import org.eclipse.core.expressions.PropertyTester;

import java.util.Collection;

/**
 * @author sstrohschein
 *         <br>Date: 31.08.2016
 *         <br>Time: 08:06
 */
public class HydraResultsViewPropertyTester extends PropertyTester
{
    private static final String PROPERTY_CAN_COPY = "canCopy";

    @Override
    public boolean test(Object aReceiver, String aProperty, Object[] aArguments, Object aExpectedValue) {
        if(PROPERTY_CAN_COPY.equals(aProperty)) {
            Collection<?> theSelectedItems = (Collection<?>)aReceiver;
            for(Object theSelectedItem: theSelectedItems) {
                if(theSelectedItem instanceof TreeCategoryItem) {
                    return true;
                }
            }
        }
        return false;
    }
}