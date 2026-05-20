package org.gnucash.api.read.impl;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.generated.GncBudget;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshBudgetAccount;
import org.gnucash.api.read.aux.GCshBudgetPeriod;
import org.gnucash.api.read.aux.GCshBudgetRecurrence;
import org.gnucash.api.read.impl.aux.GCshBudgetAccountImpl;
import org.gnucash.api.read.impl.aux.GCshBudgetRecurrenceImpl;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshBdgtID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnuCashBudgetImpl extends GnuCashObjectImpl 
							   implements GnuCashBudget 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashBudgetImpl.class);

    // -----------------------------------------------------------

    // The JWSDP-object we are wrapping.
    protected final GncBudget jwsdpPeer;

    // -----------------------------------------------------------

    /**
     * @param newPeer the JWSDP-object we are wrapping.
     * @param gcshFile 
     */
    @SuppressWarnings("exports")
    public GnuCashBudgetImpl(final GncBudget newPeer, final GnuCashFile gcshFile) {
    	super(gcshFile);
		
    	this.jwsdpPeer = newPeer;
    }

	// ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncBudget getJwsdpPeer() {
    	return jwsdpPeer;
    }

    // -----------------------------------------------------------
    
    @Override
    public GCshBdgtID getID() {
    	if ( jwsdpPeer.getBgtId().getValue() == null ) {
    		throw new IllegalStateException("id of JWSDP peer is null");
    	}
    	
    	return new GCshBdgtID(jwsdpPeer.getBgtId().getValue());
    }

    // -----------------------------------------------------------
    
	@Override
	public String getName() {
    	if ( jwsdpPeer.getBgtName() == null ) {
    		return null;
    	}

    	return jwsdpPeer.getBgtName();
	}

	@Override
	public String getDescription() {
		if ( jwsdpPeer.getBgtDescription() == null ) {
			return null;
		}
		
		return jwsdpPeer.getBgtDescription().getValue().toString();
	}

	@Override
	public int getNofPeriods() {
		return jwsdpPeer.getBgtNumPeriods();
	}

	@Override
	public GCshBudgetRecurrence getRecurrence() {
		if ( jwsdpPeer.getBgtRecurrence() == null ) {
			return null;
		}
		
		return new GCshBudgetRecurrenceImpl(this, jwsdpPeer.getBgtRecurrence(), getGnuCashFile());
	}

    // -----------------------------------------------------------
    
    @Override
    public List<GCshBudgetAccount> getAccounts() {
		List<GCshBudgetAccount> result = new ArrayList<GCshBudgetAccount>();

		for ( Slot bdgtSlot : jwsdpPeer.getBgtSlots().getSlot() ) {
			GCshBudgetAccount newBdgtAcct = new GCshBudgetAccountImpl(this, bdgtSlot, getGnuCashFile());
			result.add(newBdgtAcct);
		}

		try {
			LOGGER.debug("getEntries: Found " + result.size() + " accounts for budget " + getID());
		} catch (Exception e) {
			LOGGER.debug("getEntries: Found " + result.size() + " accounts for budget " + "ERROR");
		}

		return result;
    }

    @Override
    public List<GCshBudgetPeriod> getPeriods(final GCshAcctID acctID) {
		// List<GCshBudgetPeriod> result = new ArrayList<GCshBudgetPeriod>();

		for ( GCshBudgetAccount acct : getAccounts() ) {
			if ( acct.getAcctID().equals( acctID ) ) {
				return acct.getPeriods();
			}
		}
		
		return null;
    }

    // -----------------------------------------------------------------
	// ::CHECK
    
	@Override
	public String getUserDefinedAttribute(String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}

		if ( name.isBlank() ) {
			throw new IllegalArgumentException("argument <name> is blank");
		}

		if ( jwsdpPeer.getBgtSlots() == null ) {
			return null;
		}
		
		SlotsType slots = jwsdpPeer.getBgtSlots();
		return HasUserDefinedAttributesImpl.getUserDefinedAttributeCore(slots, name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		if ( jwsdpPeer.getBgtSlots() == null ) {
			return null;
		}
		
		SlotsType slots = jwsdpPeer.getBgtSlots();
		return HasUserDefinedAttributesImpl.getUserDefinedAttributeKeysCore(slots);
	}

    // -----------------------------------------------------------------
    
    @Override
    public String toString() {
		String result = "GCshBudgetImpl [";

		result += "id=" + getID() + ", ";
		result += "name='" + getName() + "', ";
		result += "descr='" + getDescription() + "', ";
		result += "recurr=" + getRecurrence() + ", ";
		
		result += "nof-periods=" + getNofPeriods() + ", ";
		result += "nof-accounts=" + getAccounts().size();
		
		result += "]";

		return result;
    }

}
