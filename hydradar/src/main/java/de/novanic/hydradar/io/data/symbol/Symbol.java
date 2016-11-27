package de.novanic.hydradar.io.data.symbol;

/**
 * @author sstrohschein
 *         <br>Date: 27.11.2016
 *         <br>Time: 11:02
 */
public interface Symbol extends Comparable<Symbol>
{
    String getSymbolName();
}