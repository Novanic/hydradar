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

/**
 * @author sstrohschein
 *         <br>Date: 04.09.2016
 *         <br>Time: 14:40
 */
public final class ClassNameExtractor
{
    private ClassNameExtractor() {}

    public static String extractClassName(String aSymbolId, boolean isMethod, boolean isVariable) {
        int theTypeMethodDivisorPos = aSymbolId.indexOf('#');
        int theMethodParameterDivisorPos = aSymbolId.indexOf('(');
        if(theTypeMethodDivisorPos < 0) {
            theTypeMethodDivisorPos = theMethodParameterDivisorPos;
        }
        return extractClassName(aSymbolId, isMethod, isVariable, theTypeMethodDivisorPos);
    }

    public static String extractClassName(String aSymbolId, boolean isMethod, boolean isVariable, int aTypeMethodDivisorPos) {
        String theClassName = aSymbolId;
        if(isMethod) {
            theClassName = aSymbolId.substring(0, aTypeMethodDivisorPos);
        } else if(isVariable) {
            if(aTypeMethodDivisorPos > -1) {
                theClassName = aSymbolId.substring(0, aTypeMethodDivisorPos);
            } else {
                theClassName = aSymbolId.substring(0, aSymbolId.lastIndexOf('.'));
            }
        }

        int theLocalClassDivisorPos = theClassName.indexOf('$');
        if(theLocalClassDivisorPos > -1) {
            theClassName = theClassName.substring(0, theLocalClassDivisorPos);
        }
        return theClassName;
    }
}