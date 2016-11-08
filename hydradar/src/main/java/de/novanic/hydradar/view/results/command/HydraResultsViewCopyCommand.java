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
package de.novanic.hydradar.view.results.command;

import de.novanic.hydradar.view.results.HydraResultsView;
import de.novanic.hydradar.view.results.TreeCategoryItem;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import java.util.List;

/**
 * @author sstrohschein
 *         <br>Date: 30.08.2016
 *         <br>Time: 23:01
 */
public class HydraResultsViewCopyCommand extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent aExecutionEvent) throws ExecutionException {
        HydraResultsView theHydraResultsView = (HydraResultsView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(HydraResultsView.class.getName());
        List<TreeCategoryItem> theSelectedTreeCategoryItems = theHydraResultsView.getSelectedTreeCategoryItems();
        if(!theSelectedTreeCategoryItems.isEmpty()) {
            StringBuilder theSelectedTreeCategoryItemStrings = new StringBuilder(theSelectedTreeCategoryItems.size() * 100);
            int i = 0;
            for(TreeCategoryItem theSelectedTreeCategoryItem: theSelectedTreeCategoryItems) {
                if(i > 0) {
                    theSelectedTreeCategoryItemStrings.append('\n');
                }
                theSelectedTreeCategoryItemStrings.append(theSelectedTreeCategoryItem.toString());
                i++;
            }

            Clipboard theClipboard = new Clipboard(Display.getCurrent());
            theClipboard.setContents(new Object[]{theSelectedTreeCategoryItemStrings.toString()}, new Transfer[]{TextTransfer.getInstance()});
        }
        return null;
    }
}