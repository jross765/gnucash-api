package org.gnucash.api.currency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
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
     * The two objects map a *non-qualified* commodity code (e.g. "MBG" or "FR0000120644") 
     * to a factor (in two variants: FixedPointNumber and BigFraction).
     * In order to get the value in the base-currency, the factor is to be multiplied with 
     * an amount of that commodity.
     */
    private Map<String, FixedPointNumber> mCmdtyCode2Factor    = null; // String, because unqualified
    private Map<String, BigFraction>      mCmdtyCode2FactorRat = null; // dto.

    // -----------------------------------------------------------

    public SimpleCommodityQuoteTable() {
    	mCmdtyCode2Factor    = new Hashtable<String, FixedPointNumber>();
    	mCmdtyCode2FactorRat = new Hashtable<String, BigFraction>();
    }

    // -----------------------------------------------------------

    /**
     * @param cmdtyCode a currency-name in capital letters(e.g. "GBP")
     * @return a factor {@link FixedPointNumber} that is to be multiplied with an
     *         amount of that currency to get the value in the base-currency.
     */
    @Override
    public FixedPointNumber getConversionFactor(final String cmdtyCode) {
		if ( cmdtyCode == null ) {
			throw new IllegalArgumentException("argument <cmdtyCode> is null");
		}

		if ( cmdtyCode.trim().equals("") ) {
			throw new IllegalArgumentException("argument <cmdtyCode> is empty");
		}

    	return mCmdtyCode2Factor.get(cmdtyCode);
    }

    public FixedPointNumber getConversionFactor(final GCshCmdtyID cmdtyID) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is mot set");
		}

    	return mCmdtyCode2Factor.get(cmdtyID.getCode());
    }

    @Override
    public BigFraction getConversionFactorRat(final String cmdtyCode) {
		if ( cmdtyCode == null ) {
			throw new IllegalArgumentException("argument <cmdtyCode> is null");
		}

		if ( cmdtyCode.trim().equals("") ) {
			throw new IllegalArgumentException("argument <cmdtyCode> is empty");
		}

    	return mCmdtyCode2FactorRat.get(cmdtyCode);
    }
    
    public BigFraction getConversionFactorRat(final GCshCmdtyID cmdtyID) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

    	return mCmdtyCode2FactorRat.get(cmdtyID.getCode());
    }

    // ----------------------------

    /**
     * @param cmdtyCode a currency-name in capital letters(e.g. "GBP")
     * @param factor              a factor {@link FixedPointNumber} that is to be
     *                            multiplied with an amount of that currency to get
     *                            the value in the base-currency.
     */
    @Override
    public void setConversionFactor(final String cmdtyCode, final FixedPointNumber factor) {
		if ( cmdtyCode == null ) {
			throw new IllegalArgumentException("argument <cmdtyCode> is null");
		}

		if ( cmdtyCode.trim().equals("") ) {
			throw new IllegalArgumentException("argument <cmdtyCode> is empty");
		}

		if ( factor == null ) {
			throw new IllegalArgumentException("argument <factor> is null");
		}

    	mCmdtyCode2Factor.put(cmdtyCode, factor);
    }

    public void setConversionFactor(final GCshCmdtyID cmdtyID, final FixedPointNumber factor) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		if ( factor == null ) {
			throw new IllegalArgumentException("argument <factor> is null");
		}

		mCmdtyCode2Factor.put(cmdtyID.getCode(), factor);
    }

    // ----------------------------

	@Override
	public void setConversionFactorRat(final String cmdtyCode, final BigFraction factor) {
		if ( cmdtyCode == null ) {
			throw new IllegalArgumentException("argument <cmdtyCode> is null");
		}

		if ( cmdtyCode.trim().equals("") ) {
			throw new IllegalArgumentException("argument <cmdtyCode> is empty");
		}

		if ( factor == null ) {
			throw new IllegalArgumentException("argument <factor> is null");
		}

    	mCmdtyCode2FactorRat.put(cmdtyCode, factor);
	}

    public void setConversionFactor(final GCshCmdtyID cmdtyID, final BigFraction factor) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		if ( factor == null ) {
			throw new IllegalArgumentException("argument <factor> is null");
		}

		mCmdtyCode2FactorRat.put(cmdtyID.getCode(), factor);
    }

    // ---------------------------------------------------------------

    /**
     * @param value               the value to convert
     * @param cmdtyID the currency to convert to
     * @return false if the conversion is not possible
     */
    @Override
    public FixedPointNumber convertFromBaseCurrency(final FixedPointNumber value, final String cmdtyID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( cmdtyID.trim().equals("") ) {
			throw new IllegalArgumentException("argument <cmdtyID> is empty");
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

    public FixedPointNumber convertFromBaseCurrency(final FixedPointNumber value, final GCshCmdtyID cmdtyID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

        return convertFromBaseCurrency(value, cmdtyID.getCode());
    }

    // ----------------------------

	@Override
	public BigFraction convertFromBaseCurrencyRat(final BigFraction value, final String cmdtyID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( cmdtyID.trim().equals("") ) {
			throw new IllegalArgumentException("argument <cmdtyID> is empty");
		}

		BigFraction factor = getConversionFactorRat(cmdtyID);
        if ( factor == null ) {
        	LOGGER.error("convertFromBaseCurrencyRat: Cannot get conversion factor for value = " + value + " and code = '" + cmdtyID + "'");
            return null;
        }
        
        // CAUTION: immutable
        return value.divide(factor);
	}

	public BigFraction convertFromBaseCurrencyRat(final BigFraction value, final GCshCmdtyID cmdtyID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

        return convertFromBaseCurrencyRat(value, cmdtyID.getCode());
	}

    // ----------------------------

    /**
     * @param value           the value to convert
     * @param cmdtyID it's currency
     * @return false if the conversion is not possible
     */
    @Override
    public FixedPointNumber convertToBaseCurrency(final FixedPointNumber value, final String cmdtyID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( cmdtyID.trim().equals("") ) {
			throw new IllegalArgumentException("argument <cmdtyID> is empty");
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

    public FixedPointNumber convertToBaseCurrency(final FixedPointNumber value, final GCshCmdtyID cmdtyID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		return convertToBaseCurrency(value, cmdtyID.getCode());
    }

    // ----------------------------

	@Override
	public BigFraction convertToBaseCurrencyRat(final BigFraction value, final String cmdtyID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( cmdtyID.trim().equals("") ) {
			throw new IllegalArgumentException("argument <cmdtyID> is empty");
		}

		BigFraction factor = getConversionFactorRat(cmdtyID);
		if ( factor == null ) {
        	LOGGER.error("convertToBaseCurrencyRat: Cannot get conversion factor for value = " + value + " and code = '" + cmdtyID + "'");
            return null;
		}
		
        // CAUTION: immutable
		return value.multiply(factor);
	}

	public BigFraction convertToBaseCurrencyRat(final BigFraction value, final GCshCmdtyID cmdtyID) {
		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		return convertToBaseCurrencyRat(value, cmdtyID.getCode());
	}

    // ---------------------------------------------------------------

    /**
     * @return all currency-names
     */
    @Override
    public List<String> getCodes() {
		if ( mCmdtyCode2Factor == null ) {
			throw new IllegalStateException("table is not set");
		}

    	return new ArrayList<String>(mCmdtyCode2Factor.keySet());
    }
    
    /**
     * forget all conversion-factors.
     */
    @Override
    public void clear() {
		if ( mCmdtyCode2Factor == null ||
			 mCmdtyCode2FactorRat == null ) {
			throw new IllegalStateException("table is not set");
		}

        mCmdtyCode2Factor.clear();
        mCmdtyCode2FactorRat.clear();
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
    	String result = "SimpleCommodityQuoteTable [\n";
	
    	result += "No. of entries (FP): " + mCmdtyCode2Factor.size() + "\n";
    	result += "No. of entries (BF): " + mCmdtyCode2FactorRat.size() + "\n";
	
    	result += "Entries:\n";
    	for ( String cmdtyID : mCmdtyCode2Factor.keySet() ) {
    		result += " - " + cmdtyID + ";";
    		result += mCmdtyCode2Factor.get(cmdtyID) + ";";
    		result += mCmdtyCode2FactorRat.get(cmdtyID) + "\n";
    	}
	
    	result += "]";
	
    	return result;
    }

}
