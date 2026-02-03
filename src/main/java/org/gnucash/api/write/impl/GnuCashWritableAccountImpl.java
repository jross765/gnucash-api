package org.gnucash.api.write.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.Const;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.aux.GCshAcctLot;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.aux.GCshAcctLotImpl;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.aux.GCshWritableAccountLot;
import org.gnucash.api.write.impl.aux.GCshWritableAccountLotImpl;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.InvalidCmdtyTypeException;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.UnknownAccountTypeException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Extension of GnuCashAccountImpl to allow read-write access instead of
 * read-only access.
 */
public class GnuCashWritableAccountImpl extends GnuCashAccountImpl 
                                        implements GnuCashWritableAccount 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableAccountImpl.class);

	// ---------------------------------------------------------------

	// Our helper to implement the GnuCashWritableObject-interface.
	private final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

	// Used by ${@link #getBalance()} to cache the result.
	private FixedPointNumber myBalanceCached    = null;
	private BigFraction      myBalanceCachedRat = null;

	// Used by ${@link #getBalance()} to cache the result.
	private PropertyChangeListener myBalanceCachedInvalidator = null;

	// ---------------------------------------------------------------

	/**
	 * @param jwsdpPeer
	 * @param file
	 * 
	 * @see GnuCashAccountImpl#GnuCashAccountImpl(GncAccount, GnuCashFile)
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableAccountImpl(final GncAccount jwsdpPeer, final GnuCashFileImpl file) {
		super(constr_checkPeer(jwsdpPeer), constr_checkROFile(file));
	}

	/**
	 * @param file
	 * 
	 * @see GnuCashAccountImpl#GnuCashAccountImpl(GncAccount, GnuCashFile) )
	 */
	public GnuCashWritableAccountImpl(final GnuCashWritableFileImpl file) {
		super(createAccount_int(file, new GCshAcctID(GCshID.getNew())), constr_checkRWFile(file));
	}

	public GnuCashWritableAccountImpl(final GnuCashAccountImpl acct, final boolean addSplits) {
		super(constr_getPeer(acct), constr_getGCshFile(acct));

    	if ( addSplits ) {
			if ( ! acct.isRootAccount() ) {
				for ( GnuCashTransactionSplit splt : ((GnuCashFileImpl) acct.getGnuCashFile()).getTransactionSplits_readAfresh() ) {
					if ( splt.getAccountID().equals(acct.getID()) ) {
						super.addTransactionSplit(splt);
						// NO:
//				    addTransactionSplit(new GnuCashTransactionSplitImpl(splt.getJwsdpPeer(), splt.getTransaction(), 
//		                            false, false));
					}
				} // for splt
			} // if acct
		}
	}

	// ----------------------------
	// Extra check methods for constructor, as we may do nothing before
	// calling super().
	// Cf.: https://stackoverflow.com/questions/79135581/in-java-avoiding-null-dereferences-when-calling-super-in-constructor
	
	private static GncAccount constr_checkPeer(final GncAccount jwsdpPeer) {
		if ( jwsdpPeer == null ) {
			throw new IllegalArgumentException("argument <jwsdpPeer> is null");
		}
		
		return jwsdpPeer;
	}

	private static GnuCashFileImpl constr_checkROFile(final GnuCashFileImpl file) {
		if ( file == null ) {
			throw new IllegalArgumentException("argument <file> is null");
		}
		
		return file;
	}

	private static GnuCashWritableFileImpl constr_checkRWFile(final GnuCashWritableFileImpl file) {
		if ( file == null ) {
			throw new IllegalArgumentException("argument <file> is null");
		}
		
		return file;
	}

	private static GncAccount constr_getPeer(final GnuCashAccountImpl acct) {
		if ( acct == null ) {
			throw new IllegalArgumentException("argument <acct> is null");
		}
		
		return acct.getJwsdpPeer();
	}

	private static GnuCashFile constr_getGCshFile(final GnuCashAccountImpl acct) {
		if ( acct == null ) {
			throw new IllegalArgumentException("argument <acct> is null");
		}
		
		return acct.getGnuCashFile();
	}

	// ---------------------------------------------------------------

	/**
	 * @param file
	 * @return
	 */
	private static GncAccount createAccount_int(
			final GnuCashWritableFileImpl file,
			final GCshAcctID newID) {
		if ( newID == null ) {
			throw new IllegalArgumentException("argument <mewID> is null");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("argument <newID> is not set");
		}

		ObjectFactory factory = file.getObjectFactory();

		GncAccount jwsdpAcct = file.createGncAccountType();
		// left unset account.setActCode();
		jwsdpAcct.setActCommodityScu(100); // x,yz
		jwsdpAcct.setActDescription("no description yet");
		// left unset account.setActLots();
		jwsdpAcct.setActName("UNNAMED");
		// left unset account.setActNonStandardScu();
		// left unset account.setActParent())
		jwsdpAcct.setActType(GnuCashAccount.Type.BANK.toString());

		jwsdpAcct.setVersion(Const.XML_FORMAT_VERSION);

		{
			GncAccount.ActCommodity currency = factory.createGncAccountActCommodity();
			currency.setCmdtyId(file.getDefaultCurrencyID());
			currency.setCmdtySpace(GCshCmdtyNameSpace.CURRENCY);
			jwsdpAcct.setActCommodity(currency);
		}

		{
			GncAccount.ActId guid = factory.createGncAccountActId();
			guid.setType(Const.XML_DATA_TYPE_GUID);
			guid.setValue(newID.toString());
			jwsdpAcct.setActId(guid);
		}

		{
			SlotsType slots = factory.createSlotsType();
			jwsdpAcct.setActSlots(slots);
		}

		{
			Slot slot = factory.createSlot();
			slot.setSlotKey(Const.SLOT_KEY_ACCT_PLACEHOLDER);
			SlotValue slottype = factory.createSlotValue();
			slottype.setType(Const.XML_DATA_TYPE_STRING);
			slottype.getContent().add("false");
			slot.setSlotValue(slottype);
			jwsdpAcct.getActSlots().getSlot().add(slot);
		}

		{
			Slot slot = factory.createSlot();
			slot.setSlotKey(Const.SLOT_KEY_ACCT_NOTES);
			SlotValue slottype = factory.createSlotValue();
			slottype.setType(Const.XML_DATA_TYPE_STRING);
			slottype.getContent().add("");
			slot.setSlotValue(slottype);
			jwsdpAcct.getActSlots().getSlot().add(slot);
		}

		file.getRootElement().getGncBook().getBookElements().add(jwsdpAcct);
		file.setModified(true);

		LOGGER.debug("createAccount_int: Created new account (core): " + jwsdpAcct.getActId().getValue());

		return jwsdpAcct;
	}

	// ---------------------------------------------------------------

	/**
	 * Remove this account from the system.<br/>
	 * Throws IllegalStateException if this account has splits or children.
	 */
	@Override
	public void remove() {
		if ( hasTransactions() ) {
			throw new IllegalStateException("Cannot remove account while it contains transaction-splits");
		}

		if ( hasLots() ) {
			throw new IllegalStateException("Cannot remove account while it contains lots");
		}

		if ( this.getChildren().size() > 0 ) {
			throw new IllegalStateException("Cannot remove account while it contains child-accounts");
		}

		getWritableGnuCashFile().removeAccount(this);
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GnuCashWritableFileImpl getWritableGnuCashFile() {
		return (GnuCashWritableFileImpl) super.getGnuCashFile();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GnuCashWritableFileImpl getGnuCashFile() {
		return (GnuCashWritableFileImpl) super.getGnuCashFile();
	}

	// ---------------------------------------------------------------

	/**
	 * 
	 */
	@Override
	public GnuCashWritableTransactionSplit getWritableTransactionSplitByID(final GCshSpltID spltID) {
		return (GnuCashWritableTransactionSplit) super.getTransactionSplitByID(spltID);
	}

	/**
	 * 
	 */
	@Override
	public List<GnuCashWritableTransactionSplit> getWritableTransactionSplits() {
		List<GnuCashWritableTransactionSplit> result = new ArrayList<GnuCashWritableTransactionSplit>();

		for ( GnuCashTransactionSplit splt : super.getTransactionSplits() ) {
			GnuCashWritableTransactionSplitImpl newSplt = new GnuCashWritableTransactionSplitImpl(splt);
			result.add(newSplt);
		}

		return result;
	}

	/**
	 * @param impl the split to add to mySplits
	 */
	protected void addTransactionSplit(final GnuCashWritableTransactionSplitImpl impl) {
		super.addTransactionSplit(impl);
		// ((GnuCashFileImpl) getGnuCashFile()).getAccountManager().addTransactionSplit(impl, false);
	}

	/**
	 * @see GnuCashAccount#addTransactionSplit(GnuCashTransactionSplit)
	 */
	@Override
	public void addTransactionSplit(final GnuCashTransactionSplit splt) {
		if ( splt == null ) {
			throw new IllegalArgumentException("argument <splt> is null");
		}

		if ( ! splt.getAccountID().equals(getID()) ) {
			throw new IllegalArgumentException("split " + splt.getID() + " does not belong to account " + getID());
		}

		if ( getGnuCashFile().getTopAccountIDs().contains(getID()) ||
			 getGnuCashFile().getRootAccountID().equals(getID()) ) {
			LOGGER.error("Adding transaction split is forbidden for root and top-level accounts");
			throw new UnsupportedOperationException("Adding transaction split is forbidden for root and top-level accounts");
		}

		super.addTransactionSplit(splt);

		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("transactionSplits", null, getTransactionSplits());
		}
	}

	/**
	 * @param splt the split to remove
	 */
	protected void removeTransactionSplit(final GnuCashWritableTransactionSplit splt) {
		if ( splt == null ) {
			throw new IllegalArgumentException("argument <splt> is null");
		}

		if ( getGnuCashFile().getTopAccountIDs().contains(getID()) ||
			 getGnuCashFile().getRootAccountID().equals(getID()) ) {
			LOGGER.error("Removing transaction split is forbidden for root and top-level accounts");
			throw new UnsupportedOperationException("Removing transaction split is forbidden for root and top-level accounts");
		}

		List<GnuCashTransactionSplit> transactionSplits = getTransactionSplits();
		// That does not work with writable splits:
		// transactionSplits.remove(splt);
		// Instead:
		for ( int i = 0; i < transactionSplits.size(); i++ ) {
			if ( transactionSplits.get(i).getID().equals(splt.getID()) ) {
				transactionSplits.remove(i);
				i--;
			}
		}

		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("transactionSplits", null, transactionSplits);
		}
	}

	// ---------------------------------------------------------------

	/**
	 * @see {@link #getLotByID(GCshLotID)}
	 */
	@Override
	public GCshWritableAccountLot getWritableLotByID(final GCshLotID lotID) {
		return (GCshWritableAccountLot) super.getLotByID(lotID);
	}

	/**
	 * @see #getLots()
	 */
	@Override
	public List<GCshWritableAccountLot> getWritableLots() {
		List<GCshWritableAccountLot> result = new ArrayList<GCshWritableAccountLot>();

		if ( getLots() != null ) { // important / ::TODO
			for ( GCshAcctLot lot : super.getLots() ) {
				GCshWritableAccountLot newLot = new GCshWritableAccountLotImpl((GCshAcctLotImpl) lot);
				result.add(newLot);
			}
		}

		return result;
	}

	/**
	 * @param impl the split to add to mySplits
	 */
	protected void addLot(final GCshWritableAccountLotImpl impl) {
		super.addLot(impl);
		// ((GnuCashFileImpl) getGnuCashFile()).getAccountManager().addAccountLot(impl, false);
	}

	/**
	 * Create a new lot for an account found in the jaxb-data.
	 *
	 * @param splt the jaxb-data
	 * @return the new split-instance
	 */
	@Override
	protected GCshAcctLotImpl createLot(final GncAccount.ActLots.GncLot lot) {
		GCshWritableAccountLotImpl gcshAcctLot = new GCshWritableAccountLotImpl(lot, this);
		if ( helper.getPropertyChangeSupport() != null ) {
			helper.getPropertyChangeSupport().firePropertyChange("lots", null, getWritableLots());
		}

		return gcshAcctLot;
	}

	/**
	 * @see GnuCashWritableTransaction#createWritableLot()
	 */
	@Override
	public GCshWritableAccountLot createWritableLot() {
		GCshWritableAccountLotImpl lot = new GCshWritableAccountLotImpl(this);
		addLot(lot);
		if ( helper.getPropertyChangeSupport() != null ) {
			helper.getPropertyChangeSupport().firePropertyChange("lots", null, getWritableLots());
		}
		return lot;
	}

	/**
	 * @param impl the lot to remove from this account
	 */
	@Override
	public void removeLot(final GCshWritableAccountLot impl) {
		if ( impl.hasTransactions() ) {
			throw new IllegalStateException("This lot has transaction splits and cannot be deleted");
		}

		getWritableGnuCashFile().removeAccount(this);
	}

	public void removeLots() {
		if ( getJwsdpPeer().getActLots() == null ) {
			LOGGER.debug("removeLots: No lots to remove in account " + getID());
			return;
		}

		for ( int i = 0; i < getJwsdpPeer().getActLots().getGncLot().size(); i++ ) {
			getJwsdpPeer().getActLots().getGncLot().remove(i);
		}

		if ( myLots != null ) {
			myLots.clear();
		}

		if ( helper.getPropertyChangeSupport() != null ) {
			helper.getPropertyChangeSupport().firePropertyChange("lots", null, getWritableLots());
		}
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(final String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}

		if ( name.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}

		if ( /* getGnuCashFile().getTopAccountIDs().contains(getID() ) || */
			 getGnuCashFile().getRootAccountID().equals(getID()) ) {
			// Sic, allowed for top-level accounts (as opposed to sister project)
			LOGGER.error("Setting name is forbidden for root accounts");
			throw new UnsupportedOperationException("Setting name is forbidden for root accounts");
		}

		String oldName = getName();
		if ( oldName == name ) {
			return; // nothing has changed
		}

		this.getJwsdpPeer().setActName(name);
		setIsModified();

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("name", oldName, name);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAccountCode(final String code) {
		if ( code == null ) {
			throw new IllegalArgumentException("argument <code> is null");
		}

		if ( code.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <code> is empty");
		}

		if ( getGnuCashFile().getTopAccountIDs().contains(getID()) ||
			 getGnuCashFile().getRootAccountID().equals(getID()) ) {
			LOGGER.error("Setting code is forbidden for root and top-level accounts");
			throw new UnsupportedOperationException("Setting code is forbidden for root and top-level accounts");
		}

		String oldCode = getCode();
		if ( oldCode == code ) {
			return; // nothing has changed
		}

		this.getJwsdpPeer().setActCode(code);
		setIsModified();

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("code", oldCode, code);
		}
	}

	// ----------------------------

	private void setCmdtyCurrNameSpace(final String currNameSpace) {
		if ( currNameSpace == null ) {
			throw new IllegalArgumentException("argument <currNameSpace> is null");
		}

		if ( currNameSpace.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <currNameSpace> is empty");
		}

		if ( getGnuCashFile().getTopAccountIDs().contains(getID())
				|| getGnuCashFile().getRootAccountID().equals(getID()) ) {
			LOGGER.error("Setting name is forbidden for root and top-level accounts");
			throw new UnsupportedOperationException("Setting name is forbidden for root and top-level accounts");
		}

		String oldCurrNameSpace = getCmdtyCurrID().getNameSpace();
		if ( oldCurrNameSpace == currNameSpace ) {
			return; // nothing has changed
		}

		this.getJwsdpPeer().getActCommodity().setCmdtySpace(currNameSpace);
		setIsModified();

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("currencyNameSpace", oldCurrNameSpace, currNameSpace);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCmdtyCurrID(final GCshCmdtyID cmdtyCurrID) {
		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}

		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
		}

