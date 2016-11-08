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

import java.io.File;
import java.util.*;

/**
 * @author sstrohschein
 *         <br>Date: 27.08.2016
 *         <br>Time: 21:55
 */
public class ResultData
{
    private final File myResultFile;
    private final LinkedHashMap<String, ResultSystemData> mySystemDataEntries;

    public ResultData(File aResultFile) {
        myResultFile = aResultFile;
        mySystemDataEntries = new LinkedHashMap<>();
    }

    public File getResultFile() {
        return myResultFile;
    }

    public void addSystemData(ResultSystemData aResultSystemData) {
        mySystemDataEntries.put(aResultSystemData.getName(), aResultSystemData);
    }

    public ResultSystemData getSystemDataEntry(String aSystemName) {
        return mySystemDataEntries.get(aSystemName);
    }

    public List<ResultSystemData> getSystemDataEntries() {
        return new ArrayList<>(mySystemDataEntries.values());
    }

    public List<String> getUnusedPackages() {
        SortedSet<String> theUnusedPackages = new TreeSet<>();
        for(ResultSystemData theSystemData: getSystemDataEntries()) {
            theUnusedPackages.addAll(theSystemData.getUnusedPackages());
        }
        return new ArrayList<>(theUnusedPackages);
    }

    public List<String> getUnusedTypes() {
        SortedSet<String> theUnusedTypes = new TreeSet<>();
        for(ResultSystemData theSystemData: getSystemDataEntries()) {
            theUnusedTypes.addAll(theSystemData.getUnusedTypes());
        }
        return new ArrayList<>(theUnusedTypes);
    }

    public List<String> getUnusedMethods() {
        SortedSet<String> theUnusedMethods = new TreeSet<>();
        for(ResultSystemData theSystemData: getSystemDataEntries()) {
            theUnusedMethods.addAll(theSystemData.getUnusedMethods());
        }
        return new ArrayList<>(theUnusedMethods);
    }

    public List<String> getUselessMethods() {
        SortedSet<String> theUselessMethods = new TreeSet<>();
        for(ResultSystemData theSystemData: getSystemDataEntries()) {
            theUselessMethods.addAll(theSystemData.getUselessMethods());
        }
        return new ArrayList<>(theUselessMethods);
    }

    public List<String> getUnusedVariables() {
        SortedSet<String> theUnusedVariables = new TreeSet<>();
        for(ResultSystemData theSystemData: getSystemDataEntries()) {
            theUnusedVariables.addAll(theSystemData.getUnusedVariables());
        }
        return new ArrayList<>(theUnusedVariables);
    }
}