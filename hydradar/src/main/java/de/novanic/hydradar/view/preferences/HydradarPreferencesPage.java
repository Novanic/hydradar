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
package de.novanic.hydradar.view.preferences;

import de.novanic.hydradar.HydradarPlugin;
import de.novanic.hydradar.view.results.HydraResultsView;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author sstrohschein
 *         <br>Date: 27.08.2016
 *         <br>Time: 23:53
 */
public class HydradarPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage
{
    private IWorkbench myWorkbench;
    private Text myHydraResultsLocationTextField;

    @Override
    public void init(IWorkbench aWorkbench) {
        myWorkbench = aWorkbench;
        getPreferenceStore().setDefault(HydradarPlugin.PROPERTY_HYDRA_RESULTS_LOCATION, HydradarPlugin.PROPERTY_HYDRA_RESULTS_LOCATION_DEFAULT);
    }

    protected Control createContents(Composite aParent) {
        Composite theHydraResultsLocationComposite = createComposite(aParent, 3);
        Label theHydraResultsLocationLabel = new Label(theHydraResultsLocationComposite, SWT.NONE);
        theHydraResultsLocationLabel.setText("Location");

        myHydraResultsLocationTextField = new Text(theHydraResultsLocationComposite, SWT.SINGLE | SWT.BORDER);
        myHydraResultsLocationTextField.setText(getPreferenceStore().getString(HydradarPlugin.PROPERTY_HYDRA_RESULTS_LOCATION));
        GridData theHydraResultsLocationTextFieldGridData = new GridData(GridData.FILL_HORIZONTAL);
        theHydraResultsLocationTextFieldGridData.grabExcessHorizontalSpace = true;
        myHydraResultsLocationTextField.setLayoutData(theHydraResultsLocationTextFieldGridData);

        final Button theHydraResultsLocationButton = new Button(theHydraResultsLocationComposite, SWT.NONE);
        theHydraResultsLocationButton.setText("...");
        theHydraResultsLocationButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent aSelectionEvent) {
                DirectoryDialog theDialog = new DirectoryDialog(theHydraResultsLocationButton.getShell(), SWT.OPEN);
                String thePath = theDialog.open();
                if(thePath != null) {
                    myHydraResultsLocationTextField.setText(thePath);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent aSelectionEvent) {}
        });

        return new Composite(aParent, SWT.NULL);
    }

    @Override
    public boolean performOk() {
        store(myHydraResultsLocationTextField.getText());

        HydraResultsView theHydraResultsView = (HydraResultsView)myWorkbench.getActiveWorkbenchWindow().getActivePage().findView(HydraResultsView.class.getName());
        if(theHydraResultsView != null) {
            theHydraResultsView.reload();
        }

        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        myHydraResultsLocationTextField.setText(getPreferenceStore().getDefaultString(HydradarPlugin.PROPERTY_HYDRA_RESULTS_LOCATION));
        super.performDefaults();
    }

    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return HydradarPlugin.getDefault().getPreferenceStore();
    }

    private void store(String aHydraResultsLocation) {
        getPreferenceStore().setValue(HydradarPlugin.PROPERTY_HYDRA_RESULTS_LOCATION, aHydraResultsLocation);
    }

    private Composite createComposite(Composite aParent, int aColumnCount) {
        Composite theComposite = new Composite(aParent, SWT.NONE);
        GridLayout theLayout = new GridLayout(aColumnCount, false);
        theLayout.marginWidth = 0;
        theLayout.marginHeight = 0;
        theComposite.setLayout(theLayout);
        theComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        return theComposite;
    }
}