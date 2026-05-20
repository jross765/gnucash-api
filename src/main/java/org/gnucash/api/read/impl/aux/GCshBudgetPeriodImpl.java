package org.gnucash.api.read.impl.aux;

import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.Const;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshBudgetAccount;
import org.gnucash.api.read.aux.GCshBudgetPeriod;
import org.gnucash.api.read.impl.hlp.AmountFormatter_BF;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBElement;

public class GCshBudgetPeriodImpl extends GnuCashObjectImpl 
							      implements GCshBudgetPeriod 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshBudgetPeriodImpl.class);

    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    
    // -----------------------------------------------------------

    protected final Slot jwsdpPeer;

    // -----------------------------------------------------------
    
    protected final GCshBudgetAccount parent;

    // -----------------------------------------------------------

    /**
     * @param parent 
     * @param newPeer the JWSDP-object we are wrapping.
     * @param gcshFile 
     */
    @SuppressWarnings("exports")
    public GCshBudgetPeriodImpl(
    		final GCshBudgetAccount parent, 
    		final Slot newPeer, 
    		final GnuCashFile gcshFile) {
    	super(gcshFile);
		
    	this.parent    = parent;
    	this.jwsdpPeer = newPeer;
    }

	// ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public Slot getJwsdpPeer() {
    	return jwsdpPeer;
    }

    // -----------------------------------------------------------------

	@Override
	public GCshBudgetAccount getParent() {
		return parent;
	}

    // -----------------------------------------------------------------

	@Override
	public BigInteger getPeriodIndex() {
		if ( jwsdpPeer.getSlotKey() == null ) {
			return null;
		}
		
		return new BigInteger(jwsdpPeer.getSlotKey());
	}

	@Override
	public BigFraction getAmount() {
		if ( jwsdpPeer.getSlotValue() == null ) {
			return null;
		}
		
		if ( ! jwsdpPeer.getSlotValue().getType().equals(Const.XML_DATA_TYPE_NUMERIC) ) {
			throw new IllegalStateException("Slot value is not of type '" + Const.XML_DATA_TYPE_NUMERIC + "'");
		}
		
		String amtStr = "";
		
		// ::TODO: The following code is partially redundant to 
		// HasUserDefinedAttributesImpl.getUserDefinedAttributeCore():
		List<Object> objList = jwsdpPeer.getSlotValue().getContent();
		if ( objList == null || objList.size() == 0 )
			return null;
		Object valElt = objList.get(0);
		if ( valElt == null )
			return null;
		LOGGER.debug("getAmount: User-defined attribute for key '" + getPeriodIndex() + "' may not be a String."
				+ " It is of type [" + valElt.getClass().getName() + "]");
		if ( valElt instanceof String ) {
			amtStr = (String) valElt;
		} else if ( valElt instanceof JAXBElement ) {
			amtStr = ((JAXBElement) valElt).getValue().toString();
		} else {
			LOGGER.error("getAmount: User-defined attribute for key '" + getPeriodIndex() + "' may not be a String."
					+ " It is of UNKNOWN type [" + valElt.getClass().getName() + "]");
			throw new IllegalStateException("Unknown type");
		}

		return BigFraction.parse(amtStr);
	}

	@Override
	public String getAmountFormatted() {
		return getAmountFormatted(Locale.getDefault());
	}

	public String getAmountFormatted(final Locale lcl) {
		GnuCashAccount acct = getGnuCashFile().getAccountByID(parent.getAcctID());
    	return AmountFormatter_BF.formatAmount( getGnuCashFile(),
    											getAmount(), acct.getCmdtyID(), lcl );	}

    // ----------------------------

    @Override
    public String toString() {
		String result = "GCshBudgetPeriodImpl [";

		try {
			result += "period-index=" + getPeriodIndex();
		} catch (Exception e) {
			result += "period-index=" + "ERROR";
		}

		try {
			result += ", amount=" + getAmountFormatted();
		} catch (Exception e) {
			result += ", amount=" + "ERROR";
		}

		result += "]";

		return result;
    }

}