//    	if ( getGnuCashFile().getTopAccountIDs().contains(getID()) ||
//			 getGnuCashFile().getRootAccountID().equals(getID())) {
//			LOGGER.error("Setting name is forbidden for root and top-level accounts");
//			throw new UnsupportedOperationException("Setting name is forbidden for root and top-level accounts");
//		}

		setCmdtyCurrNameSpace(cmdtyCurrID.getNameSpace());
		setCmdtyCurrCode(cmdtyCurrID.getCode());
	}

	private void setCmdtyCurrCode(final String currID) {
		if ( currID == null ) {
			throw new IllegalArgumentException("argument <currID> is null");
		}

		if ( currID.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <currID> is null");
		}

		if ( getGnuCashFile().getTopAccountIDs().contains(getID()) ||
			 getGnuCashFile().getRootAccountID().equals(getID()) ) {
			LOGGER.error("Setting name is forbidden for root and top-level accounts");
			throw new UnsupportedOperationException("Setting name is forbidden for root and top-level accounts");
		}

		String oldCurrencyId = getCmdtyCurrID().getCode();
		if ( oldCurrencyId == currID ) {
			return; // nothing has changed
		}

		this.getJwsdpPeer().getActCommodity().setCmdtyId(currID);
		setIsModified();

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("currencyID", oldCurrencyId, currID);
		}
	}

	// ----------------------------

	protected void setIsModified() {
		GnuCashWritableFile writableFile = getWritableGnuCashFile();
		writableFile.setModified(true);
	}

	// ---------------------------------------------------------------

	/**
	 * @see GnuCashWritableAccount#setName(java.lang.String)
	 */
	@Override
	public void setDescription(final String descr) {
		if ( descr == null ) {
			throw new IllegalArgumentException("argument <descr> is null");
		}

		// Caution: empty string allowed here
		// if ( descr.trim().length() == 0 ) {
		//   throw new IllegalArgumentException("argument <descr> is null");
		// }

		if ( getGnuCashFile().getTopAccountIDs().contains(getID())
				|| getGnuCashFile().getRootAccountID().equals(getID()) ) {
			LOGGER.error("Setting description is forbidden for root and top-level accounts");
			throw new UnsupportedOperationException("Setting description is forbidden for root and top-level accounts");
		}

		String oldDescr = getDescription();
		if ( oldDescr == descr ) {
			return; // nothing has changed
		}

		getJwsdpPeer().setActDescription(descr);
		setIsModified();

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("description", oldDescr, descr);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setType(final Type type) {
		if ( type == null ) {
			throw new IllegalArgumentException("argument <type> is null");
		}

		if ( type == Type.ROOT ) {
			throw new UnsupportedOperationException("no account's type may be set to '" + Type.ROOT.toString() + "'");
		}

		if ( getGnuCashFile().getTopAccountIDs().contains(getID()) ||
			 getGnuCashFile().getRootAccountID().equals(getID()) ) {
			LOGGER.error("Setting type is forbidden for root and top-level accounts");
			throw new UnsupportedOperationException("Setting type is forbidden for root and top-level accounts");
		}

		Type oldType = getType();
		if ( oldType == type ) {
			return; // nothing has changed
		}

		// ::CHECK
		// Not sure whether we should allow this action at all...
		// It does happen that you set an account's type wrong by accident,
		// and it should be possibly to correct that.
		// It does not seem prudent to change an account's type when
		// there are already transactions pointing to/from it.
		if ( hasTransactions() ) {
			LOGGER.error("Changing account type is forbidden for accounts that already contain transactions: " + getID());
			throw new UnsupportedOperationException("Changing account type is forbidden for accounts that already contain transactions: " + getID());
		}

		getJwsdpPeer().setActType(type.toString());
		setIsModified();

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("type", oldType, type);
		}
	}

	/**
	 * @see GnuCashWritableAccount#setParentAccount(GnuCashAccount)
	 */
	@Override
	public void setParentAccountID(final GCshAcctID prntAcctID) {
		if ( prntAcctID == null ) {
			setParentAccount(null);
			return;
		}

		if ( ! prntAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <prntAcctID> is not set");
		}

		// check if new parent is a child-account recursively
		GnuCashAccount prntAcct = getGnuCashFile().getAccountByID(prntAcctID);
		if ( isChildAccountRecursive(prntAcct) ) {
			throw new IllegalArgumentException("An account may not be set as its own (grand-)parent");
		}

		if ( getGnuCashFile().getTopAccountIDs().contains(getID()) ||
			 getGnuCashFile().getRootAccountID().equals(getID()) ) {
			LOGGER.error("Setting parent-account ID is forbidden for root and top-level accounts");
			throw new UnsupportedOperationException("Setting parent-account ID is forbidden for root and top-level accounts");
		}

		GCshAcctID oldPrntAcctID = null;
		GncAccount.ActParent jwsdpPrntID = getJwsdpPeer().getActParent();
		if ( jwsdpPrntID == null ) {
			jwsdpPrntID = ((GnuCashWritableFileImpl) getWritableGnuCashFile()).getObjectFactory()
					.createGncAccountActParent();
			jwsdpPrntID.setType(Const.XML_DATA_TYPE_GUID);
			jwsdpPrntID.setValue(prntAcctID.toString());
			getJwsdpPeer().setActParent(jwsdpPrntID);

		} else {
			oldPrntAcctID = getParentAccount().getID();
			jwsdpPrntID.setValue(prntAcctID.toString());
		}
		setIsModified();

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("parentAccount", oldPrntAcctID, prntAcctID);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParentAccount(final GnuCashAccount prntAcct) {
		if ( prntAcct == null ) {
			this.getJwsdpPeer().setActParent(null);
			return;
		}

		if ( prntAcct == this ) {
			throw new IllegalArgumentException("An account cannot be set its own parent");
		}

		setParentAccountID(prntAcct.getID());
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FixedPointNumber getBalance() {
		if ( myBalanceCached != null ) {
			return myBalanceCached;
		}

		List<GnuCashTransactionSplit> after = new ArrayList<GnuCashTransactionSplit>();
		FixedPointNumber balance = getBalance(LocalDate.now(), after);

		if ( after.isEmpty() ) {
			myBalanceCached = balance;

			// add a listener to keep the cache up to date
			if ( myBalanceCachedInvalidator != null ) {
				myBalanceCachedInvalidator = new PropertyChangeListener() {
					private final Collection<GnuCashTransactionSplit> splitsWeAreAddedTo = new HashSet<GnuCashTransactionSplit>();

					public void propertyChange(final PropertyChangeEvent evt) {
						myBalanceCached = null;

						// we don't handle the case of removing an account
						// because that happens seldomly enough

						if ( evt.getPropertyName().equals("account") && 
							 evt.getSource() instanceof GnuCashWritableTransactionSplit ) {
							GnuCashWritableTransactionSplit splitw = (GnuCashWritableTransactionSplit) evt.getSource();
							if ( splitw.getAccount() != GnuCashWritableAccountImpl.this ) {
								helper.removePropertyChangeListener("account", this);
								helper.removePropertyChangeListener("quantity", this);
								helper.removePropertyChangeListener("datePosted", this);
								splitsWeAreAddedTo.remove(splitw);
							}

						}

						if ( evt.getPropertyName().equals("transactionSplits") ) {
							List<GnuCashTransactionSplit> splits = (List<GnuCashTransactionSplit>) evt.getNewValue();
							for ( GnuCashTransactionSplit splt : splits ) {
								if ( ! ( splt instanceof GnuCashWritableTransactionSplit ) ||
									 splitsWeAreAddedTo.contains(splt) ) {
									continue;
								}
								GnuCashWritableTransactionSplit splitw = (GnuCashWritableTransactionSplit) splt;
								helper.addPropertyChangeListener("account", this);
								helper.addPropertyChangeListener("quantity", this);
								helper.addPropertyChangeListener("datePosted", this);
								splitsWeAreAddedTo.add(splitw);
							}
						}
					}
				};

				helper.addPropertyChangeListener("currencyID", myBalanceCachedInvalidator);
				helper.addPropertyChangeListener("currencyNameSpace", myBalanceCachedInvalidator);
				helper.addPropertyChangeListener("transactionSplits", myBalanceCachedInvalidator);
			}
		}

		return balance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigFraction getBalanceRat() {
		if ( myBalanceCachedRat != null ) {
			return myBalanceCachedRat;
		}

		List<GnuCashTransactionSplit> after = new ArrayList<GnuCashTransactionSplit>();
		BigFraction balance = getBalanceRat(LocalDate.now(), after);

		if ( after.isEmpty() ) {
			myBalanceCachedRat = balance;

			// add a listener to keep the cache up to date
			if ( myBalanceCachedInvalidator != null ) {
				myBalanceCachedInvalidator = new PropertyChangeListener() {
					private final Collection<GnuCashTransactionSplit> splitsWeAreAddedTo = new HashSet<GnuCashTransactionSplit>();

					public void propertyChange(final PropertyChangeEvent evt) {
						myBalanceCachedRat = null;

						// we don't handle the case of removing an account
						// because that happens seldomly enough

						if ( evt.getPropertyName().equals("account") && 
							 evt.getSource() instanceof GnuCashWritableTransactionSplit ) {
							GnuCashWritableTransactionSplit splitw = (GnuCashWritableTransactionSplit) evt.getSource();
							if ( splitw.getAccount() != GnuCashWritableAccountImpl.this ) {
								helper.removePropertyChangeListener("account", this);
								helper.removePropertyChangeListener("quantity", this);
								helper.removePropertyChangeListener("datePosted", this);
								splitsWeAreAddedTo.remove(splitw);
							}

						}

						if ( evt.getPropertyName().equals("transactionSplits") ) {
							List<GnuCashTransactionSplit> splits = (List<GnuCashTransactionSplit>) evt.getNewValue();
							for ( GnuCashTransactionSplit splt : splits ) {
								if ( ! ( splt instanceof GnuCashWritableTransactionSplit ) ||
									 splitsWeAreAddedTo.contains(splt) ) {
									continue;
								}
								GnuCashWritableTransactionSplit splitw = (GnuCashWritableTransactionSplit) splt;
								helper.addPropertyChangeListener("account", this);
								helper.addPropertyChangeListener("quantity", this);
								helper.addPropertyChangeListener("datePosted", this);
								splitsWeAreAddedTo.add(splitw);
							}
						}
					}
				};

				helper.addPropertyChangeListener("currencyID", myBalanceCachedInvalidator);
				helper.addPropertyChangeListener("currencyNameSpace", myBalanceCachedInvalidator);
				helper.addPropertyChangeListener("transactionSplits", myBalanceCachedInvalidator);
			}
		}

		return balance;
	}
	
	// ----------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FixedPointNumber getBalanceChange(final LocalDate from, final LocalDate to) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashTransactionSplit splt : getTransactionSplits() ) {
			LocalDateTime whenHappened = splt.getTransaction().getDatePosted().toLocalDateTime();

			if ( ! whenHappened.isBefore( to.atStartOfDay() ) ) {
				continue;
			}

			if ( whenHappened.isBefore( from.atStartOfDay() ) ) {
				continue;
			}

			retval.add(splt.getQuantity()); // mutable
		}

		return retval;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigFraction getBalanceChangeRat(final LocalDate from, final LocalDate to) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashTransactionSplit splt : getTransactionSplits() ) {
			LocalDateTime whenHappened = splt.getTransaction().getDatePosted().toLocalDateTime();

			if ( ! whenHappened.isBefore( to.atStartOfDay() ) ) {
				continue;
			}

			if ( whenHappened.isBefore( from.atStartOfDay() ) ) {
				continue;
			}

			retval = retval.add(splt.getQuantityRat()); // immutable
		}

		return retval;
	}

	// -------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addUserDefinedAttribute(final String type, final String name, final String value) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}

		if ( name.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}

		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}

		if ( value.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <value> is empty");
		}

		if ( getGnuCashFile().getTopAccountIDs().contains(getID()) ||
			 getGnuCashFile().getRootAccountID().equals(getID()) ) {
			LOGGER.error("Adding user-defined attribute is forbidden for root and top-level accounts");
			throw new UnsupportedOperationException("Adding user-defined attribute is forbidden for root and top-level accounts");
		}

		if ( jwsdpPeer.getActSlots() == null ) {
			ObjectFactory fact = getGnuCashFile().getObjectFactory();
			SlotsType newSlotsType = fact.createSlotsType();
			jwsdpPeer.setActSlots(newSlotsType);
		}

		HasWritableUserDefinedAttributesImpl
			.addUserDefinedAttributeCore(jwsdpPeer.getActSlots(), getWritableGnuCashFile(),
										 type, name, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeUserDefinedAttribute(final String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}

		if ( name.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}

		if ( getGnuCashFile().getTopAccountIDs().contains(getID()) ||
			 getGnuCashFile().getRootAccountID().equals(getID()) ) {
			LOGGER.error("Removing user-defined attribute is forbidden for root and top-level accounts");
			throw new UnsupportedOperationException("Removing user-defined attribute is forbidden for root and top-level accounts");
		}

		if ( jwsdpPeer.getActSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}

		HasWritableUserDefinedAttributesImpl
			.removeUserDefinedAttributeCore(jwsdpPeer.getActSlots(), getWritableGnuCashFile(), 
											name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUserDefinedAttribute(final String name, final String value) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}

		if ( name.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}

		if ( getGnuCashFile().getTopAccountIDs().contains(getID()) ||
			 getGnuCashFile().getRootAccountID().equals(getID()) ) {
			LOGGER.error("Setting user-defined attribute is forbidden for root and top-level accounts");
			throw new UnsupportedOperationException("Setting user-defined attribute is forbidden for root and top-level accounts");
		}

		if ( jwsdpPeer.getActSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}

		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getActSlots(), getWritableGnuCashFile(),
										 name, value);
	}

	// ----------------------------

	public void clean() {
		LOGGER.debug("clean: [account-id=" + getID() + "]");
		HasWritableUserDefinedAttributesImpl.cleanSlots(getJwsdpPeer().getActSlots());
	}

	// ---------------------------------------------------------------
	
	@Override
	public void setHidden()
	{
		if ( isHidden() )
			return;
		
		if ( getUserDefinedAttribute(Const.SLOT_KEY_ACCT_HIDDEN) == null ) {
			addUserDefinedAttribute(Const.XML_DATA_TYPE_STRING, Const.SLOT_KEY_ACCT_HIDDEN, "true");
		} else {
			setUserDefinedAttribute(Const.SLOT_KEY_ACCT_HIDDEN, "true");
		}
	}

	@Override
	public void unsetHidden()
	{
		if ( ! isHidden() )
			return;
		
		removeUserDefinedAttribute(Const.SLOT_KEY_ACCT_HIDDEN);
	}

	// ---------------------------------------------------------------

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashWritableAccountImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", code='");
		buffer.append(getCode() + "'");

		buffer.append(", type=");
		try {
			buffer.append(getType());
		} catch (UnknownAccountTypeException e) {
			buffer.append("ERROR");
		}

		buffer.append(", qualif-name='");
		buffer.append(getQualifiedName() + "'");

		buffer.append(", commodity/currency='");
		try {
			buffer.append(getCmdtyCurrID() + "'");
		} catch (InvalidCmdtyTypeException e) {
			buffer.append("ERROR");
		}

		buffer.append("]");

		return buffer.toString();
	}

}
