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
package de.novanic.hydradar.view.results.content.category;

import de.novanic.hydradar.io.data.ResultModuleData;
import de.novanic.hydradar.view.util.IconLoader;
import org.eclipse.swt.graphics.Image;

/**
 * @author sstrohschein
 *         <br>Date: 24.10.2016
 *         <br>Time: 14:19
 */
public class UselessMethodsTreeCategory extends TreeCategory
{
    private final Image IMAGE_METHOD = IconLoader.loadImage("eview16/call_hierarchy.png");

    public UselessMethodsTreeCategory(ResultModuleData aParentModuleData) {
        super("Useless methods", aParentModuleData);
    }

    @Override
    public Image getIcon() {
        return IMAGE_METHOD;
    }
}