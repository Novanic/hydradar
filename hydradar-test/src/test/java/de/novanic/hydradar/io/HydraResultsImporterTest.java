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

import de.novanic.hydradar.io.data.ResultData;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/**
 * @author sstrohschein
 *         <br>Date: 08.11.2016
 *         <br>Time: 16:27
 */
public class HydraResultsImporterTest
{
    private static final String MODULE_LINE = "Module: test\n";

    private HydraResultsImporter myHydraResultsImporter;
    private ResultData myEmptyResultData;

    @Before
    public void before() {
        myHydraResultsImporter = new HydraResultsImporter();
        myEmptyResultData = new ResultData(null);
    }

    @Test
    public void testLoad_with_module() throws Exception {
        ResultData theResultData = myHydraResultsImporter.load(myEmptyResultData, new StringReader("Module: testModule\nUnused class: de.novanic.hydradar.test.ClassUnused"));
        assertEquals(1, theResultData.getSystemDataEntries().size());
        assertEquals("<system>", theResultData.getSystemDataEntries().get(0).getName());
        assertEquals(1, theResultData.getSystemDataEntries().get(0).getModuleDataEntries().size());
        assertEquals("testModule", theResultData.getSystemDataEntries().get(0).getModuleDataEntries().get(0).getName());
    }

    @Test
    public void testLoad_with_system() throws Exception {
        ResultData theResultData = myHydraResultsImporter.load(myEmptyResultData, new StringReader("Module: testSystem/testModule\nUnused class: de.novanic.hydradar.test.ClassUnused"));
        assertEquals(1, theResultData.getSystemDataEntries().size());
        assertEquals("testSystem", theResultData.getSystemDataEntries().get(0).getName());
        assertEquals(1, theResultData.getSystemDataEntries().get(0).getModuleDataEntries().size());
        assertEquals("testModule", theResultData.getSystemDataEntries().get(0).getModuleDataEntries().get(0).getName());
    }

    @Test
    public void testLoad_unusedPackage() throws Exception {
        ResultData theResultData = myHydraResultsImporter.load(myEmptyResultData, new StringReader(MODULE_LINE + "Unused package: de.novanic.hydradar.test.package"));
        assertEquals(1, theResultData.getUnusedPackages().size());
        assertEquals(0, theResultData.getUnusedTypes().size());
        assertEquals(0, theResultData.getUnusedMethods().size());
        assertEquals(0, theResultData.getUselessMethods().size());
        assertEquals(0, theResultData.getUnusedVariables().size());
    }

    @Test
    public void testLoad_unusedClass() throws Exception {
        ResultData theResultData = myHydraResultsImporter.load(myEmptyResultData, new StringReader(MODULE_LINE + "Unused class: de.novanic.hydradar.test.ClassUnused"));
        assertEquals(0, theResultData.getUnusedPackages().size());
        assertEquals(1, theResultData.getUnusedTypes().size());
        assertEquals(0, theResultData.getUnusedMethods().size());
        assertEquals(0, theResultData.getUselessMethods().size());
        assertEquals(0, theResultData.getUnusedVariables().size());
    }

    @Test
    public void testLoad_unusedConstructor() throws Exception {
        ResultData theResultData = myHydraResultsImporter.load(myEmptyResultData, new StringReader(MODULE_LINE + "Unused constructor: de.novanic.hydradar.test.ClassUsed()"));
        assertEquals(0, theResultData.getUnusedPackages().size());
        assertEquals(0, theResultData.getUnusedTypes().size());
        assertEquals(1, theResultData.getUnusedMethods().size());
        assertEquals(0, theResultData.getUselessMethods().size());
        assertEquals(0, theResultData.getUnusedVariables().size());
    }

    @Test
    public void testLoad_uselessConstructor() throws Exception {
        ResultData theResultData = myHydraResultsImporter.load(myEmptyResultData, new StringReader(MODULE_LINE + "Useless constructor: de.novanic.hydradar.test.ClassUsed()"));
        assertEquals(0, theResultData.getUnusedPackages().size());
        assertEquals(0, theResultData.getUnusedTypes().size());
        assertEquals(0, theResultData.getUnusedMethods().size());
        assertEquals(1, theResultData.getUselessMethods().size());
        assertEquals(0, theResultData.getUnusedVariables().size());
    }

    @Test
    public void testLoad_unusedMethod() throws Exception {
        ResultData theResultData = myHydraResultsImporter.load(myEmptyResultData, new StringReader(MODULE_LINE + "Unused method: de.novanic.hydradar.test.ClassUsed#methodUnused()"));
        assertEquals(0, theResultData.getUnusedPackages().size());
        assertEquals(0, theResultData.getUnusedTypes().size());
        assertEquals(1, theResultData.getUnusedMethods().size());
        assertEquals(0, theResultData.getUselessMethods().size());
        assertEquals(0, theResultData.getUnusedVariables().size());
    }

    @Test
    public void testLoad_uselessMethod() throws Exception {
        ResultData theResultData = myHydraResultsImporter.load(myEmptyResultData, new StringReader(MODULE_LINE + "Useless method: de.novanic.hydradar.test.ClassUsed#methodUnused()"));
        assertEquals(0, theResultData.getUnusedPackages().size());
        assertEquals(0, theResultData.getUnusedTypes().size());
        assertEquals(0, theResultData.getUnusedMethods().size());
        assertEquals(1, theResultData.getUselessMethods().size());
        assertEquals(0, theResultData.getUnusedVariables().size());
    }

    @Test
    public void testLoad_unusedVariable() throws Exception {
        ResultData theResultData = myHydraResultsImporter.load(myEmptyResultData, new StringReader(MODULE_LINE + "Unused variable: de.novanic.hydradar.test.ClassUsed.myMember"));
        assertEquals(0, theResultData.getUnusedPackages().size());
        assertEquals(0, theResultData.getUnusedTypes().size());
        assertEquals(0, theResultData.getUnusedMethods().size());
        assertEquals(0, theResultData.getUselessMethods().size());
        assertEquals(1, theResultData.getUnusedVariables().size());
    }
}