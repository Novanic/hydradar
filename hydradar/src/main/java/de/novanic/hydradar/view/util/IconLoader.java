package de.novanic.hydradar.view.util;

import de.novanic.hydradar.HydradarRuntimeException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author sstrohschein
 *         <br>Date: 08.11.2016
 *         <br>Time: 21:46
 */
public final class IconLoader
{
    private IconLoader() {}

    public static Image loadImage(String aIconPath) {
        String theURLString = "platform:/plugin/org.eclipse.jdt.ui/icons/full/" + aIconPath;
        try {
            return ImageDescriptor.createFromURL(new URL(theURLString)).createImage();
        } catch(MalformedURLException e) {
            throw new HydradarRuntimeException("Error on loading image \"" + theURLString + "\"!", e);
        }
    }
}