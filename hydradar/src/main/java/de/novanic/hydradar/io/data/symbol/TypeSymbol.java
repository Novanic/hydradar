package de.novanic.hydradar.io.data.symbol;

import de.novanic.hydradar.util.ClassNameExtractor;

/**
 * @author sstrohschein
 *         <br>Date: 27.11.2016
 *         <br>Time: 10:57
 */
public class TypeSymbol extends AbstractSymbol
{
    private final String myEnclosingTypeName;

    public TypeSymbol(String aSymbolName) {
        super(aSymbolName);
        myEnclosingTypeName = ClassNameExtractor.extractClassName(aSymbolName, false, false, -1).intern();
    }

    public String getEnclosingTypeName() {
        return myEnclosingTypeName;
    }
}