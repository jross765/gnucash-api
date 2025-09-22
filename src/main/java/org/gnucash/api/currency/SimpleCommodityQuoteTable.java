package org.gnucash.api.currency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class SimpleCommodityQuoteTable implements SimplePriceTable,
                                                 Serializable 
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCommodityQuoteTable.class);

    private static final long serialVersionUID = 5339294652682952052L;

    // -----------------------------------------------------------

    /**
     * maps a currency-name in capital letters(e.g. "GBP") to a factor
     * {@link FixedPointNumber} that is to be multiplied with an amount of that
     * currency to get the value in the base-currency.
     *
     * @see {@link #getConversionFactor(String)}
     */
    private Map<String, FixedPointNumber> mCmdtyID2Factor = null;

    // -----------------------------------------------------------

    public SimpleCommodityQuoteTable() {
	mCmdtyID2Factor = new Hashtable<String, FixedPointNumber>();
    }

    // -----------------------------------------------------------

    /**
     * @param cmdtyID a currency-name in capital letters(e.g. "GBP")
     * @return a factor {@link FixedPointNumber} that is to be multiplied with an
     *         amount of that currency to get the value in the base-currency.
     */
    @Override
    public FixedPointNumber getConversionFactor(final String cmdtyID) {
	return mCmdtyID2Factor.get(cmdtyID);
    }

    /**
     * @param cmdtyQualifID a currency-name in capital letters(e.g. "GBP")
     * @param factor              a factor {@link FixedPointNumber} that is to be
     *                            multiplied with an amount of that currency to get
     *                            the value in the base-currency.
     */
    @Override
    public void setConversionFactor(final String cmdtyQualifID, final FixedPointNumber factor) {
	mCmdtyID2Factor.put(cmdtyQualifID, factor);
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

		mCmdtyID2Factor.put(cmdtyID.toString(), factor);
    }

    public void setConversionFactor(final String nameSpace, final String code, 
	                            final FixedPointNumber factor) {
		mCmdtyID2Factor.put(new GCshCmdtyID(nameSpace, code).toString(), factor);
    }

    // ---------------------------------------------------------------

    /**
     * @param value               the value to convert
     * @param cmdtyID the currency to convert to
     * @return false if the conversion is not possible
     */
    @Override
    public boolean convertFromBaseCurrency(FixedPointNumber value, final String cmdtyID) {
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
        if (factor == null) {
            return false;
        }
        
        value.divide(factor);
        return true;
    }

    /**
     * @param value           the value to convert
     * @param cmdtyID it's currency
     * @return false if the conversion is not possible
     */
    @Override
    public boolean convertToBaseCurrency(FixedPointNumber value, final String cmdtyID) {
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
		if (factor == null) {
			return false;
		}
		
		value.multiply(factor);
		return true;
    }

    // ---------------------------------------------------------------

    /**
     * @return all currency-names
     */
    @Override
    public List<String> getCurrencies() {
	return new ArrayList<String>(mCmdtyID2Factor.keySet());
    }
    
    /**
     * forget all conversion-factors.
     */
    @Override
    public void clear() {
        mCmdtyID2Factor.clear();
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	String result = "SimpleCommodityQuoteTable [\n";
	
	result += "No. of entries: " + mCmdtyID2Factor.size() + "\n";
	
	result += "Entries:\n";
	for ( String cmdtyID : mCmdtyID2Factor.keySet() ) {
	    // result += " - " + cmdtyID + "\n";
	    result += " - " + cmdtyID + ";" + mCmdtyID2Factor.get(cmdtyID) + "\n";
	}
	
	result += "]";
	
	return result;
    }

}
