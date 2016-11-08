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
package de.novanic.hydradar.io.data;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author sstrohschein
 *         <br>Date: 27.08.2016
 *         <br>Time: 21:57
 */
public class ResultModuleData
{
    private final ResultSystemData myResultSystemData;
    private final String myName;
    private final SortedSet<String> myUnusedPackages;
    private final SortedSet<String> myUnusedTypes;
    private final SortedSet<String> myUnusedMethods;
    private final SortedSet<String> myUselessMethods;
    private final SortedSet<String> myUnusedVariables;

    public ResultModuleData(ResultSystemData aResultSystemData, String aName) {
        myResultSystemData = aResultSystemData;
        myName = aName;
        myUnusedPackages = new TreeSet<>();
        myUnusedTypes = new TreeSet<>();
        myUnusedMethods = new TreeSet<>();
        myUselessMethods = new TreeSet<>();
        myUnusedVariables = new TreeSet<>();
    }

    public ResultSystemData getResultSystemData() {
        return myResultSystemData;
    }

    public String getName() {
        return myName;
    }

    public void addUnusedPackage(String aPackage) {
        myUnusedPackages.add(aPackage);
    }

    public List<String> getUnusedPackages() {
        return new ArrayList<>(myUnusedPackages);
    }

    public void addUnusedType(String aType) {
        myUnusedTypes.add(aType);
    }

    public List<String> getUnusedTypes() {
        return new ArrayList<>(myUnusedTypes);
    }

    public void addUnusedMethod(String aMethod) {
        myUnusedMethods.add(aMethod);
    }

    public List<String> getUnusedMethods() {
        return new ArrayList<>(myUnusedMethods);
    }

    public void addUselessMethod(String aMethod) {
        myUselessMethods.add(aMethod);
    }

    public List<String> getUselessMethods() {
        return new ArrayList<>(myUselessMethods);
    }

    public void addUnusedVariable(String aVariable) {
        myUnusedVariables.add(aVariable);
    }

    public List<String> getUnusedVariables() {
        return new ArrayList<>(myUnusedVariables);
    }

    @Override
    public String toString() {
        return myName;
    }

    @Override
    public boolean equals(Object aObject) {
        if(this == aObject) return true;
        if(aObject == null || getClass() != aObject.getClass()) return false;

        ResultModuleData theObject = (ResultModuleData) aObject;
        return myResultSystemData.equals(theObject.myResultSystemData) && myName.equals(theObject.myName);
    }

    @Override
    public int hashCode() {
        int theResult = myResultSystemData.hashCode();
        theResult = 31 * theResult + myName.hashCode();
        return theResult;
    }
}