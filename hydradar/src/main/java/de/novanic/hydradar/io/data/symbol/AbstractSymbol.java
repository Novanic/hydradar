package de.novanic.hydradar.io.data.symbol;

/**
 * @author sstrohschein
 *         <br>Date: 27.11.2016
 *         <br>Time: 11:10
 */
public class AbstractSymbol implements Symbol
{
    private final String mySymbolName;

    public AbstractSymbol(String aSymbolName) {
        mySymbolName = aSymbolName;
    }

    @Override
    public String getSymbolName() {
        return mySymbolName;
    }

    @Override
    public int compareTo(Symbol aOther) {
        return mySymbolName.compareTo(aOther.getSymbolName());
    }

    @Override
    public boolean equals(Object aOther) {
        if(this == aOther) return true;
        if(aOther == null || getClass() != aOther.getClass()) return false;

        Symbol theOther = (Symbol)aOther;
        return mySymbolName.equals(theOther.getSymbolName());
    }

    @Override
    public int hashCode() {
        return mySymbolName.hashCode();
    }
}