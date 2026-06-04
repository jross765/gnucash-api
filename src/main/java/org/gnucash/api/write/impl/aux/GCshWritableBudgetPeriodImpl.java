package org.gnucash.api.write.impl.aux;

import java.math.BigInteger;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.Const;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.read.impl.aux.GCshBudgetPeriodImpl;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.aux.GCshWritableBudgetAccount;
import org.gnucash.api.write.aux.GCshWritableBudgetPeriod;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GCshBudgetPeriodImpl to allow read-write access instead of
 * read-only access.
 */
public class GCshWritableBudgetPeriodImpl extends GCshBudgetPeriodImpl 
                                          implements GCshWritableBudgetPeriod 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritableBudgetPeriodImpl.class);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GCshWritableBudgetPeriodImpl(
    		final GCshWritableBudgetAccount parent,
    		final Slot jwsdpPeer,
    		final GnuCashWritableFile gcshFile) {
    	super(parent, jwsdpPeer, gcshFile);
    }

    public GCshWritableBudgetPeriodImpl(final GCshBudgetPeriodImpl bdgtPrd) {
    	super(bdgtPrd.getParent(), bdgtPrd.getJwsdpPeer(), bdgtPrd.getGnuCashFile());
    }

	public GCshWritableBudgetPeriodImpl(final GCshWritableBudgetAccountImpl bdgtAcct) {
		super(bdgtAcct,
			  createBudgetPeriod_int(bdgtAcct.getWritableGnuCashFile(), bdgtAcct), 
			  bdgtAcct.getGnuCashFile());

		bdgtAcct.addPeriod(this);
    }

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
    @Override
    public GnuCashWritableFileImpl getWritableGnuCashFile() {
    	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

//	/**
//	 * {@inheritDoc}
//	 */
//    @Override
//    public GnuCashWritableFileImpl getGnuCashFile() {
//    	return (GnuCashWritableFileImpl) super.getGnuCashFile();
//    }

	// ---------------------------------------------------------------
	
	/**
	 * Creates a new budget account and adds it to the given GnuCash file.
	 */
	protected static Slot createBudgetPeriod_int(
			final GnuCashWritableFileImpl file, 
			final GCshWritableBudgetAccountImpl bdgtAcct) {
		if ( file == null ) {
			throw new IllegalArgumentException("argument <file> is null");
		}

		if ( bdgtAcct == null ) {
			throw new IllegalArgumentException("argument <bdgtAcct> is null");
		}
		
		// This is needed because bdgtAcct.addPeriod() later
		// must have an already built period.
		// Otherwise, it will create it from the JAXB-Data
		// thus 2 instances of this GnuCashWritableAddressImpl
		// will exist. One created in getAccounts() from this JAXB-Data
		// the other is this object.
		bdgtAcct.getPeriods();

		ObjectFactory fact = file.getObjectFactory();

		Slot slt = fact.createSlot();
		slt.setSlotKey(Const.SLOT_KEY_ACCT_NOTES);
		SlotValue sltVal = fact.createSlotValue();
		sltVal.setType(Const.XML_DATA_TYPE_NUMERIC);
		sltVal.getContent().add("1/100"); // sic
		slt.setSlotValue(sltVal);

		if ( bdgtAcct.getJwsdpPeer().getSlotValue() != null ) {
			bdgtAcct.getJwsdpPeer().getSlotValue().getContent().add(slt);
		} else {
			Slot prntSlot = fact.createSlot();
			prntSlot.setSlotKey(bdgtAcct.getAcctID().toString());
			SlotValue prntSltVal = fact.createSlotValue();
			prntSltVal.setType(Const.XML_DATA_TYPE_FRAME);
			prntSlot.setSlotValue(prntSltVal);

			bdgtAcct.getJwsdpPeer().setSlotValue(prntSltVal);	
		}
		
		// NO, not here but in the calling method:
		// trx.addAccount(new GnuCashWritableBudgetAccountImpl(jwsdpBdgtAcct, bdgt.getGnuCashFile(), bdgt));
		file.setModified(true);
    
        LOGGER.debug("createBudgetPeriod_int: Created new budget period (core): " + slt);
		
        return slt;
	}

    // ---------------------------------------------------------------

	@Override
	public void setIndex(final BigInteger idx) {
		if ( idx == null ) {
			throw new IllegalArgumentException("argument <idx> is null");
		}
		
		if ( idx.compareTo(BigInteger.ZERO) < 0 ) {
			throw new IllegalArgumentException("argument <idx> is < 0");
		}
		
		// ::TODO
//		BigInteger oldIdx = getIndex();
		
		jwsdpPeer.setSlotKey(idx.toString());

		getWritableGnuCashFile().setModified(true);

		// ::TODO
//		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
//    	if ( propertyChangeSupport != null) {
//    		propertyChangeSupport.firePropertyChange("index", oldIdx, idx);
//    	}
	}

	@Override
	public void setAmount(final BigFraction amt) {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ::TODO
		// BigFraction oldAmt = getAmount();
		
		// ::TODO
//		if ( jwsdpPeer.getSlotValue() != null ) {
//			jwsdpPeer.getSlotValue().getXYZ.set( amt.toString().replaceAll(" ", "") );
//		} else {
			ObjectFactory fact = getWritableGnuCashFile().getObjectFactory();
			SlotValue sltVal = fact.createSlotValue();
			sltVal.setType(Const.XML_DATA_TYPE_NUMERIC);
			sltVal.getContent().add(amt.toString().replaceAll(" ", ""));
			jwsdpPeer.setSlotValue(sltVal);
//		}

		getWritableGnuCashFile().setModified(true);

		// ::TODO
//		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
//    	if ( propertyChangeSupport != null) {
//    		propertyChangeSupport.firePropertyChange("amount", oldAmt, amt);
//    	}
	}

    // ---------------------------------------------------------------

    @Override
    public String toString() {
		String result = "GCshWritableBudgetPeriodImpl [";

		try {
			result += "index=" + getIndex();
		} catch (Exception e) {
			result += "index=" + "ERROR";
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
