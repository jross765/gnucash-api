package org.gnucash.api.write.impl.spec;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.impl.spec.GnuCashCustomerInvoiceEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashCustomerInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerInvoiceEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoiceEntry;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshCustID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;
import org.gnucash.base.basetypes.simple.spec.GCshCustInvcEntrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Customer invoice that can be modified if {@link #isModifiable()} returns true.
 * 
 * @see GnuCashCustomerInvoice
 * 
 * @see GnuCashWritableEmployeeVoucherImpl
 * @see GnuCashWritableVendorBillImpl
 * @see GnuCashWritableJobInvoiceImpl
 */
public class GnuCashWritableCustomerInvoiceImpl extends GnuCashWritableGenerInvoiceImpl 
                                                implements GnuCashWritableCustomerInvoice 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableCustomerInvoiceImpl.class);

	// ---------------------------------------------------------------
	/**
	 * Create an editable invoice facading an existing JWSDP-peer.
	 *
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @param gcshFile      the file to register under
	 * @see GnuCashGenerInvoiceImpl
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableCustomerInvoiceImpl(final GncGncInvoice jwsdpPeer, final GnuCashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

	/**
	 * @param file the file we are associated with.
	 * @param number 
	 * @param cust 
	 * @param incomeAcct 
	 * @param receivableAcct 
	 * @param openedDate 
	 * @param postDate 
	 * @param dueDate 
	 * @throws WrongOwnerTypeException
	 * @throws IllegalTransactionSplitActionException
	 */
	public GnuCashWritableCustomerInvoiceImpl(
			final GnuCashWritableFileImpl file, 
			final String number,
			final GnuCashCustomer cust, 
			final GnuCashAccountImpl incomeAcct, 
			final GnuCashAccountImpl receivableAcct,
			final LocalDate openedDate, 
			final LocalDate postDate, 
			final LocalDate dueDate)
			throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
		super(createCustomerInvoice_int(file, number, cust, false, // <-- caution!
				incomeAcct, receivableAcct, openedDate, postDate, dueDate), file);
	}

	/**
	 * @param invc 
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableCustomerInvoiceImpl(final GnuCashWritableGenerInvoiceImpl invc)
			throws TaxTableNotFoundException {
		super(invc.getJwsdpPeer(), invc.getGnuCashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.CUSTOMER )
			throw new WrongInvoiceTypeException();

		// Caution: In the following two loops, we may *not* iterate directly over
		// invc.getGenerEntries(), because else, we will produce a
		// ConcurrentModificationException.
		// (It only works if the invoice has one single entry.)
		// Hence the indirection via the redundant "entries" hash set.
		Collection<GnuCashGenerInvoiceEntry> entries = new HashSet<GnuCashGenerInvoiceEntry>();
		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			entries.add(entry);
		}

		for ( GnuCashGenerInvoiceEntry entry : entries ) {
			addEntry(new GnuCashWritableCustomerInvoiceEntryImpl(entry));
		}

		// Caution: Indirection via a redundant "trxs" hash set.
		// Same reason as above.
		Collection<GnuCashTransaction> trxs = new HashSet<GnuCashTransaction>();
		for ( GnuCashTransaction trx : invc.getPayingTransactions() ) {
			trxs.add(trx);
		}

		for ( GnuCashTransaction trx : trxs ) {
			for ( GnuCashTransactionSplit splt : trx.getSplits() ) {
				GCshLotID lot = splt.getLotID();
				if ( lot != null ) {
					for ( GnuCashGenerInvoice invc1 : splt.getTransaction().getGnuCashFile().getGenerInvoices() ) {
						GCshLotID lotID = invc1.getLotID();
						if ( lotID != null && lotID.equals(lot) ) {
							// Check if it's a payment transaction.
							// If so, add it to the invoice's list of payment transactions.
							if ( splt.getAction() == GnuCashTransactionSplit.Action.PAYMENT ) {
								addPayingTransaction(splt);
							}
						} // if lotID
					} // for invc
				} // if lot
			} // for splt
		} // for trx
	}

    public GnuCashWritableCustomerInvoiceImpl(final GnuCashCustomerInvoiceImpl invc) {
    	super(invc.getJwsdpPeer(), invc.getGnuCashFile());
    }
    
	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public void setCustomer(GnuCashCustomer cust) {
    	if ( cust == null ) {
    	    throw new IllegalArgumentException("argument <cust> is null");
    	}
    	
		GnuCashCustomer oldCust = getCustomer();
		if ( oldCust == cust ||
			 oldCust.getID().equals(getID()) ) {
			return; // nothing has changed
		}

    	attemptChange();
		getJwsdpPeer().getInvoiceOwner().getOwnerId().setValue(cust.getID().toString());
		getWritableGnuCashFile().setModified(true);

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("customer", oldCust, cust);
		}
	}

	// -----------------------------------------------------------

	/**
	 * create and add a new entry.
	 * 
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableCustomerInvoiceEntry createEntry(
			final GnuCashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity)
			throws TaxTableNotFoundException {
		GnuCashWritableCustomerInvoiceEntry entry = createCustInvcEntry(acct, singleUnitPrice, quantity);
		return entry;
	}

	/**
	 * create and add a new entry.<br/>
	 * The entry will use the accounts of the SKR03.
	 * 
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableCustomerInvoiceEntry createEntry(
			final GnuCashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity, 
			final String taxTabName)
			throws TaxTableNotFoundException {
		GnuCashWritableCustomerInvoiceEntry entry = createCustInvcEntry(acct, singleUnitPrice, quantity, taxTabName);
		return entry;
	}

	/**
	 * create and add a new entry.<br/>
	 *
	 * @return an entry using the given Tax-Table
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableCustomerInvoiceEntry createEntry(
			final GnuCashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity, 
			final GCshTaxTable taxTab)
			throws TaxTableNotFoundException {
		GnuCashWritableCustomerInvoiceEntry entry = createCustInvcEntry(acct, singleUnitPrice, quantity, taxTab);
		LOGGER.info("createEntry: Created customer invoice entry: " + entry.getID());
		return entry;
	}

	// -----------------------------------------------------------

	/**
	 * @throws TaxTableNotFoundException
	 * @see #addInvcEntry(GnuCashGenerInvoiceEntryImpl)
	 */
	protected void removeEntry(final GnuCashWritableCustomerInvoiceEntryImpl entry)
			throws TaxTableNotFoundException {
		removeInvcEntry(entry);
		LOGGER.info("removeEntry: Removed customer invoice entry: " + entry.getID());
	}

	/**
	 * Called by
	 * ${@link GnuCashWritableCustomerInvoiceEntryImpl#createCustInvoiceEntry_int(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}.
	 *
	 * @param entr the entry to add to our internal list of customer-invoice-entries
	 * @throws TaxTableNotFoundException
	 */
	protected void addEntry(final GnuCashWritableCustomerInvoiceEntryImpl entry) 
			throws TaxTableNotFoundException {
		addInvcEntry(entry);
		LOGGER.info("addEntry: Added customer invoice entry: " + entry.getID());
	}

	protected void subtractEntry(final GnuCashGenerInvoiceEntryImpl entry)
			throws TaxTableNotFoundException {
		subtractInvcEntry(entry);
		LOGGER.info("subtractEntry: Subtracted customer invoice entry: " + entry.getID());
	}

	// ---------------------------------------------------------------
	
	/**
	 * @return the ID of the Account to transfer the money from
	 */
	@SuppressWarnings("unused")
	private GCshAcctID getPostAccountID(final GnuCashCustomerInvoiceEntryImpl entry) {
		return getCustInvcPostAccountID(entry);
	}

	/**
	 * Do not use
	 */
	@Override
	protected GCshAcctID getVendBllPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	protected GCshAcctID getEmplVchPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	protected GCshAcctID getJobInvcPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
		throw new WrongInvoiceTypeException();
	}

	// ---------------------------------------------------------------
	
	/**
	 * Throw an IllegalStateException if we are not modifiable.
	 *
	 * @see #isModifiable()
	 */
	protected void attemptChange() {
		if ( !isModifiable() ) {
			throw new IllegalStateException(
					"this customer invoice is NOT modifiable because there are already payment for it made");
		}
	}

	/**
	 * @see #getGenerEntryByID(GCshGenerInvcEntrID)
	 */
	public GnuCashWritableCustomerInvoiceEntry getWritableEntryByID(final GCshGenerInvcEntrID entrID) {
		return new GnuCashWritableCustomerInvoiceEntryImpl(getGenerEntryByID(entrID));
	}

	// ---------------------------------------------------------------

	/**
	 * @return the ID of the customer who/that owns the invoice
	 */
	public GCshCustID getCustomerID() {
		return new GCshCustID( getOwnerID() );
	}

	/**
	 * @return the customer who/that owns the invoice 
	 */
	public GnuCashCustomer getCustomer() {
		return getGnuCashFile().getCustomerByID(getCustomerID());
	}

	// ---------------------------------------------------------------

	@Override
	public void post(final GnuCashAccount incomeAcct, final GnuCashAccount receivableAcct, final LocalDate postDate,
			final LocalDate dueDate) throws WrongOwnerTypeException,
			IllegalTransactionSplitActionException {
		postCustomerInvoice(getGnuCashFile(), this, getCustomer(), incomeAcct, receivableAcct, postDate, dueDate);
	}

	// ---------------------------------------------------------------

	public static GnuCashCustomerInvoiceImpl toReadable(GnuCashWritableCustomerInvoiceImpl invc) {
		GnuCashCustomerInvoiceImpl result = new GnuCashCustomerInvoiceImpl(invc.getJwsdpPeer(), invc.getGnuCashFile());
		return result;
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashCustomerInvoiceEntry getEntryByID(GCshCustInvcEntrID entrID) {
		return new GnuCashCustomerInvoiceEntryImpl(getGenerEntryByID(entrID));
	}

	@Override
	public Collection<GnuCashCustomerInvoiceEntry> getEntries() {
		Collection<GnuCashCustomerInvoiceEntry> castEntries = new HashSet<GnuCashCustomerInvoiceEntry>();

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == GCshOwner.Type.CUSTOMER ) {
				castEntries.add(new GnuCashCustomerInvoiceEntryImpl(entry));
			}
		}

		return castEntries;
	}

	@Override
	public void addEntry(GnuCashCustomerInvoiceEntry entry) {
		addGenerEntry(entry);
	}

	// ---------------------------------------------------------------

	@Override
	public FixedPointNumber getAmountUnpaidWithTaxes() {
		return getCustInvcAmountUnpaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithTaxes() {
		return getCustInvcAmountPaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithoutTaxes() {
		return getCustInvcAmountPaidWithoutTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithTaxes() {
		return getCustInvcAmountWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithoutTaxes() {
		return getCustInvcAmountWithoutTaxes();
	}

	// ----------------------------

	@Override
	public String getAmountUnpaidWithTaxesFormatted() {
		return getCustInvcAmountUnpaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithTaxesFormatted() {
		return getCustInvcAmountPaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithoutTaxesFormatted() {
		return getCustInvcAmountPaidWithoutTaxesFormatted();
	}

	@Override
	public String getAmountWithTaxesFormatted() {
		return getCustInvcAmountWithTaxesFormatted();
	}

	@Override
	public String getAmountWithoutTaxesFormatted() {
		return getCustInvcAmountWithoutTaxesFormatted();
	}

	// ---------------------------------------------------------------

	@Override
	public boolean isFullyPaid() {
		return isCustInvcFullyPaid();
	}

	@Override
	public boolean isNotFullyPaid() {
		return isNotCustInvcFullyPaid();
	}

}
