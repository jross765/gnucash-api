package org.gnucash.api.write.impl.aux;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.Const;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.read.aux.GCshBudgetPeriod;
import org.gnucash.api.read.impl.aux.GCshBudgetAccountImpl;
import org.gnucash.api.read.impl.aux.GCshBudgetPeriodImpl;
import org.gnucash.api.write.GnuCashWritableBudget;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.aux.GCshWritableBudgetAccount;
import org.gnucash.api.write.aux.GCshWritableBudgetPeriod;
import org.gnucash.api.write.impl.GnuCashWritableBudgetImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GCshBudgetAccountImpl to allow read-write access instead of
 * read-only access.
 */
public class GCshWritableBudgetAccountImpl extends GCshBudgetAccountImpl 
                                           implements GCshWritableBudgetAccount 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritableBudgetAccountImpl.class);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GCshWritableBudgetAccountImpl(
    		final GnuCashWritableBudget parent,
    		final Slot jwsdpPeer,
    		final GnuCashWritableFile gcshFile) {
    	super(parent, jwsdpPeer, gcshFile);
    }

    public GCshWritableBudgetAccountImpl(final GCshBudgetAccountImpl bdgtAcct) {
    	super(bdgtAcct.getParent(), bdgtAcct.getJwsdpPeer(), bdgtAcct.getGnuCashFile());
    }

	public GCshWritableBudgetAccountImpl(final GnuCashWritableBudgetImpl bdgt, 
										 final GCshAcctID acctID) {
		super(bdgt,
			  createBudgetAccount_int(bdgt.getWritableGnuCashFile(), bdgt,
					  				  acctID), 
			  bdgt.getGnuCashFile());

		bdgt.addAccount(this);
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
	protected static Slot createBudgetAccount_int(
			final GnuCashWritableFileImpl file, 
			final GnuCashWritableBudgetImpl bdgt,
			final GCshAcctID acctID) {
		if ( file == null ) {
			throw new IllegalArgumentException("argument <file> is null");
		}

		if ( bdgt == null ) {
			throw new IllegalArgumentException("argument <bdgt> is null");
		}

		if ( acctID == null ) {
			throw new IllegalArgumentException("argument <acctID> is null");
		}

		if ( ! acctID.isSet() ) {
			throw new IllegalArgumentException("argument <acctID> is not set");
		}

		// This is needed because transaction.addAccount() later
		// must have an already built account.
		// Otherwise, it will create it from the JAXB-Data
		// thus 2 instances of this GnuCashWritableAccountImpl
		// will exist. One created in getAccounts() from this JAXB-Data
		// the other is this object.
		bdgt.getAccounts();

		ObjectFactory fact = file.getObjectFactory();

		Slot jwsdpBdgtAcct = fact.createSlot();
		jwsdpBdgtAcct.setSlotKey(acctID.toString());
		
		// NO, not here but in the calling method:
		// bdgt.addAccount(new GnuCashWritableBudgetAccountImpl(jwsdpBdgtAcct, bdgt.getGnuCashFile(), bdgt));
		file.setModified(true);
    
        LOGGER.debug("createBudgetAccount_int: Created new budget account (core): " + jwsdpBdgtAcct);
		
        return jwsdpBdgtAcct;
	}

    // ---------------------------------------------------------------

    @Override
    public void setAcctID(final GCshAcctID acctID) {
    	if ( acctID == null ) {
    		throw new IllegalArgumentException("argument <acctID> is null");
    	}
    	
    	if ( ! acctID.isSet() ) {
    		throw new IllegalArgumentException("argument <acctID> is not set ");
    	}
    	
    	jwsdpPeer.setSlotKey( acctID.toString() );
		getWritableGnuCashFile().setModified(true);
    }

    // ---------------------------------------------------------------

    @Override
    public void clearPeriods() {
		if ( getPeriods() == null )
			return;
		
		if ( getPeriods().size() == 0 )
			return;
		
		// ---
		
		if ( jwsdpPeer.getSlotValue().getType().equals(Const.XML_DATA_TYPE_FRAME) ) {
			List<Object> objList = jwsdpPeer.getSlotValue().getContent();
			if ( objList == null || objList.size() == 0 )
				return;
			for ( Object obj : objList ) {
				if ( obj instanceof Slot ) {
					obj = null;
				}
			}
		} else {
			LOGGER.error("clearPeriods: JWSDP Peer is not of type '" + Const.XML_DATA_TYPE_FRAME + "'");
			throw new IllegalStateException("Wrong slot type");
		}
		
		getWritableGnuCashFile().setModified(true);
	}

	@Override
	public void removePeriod(GCshBudgetPeriod bdgtPrd) {
		if ( bdgtPrd == null ) {
			throw new IllegalArgumentException("argument <bdgtPrd> is null");
		}
		
		if ( getPeriods() == null )
			return;
		
		if ( getPeriods().size() == 0 )
			return;
		
		// ---
		
		if ( jwsdpPeer.getSlotValue().getType().equals(Const.XML_DATA_TYPE_FRAME) ) {
			List<Object> objList = jwsdpPeer.getSlotValue().getContent();
			if ( objList == null || objList.size() == 0 )
				return;
			objList.remove( ((GCshBudgetPeriodImpl) bdgtPrd).getJwsdpPeer() );
			
			getWritableGnuCashFile().setModified(true);
		} else {
			LOGGER.error("removePeriod: JWSDP Peer is not of type '" + Const.XML_DATA_TYPE_FRAME + "'");
			throw new IllegalStateException("Wrong slot type");
		}
	}

	@Override
	public GCshWritableBudgetPeriod createWritablePeriod(BigInteger idx, BigFraction amt) {
		if ( idx == null ) {
			throw new IllegalArgumentException("argument <idx> is null");
		}
		
		if ( idx.compareTo( BigInteger.ZERO ) < 0 ) {
			throw new IllegalStateException("argument <idx> is < 0");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		GCshWritableBudgetPeriodImpl bdgtPrd = new GCshWritableBudgetPeriodImpl(this);
		bdgtPrd.setIndex(idx);
		bdgtPrd.setAmount(amt);
		addPeriod(bdgtPrd);
		
		return bdgtPrd;
	}

	@Override
	public List<GCshWritableBudgetPeriod> getWritablePeriods() {
		List<GCshWritableBudgetPeriod> result = new ArrayList<GCshWritableBudgetPeriod>();
		
		for ( GCshBudgetPeriod bdgtPrd : super.getPeriods() ) {
			GCshWritableBudgetPeriod newBdgtPrd = new GCshWritableBudgetPeriodImpl((GCshBudgetPeriodImpl) bdgtPrd);
		    result.add(newBdgtPrd);
		}

		return result;
	}

//    public void addPeriod(final GCshWritableBudgetPeriodImpl bdgtPrd) {
//		super.addPeriod(bdgtPrd);
//    	
//    	// ----
//    	// ::TODO: MOVE FOLLOWING CODE
//		if ( jwsdpPeer.getSlotValue().getType().equals(Const.XML_DATA_TYPE_FRAME) ) {
//			List<Object> objList = jwsdpPeer.getSlotValue().getContent();
//			if ( objList == null || objList.size() == 0 )
//				return;
//			for ( Object obj : objList ) {
//				if ( obj instanceof Slot ) {
//					obj = null;
//				}
//			}
//		} else {
//			LOGGER.error("clearPeriods: JWSDP Peer is not of type '" + Const.XML_DATA_TYPE_FRAME + "'");
//			throw new IllegalStateException("Wrong slot type");
//		}
//		
//		ObjectFactory fact = new ObjectFactory();
//		SlotValue newValue = fact.createSlotValue();
//		newValue.setType(Const.XML_DATA_TYPE_NUMERIC);
//		newValue.getContent().add( bdgtPrd.getAmount().toString().replaceAll(" ", "") );
//		jwsdpPeer.setSlotValue(newValue);
//		LOGGER.debug("addPeriod: adding new slot for budget period " + bdgtPrd.toString());
//		
//		getWritableGnuCashFile().setModified(true);
//    }
//
	public void addPeriod(final GCshWritableBudgetPeriodImpl bdgtPrd) {
		super.addPeriod(bdgtPrd);		
	}

    // ---------------------------------------------------------------

	@Override
	public String toString() {
		String result = "GCshWritableBudgetAccountImpl [";

		try {
			result += "account-id=" + getAcctID();
		} catch (Exception e) {
			result += "account-id=" + "ERROR";
		}

		try {
			result += ", nof-periods=" + getPeriods().size();
		} catch (Exception e) {
			result += ", nof-periods=" + getAcctID();
		}

		result += "]";

		return result;
	}

}
