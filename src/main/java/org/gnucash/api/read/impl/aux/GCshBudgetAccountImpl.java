package org.gnucash.api.read.impl.aux;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshBudgetAccount;
import org.gnucash.api.read.aux.GCshBudgetPeriod;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshBudgetAccountImpl extends GnuCashObjectImpl 
							       implements GCshBudgetAccount 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshBudgetAccountImpl.class);

    // -----------------------------------------------------------

    protected final Slot jwsdpPeer;

    // -----------------------------------------------------------
    
    protected GnuCashBudget parent = null;

    // -----------------------------------------------------------

    /**
     * @param parent 
     * @param newPeer the JWSDP-object we are wrapping.
     * @param gcshFile 
     */
    @SuppressWarnings("exports")
    public GCshBudgetAccountImpl(
    		final GnuCashBudget parent, 
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
	public GnuCashBudget getParent() {
		return parent;
	}

    // -----------------------------------------------------------------

	@Override
	public GCshAcctID getAcctID() {
		return new GCshAcctID(jwsdpPeer.getSlotKey());
	}

	@Override
	public List<GCshBudgetPeriod> getPeriods() {
		List<GCshBudgetPeriod> result = new ArrayList<GCshBudgetPeriod>();

		// ::TODO: The following code is partially redundant to 
		// HasUserDefinedAttributesImpl.getUserDefinedAttributeCore():
		if ( jwsdpPeer.getSlotValue().getType().equals(Const.XML_DATA_TYPE_FRAME) ) {
			List<Object> objList = jwsdpPeer.getSlotValue().getContent();
			if ( objList == null || objList.size() == 0 )
				return null;
			for ( Object obj : objList ) {
				if ( obj instanceof Slot ) {
					Slot subSlot = (Slot) obj;
					GCshBudgetPeriodImpl newBdgtPrd = new GCshBudgetPeriodImpl(this, subSlot, getGnuCashFile());
					result.add(newBdgtPrd);
				}
			}
		} else {
			LOGGER.error("getPeriods: JWSDP Peer is not of type '" + Const.XML_DATA_TYPE_FRAME + "'");
			throw new IllegalStateException("Wrong slot type");
		}

		try {
			LOGGER.debug("getPeriods: Found " + result.size() + " periods for account " + getAcctID());
		} catch (Exception e) {
			LOGGER.debug("getPeriods: Found " + result.size() + " periods for account " + "ERROR");
		}

		return result;
	}

    // -----------------------------------------------------------------

    @Override
    public String toString() {
		String result = "GCshBudgetAccountImpl [";

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
