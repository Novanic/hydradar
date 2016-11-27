package de.novanic.hydradar.io.data.symbol;

import de.novanic.hydradar.util.ClassNameExtractor;

/**
 * @author sstrohschein
 *         <br>Date: 27.11.2016
 *         <br>Time: 11:07
 */
public class VariableSymbol extends AbstractSymbol
{
    private final String myTypeName;

    public VariableSymbol(String aSymbolName) {
        super(aSymbolName);
        myTypeName = ClassNameExtractor.extractClassName(aSymbolName, false, true);
    }

    public String getTypeName() {
        return myTypeName;
    }
}