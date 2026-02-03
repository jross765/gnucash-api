package org.gnucash.api.currency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class SimpleCommodityQuoteTable implements SimplePriceTable,
                                                  Serializable 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCommodityQuoteTable.class);

    private static final long serialVersionUID = 5339294652682952052L;

    // -----------------------------------------------------------

    /*
     * The two objects map a *fully-qualified* commodity ID (e.g. "EURONEXT:MBG" or "ISIN:FR0000120644")
     * (*not* the security code (such as "MBG" or "FR0000120644") 
     * to a factor (in two variants: FixedPointNumber and BigFraction).
     * In order to get the value in the base-currency, the factor is to be multiplied with 
     * an amount of that security.
     */
    private Map<String, FixedPointNumber> cmdtyID2Factor    = null; // String, because unqualified
    private Map<String, BigFraction>      cmdtyID2FactorRat = null; // dto.

    // -----------------------------------------------------------

    public SimpleCommodityQuoteTable() {
    	cmdtyID2Factor    = new Hashtable<String, FixedPointNumber>();
    	cmdtyID2FactorRat = new Hashtable<String, BigFraction>();
    }

    // -----------------------------------------------------------

    /**
     * @param cmdtyID a commodity ID (e.g. "EURONEXT:MBG")
     * @return a factor {@link FixedPointNumber} that is to be multiplied with an
     *         amount of that currency to get the value in the base-currency.
     */
    public FixedPointNumber getConversionFactor(final GCshSecID cmdtyID) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is mot set");
		}

    	return cmdtyID2Factor.get(cmdtyID.toString());
    }

    public BigFraction getConversionFactorRat(final GCshSecID cmdtyID) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

    	return cmdtyID2FactorRat.get(cmdtyID.toString());
    }

    // ----------------------------

    /**
     * @param cmdtyID a a commodity ID (e.g. "EURONEXT:MBG")
     * @param factor              a factor {@link FixedPointNumber} that is to be
     *                            multiplied with an amount of that currency to get
     *                            the value in the base-currency.
     */
    public void setConversionFactor(final GCshSecID cmdtyID, final FixedPointNumber factor) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		if ( factor == null ) {
			throw new IllegalArgumentException("argument <factor> is null");
		}

		cmdtyID2Factor.put(cmdtyID.toString(), factor);
    }

    // ----------------------------

    public void setConversionFactorRat(final GCshSecID cmdtyID, final BigFraction factor) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		if ( factor == null ) {
			throw new IllegalArgumentException("argument <factor> is null");
		}

		cmdtyID2FactorRat.put(cmdtyID.toString(), factor);
    }

    // ---------------------------------------------------------------

    /**
     * @param value               the value to convert
     * @param cmdtyID the currency to convert to
     * @return false if the conversion is not possible
     */
    public FixedPointNumber convertFromBaseCurrency(final FixedPointNumber value, final GCshSecID cmdtyID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

        FixedPointNumber factor = getConversionFactor(cmdtyID);
        if ( factor == null ) {
        	LOGGER.error("convertFromBaseCurrency: Cannot get conversion factor for value = " + value + " and code = '" + cmdtyID + "'");
            return null;
        }
        
        // CAUTION: mutable
        FixedPointNumber result = value.copy();
        result.divide(factor);
        return result;
    }

    // ----------------------------

	public BigFraction convertFromBaseCurrencyRat(final BigFraction value, final GCshSecID cmdtyID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		BigFraction factor = getConversionFactorRat(cmdtyID);
        if ( factor == null ) {
        	LOGGER.error("convertFromBaseCurrencyRat: Cannot get conversion factor for value = " + value + " and code = '" + cmdtyID + "'");
            return null;
        }
        
        // CAUTION: immutable
        return value.divide(factor);
	}

    // ----------------------------

    /**
     * @param value           the value to convert
     * @param cmdtyID the commodity's ID
     * @return false if the conversion is not possible
     */
    public FixedPointNumber convertToBaseCurrency(final FixedPointNumber value, final GCshSecID cmdtyID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		FixedPointNumber factor = getConversionFactor(cmdtyID);
		if ( factor == null ) {
        	LOGGER.error("convertToBaseCurrency: Cannot get conversion factor for value = " + value + " and code = '" + cmdtyID + "'");
			return null;
		}
		
        // CAUTION: mutable
        FixedPointNumber result = value.copy();
        result.multiply(factor);
		return result;
    }

    // ----------------------------

	public BigFraction convertToBaseCurrencyRat(final BigFraction value, final GCshSecID cmdtyID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		BigFraction factor = getConversionFactorRat(cmdtyID);
		if ( factor == null ) {
        	LOGGER.error("convertToBaseCurrencyRat: Cannot get conversion factor for value = " + value + " and code = '" + cmdtyID + "'");
            return null;
		}
		
        // CAUTION: immutable
		return value.multiply(factor);
	}

    // ---------------------------------------------------------------

    /**
     * @return all currency-names
     */
    @Override
    public List<String> getCodes() {
		if ( cmdtyID2Factor == null ) {
			throw new IllegalStateException("table is not set");
		}

		ArrayList<String> result = new ArrayList<String>();
    	for ( String key : cmdtyID2Factor.keySet() ) {
    		result.add(key.toString());
    	}
    	
    	return result;
    }
    
    /**
     * forget all conversion-factors.
     */
    @Override
    public void clear() {
		if ( cmdtyID2Factor == null ||
			 cmdtyID2FactorRat == null ) {
			throw new IllegalStateException("table is not set");
		}

        cmdtyID2Factor.clear();
        cmdtyID2FactorRat.clear();
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
    	String result = "SimpleCommodityQuoteTable [\n";
	
    	result += "No. of entries (FP): " + cmdtyID2Factor.size() + "\n";
    	result += "No. of entries (BF): " + cmdtyID2FactorRat.size() + "\n";
	
    	result += "Entries:\n";
    	for ( String cmdtyID : cmdtyID2Factor.keySet() ) {
    		result += " - " + cmdtyID + ";";
    		result += cmdtyID2Factor.get(cmdtyID) + ";";
    		result += cmdtyID2FactorRat.get(cmdtyID) + "\n";
    	}
	
    	result += "]";
	
    	return result;
    }

}
