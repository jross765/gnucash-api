package org.gnucash.api.read.impl;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncCommodity;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnuCashCommodityImpl extends GnuCashObjectImpl 
								  implements GnuCashCommodity 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashCommodityImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncCommodity jwsdpPeer;

    // ---------------------------------------------------------------

    /**
     * @param peer    the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     */
    @SuppressWarnings("exports")
    public GnuCashCommodityImpl(final GncCommodity peer, final GnuCashFile gcshFile) {
    	super(gcshFile);
    	
    	this.jwsdpPeer = peer;
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncCommodity getJwsdpPeer() {
    	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    protected String getNameSpace() {
    	if ( jwsdpPeer.getCmdtySpace() == null ) {
			return null;
		}
	
    	return jwsdpPeer.getCmdtySpace();
    }

    private String getID() {
    	return jwsdpPeer.getCmdtyId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GCshCmdtyCurrID getQualifID() {
    	if ( getNameSpace() == null ||
    		 getID() == null ) {
			return null;
		}
	
    	return new GCshCmdtyCurrID(getNameSpace(), getID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
    	if ( jwsdpPeer.getCmdtyName() == null ) {
			return ""; // sic, important for compareToByName()
		}
	
    	return jwsdpPeer.getCmdtyName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSymbol() {
    	return getUserDefinedAttribute(Const.SLOT_KEY_CMDTY_USER_SYMBOL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXCode() {
    	return jwsdpPeer.getCmdtyXcode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getFraction() {
    	return jwsdpPeer.getCmdtyFraction();
    }

    // ---------------------------------------------------------------

	@Override
	public List<GnuCashAccount> getStockAccounts() {
		List<GnuCashAccount> result = new ArrayList<GnuCashAccount>();
		
		for ( GnuCashAccount acct : getGnuCashFile().getAccountsByType(GnuCashAccount.Type.STOCK) ) {
			GCshCmdtyCurrID cmdtyCurrID = acct.getCmdtyCurrID();
			if ( this.getQualifID().equals(cmdtyCurrID) ) {
				result.add(acct);
			}
		}
		
		return result;
	}

    // -----------------------------------------------------------------

    @Override
    public List<GnuCashPrice> getQuotes() {
    	return getGnuCashFile().getPricesByCmdtyCurrID(getQualifID());
    }

    @Override
    public GnuCashPrice getYoungestQuote() {
    	List<GnuCashPrice> qutList = getQuotes();
    	if ( qutList.size() == 0 ) {
			return null;
		}
    	
    	return qutList.get(0);
    }

    // -----------------------------------------------------------------

    @Override
    public List<GnuCashTransactionSplit> getTransactionSplits() {
    	return getGnuCashFile().getTransactionSplitsByCmdtyCurrID(getQualifID());
    }

    // -----------------------------------------------------------------

	@Override
	public String getUserDefinedAttribute(String name) {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeCore(jwsdpPeer.getCmdtySlots(), name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeKeysCore(jwsdpPeer.getCmdtySlots());
	}

    // -----------------------------------------------------------------

	@Override
	public int compareTo(final GnuCashCommodity otherCmdty) {
		int i = compareToByName(otherCmdty);
		if ( i != 0 ) {
			return i;
		}

		i = compareToByQualifID(otherCmdty);
		if ( i != 0 ) {
			return i;
		}

		return ("" + hashCode()).compareTo("" + otherCmdty.hashCode());
	}
	
//	private int compareToByID(final GnuCashCommodity otherCmdty) {
//		return getID().toString().compareTo(otherCmdty.getID().toString());
//	}

	private int compareToByQualifID(final GnuCashCommodity otherCmdty) {
		return getQualifID().toString().compareTo(otherCmdty.getQualifID().toString());
	}

	private int compareToByName(final GnuCashCommodity otherCmdty) {
		return getName().compareTo(otherCmdty.getName());
	}

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	
    	String result = "GnuCashCommodityImpl [";

    	try {
    		result += "qualif-id='" + getQualifID().toString() + "'";
    	} catch (InvalidCmdtyCurrTypeException e) {
    		result += "qualif-id=" + "ERROR";
    	}
	
    	result += ", namespace='" + getNameSpace() + "'"; 
    	result += ", name='" + getName() + "'"; 
    	result += ", x-code='" + getXCode() + "'"; 
    	result += ", fraction=" + getFraction() + "]";
	
    	return result;
    }

}
