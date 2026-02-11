package org.gnucash.api.pricedb;

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

public class SimpleSecurityQuoteTable implements SimplePriceTable,
                                                  Serializable 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSecurityQuoteTable.class);

    private static final long serialVersionUID = 5339294652682952052L;

    // -----------------------------------------------------------

    /*
     * The two objects map a *fully-qualified* security ID (e.g. "EURONEXT:MBG" or "ISIN:FR0000120644")
     * (*not* the security code (such as "MBG" or "FR0000120644") 
     * to a factor (in two variants: FixedPointNumber and BigFraction).
     * In order to get the value in the base-currency, the factor is to be multiplied with 
     * an amount of that security.
     */
    private Map<String, FixedPointNumber> secID2Factor    = null; // String, because unqualified
    private Map<String, BigFraction>      secID2FactorRat = null; // dto.

    // -----------------------------------------------------------

    public SimpleSecurityQuoteTable() {
    	secID2Factor    = new Hashtable<String, FixedPointNumber>();
    	secID2FactorRat = new Hashtable<String, BigFraction>();
    }

    // -----------------------------------------------------------

    /**
     * @param secID a security ID (e.g. "EURONEXT:MBG")
     * @return a factor {@link FixedPointNumber} that is to be multiplied with an
     *         amount of that currency to get the value in the base-currency.
     */
    public FixedPointNumber getConversionFactor(final GCshSecID secID) {
		if ( secID == null ) {
			throw new IllegalArgumentException("argument <secID> is null");
		}

		if ( ! secID.isSet() ) {
			throw new IllegalArgumentException("argument <secID> is mot set");
		}

    	return secID2Factor.get(secID.toString());
    }

    public BigFraction getConversionFactorRat(final GCshSecID secID) {
		if ( secID == null ) {
			throw new IllegalArgumentException("argument <secID> is null");
		}

		if ( ! secID.isSet() ) {
			throw new IllegalArgumentException("argument <secID> is not set");
		}

    	return secID2FactorRat.get(secID.toString());
    }

    // ----------------------------

    /**
     * @param secID a a commodity ID (e.g. "EURONEXT:MBG")
     * @param factor              a factor {@link FixedPointNumber} that is to be
     *                            multiplied with an amount of that currency to get
     *                            the value in the base-currency.
     */
    public void setConversionFactor(final GCshSecID secID, final FixedPointNumber factor) {
		if ( secID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! secID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		if ( factor == null ) {
			throw new IllegalArgumentException("argument <factor> is null");
		}

		secID2Factor.put(secID.toString(), factor);
    }

    // ----------------------------

    public void setConversionFactorRat(final GCshSecID secID, final BigFraction factor) {
		if ( secID == null ) {
			throw new IllegalArgumentException("argument <secID> is null");
		}

		if ( ! secID.isSet() ) {
			throw new IllegalArgumentException("argument <secID> is not set");
		}

		if ( factor == null ) {
			throw new IllegalArgumentException("argument <factor> is null");
		}

		secID2FactorRat.put(secID.toString(), factor);
    }

    // ---------------------------------------------------------------

    /**
     * @param value               the value to convert
     * @param secID the currency to convert to
     * @return false if the conversion is not possible
     */
    public FixedPointNumber convertFromBaseCurrency(final FixedPointNumber value, final GCshSecID secID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( secID == null ) {
			throw new IllegalArgumentException("argument <secID> is null");
		}

		if ( ! secID.isSet() ) {
			throw new IllegalArgumentException("argument <secID> is not set");
		}

        FixedPointNumber factor = getConversionFactor(secID);
        if ( factor == null ) {
        	LOGGER.error("convertFromBaseCurrency: Cannot get conversion factor for value = " + value + " and code = '" + secID + "'");
            return null;
        }
        
        // CAUTION: mutable
        FixedPointNumber result = value.copy();
        result.divide(factor);
        return result;
    }

    // ----------------------------

	public BigFraction convertFromBaseCurrencyRat(final BigFraction value, final GCshSecID secID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( secID == null ) {
			throw new IllegalArgumentException("argument <secID> is null");
		}

		if ( ! secID.isSet() ) {
			throw new IllegalArgumentException("argument <secID> is not set");
		}

		BigFraction factor = getConversionFactorRat(secID);
        if ( factor == null ) {
        	LOGGER.error("convertFromBaseCurrencyRat: Cannot get conversion factor for value = " + value + " and code = '" + secID + "'");
            return null;
        }
        
        // CAUTION: immutable
        return value.divide(factor);
	}

    // ----------------------------

    /**
     * @param value           the value to convert
     * @param secID the security's ID
     * @return false if the conversion is not possible
     */
    public FixedPointNumber convertToBaseCurrency(final FixedPointNumber value, final GCshSecID secID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( secID == null ) {
			throw new IllegalArgumentException("argument <secID> is null");
		}

		if ( ! secID.isSet() ) {
			throw new IllegalArgumentException("argument <secID> is not set");
		}

		FixedPointNumber factor = getConversionFactor(secID);
		if ( factor == null ) {
        	LOGGER.error("convertToBaseCurrency: Cannot get conversion factor for value = " + value + " and code = '" + secID + "'");
			return null;
		}
		
        // CAUTION: mutable
        FixedPointNumber result = value.copy();
        result.multiply(factor);
		return result;
    }

    // ----------------------------

	public BigFraction convertToBaseCurrencyRat(final BigFraction value, final GCshSecID secID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( secID == null ) {
			throw new IllegalArgumentException("argument <secID> is null");
		}

		if ( ! secID.isSet() ) {
			throw new IllegalArgumentException("argument <secID> is not set");
		}

		BigFraction factor = getConversionFactorRat(secID);
		if ( factor == null ) {
        	LOGGER.error("convertToBaseCurrencyRat: Cannot get conversion factor for value = " + value + " and code = '" + secID + "'");
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
		if ( secID2Factor == null ) {
			throw new IllegalStateException("table is not set");
		}

		ArrayList<String> result = new ArrayList<String>();
    	for ( String key : secID2Factor.keySet() ) {
    		result.add(key.toString());
    	}
    	
    	return result;
    }
    
    /**
     * forget all conversion-factors.
     */
    @Override
    public void clear() {
		if ( secID2Factor == null ||
			 secID2FactorRat == null ) {
			throw new IllegalStateException("table is not set");
		}

        secID2Factor.clear();
        secID2FactorRat.clear();
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
    	String result = "SimpleSecurityQuoteTable [\n";
	
    	result += "No. of entries (FP): " + secID2Factor.size() + "\n";
    	result += "No. of entries (BF): " + secID2FactorRat.size() + "\n";
	
    	result += "Entries:\n";
    	for ( String cmdtyID : secID2Factor.keySet() ) {
    		result += " - " + cmdtyID + ";";
    		result += secID2Factor.get(cmdtyID) + ";";
    		result += secID2FactorRat.get(cmdtyID) + "\n";
    	}
	
    	result += "]";
	
    	return result;
    }

}
