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

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author sstrohschein
 *         <br>Date: 28.08.2016
 *         <br>Time: 10:55
 */
public class HydradarPlugin extends AbstractUIPlugin
{
    public static final String PROPERTY_HYDRA_RESULTS_LOCATION = "hydraResultsLocation";
    public static final String PROPERTY_HYDRA_RESULTS_LOCATION_DEFAULT = "W:\\sstrohschein\\ungenutzter Code";
    public static final String PROPERTY_SYSTEM_MODULE_GROUP = "hydraSystemModuleGroupToggle";
    public static final String PROPERTY_SHOW_ONLY_CURRENT_TYPE = "hydraShowOnlyCurrentType";

    private static HydradarPlugin myInstance;

    private HydradarPlugin() {}

    public static HydradarPlugin getDefault() {
        if(myInstance == null) {
            myInstance = new HydradarPlugin();
        }
        return myInstance;
    }
}