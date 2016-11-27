package de.novanic.hydradar.view.results;

import de.novanic.hydradar.HydradarPlugin;
import de.novanic.hydradar.HydradarRuntimeException;
import de.novanic.hydradar.view.util.IconLoader;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.osgi.service.prefs.BackingStoreException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sstrohschein
 *         <br>Date: 27.11.2016
 *         <br>Time: 11:37
 */
public class HydraResultsToolbar
{
    private final Image IMAGE_SYSTEM_MODULE_GROUP = IconLoader.loadImage("elcl16/hierarchicalLayout.png");
    private final Image IMAGE_SHOW_ONLY_CURRENT_TYPE = IconLoader.loadImage("elcl16/synced.png");

    private final Action myShowCurrentTypeAction;
    private final Action mySystemModuleGroupToggleAction;
    private final List<HydraResultsToolbarListener> myListeners;

    public HydraResultsToolbar(IToolBarManager aToolBarManager) {
        myListeners = new ArrayList<>();

        myShowCurrentTypeAction = new Action("Show only current type", Action.AS_CHECK_BOX) {};
        myShowCurrentTypeAction.setImageDescriptor(new ImageDescriptor() {
            @Override
            public ImageData getImageData() {
                return IMAGE_SHOW_ONLY_CURRENT_TYPE.getImageData();
            }
        });

        mySystemModuleGroupToggleAction = new Action("Group by system/module", Action.AS_CHECK_BOX) {};
        mySystemModuleGroupToggleAction.setImageDescriptor(new ImageDescriptor() {
            @Override
            public ImageData getImageData() {
                return IMAGE_SYSTEM_MODULE_GROUP.getImageData();
            }
        });

        myShowCurrentTypeAction.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent aPropertyChangeEvent) {
                if(Action.CHECKED.equals(aPropertyChangeEvent.getProperty())) {
                    boolean theSetting = (boolean)aPropertyChangeEvent.getNewValue();

                    if(theSetting) {
                        mySystemModuleGroupToggleAction.setEnabled(false);
                        mySystemModuleGroupToggleAction.setChecked(false);
                    } else {
                        mySystemModuleGroupToggleAction.setEnabled(true);
                    }

                    IEclipsePreferences thePreferences = InstanceScope.INSTANCE.getNode(HydraResultsView.class.getName());
                    thePreferences.putBoolean(HydradarPlugin.PROPERTY_SHOW_ONLY_CURRENT_TYPE, theSetting);
                    thePreferences.putBoolean(HydradarPlugin.PROPERTY_SYSTEM_MODULE_GROUP, mySystemModuleGroupToggleAction.isChecked()); //it is changed above
                    savePreferences(thePreferences);

                    for(HydraResultsToolbarListener theListener: myListeners) {
                        theListener.onToggleShowCurrentType(theSetting);
                    }
                }
            }
        });
        mySystemModuleGroupToggleAction.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent aPropertyChangeEvent) {
                if(Action.CHECKED.equals(aPropertyChangeEvent.getProperty()) && mySystemModuleGroupToggleAction.isEnabled()) {
                    boolean theSetting = (boolean)aPropertyChangeEvent.getNewValue();

                    IEclipsePreferences thePreferences = InstanceScope.INSTANCE.getNode(HydraResultsView.class.getName());
                    thePreferences.putBoolean(HydradarPlugin.PROPERTY_SYSTEM_MODULE_GROUP, theSetting);
                    savePreferences(thePreferences);

                    for(HydraResultsToolbarListener theListener: myListeners) {
                        theListener.onToggleSystemModuleGroup(theSetting);
                    }
                }
            }
        });

        IEclipsePreferences thePreferences = InstanceScope.INSTANCE.getNode(HydraResultsView.class.getName());
        myShowCurrentTypeAction.setChecked(thePreferences.getBoolean(HydradarPlugin.PROPERTY_SHOW_ONLY_CURRENT_TYPE, false));
        mySystemModuleGroupToggleAction.setChecked(thePreferences.getBoolean(HydradarPlugin.PROPERTY_SYSTEM_MODULE_GROUP, false));
        savePreferences(thePreferences);

        aToolBarManager.add(myShowCurrentTypeAction);
        aToolBarManager.add(mySystemModuleGroupToggleAction);
    }

    public void enable() {
        setEnabled(true);
    }

    public void disable() {
        setEnabled(false);
    }

    private void setEnabled(boolean isEnabled) {
        myShowCurrentTypeAction.setEnabled(isEnabled);
        mySystemModuleGroupToggleAction.setEnabled(isEnabled);
    }

    public void addListener(HydraResultsToolbarListener aListener) {
        myListeners.add(aListener);
    }

    public boolean isShowCurrentTypeActionChecked() {
        return myShowCurrentTypeAction.isChecked();
    }

    public boolean isSystemGroupActionChecked() {
        return mySystemModuleGroupToggleAction.isChecked();
    }

    private void savePreferences(IEclipsePreferences aPreferences) {
        try {
            aPreferences.flush();
        } catch(BackingStoreException e) {
            throw new HydradarRuntimeException("Error on saving preferences!", e);
        }
    }
}