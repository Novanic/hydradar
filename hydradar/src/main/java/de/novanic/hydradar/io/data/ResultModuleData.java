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

import de.novanic.hydradar.io.data.symbol.MethodSymbol;
import de.novanic.hydradar.io.data.symbol.PackageSymbol;
import de.novanic.hydradar.io.data.symbol.TypeSymbol;
import de.novanic.hydradar.io.data.symbol.VariableSymbol;

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
    private final SortedSet<PackageSymbol> myUnusedPackages;
    private final SortedSet<TypeSymbol> myUnusedTypes;
    private final SortedSet<MethodSymbol> myUnusedMethods;
    private final SortedSet<MethodSymbol> myUselessMethods;
    private final SortedSet<VariableSymbol> myUnusedVariables;

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

    public void addUnusedPackage(PackageSymbol aPackage) {
        myUnusedPackages.add(aPackage);
    }

    public List<PackageSymbol> getUnusedPackages() {
        return new ArrayList<>(myUnusedPackages);
    }

    public void addUnusedType(TypeSymbol aType) {
        myUnusedTypes.add(aType);
    }

    public List<TypeSymbol> getUnusedTypes() {
        return new ArrayList<>(myUnusedTypes);
    }

    public void addUnusedMethod(MethodSymbol aMethod) {
        myUnusedMethods.add(aMethod);
    }

    public List<MethodSymbol> getUnusedMethods() {
        return new ArrayList<>(myUnusedMethods);
    }

    public void addUselessMethod(MethodSymbol aMethod) {
        myUselessMethods.add(aMethod);
    }

    public List<MethodSymbol> getUselessMethods() {
        return new ArrayList<>(myUselessMethods);
    }

    public void addUnusedVariable(VariableSymbol aVariable) {
        myUnusedVariables.add(aVariable);
    }

    public List<VariableSymbol> getUnusedVariables() {
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