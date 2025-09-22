package org.gnucash.api.write.impl;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncCommodity;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.GnuCashCommodityImpl;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.GnuCashWritableCommodity;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.ObjectCascadeException;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnuCashCommodityImpl to allow read-write access instead of
 * read-only access.
 */
public class GnuCashWritableCommodityImpl extends GnuCashCommodityImpl 
                                          implements GnuCashWritableCommodity
{

    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableCommodityImpl.class);
    
    // ---------------------------------------------------------------

    /**
     * Our helper to implement the GnuCashWritableObject-interface.
     */
    private final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

    // ---------------------------------------------------------------

    /**
     * Please use ${@link GnuCashWritableFile#createWritableCommodity(GCshCmdtyID, String, String)
     *
     * @param file      the file we belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GnuCashWritableCommodityImpl(final GncCommodity jwsdpPeer,
	    final GnuCashWritableFileImpl file) {
    	super(jwsdpPeer, file);
    }

    /**
     * Please use ${@link GnuCashWritableFile#createWritableCommodity()}.
     *
     * @param file the file we belong to
     * @param id   the ID we shall have
     */
    protected GnuCashWritableCommodityImpl(
    		final GnuCashWritableFileImpl file,
    		final GCshCmdtyID cmdtyID) {
    	super(createCommodity_int(file, cmdtyID), file);
    }

    public GnuCashWritableCommodityImpl(GnuCashCommodityImpl cmdty) {
    	super(cmdty.getJwsdpPeer(), cmdty.getGnuCashFile());
    }

    // ---------------------------------------------------------------

    /**
     * Delete this commodity and remove it from the file.
     * @throws ObjectCascadeException 
     *
     * @see GnuCashWritableCommodity#remove()
     */
    public void remove() throws ObjectCascadeException {
		GncCommodity peer = getJwsdpPeer();
		(getGnuCashFile()).getRootElement().getGncBook().getBookElements().remove(peer);
		(getGnuCashFile()).removeCommodity(this);
    }

    // ---------------------------------------------------------------

    /**
     * Creates a new Transaction and add's it to the given GnuCash file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param guid the ID we shall have
     * @return a new jwsdp-peer already entered into th jwsdp-peer of the file
     */
    protected static GncCommodity createCommodity_int(
    		final GnuCashWritableFileImpl file,
    		final GCshCmdtyID cmdtyID) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( !cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		GncCommodity jwsdpCmdty = file.createGncGncCommodityType();

		jwsdpCmdty.setCmdtyFraction(Const.CMDTY_FRACTION_DEFAULT);
		jwsdpCmdty.setVersion(Const.XML_FORMAT_VERSION);
		jwsdpCmdty.setCmdtyName("no name given");
		jwsdpCmdty.setCmdtySpace(cmdtyID.getNameSpace());
		jwsdpCmdty.setCmdtyId(cmdtyID.getCode());
		jwsdpCmdty.setCmdtyXcode(Const.CMDTY_XCODE_DEFAULT);

		file.getRootElement().getGncBook().getBookElements().add(jwsdpCmdty);
		file.setModified(true);

		LOGGER.debug("createCommodity_int: Created new commodity (core): " + jwsdpCmdty.getCmdtySpace() + ":"
				+ jwsdpCmdty.getCmdtyId());

		return jwsdpCmdty;
    }

    // ---------------------------------------------------------------

	@Override
	public List<GnuCashWritableAccount> getWritableStockAccounts() {
		List<GnuCashWritableAccount> result = new ArrayList<GnuCashWritableAccount>();
		
		for ( GnuCashAccount acct : getStockAccounts() ) {
			GnuCashWritableAccountImpl newAcct = new GnuCashWritableAccountImpl((GnuCashAccountImpl) acct, true);
			result.add(newAcct);
		}
		
		return result;
	}

    // ---------------------------------------------------------------

    @Override
    public void setQualifID(GCshCmdtyCurrID qualifId) {
		if ( qualifId == null ) {
			throw new IllegalArgumentException("argument <qualifID> is null");
		}

		getJwsdpPeer().setCmdtySpace(qualifId.getNameSpace());
		getJwsdpPeer().setCmdtyId(qualifId.getCode());

		getGnuCashFile().setModified(true);
    }

    @Override
    public void setSymbol(String symb) {
		if ( symb == null ) {
			throw new IllegalArgumentException("argument <symb> is null");
		}

		if ( symb.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <symb> is empty");
		}

		try {
			setUserDefinedAttribute(Const.SLOT_KEY_CMDTY_USER_SYMBOL, symb);
		} catch (SlotListDoesNotContainKeyException exc) {
			addUserDefinedAttribute(Const.XML_DATA_TYPE_STRING, Const.SLOT_KEY_CMDTY_USER_SYMBOL, symb);
		}
    }

    @Override
    public void setXCode(String xCode) {
		if ( xCode == null ) {
			throw new IllegalArgumentException("argument <xCode> is null");
		}

		if ( xCode.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <xCode> is empty");
		}

		getJwsdpPeer().setCmdtyXcode(xCode);
		getGnuCashFile().setModified(true);
    }

    @Override
    public void setName(String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}

		if ( name.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}

		getJwsdpPeer().setCmdtyName(name);
		getGnuCashFile().setModified(true);
    }

    @Override
    public void setFraction(Integer fract) {
		if ( fract <= 0 ) {
			throw new IllegalArgumentException("Fraction is <= 0");
		}

		getJwsdpPeer().setCmdtyFraction(fract);
		getGnuCashFile().setModified(true);
    }

    // ---------------------------------------------------------------

    /**
     * The GnuCash file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    public GnuCashWritableFileImpl getWritableGnuCashFile() {
    	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

    /**
     * The GnuCash file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnuCashWritableFileImpl getGnuCashFile() {
    	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

    // ---------------------------------------------------------------

    @Override
    public void addUserDefinedAttribute(final String type, final String name, final String value) {
		if ( jwsdpPeer.getCmdtySlots() == null ) {
			ObjectFactory fact = getGnuCashFile().getObjectFactory();
			SlotsType newSlotsType = fact.createSlotsType();
			jwsdpPeer.setCmdtySlots(newSlotsType);
		}
		
    	HasWritableUserDefinedAttributesImpl
    		.addUserDefinedAttributeCore(jwsdpPeer.getCmdtySlots(), 
    									 getGnuCashFile(), 
    									 type, name, value);
    }

    @Override
    public void removeUserDefinedAttribute(final String name) {
		if ( jwsdpPeer.getCmdtySlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
    	HasWritableUserDefinedAttributesImpl
    		.removeUserDefinedAttributeCore(jwsdpPeer.getCmdtySlots(), 
    										getGnuCashFile(),
    										name);
    }

    @Override
    public void setUserDefinedAttribute(final String name, final String value) {
		if ( jwsdpPeer.getCmdtySlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
    	HasWritableUserDefinedAttributesImpl
    		.setUserDefinedAttributeCore(jwsdpPeer.getCmdtySlots(), 
    								 	 getGnuCashFile(), 
    								 	 name, value);
    }

    public void clean() {
    	HasWritableUserDefinedAttributesImpl.cleanSlots(jwsdpPeer.getCmdtySlots());
    }

    // -----------------------------------------------------------------

    @Override
    public String toString() {
		String result = "GnuCashWritableCommodityImpl [";

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
