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
 *         <br>Time: 21:56
 */
public class ResultSystemData
{
    private final String myName;
    private final List<ResultModuleData> myModuleDataEntries;

    public ResultSystemData(String aName) {
        myName = aName;
        myModuleDataEntries = new ArrayList<>();
    }

    public String getName() {
        return myName;
    }

    public void addModuleData(ResultModuleData aModuleData) {
        myModuleDataEntries.add(aModuleData);
    }

    public List<ResultModuleData> getModuleDataEntries() {
        return myModuleDataEntries;
    }

    public List<PackageSymbol> getUnusedPackages() {
        SortedSet<PackageSymbol> theUnusedPackages = new TreeSet<>();
        for(ResultModuleData theModuleData: myModuleDataEntries) {
            theUnusedPackages.addAll(theModuleData.getUnusedPackages());
        }
        return new ArrayList<>(theUnusedPackages);
    }

    public List<TypeSymbol> getUnusedTypes() {
        SortedSet<TypeSymbol> theUnusedTypes = new TreeSet<>();
        for(ResultModuleData theModuleData: myModuleDataEntries) {
            theUnusedTypes.addAll(theModuleData.getUnusedTypes());
        }
        return new ArrayList<>(theUnusedTypes);
    }

    public List<MethodSymbol> getUnusedMethods() {
        SortedSet<MethodSymbol> theUnusedMethods = new TreeSet<>();
        for(ResultModuleData theModuleData: myModuleDataEntries) {
            theUnusedMethods.addAll(theModuleData.getUnusedMethods());
        }
        return new ArrayList<>(theUnusedMethods);
    }

    public List<MethodSymbol> getUselessMethods() {
        SortedSet<MethodSymbol> theUselessMethods = new TreeSet<>();
        for(ResultModuleData theModuleData: myModuleDataEntries) {
            theUselessMethods.addAll(theModuleData.getUselessMethods());
        }
        return new ArrayList<>(theUselessMethods);
    }

    public List<VariableSymbol> getUnusedVariables() {
        SortedSet<VariableSymbol> theUnusedVariables = new TreeSet<>();
        for(ResultModuleData theModuleData: myModuleDataEntries) {
            theUnusedVariables.addAll(theModuleData.getUnusedVariables());
        }
        return new ArrayList<>(theUnusedVariables);
    }

    @Override
    public String toString() {
        return myName;
    }

    @Override
    public boolean equals(Object aObject) {
        if(this == aObject) return true;
        if(aObject == null || getClass() != aObject.getClass()) return false;

        ResultSystemData theObject = (ResultSystemData)aObject;
        return myName.equals(theObject.myName);
    }

    @Override
    public int hashCode() {
        return myName.hashCode();
    }
}