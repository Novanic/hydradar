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
package de.novanic.hydradar;

/**
 * @author sstrohschein
 *         <br>Date: 27.08.2016
 *         <br>Time: 22:54
 */
public class HydradarRuntimeException extends RuntimeException
{
    public HydradarRuntimeException(String aMessage) {
        super(aMessage);
    }

    public HydradarRuntimeException(String aMessage, Throwable aThrowable) {
        super(aMessage, aThrowable);
    }
}