package de.novanic.hydradar.view.results;

import de.novanic.hydradar.io.data.ResultModuleData;
import de.novanic.hydradar.io.data.ResultSystemData;
import de.novanic.hydradar.view.results.content.category.TreeCategory;
import de.novanic.hydradar.view.util.IconLoader;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author sstrohschein
 *         <br>Date: 27.11.2016
 *         <br>Time: 13:53
 */
public class TreeCategoryLabelProvider extends LabelProvider
{
    private final Image IMAGE_MODULE = IconLoader.loadImage("eview16/packages.png");

    @Override
    public Image getImage(Object aElement) {
        if(aElement instanceof TreeCategoryItem) {
            return getImage(((TreeCategoryItem)aElement).getTreeCategory());
        } else if(aElement instanceof TreeCategory) {
            return ((TreeCategory)aElement).getIcon();
        } else if(aElement instanceof ResultSystemData || aElement instanceof ResultModuleData) {
            return IMAGE_MODULE;
        }
        return super.getImage(aElement);
    }
}