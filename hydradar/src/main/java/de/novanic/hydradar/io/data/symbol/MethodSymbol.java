package de.novanic.hydradar.io.data.symbol;

import de.novanic.hydradar.util.ClassNameExtractor;

/**
 * @author sstrohschein
 *         <br>Date: 27.11.2016
 *         <br>Time: 11:02
 */
public class MethodSymbol extends AbstractSymbol
{
    private final String myTypeName;

    public MethodSymbol(String aSymbolName) {
        super(aSymbolName);
        myTypeName = ClassNameExtractor.extractClassName(aSymbolName, true, false).intern();
    }

    public String getTypeName() {
        return myTypeName;
    }
}