package org.gnucash.api.write.impl;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.Const;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashTransactionSplitImpl;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.GCshIDNotSetException;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Transaction-Split that can be newly created or removed from its transaction.
 */
public class GnuCashWritableTransactionSplitImpl extends GnuCashTransactionSplitImpl 
                                                 implements GnuCashWritableTransactionSplit
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableTransactionSplitImpl.class);

	// ---------------------------------------------------------------

	// Our helper to implement the GnuCashWritableObject-interface.
	private final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

	// ---------------------------------------------------------------

	/**
	 * @param jwsdpPeer   the JWSDP-object we are facading.
	 * @param trx the transaction we belong to
	 * @param addSpltToAcct 
	 * @param addSpltToInvc 
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableTransactionSplitImpl(
			final GncTransaction.TrnSplits.TrnSplit jwsdpPeer,
			final GnuCashWritableTransaction trx, 
			final boolean addSpltToAcct, 
			final boolean addSpltToInvc) {
		super(jwsdpPeer, trx, 
			  addSpltToAcct, addSpltToInvc);
	}

	/**
	 * create a new split and and add it to the given transaction.
	 * 
	 * @param trx transaction the transaction we will belong to
	 * @param acct the account the split will be assigned to
	 */
	public GnuCashWritableTransactionSplitImpl(
			final GnuCashWritableTransactionImpl trx,
			final GnuCashAccount acct) {
		super(createTransactionSplit_int(trx, acct,
										new GCshSpltID( GCshID.getNew()) ), 
			  trx,
			  true, false);

		// ::TODO ::CHECK
		// this is a workaround.
		// if super does account.addSplit(this) it adds an instance on
		// GnuCashTransactionSplitImpl that is "!=
		// (GnuCashWritableTransactionSplitImpl)this";
		// thus we would get warnings about duplicate split-ids and can no longer
		// compare splits by instance.
		// if(account!=null)
		// ((GnuCashAccountImpl)account).replaceTransactionSplit(account.getTransactionSplitByID(getID()),
		// GnuCashWritableTransactionSplitImpl.this);

		trx.addSplit(this);
	}

	public GnuCashWritableTransactionSplitImpl(final GnuCashTransactionSplitImpl splt) {
		super(splt.getJwsdpPeer(), splt.getTransaction(), 
			  false, false);
	}

	public GnuCashWritableTransactionSplitImpl(
			final GnuCashTransactionSplit splt,
			final boolean addSpltToAcct,
			final boolean addSpltToInvc) {
		super(splt.getJwsdpPeer(), splt.getTransaction(), 
			  addSpltToAcct, addSpltToInvc);
	}

	// ---------------------------------------------------------------

	/**
	 * Creates a new Transaction and add's it to the given GnuCash file Don't modify
	 * the ID of the new transaction!
	 */
	protected static GncTransaction.TrnSplits.TrnSplit createTransactionSplit_int(
	    final GnuCashWritableTransactionImpl trx, 
	    final GnuCashAccount acct, 
	    final GCshSpltID newID) {
		if ( trx == null ) {
			throw new IllegalArgumentException("argument <trx> is null");
		}

		if ( acct == null ) {
			throw new IllegalArgumentException("argument <acct> is null");
		}

		if ( newID == null ) {
			throw new IllegalArgumentException("argument <newID> is null");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("argument <newID> is not set");
		}

		// This is needed because transaction.addSplit() later
		// must have an already built list of splits.
		// Otherwise, it will create the list from the JAXB-Data
		// Thus, 2 instances of this GnuCashWritableTransactionSplitImpl
		// will exist: One created in getSplits() from this JAXB-Data
		// the other is this object.
		trx.getSplits();

		GnuCashWritableFileImpl gnucashFileImpl = trx.getWritableGnuCashFile();
		ObjectFactory factory = gnucashFileImpl.getObjectFactory();

		GncTransaction.TrnSplits.TrnSplit jwsdpSplt = gnucashFileImpl.createGncTransactionSplitType();

		{
			GncTransaction.TrnSplits.TrnSplit.SplitId id = factory.createGncTransactionTrnSplitsTrnSplitSplitId();
			id.setType(Const.XML_DATA_TYPE_GUID);
			id.setValue(newID.toString());
			jwsdpSplt.setSplitId(id);
		}

		jwsdpSplt.setSplitReconciledState(GnuCashTransactionSplit.ReconState.NOT_RECONCILED.getCode());

		jwsdpSplt.setSplitQuantity("0/100");
		jwsdpSplt.setSplitValue("0/100");
		{
			GncTransaction.TrnSplits.TrnSplit.SplitAccount splitaccount = factory
					.createGncTransactionTrnSplitsTrnSplitSplitAccount();
			splitaccount.setType(Const.XML_DATA_TYPE_GUID);
			splitaccount.setValue(acct.getID().toString());
			jwsdpSplt.setSplitAccount(splitaccount);
		}

		LOGGER.debug("createTransactionSplit_int: Created new transaction split (core): " + jwsdpSplt.getSplitId().getValue());

		return jwsdpSplt;
	}

	// ---------------------------------------------------------------

	/**
	 * @see GnuCashTransactionSplitImpl#getTransaction()
	 */
	@Override
	public GnuCashWritableTransaction getTransaction() {
		return (GnuCashWritableTransaction) super.getTransaction();
	}

	/**
	 * remove this split from its transaction.
	 */
	@Override
	public void remove() {
		getTransaction().removeSplit(this);
	}

	/**
	 * @see GnuCashWritableTransactionSplit#setAccount(GnuCashAccount)
	 */
	@Override
	public void setAccountID(final GCshAcctID acctID) {
		if ( acctID == null ) {
			throw new IllegalArgumentException("argument <acctID> is null");
		}
		
		if ( ! acctID.isSet() ) {
			throw new IllegalArgumentException("argument <acctID> is not set");
		}
		
		String oldAcctID = (getJwsdpPeer().getSplitAccount() == null ? null : getJwsdpPeer().getSplitAccount().getValue());
		jwsdpPeer.getSplitAccount().setType(Const.XML_DATA_TYPE_GUID);
		jwsdpPeer.getSplitAccount().setValue(acctID.toString());

		// No, the following is nonsense:
//		if ( ! isCurrencyMatching() ) {
//			LOGGER.error("setAccountID: Transaction Split: " + getID() + ": New account's security/currency is not the same as transaction's security/currency");
//			LOGGER.error("Reverting change");
//			jwsdpPeer.getSplitAccount().setValue(oldAcctID);
//			throw new IllegalStateException("Transaction Split: " + getID() + ": New account's security/currency is not the same as transaction's security/currency");
//		}
		
		getWritableGnuCashFile().setModified(true);

		if ( oldAcctID == null || 
			 ! oldAcctID.equals(acctID.toString()) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("accountID", oldAcctID, acctID.toString());
			}
		}
	}

	/**
	 * @see GnuCashWritableTransactionSplit#setAccount(GnuCashAccount)
	 */
	@Override
	public void setAccount(final GnuCashAccount acct) {
		if ( acct == null ) {
			throw new IllegalArgumentException("argument <acct> is null");
		}
		
		setAccountID(acct.getID());
	}

	/**
	 * @return true if the currency of transaction and account match
	 * 
	 * ::CHECK: What could we possibly need that for?
	 * 
	 * It is definitely *not* the case that a transaction's split's
	 * account's security/currency has to be the same as its resp.
	 * transaction's security/currency -- there are many real-world 
	 * instances where that is not / must not be the case (a dividend 
	 * transaction, for example).
	 * 
	 * This (or a similar) wrong assumption obviously was in one of
	 * the previous maintainers' mind, because such a check was
	 * coded into this lib before the current maintainer took over
	 * (and it took him a while to see the error).
	 * 
	 * It is, however, true that this condition must hold for at
	 * least *one* of a transaction's splits. But this is, by definition,
	 * a check to be performed on a transaction object, not a 
	 * single split object. For such a check, this could be a helper
	 * function.
	 */
	private boolean isCmdtyMatching() {
		GnuCashAccount acct = getAccount();
		if ( acct == null ) {
			throw new IllegalStateException("account is null");
		}
		
		GnuCashWritableTransaction trx = getTransaction();
		if ( trx == null ) {
			throw new IllegalStateException("transaction is null");
		}
		
		GCshCmdtyID acctCmdtyID = acct.getCmdtyID();
		if ( acctCmdtyID == null ) {
			throw new IllegalStateException("account's security/currency is null");
		}

		if ( ! acctCmdtyID.isSet() ) {
			throw new IllegalStateException("account's security/currency is not set");
		}

		GCshCmdtyID trxCmdtyID = trx.getCmdtyID();
		if ( trxCmdtyID == null ) {
			throw new IllegalStateException("transaction's security/currency is null");
		}
		if ( ! trxCmdtyID.isSet() ) {
			throw new IllegalStateException("transaction's security/currency is not set");
		}

		// Important: Don't forget to cast the IDs to their most basic type
		return ((GCshCmdtyID) acctCmdtyID).equals( (GCshCmdtyID) trxCmdtyID );
	}

	/**
	 * @see GnuCashWritableTransactionSplit#setQuantity(FixedPointNumber)
	 */
	@Override
	@Deprecated
	public void setQuantity(final FixedPointNumber qty) {
		if ( qty == null ) {
			throw new NullPointerException("argument <qty> is null");
		}

		FixedPointNumber oldQty = getQuantity();
		getJwsdpPeer().setSplitQuantity(qty.toGnuCashString());
		getWritableGnuCashFile().setModified(true);
		
		if ( oldQty == null || ! oldQty.equals(qty) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("quantity", oldQty, qty);
			}
		}
	}

	@Override
	public void setQuantity(final BigFraction qty) {
		if ( qty == null ) {
			throw new IllegalArgumentException("argument <qty> is null");
		}

		BigFraction oldQty = getQuantityRat();
		getJwsdpPeer().setSplitQuantity(qty.toString().replaceAll("\\s", ""));
		getWritableGnuCashFile().setModified(true);
		
		if ( oldQty == null || ! oldQty.equals(qty) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("quantity", oldQty, qty);
			}
		}
	}

	/**
	 * @see GnuCashWritableTransactionSplit#setValue(FixedPointNumber)
	 */
	@Override
	@Deprecated
	public void setValue(final FixedPointNumber val) {
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		FixedPointNumber oldVal = getValue();
		jwsdpPeer.setSplitValue(val.toGnuCashString());
		getWritableGnuCashFile().setModified(true);
	
		if ( oldVal == null || ! oldVal.equals(val) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("value", oldVal, val);
			}
		}
	}

	@Override
	public void setValue(final BigFraction val) {
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}

		BigFraction oldVal = getValueRat();
		jwsdpPeer.setSplitValue(val.toString().replaceAll("\\s", ""));
		getWritableGnuCashFile().setModified(true);

		if ( oldVal == null || ! oldVal.equals(val) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("value", oldVal, val);
			}
		}
	}
	
	// ---------------------------------------------------------------

	/**
	 * Set the description-text.
	 *
	 * @param descr the new description
	 */
	@Override
	public void setDescription(final String descr) {
		if ( descr == null ) {
			throw new IllegalArgumentException("argument <descr> is null. Please use the empty string instead of null for an empty description");
		}

		// Yes, empty descr is valid
//		if ( descr.isBlank() ) {
//			throw new IllegalArgumentException("argument <descr> is blank");
//		}

		String old = getJwsdpPeer().getSplitMemo();
		getJwsdpPeer().setSplitMemo(descr);
		getWritableGnuCashFile().setModified(true);

		if ( old == null || ! old.equals(descr) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("description", old, descr);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAction(final Action act) {
		setActionStr(act.getLocaleString());
	}

	/**
	 * {@inheritDoc}
	 */
	public void setActionStr(final String actStr) throws IllegalTransactionSplitActionException {
		if ( actStr == null ) {
			throw new IllegalArgumentException("argument <actStr> is null");
		}

		if ( actStr.isBlank() ) {
			throw new IllegalArgumentException("argument <actStr> is blank");
		}

		String oldActStr = getActionStr();
		getJwsdpPeer().setSplitAction(actStr);
		getWritableGnuCashFile().setModified(true);

		if ( oldActStr == null || ! oldActStr.equals(actStr) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("splitAction", oldActStr, actStr);
			}
		}
	}

	@Override
	public void setReconState(ReconState stat) {
		if ( stat == null ) {
			throw new IllegalArgumentException("argument <stat> is null");
		}

		ReconState oldStat = getReconState();
		getJwsdpPeer().setSplitReconciledState( stat.getCode() );
		getWritableGnuCashFile().setModified(true);

		if ( oldStat == null || ! oldStat.equals(stat) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("reconState", oldStat, stat);
			}
		}
	}

	// ---------------------------------------------------------------

	public void setLotID(final GCshLotID lotID) {
		if ( lotID == null ) {
			throw new IllegalArgumentException("argument <lotID> is null");
		}

		if ( ! lotID.isSet() ) {
			throw new IllegalArgumentException("argument <lotID> is not set");
		}

		GnuCashWritableTransactionImpl trx = (GnuCashWritableTransactionImpl) getTransaction();
		GnuCashWritableFileImpl writingFile = trx.getWritableGnuCashFile();
		ObjectFactory factory = writingFile.getObjectFactory();

		if ( getJwsdpPeer().getSplitLot() == null ) {
			GncTransaction.TrnSplits.TrnSplit.SplitLot lot = factory.createGncTransactionTrnSplitsTrnSplitSplitLot();
			getJwsdpPeer().setSplitLot(lot);
		}

		try {
			getJwsdpPeer().getSplitLot().setValue(lotID.get());
			getJwsdpPeer().getSplitLot().setType(Const.XML_DATA_TYPE_GUID);
		} catch (GCshIDNotSetException exc) {
			throw new IllegalArgumentException("UUID not set"); // Compiler happy
		}

		// if we have a lot, and if we are a paying transaction, then check the slots
		// ::TODO ::CHECK
		// 09.10.2023: This code, in the current setting, generates wrong
		// output (a closing split slot tag without an opening one, and
		// we don't (always?) need a split slot anyway.
//		SlotsType slots = getJwsdpPeer().getSplitSlots();
//		if (slots == null) {
//			slots = factory.createSlotsType();
//			getJwsdpPeer().setSplitSlots(slots);
//		}
//		if (slots.getSlot() == null) {
//			Slot slot = factory.createSlot();
//			slot.setSlotKey("trans-txn-type");
//			SlotValue value = factory.createSlotValue();
//			value.setType(Const.XML_DATA_TYPE_STRING);
//			value.getContent().add(GnuCashTransaction.TYPE_PAYMENT);
//			slot.setSlotValue(value);
//			slots.getSlot().add(slot);
//		}

	}

	public void unsetLotID() {
		if ( getLotID() == null ) {
			throw new IllegalStateException("no lot ID in this transaction split");
		}

		getJwsdpPeer().setSplitLot(null);
	}

	// ---------------------------------------------------------------

	/**
	 * The GnuCash file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
	@Override
	public GnuCashWritableFileImpl getWritableGnuCashFile() {
		return (GnuCashWritableFileImpl) super.getGnuCashFile();
	}

//	/**
//	 * The GnuCash file is the top-level class to contain everything.
//	 *
//	 * @return the file we are associated with
//	 */
//	@Override
//	public GnuCashWritableFileImpl getGnuCashFile() {
//		return (GnuCashWritableFileImpl) super.getGnuCashFile();
//	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addUserDefinedAttribute(String type, String name, String value) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}
		
		if ( name.isBlank() ) {
			throw new IllegalArgumentException("argument <name> is emptys");
		}

		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}
		
		if ( value.isBlank() ) {
			throw new IllegalArgumentException("argument <value> is blank");
		}

		if ( jwsdpPeer.getSplitSlots() == null ) {
			ObjectFactory fact = getWritableGnuCashFile().getObjectFactory();
			SlotsType newSlotsType = fact.createSlotsType();
			jwsdpPeer.setSplitSlots(newSlotsType);
		}
		
		HasWritableUserDefinedAttributesImpl
			.addUserDefinedAttributeCore(jwsdpPeer.getSplitSlots(), getWritableGnuCashFile(), 
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
		
		if ( name.isBlank() ) {
			throw new IllegalArgumentException("argument <name> is blank");
		}

		if ( jwsdpPeer.getSplitSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.removeUserDefinedAttributeCore(jwsdpPeer.getSplitSlots(), getWritableGnuCashFile(), 
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
		
		if ( name.isBlank() ) {
			throw new IllegalArgumentException("argument <name> is blank");
		}

		if ( value == null ) {
			throw new IllegalArgumentException("argument <value> is null");
		}
		
		if ( value.isBlank() ) {
			throw new IllegalArgumentException("argument <value> is blank");
		}

		if ( jwsdpPeer.getSplitSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getSplitSlots(),
										 getWritableGnuCashFile(),
										 name, value);
	}

	public void clean() {
		HasWritableUserDefinedAttributesImpl.cleanSlots(getJwsdpPeer().getSplitSlots());
	}

	// ---------------------------------------------------------------

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashWritableTransactionSplitImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", action=");
		try {
			buffer.append(getAction());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", recon-state=");
		try {
			buffer.append(getReconState());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", transaction-id=");
		buffer.append(getTransaction().getID());

		buffer.append(", account-id=");
		buffer.append(getAccountID());

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append(", value=");
		buffer.append(getValue());

		buffer.append(", quantity=");
		buffer.append(getQuantity());

		buffer.append("]");

		return buffer.toString();
	}

}
