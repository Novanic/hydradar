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
package de.novanic.hydradar.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 08.11.2016
 *         <br>Time: 14:58
 */
public class ClassNameExtractorTest
{
    @Test
    public void testExtractClassName_Class() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.ClassUnused", false, false);
        assertEquals("de.novanic.hydradar.test.ClassUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Constructor() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.ConstructorUnused()", true, false);
        assertEquals("de.novanic.hydradar.test.ConstructorUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Constructor_with_Object_parameter() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.ConstructorUnused(java.lang.String)", true, false);
        assertEquals("de.novanic.hydradar.test.ConstructorUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Constructor_with_primitive_parameter() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.ConstructorUnused(int)", true, false);
        assertEquals("de.novanic.hydradar.test.ConstructorUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Method() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.MethodUnused#methodUnused()", true, false);
        assertEquals("de.novanic.hydradar.test.MethodUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Method_with_Object_parameter() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.MethodUnused#methodUnused(java.lang.String)", true, false);
        assertEquals("de.novanic.hydradar.test.MethodUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Method_with_primitive_parameter() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.MethodUnused#methodUnused(int)", true, false);
        assertEquals("de.novanic.hydradar.test.MethodUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Method_of_innerClass() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.MethodUnused$InnerClass#methodUnused()", true, false);
        assertEquals("de.novanic.hydradar.test.MethodUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Method_of_localInnerClass() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.MethodUnused$1LocalInnerClass#methodUnused()", true, false);
        assertEquals("de.novanic.hydradar.test.MethodUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Method_of_anonymousClass() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.MethodUnused$1#methodUnused()", true, false);
        assertEquals("de.novanic.hydradar.test.MethodUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Variable_Field() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.VariableUnused.myMember", false, true);
        assertEquals("de.novanic.hydradar.test.VariableUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Variable_Field_of_innerClass() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.VariableUnused$InnerClass.myMember", false, true);
        assertEquals("de.novanic.hydradar.test.VariableUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Variable_Field_of_localInnerClass() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.VariableUnused$1LocalInnerClass.myMember", false, true);
        assertEquals("de.novanic.hydradar.test.VariableUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Variable_Field_of_anonymousInnerClass() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.VariableUnused$1.myMember", false, true);
        assertEquals("de.novanic.hydradar.test.VariableUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Variable_Parameter_of_constructor() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.VariableUnused(java.lang.String).aParameter;Pos:0", false, true);
        assertEquals("de.novanic.hydradar.test.VariableUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Variable_Parameter_of_method() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.VariableUnused#method(java.lang.String).aParameter;Pos:0", false, true);
        assertEquals("de.novanic.hydradar.test.VariableUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Variable_Return() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.VariableUnused#method(java.lang.String).<return>", false, true);
        assertEquals("de.novanic.hydradar.test.VariableUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Variable_LocalVariable_of_constructor() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.VariableUnused(java.lang.String).theVariable;Block:1", false, true);
        assertEquals("de.novanic.hydradar.test.VariableUnused", theClassName);
    }

    @Test
    public void testExtractClassName_Variable_LocalVariable_of_method() {
        String theClassName = ClassNameExtractor.extractClassName("de.novanic.hydradar.test.VariableUnused#method(java.lang.String).theVariable;Block:1", false, true);
        assertEquals("de.novanic.hydradar.test.VariableUnused", theClassName);
    }
}