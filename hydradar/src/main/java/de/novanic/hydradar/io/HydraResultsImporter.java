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
package de.novanic.hydradar.io;

import de.novanic.hydradar.HydradarPlugin;
import de.novanic.hydradar.HydradarRuntimeException;
import de.novanic.hydradar.io.data.ResultData;
import de.novanic.hydradar.io.data.ResultModuleData;
import de.novanic.hydradar.io.data.ResultSystemData;
import de.novanic.hydradar.io.data.symbol.MethodSymbol;
import de.novanic.hydradar.io.data.symbol.PackageSymbol;
import de.novanic.hydradar.io.data.symbol.TypeSymbol;
import de.novanic.hydradar.io.data.symbol.VariableSymbol;

import java.io.*;
import java.util.TreeMap;

/**
 * @author sstrohschein
 *         <br>Date: 27.08.2016
 *         <br>Time: 21:55
 */
public class HydraResultsImporter
{
    public ResultData load() {

        File theHydraResultsFile = findLatestHydraResultsFile();
        if(theHydraResultsFile == null) {
            return new ResultData(null);
        }

        ResultData theResultData = new ResultData(theHydraResultsFile);
        try {
            try(Reader theReader = new FileReader(theHydraResultsFile)) {
                load(theResultData, theReader);
            }
        } catch(IOException e) {
            throw new HydradarRuntimeException("Error on parsing \"" + theHydraResultsFile + "\"!", e);
        }
        return theResultData;
    }

    ResultData load(ResultData aResultData, Reader aReader) throws IOException {
        try(BufferedReader theBufferedReader = new BufferedReader(aReader)) {
            ResultModuleData theCurrentResultModuleData = null;
            String theLine;
            while((theLine = theBufferedReader.readLine()) != null) {
                if(theLine.startsWith("Module: ")) {
                    String theSystemModule = theLine.substring("Module: ".length());
                    int theSystemModuleDivider = theSystemModule.indexOf('/');

                    String theSystem;
                    String theModule;
                    if(theSystemModuleDivider > -1) {
                        theSystem = theSystemModule.substring(0, theSystemModuleDivider);
                        theModule = theSystemModule.substring(theSystemModuleDivider + 1, theSystemModule.length());
                    } else {
                        theSystem = "<system>";
                        theModule = theSystemModule;
                    }

                    ResultSystemData theResultSystemData = aResultData.getSystemDataEntry(theSystem);
                    if(theResultSystemData == null) {
                        theResultSystemData = new ResultSystemData(theSystem);
                        aResultData.addSystemData(theResultSystemData);
                    }
                    theCurrentResultModuleData = new ResultModuleData(theResultSystemData, theModule);
                    theResultSystemData.addModuleData(theCurrentResultModuleData);
                } else if(theLine.startsWith("Unused package: ")) {
                    theCurrentResultModuleData.addUnusedPackage(new PackageSymbol(theLine.substring("Unused package: ".length(), theLine.length())));
                } else if(theLine.startsWith("Unused class: ")) {
                    theCurrentResultModuleData.addUnusedType(new TypeSymbol(theLine.substring("Unused class: ".length(), theLine.length())));
                } else if(theLine.startsWith("Unused constructor: ")) {
                    theCurrentResultModuleData.addUnusedMethod(new MethodSymbol(theLine.substring("Unused constructor: ".length(), theLine.length())));
                } else if(theLine.startsWith("Useless constructor: ")) {
                    theCurrentResultModuleData.addUselessMethod(new MethodSymbol(theLine.substring("Useless constructor: ".length(), theLine.length())));
                } else if(theLine.startsWith("Unused method: ")) {
                    theCurrentResultModuleData.addUnusedMethod(new MethodSymbol(theLine.substring("Unused method: ".length(), theLine.length())));
                } else if(theLine.startsWith("Useless method: ")) {
                    theCurrentResultModuleData.addUselessMethod(new MethodSymbol(theLine.substring("Useless method: ".length(), theLine.length())));
                } else if(theLine.startsWith("Unused variable: ")) {
                    theCurrentResultModuleData.addUnusedVariable(new VariableSymbol(theLine.substring("Unused variable: ".length(), theLine.length())));
                }
            }
        }
        return aResultData;
    }

    private File findLatestHydraResultsFile() {
        HydradarPlugin.getDefault().getPreferenceStore().setDefault(HydradarPlugin.PROPERTY_HYDRA_RESULTS_LOCATION, HydradarPlugin.PROPERTY_HYDRA_RESULTS_LOCATION_DEFAULT);
        String theHydraResultsLocation = HydradarPlugin.getDefault().getPreferenceStore().getString(HydradarPlugin.PROPERTY_HYDRA_RESULTS_LOCATION);
        File theHydraResultsLocationFile = new File(theHydraResultsLocation);

        File[] theFiles = theHydraResultsLocationFile.listFiles();
        if(theFiles == null) {
            return null;
        }

        TreeMap<String, File> theHydraResultFileMap = new TreeMap<>();
        for(File theFile: theFiles) {
            String theFileName = theFile.getName();
            if(theFileName.startsWith("hydra-") && theFileName.endsWith("-results.log")) {
                theHydraResultFileMap.put(theFileName, theFile);
            }
        }

        if(!theHydraResultFileMap.isEmpty()) {
            return theHydraResultFileMap.lastEntry().getValue();
        }
        return null;
    }
}