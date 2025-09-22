package org.gnucash.api.write.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncBudget;
import org.gnucash.api.generated.GncCommodity;
import org.gnucash.api.generated.GncCountData;
import org.gnucash.api.generated.GncGncBillTerm;
import org.gnucash.api.generated.GncGncCustomer;
import org.gnucash.api.generated.GncGncEmployee;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.GncGncTaxTable;
import org.gnucash.api.generated.GncGncVendor;
import org.gnucash.api.generated.GncPricedb;
import org.gnucash.api.generated.GncSchedxaction;
import org.gnucash.api.generated.GncTemplateTransactions;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Price;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.GnuCashCommodityImpl;
import org.gnucash.api.read.impl.GnuCashCustomerImpl;
import org.gnucash.api.read.impl.GnuCashEmployeeImpl;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.gnucash.api.read.impl.GnuCashPriceImpl;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.read.impl.GnuCashTransactionSplitImpl;
import org.gnucash.api.read.impl.GnuCashVendorImpl;
import org.gnucash.api.read.impl.aux.GCshBillTermsImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.read.impl.aux.GCshTaxTableImpl;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.read.impl.spec.GnuCashCustomerJobImpl;
import org.gnucash.api.read.impl.spec.GnuCashVendorJobImpl;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.GnuCashWritableCommodity;
import org.gnucash.api.write.GnuCashWritableCustomer;
import org.gnucash.api.write.GnuCashWritableEmployee;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.GnuCashWritableGenerJob;
import org.gnucash.api.write.GnuCashWritablePrice;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.GnuCashWritableVendor;
import org.gnucash.api.write.ObjectCascadeException;
import org.gnucash.api.write.aux.GCshWritableBillTerms;
import org.gnucash.api.write.aux.GCshWritableTaxTable;
import org.gnucash.api.write.impl.aux.GCshWritableBillTermsImpl;
import org.gnucash.api.write.impl.aux.GCshWritableTaxTableImpl;
import org.gnucash.api.write.impl.hlp.BookElementsSorter;
import org.gnucash.api.write.impl.hlp.FilePriceManager;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.api.write.impl.hlp.NamespaceAdderWriter;
import org.gnucash.api.write.impl.hlp.WritingContentHandler;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerInvoiceEntryImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerJobImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableEmployeeVoucherEntryImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableJobInvoiceEntryImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableJobInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorBillEntryImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorBillImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorJobImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoiceEntry;
import org.gnucash.api.write.spec.GnuCashWritableCustomerJob;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucherEntry;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoiceEntry;
import org.gnucash.api.write.spec.GnuCashWritableVendorBill;
import org.gnucash.api.write.spec.GnuCashWritableVendorBillEntry;
import org.gnucash.api.write.spec.GnuCashWritableVendorJob;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshCustID;
import org.gnucash.base.basetypes.simple.GCshEmplID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.GCshIDNotSetException;
import org.gnucash.base.basetypes.simple.GCshPrcID;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.gnucash.base.basetypes.simple.GCshVendID;
import org.gnucash.base.basetypes.simple.aux.GCshBllTrmID;
import org.gnucash.base.basetypes.simple.aux.GCshTaxTabID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Extension of GnuCashFileImpl to allow read-write access instead of read-only
 * access.
 */
public class GnuCashWritableFileImpl extends GnuCashFileImpl 
                                     implements GnuCashWritableFile 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableFileImpl.class);

	// ::MAGIC
	private static final String CODEPAGE = "UTF-8";

	// ---------------------------------------------------------------

	// true if this file has been modified.
	private boolean modified = false;

	private long lastWriteTime = 0;

	// ---------------------------------------------------------------

	/**
	 * @param file the file to load
	 * @throws IOException                   on bsic io-problems such as a
	 *                                       FileNotFoundException
	 */
	public GnuCashWritableFileImpl(final File file) throws IOException {
		super(file);
		setModified(false);

    	// CAUTION: The order matters
		acctMgr = new org.gnucash.api.write.impl.hlp.FileAccountManager(this);
		trxMgr = new org.gnucash.api.write.impl.hlp.FileTransactionManager(this);

		invcMgr = new org.gnucash.api.write.impl.hlp.FileInvoiceManager(this);
		invcEntrMgr = new org.gnucash.api.write.impl.hlp.FileInvoiceEntryManager(this);

		custMgr = new org.gnucash.api.write.impl.hlp.FileCustomerManager(this);
		vendMgr = new org.gnucash.api.write.impl.hlp.FileVendorManager(this);
		emplMgr = new org.gnucash.api.write.impl.hlp.FileEmployeeManager(this);
		jobMgr = new org.gnucash.api.write.impl.hlp.FileJobManager(this);

		cmdtyMgr = new org.gnucash.api.write.impl.hlp.FileCommodityManager(this);
		prcMgr = new org.gnucash.api.write.impl.hlp.FilePriceManager(this);
	}

	public GnuCashWritableFileImpl(final InputStream is) throws IOException {
		super(is);

    	// CAUTION: The order matters
		acctMgr = new org.gnucash.api.write.impl.hlp.FileAccountManager(this);
		trxMgr = new org.gnucash.api.write.impl.hlp.FileTransactionManager(this);

		invcMgr = new org.gnucash.api.write.impl.hlp.FileInvoiceManager(this);
		invcEntrMgr = new org.gnucash.api.write.impl.hlp.FileInvoiceEntryManager(this);

		custMgr = new org.gnucash.api.write.impl.hlp.FileCustomerManager(this);
		vendMgr = new org.gnucash.api.write.impl.hlp.FileVendorManager(this);
		emplMgr = new org.gnucash.api.write.impl.hlp.FileEmployeeManager(this);
		jobMgr = new org.gnucash.api.write.impl.hlp.FileJobManager(this);

		cmdtyMgr = new org.gnucash.api.write.impl.hlp.FileCommodityManager(this);
		prcMgr = new org.gnucash.api.write.impl.hlp.FilePriceManager(this);
	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GnuCashWritableFile getWritableGnuCashFile() {
		return this;
	}
	
	// ---------------------------------------------------------------

	@Override
	public void addUserDefinedAttribute(final String type, final String aName, final String aValue) {
		if ( getRootElement().getGncBook().getBookSlots() == null ) {
			ObjectFactory fact = getObjectFactory();
			SlotsType newSlotsType = fact.createSlotsType();
			getRootElement().getGncBook().setBookSlots(newSlotsType);
		}
		
		HasWritableUserDefinedAttributesImpl
			.addUserDefinedAttributeCore(getRootElement().getGncBook().getBookSlots(), 
										 getWritableGnuCashFile(), 
										 type, aName, aValue);
	}

	@Override
	public void removeUserDefinedAttribute(final String aName) {
		if ( getRootElement().getGncBook().getBookSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.removeUserDefinedAttributeCore(getRootElement().getGncBook().getBookSlots(), 
										 	getWritableGnuCashFile(), 
										 	aName);
	}

	@Override
	public void setUserDefinedAttribute(final String aName, final String aValue) {
		if ( getRootElement().getGncBook().getBookSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(getRootElement().getGncBook().getBookSlots(), 
										 getWritableGnuCashFile(), 
										 aName, aValue);
	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	/**
	 * @param pModified true if this file has been modified false after save, load
	 *                  or undo of changes
	 */
	@Override
	public void setModified(final boolean pModified) {
		// boolean old = this.modified;
		modified = pModified;
		// if (propertyChange != null)
		// propertyChange.firePropertyChange("modified", old, pModified);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isModified() {
		return modified;
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadFile(final File pFile) throws IOException {
		super.loadFile(pFile);
		lastWriteTime = Math.max(pFile.lastModified(), System.currentTimeMillis());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeFile(final File file) throws IOException {
		writeFile(file, CompressMode.GUESS_FROM_FILENAME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeFile(final File file, CompressMode compMode) throws IOException {
		if ( file == null ) {
			throw new IllegalArgumentException("argument <file> is null");
		}

		if ( file.exists() ) {
			throw new IllegalArgumentException("File '" + file.getAbsolutePath() + "' already exists");
		}

		checkAllCountData();
		clean();

		setFile(file);

		OutputStream out = new FileOutputStream(file);
		out = new BufferedOutputStream(out);
		if ( compMode == CompressMode.COMPRESS ) {
			out = new GZIPOutputStream(out);
		} else if ( compMode == CompressMode.GUESS_FROM_FILENAME ) {
			if ( file.getName().endsWith(FILE_EXT_ZIPPED_1) ||
				 file.getName().endsWith(FILE_EXT_ZIPPED_2) ) {
				out = new GZIPOutputStream(out);
			}
		}

		Writer writer = new NamespaceAdderWriter(new OutputStreamWriter(out, CODEPAGE));
		try {
			JAXBContext context = getJAXBContext();
			Marshaller marsh = context.createMarshaller();

			// marsh.marshal(getRootElement(), writer);
			// marsh.marshal(getRootElement(), new PrintWriter( System.out ) );
			marsh.marshal(getRootElement(), new WritingContentHandler(writer));

			setModified(false);
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			writer.close();
		}

		out.close();

		lastWriteTime = Math.max(file.lastModified(), System.currentTimeMillis());
	}

	/**
	 * @return the time in ms (compatible with File.lastModified) of the last
	 *         write-operation
	 */
	@Override
	public long getLastWriteTime() {
		return lastWriteTime;
	}

	// ---------------------------------------------------------------

	/**
	 * Keep the count-data up to date.
	 *
	 * @param type  the type to set it for
	 * @param val the value
	 */
	protected void setCountDataFor(final String type, final int val) {
		if ( type == null ) {
			throw new IllegalArgumentException("argument <type> is null");
		}
	
		if ( type.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <type> is empty");
		}

		if ( val < 0 ) {
			throw new IllegalArgumentException("argument <val> is < 0");
		}
	
		List<GncCountData> cdList = getRootElement().getGncBook().getGncCountData();
		for ( GncCountData gncCountData : cdList ) {
			if ( type.equals(gncCountData.getCdType()) ) {
				gncCountData.setValue(val);
				setModified(true);
			}
		}
	}

	/**
	 * Keep the count-data up to date. The count-data is re-calculated on the fly
	 * before writing but we like to keep our internal model up-to-date just to be
	 * defensive. <gnc:count-data cd:type="commodity">2</gnc:count-data>
	 * <gnc:count-data cd:type="account">394</gnc:count-data> ... (etc.)
	 *
	 * @param type the type to set it for
	 */
	protected void incrementCountDataFor(final String type) {
		if ( type == null ) {
			throw new IllegalArgumentException("argument <type> is null");
		}

		if ( type.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <type> is empty");
		}
		
		incrementCountDataForCore(type, 1);
	}

	/**
	 * Keep the count-data up to date. The count-data is re-calculated on the fly
	 * before writing but we like to keep our internal model up-to-date just to be
	 * defensive.
	 *
	 * @param type the type to set it for
	 */
	protected void decrementCountDataFor(final String type) {
		if ( type == null ) {
			throw new IllegalArgumentException("argument <type> is null");
		}

		if ( type.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <type> is empty");
		}
		
		incrementCountDataForCore(type, -1);
	}

	private void incrementCountDataForCore(final String type, final int val) {
		if ( type == null ) {
			throw new IllegalArgumentException("argument <type> is null");
		}

		if ( type.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <type> is empty");
		}
		
		for ( GncCountData gncCountData : getRootElement().getGncBook().getGncCountData() ) {
			if ( type.equals(gncCountData.getCdType()) ) {
				gncCountData.setValue(gncCountData.getValue() + val);
				setModified(true);
				return;
			}
		}

		throw new IllegalArgumentException("Unknown type '" + type + "'");
	}

	/**
	 * Calculate and set the correct values for all the following count-data.<br/>
	 * Also check the that only valid elements are in the book-element and that they
	 * have the correct order.
	 */
	private void checkAllCountData() {
		int cntAccount = 0;
		int cntTransaction = 0;
		int cntInvoice = 0;
		int cntIncEntry = 0;
		int cntCustomer = 0;
		int cntVendor = 0;
		int cntEmployee = 0;
		int cntJob = 0;
		int cntTaxTable = 0;
		int cntBillTerm = 0;
		int cntCommodity = 0;
		int cntPrice = 0;

		/**
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link GncTemplateTransactions} {@link GncGncInvoice} {@link GncGncEntry}
		 * {@link GncGncJob} {@link GncGncTaxTable} {@link GncCommodity}
		 * {@link GncGncCustomer} {@link GncSchedxaction} {@link GncBudget}
		 * {@link GncAccount} {@link GncPricedb} {@link GncTransaction}
		 */
		List<Object> bookElements = getRootElement().getGncBook().getBookElements();
		for ( Object element : bookElements ) {
			if ( element instanceof GncAccount ) {
				cntAccount++;
			} else if ( element instanceof GncTransaction ) {
				cntTransaction++;
			} else if ( element instanceof GncGncInvoice ) {
				cntInvoice++;
			} else if ( element instanceof GncGncEntry ) {
				cntIncEntry++;
			} else if ( element instanceof GncGncCustomer ) {
				cntCustomer++;
			} else if ( element instanceof GncGncVendor ) {
				cntVendor++;
			} else if ( element instanceof GncGncEmployee ) {
				cntEmployee++;
			} else if ( element instanceof GncGncJob ) {
				cntJob++;
			} else if ( element instanceof GncGncTaxTable ) {
				cntTaxTable++;
			} else if ( element instanceof GncGncBillTerm ) {
				cntBillTerm++;
			} else if ( element instanceof GncCommodity ) {
				cntCommodity++;
			} else if ( element instanceof GncPricedb ) {
				cntPrice += ((GncPricedb) element).getPrice().size();
			} else if ( element instanceof GncTemplateTransactions ) {
				// ::TODO
			} else if ( element instanceof GncSchedxaction ) {
				// ::TODO
			} else if ( element instanceof GncBudget ) {
				// ::TODO
			} else {
				throw new IllegalStateException("Found unexpected element in GNC:Book: '" + element.toString() + "'");
			}
		}
		
		// Special case commoditiy-counter: 
		// The template entry is not accounted for. ( => minus 1 )
		// In addition to that, there seems to be a bug in GnuCash:
		// the counter is always set to even one less when saved
		// with the original software.
		// (If there is a reason for that, I do not understaand it; seems weird to me.)
		// Anyway: In order to maintain consistency, we need to mimick this 
		// bug (?) here. ( => again minus 1 ).
		cntCommodity += -2;

		setCountDataFor("account", cntAccount);
		setCountDataFor("transaction", cntTransaction);
		setCountDataFor("gnc:GncInvoice", cntInvoice);
		setCountDataFor("gnc:GncEntry", cntIncEntry);
		setCountDataFor("gnc:GncCustomer", cntCustomer);
		setCountDataFor("gnc:GncVendor", cntVendor);
		setCountDataFor("gnc:GncEmployee", cntEmployee);
		setCountDataFor("gnc:GncJob", cntJob);
		setCountDataFor("gnc:GncTaxTable", cntTaxTable);
		setCountDataFor("gnc:GncBillTerm", cntBillTerm);
		setCountDataFor("commodity", cntCommodity);
		setCountDataFor("price", cntPrice);

		// Make sure the correct sort-order of the entity-types is honored
		// (we do not enforce this in the XML schema to allow for reading files
		// that do not honor that order).
		java.util.Collections.sort(bookElements, new BookElementsSorter());
	}

	// ---------------------------------------------------------------

	/**
	 * @return the underlying JAXB-element
	 * @see GnuCashWritableFile#getRootElement()
	 */
	@SuppressWarnings("exports")
	@Override
	public GncV2 getRootElement() {
		return super.getRootElement();
	}

	/**
	 * @see #getRootElement()
	 */
	@Override
	protected void setRootElement(final GncV2 rootElement) {
		super.setRootElement(rootElement);
	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	/**
	 * @param type the type to look for
	 * @return A modifiable version of all accounts of that type.
	 * 
	 * @see #getAccountsByTypeAndName(org.gnucash.api.read.GnuCashAccount.Type, String, boolean, boolean)
	 */
	@Override
	public Collection<GnuCashWritableAccount> getWritableAccountsByType(final GnuCashAccount.Type type) {
		Collection<GnuCashWritableAccount> retval = new ArrayList<GnuCashWritableAccount>();
		
		for ( GnuCashWritableAccount acct : getWritableAccounts() ) {

			if ( acct.getType() == null ) {
				if ( type == null ) {
					retval.add(acct);
				}
			} else if ( acct.getType() == type ) {
				retval.add(acct);
			}

		}
		
		return retval;
	}

	/**
	 * @param acctID the unique account-id
	 * @return A modifiable version of the account or null if not found.
	 * 
	 * @see #getAccountByID(GCshID)
	 */
	@Override
	public GnuCashWritableAccount getWritableAccountByID(final GCshAcctID acctID) {
		if ( acctID == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}

		if ( !acctID.isSet() ) {
			throw new IllegalArgumentException("argument <name> is not set");
		}

		try {
			GnuCashAccount acct = super.getAccountByID(acctID);
			return new GnuCashWritableAccountImpl((GnuCashAccountImpl) acct, true);
		} catch (Exception exc) {
			LOGGER.error(
					"getWritableAccountByID: Could not instantiate writable account object from read-only account object (ID: "
							+ acctID + ")");
			throw new RuntimeException(
					"Could not instantiate writable account object from read-only account object (ID: " + acctID + ")");
		}
	}

	/**
	 * @param name the name of the account
	 * @return A modifiable version of the first account with that name.
	 * 
	 * @see #getAccountByNameUniq(String, boolean)
	 */
	@Override
	public GnuCashWritableAccount getWritableAccountByNameUniq(final String name, final boolean qualif)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		return (GnuCashWritableAccount) super.getAccountByNameUniq(name, qualif);
	}

	/**
	 * @return a read-write collection of all accounts
	 * 
	 * @see #getAccounts()
	 */
	@Override
	public Collection<GnuCashWritableAccount> getWritableAccounts() {
		TreeSet<GnuCashWritableAccount> retval = new TreeSet<GnuCashWritableAccount>();

		for ( GnuCashAccount acct : getAccounts() ) {
			retval.add((GnuCashWritableAccount) acct);
		}

		return retval;
	}

	/**
	 * @return a read-only collection of all accounts that have no parent
	 * 
	 * @see #getWritableParentlessAccounts()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<? extends GnuCashWritableAccount> getWritableParentlessAccounts() {
		return (Collection<? extends GnuCashWritableAccount>) getParentlessAccounts();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gnucash.write.jwsdpimpl.GnuCashFileImpl#getRootAccounts()
	 */
	@Override
	public List<? extends GnuCashAccount> getParentlessAccounts() {
		// TODO Auto-generated method stub
		List<? extends GnuCashAccount> rootAcctList = super.getParentlessAccounts();
		
		if ( rootAcctList.size() > 1 ) {
			GnuCashAccount root = null;
			StringBuilder roots = new StringBuilder();
			for ( GnuCashAccount gcshAcct : rootAcctList ) {
				if ( gcshAcct == null ) {
					continue;
				}
				if ( gcshAcct.getType() != null && 
					 gcshAcct.getType() == GnuCashAccount.Type.ROOT ) {
					root = gcshAcct;
					continue;
				}
				roots.append(gcshAcct.getID()).append("=\"").append(gcshAcct.getName()).append("\" ");
			}
			LOGGER.warn("getParentlessAccounts: File has more than one root-account! Attaching excess accounts to root-account: "
							+ roots.toString());
			ArrayList<GnuCashAccount> rootAccounts2 = new ArrayList<GnuCashAccount>();
			rootAccounts2.add(root);
			for ( GnuCashAccount acct : rootAcctList ) {
				if ( acct == null ) {
					continue;
				}
				if ( acct == root ) {
					continue;
				}
				((GnuCashWritableAccount) acct).setParentAccount(root);

			}
			rootAcctList = rootAccounts2;
		}
		
		return rootAcctList;
	}

	// ----------------------------

	@Override
	@Deprecated
	public GnuCashWritableAccount createWritableAccount() {
		GnuCashWritableAccount acct = new GnuCashWritableAccountImpl(this);
		((org.gnucash.api.write.impl.hlp.FileAccountManager) super.acctMgr)
			.addAccount(acct);
		
		return acct;
	}

	@Override
	public GnuCashWritableAccount createWritableAccount(GnuCashAccount.Type type,
														GCshCmdtyCurrID cmdtyCurrID,
														GCshAcctID parentID,
														String name) {
		GnuCashWritableAccount acct = new GnuCashWritableAccountImpl(this);
		((org.gnucash.api.write.impl.hlp.FileAccountManager) super.acctMgr)
			.addAccount(acct);
		
		acct.setType(type);
		acct.setCmdtyCurrID(cmdtyCurrID);
		acct.setParentAccountID(parentID);
		acct.setName(name);
		
		return acct;
	}

	@Override
    public GnuCashWritableAccount createWritableAccount(GnuCashAccount.Type type,
    													GCshCmdtyID cmdtyID,
    													GCshAcctID parentID,
    													String name) {
		return createWritableAccount(type, (GCshCmdtyCurrID) cmdtyID, parentID, name);
	}

	@Override
	public GnuCashWritableAccount createWritableAccount(GnuCashAccount.Type type,
														GCshCurrID currID,
														GCshAcctID parentID,
														String name) {
		return createWritableAccount(type, (GCshCmdtyCurrID) currID, parentID, name);
	}

	/**
	 * @param acct what to remove
	 */
	@Override
	public void removeAccount(final GnuCashWritableAccount acct) {
		if ( acct.hasTransactions() ) {
			throw new IllegalStateException("cannot remove account while it contains transaction-splits");
		}

		// 1) Remove avatar in account manager
		((org.gnucash.api.write.impl.hlp.FileAccountManager) super.acctMgr)
			.removeAccount(acct);
		
		// 2) Remove lots, if any
		((GnuCashWritableAccountImpl) acct).removeLots();

		// 3) remove account
		getRootElement().getGncBook().getBookElements().remove(((GnuCashWritableAccountImpl) acct).getJwsdpPeer());
		setModified(true);
	}

	// ---------------------------------------------------------------

	/**
	 * @see #getTransactionByID(GCshID)
	 */
	@Override
	public GnuCashWritableTransaction getWritableTransactionByID(final GCshTrxID trxID) {
		if ( trxID == null ) {
			throw new IllegalArgumentException("argument <trxID> is null");
		}

		if ( !trxID.isSet() ) {
			throw new IllegalArgumentException("argument <trxID> is null");
		}

		try {
			return new GnuCashWritableTransactionImpl(super.getTransactionByID(trxID));
		} catch (Exception exc) {
			LOGGER.error("getWritableTransactionByID: Could not instantiate writable transaction object from read-only transaction object (ID: " + trxID + ")");
			throw new RuntimeException("Could not instantiate writable transaction object from read-only transaction object (ID: " + trxID + ")");
		}
	}

	/**
	 * @see #getTransactions()
	 */
	@Override
	public Collection<? extends GnuCashWritableTransaction> getWritableTransactions() {
		Collection<GnuCashWritableTransaction> result = new ArrayList<GnuCashWritableTransaction>();

		for ( GnuCashTransaction trx : super.getTransactions() ) {
			GnuCashWritableTransaction newTrx = new GnuCashWritableTransactionImpl(
					(GnuCashWritableTransactionImpl) trx);
			result.add(newTrx);
		}

		return result;
	}

	// ----------------------------

	@Override
	public GnuCashWritableTransaction createWritableTransaction() {
		return new GnuCashWritableTransactionImpl(this);
	}

	/**
	 * Used by GnuCashTransactionImpl.createTransaction to add a new Transaction to
	 * this file.
	 * 
	 * @see GnuCashTransactionImpl#createSplit(GncTransaction.TrnSplits.TrnSplit)
	 */
	protected void addTransaction(final GnuCashTransactionImpl trx) {
		getRootElement().getGncBook().getBookElements().add(trx.getJwsdpPeer());
		setModified(true);
		((org.gnucash.api.write.impl.hlp.FileTransactionManager) super.trxMgr)
			.addTransaction(trx);
	}

	/**
	 * @param trx what to remove
	 * 
	 */
	@Override
	public void removeTransaction(final GnuCashWritableTransaction trx) {
		Collection<GnuCashWritableTransactionSplit> spltList = new ArrayList<GnuCashWritableTransactionSplit>();
		
		spltList.addAll(trx.getWritableSplits());
		for ( GnuCashWritableTransactionSplit splt : spltList ) {
			splt.remove();
		}

		getRootElement().getGncBook().getBookElements().remove(((GnuCashWritableTransactionImpl) trx).getJwsdpPeer());
		setModified(true);
		((org.gnucash.api.write.impl.hlp.FileTransactionManager) super.trxMgr)
			.removeTransaction(trx);		
	}

	/**
	 * @param splt what to remove
	 * 
	 */
	// By purpose, this method has not been defined in the interface
	// @Override
	public void removeTransactionSplit(final GnuCashWritableTransactionSplit splt) {
		// 1) remove avatar in transaction manager
		((org.gnucash.api.write.impl.hlp.FileTransactionManager) super.trxMgr)
			.removeTransactionSplit(splt, false);
		
		// 2) remove transaction split
		GCshID trxID = splt.getTransactionID();
		String trxIDStr = null;
		try {
			trxIDStr = trxID.get();
		} catch (GCshIDNotSetException e) {
			throw new IllegalStateException("Transaction-split " + splt + " does not seem to have a correct transaction (ID)");
		}
		
		// Does not work like that, as splits are embedded in transactions:
		// getRootElement().getGncBook().getBookElements().remove(((GnuCashWritableTransactionSplitImpl) splt).getJwsdpPeer());
		// Instead:
		((org.gnucash.api.write.impl.hlp.FileTransactionManager) trxMgr)
			.removeTransactionSplit_raw(splt.getTransactionID(), splt.getID());
		
		// 3) remove transaction, if no splits left
		// ::TODO / ::CHECK
		// uncomment?
		// cf. according code in removePrice()
//		for ( TRANSACTION jwsdpTrx : getRootElement().getTRANSACTIONS().getTRANSACTION() ) {
//			if ( jwsdpTrx.getId().equals(trx.getID().get()) ) {
//				if ( jwsdpTrx.getSPLITS().size() == 0 ) {
//					// CAUTION concurrency ::CHECK
//					getRootElement().getTRANSACTIONS().getTRANSACTION().remove(jwsdpTrx);
//					break;
//				}
//			}
//		}
		
		// 4) set 'modified' flag
		setModified(true);
	}

	// ---------------------------------------------------------------

	/**
	 * @param spltID
	 * @return
	 * 
	 * @see #getTransactionSplitByID(GCshID)
	 */
	@Override
	public GnuCashWritableTransactionSplit getWritableTransactionSplitByID(final GCshSpltID spltID) {
		if ( spltID == null ) {
			throw new IllegalArgumentException("argument <spltID> is null");
		}

		if ( !spltID.isSet() ) {
			throw new IllegalArgumentException("argument <spltID> is null");
		}

		GnuCashTransactionSplit splt = super.getTransactionSplitByID(spltID);
		// ::TODO
		// !!! Diese nicht-triviale Änderung nochmal ganz genau abtesten !!!
		return new GnuCashWritableTransactionSplitImpl((GnuCashTransactionSplitImpl) splt, false, false);
	}

	/**
	 * @return
	 * 
	 * @see #getTransactionSplits()
	 */
	@Override
	public Collection<GnuCashWritableTransactionSplit> getWritableTransactionSplits() {
		Collection<GnuCashWritableTransactionSplit> result = new ArrayList<GnuCashWritableTransactionSplit>();

		for ( GnuCashTransactionSplit trx : super.getTransactionSplits() ) {
			GnuCashWritableTransactionSplit newTrx = new GnuCashWritableTransactionSplitImpl(
					(GnuCashWritableTransactionSplitImpl) trx);
			result.add(newTrx);
		}

		return result;
	}

	// ---------------------------------------------------------------

	/**
	 * @param invcID the unique invoice-id
	 * @return A modifiable version of the Invoice or null if not found.
	 * @see GnuCashFile#getGenerInvoiceByID(GCshGenerInvcID)
	 */
	@Override
	public GnuCashWritableGenerInvoice getWritableGenerInvoiceByID(final GCshGenerInvcID invcID) {
		if ( invcID == null ) {
			throw new IllegalArgumentException("argument <invcID> is null");
		}

		if ( !invcID.isSet() ) {
			throw new IllegalArgumentException("argument <invcID> is null");
		}

		GnuCashGenerInvoice invc = super.getGenerInvoiceByID(invcID);
		return new GnuCashWritableGenerInvoiceImpl((GnuCashGenerInvoiceImpl) invc, true, true);
	}

	/**
	 * @see GnuCashWritableFile#getWritableGenerJobs()
	 */
	@Override
	public Collection<GnuCashWritableGenerInvoice> getWritableGenerInvoices() {
		Collection<GnuCashGenerInvoice> invcList = getGenerInvoices();

		if ( invcList == null ) {
			throw new IllegalStateException("getGenerInvoices() returned null");
		}

		Collection<GnuCashWritableGenerInvoice> retval = new ArrayList<GnuCashWritableGenerInvoice>();
		for ( GnuCashGenerInvoice invc : invcList ) {
			retval.add((GnuCashWritableGenerInvoice) invc);
		}

		return retval;
	}

	// ----------------------------

	/**
	 * FOR USE BY EXTENSIONS ONLY!
	 * 
	 * @throws WrongOwnerTypeException
	 * @throws IllegalTransactionSplitActionException
	 * @see GnuCashWritableFile#createWritableTransaction()
	 */
	@Override
	public GnuCashWritableCustomerInvoice createWritableCustomerInvoice(final String number, final GnuCashCustomer cust,
			final GnuCashAccount incomeAcct, final GnuCashAccount receivableAcct, final LocalDate openedDate,
			final LocalDate postDate, final LocalDate dueDate)
			throws WrongOwnerTypeException,
			IllegalTransactionSplitActionException {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}

		if ( incomeAcct == null ) {
			throw new IllegalArgumentException("argument <incomeAcct> is null");
		}

		if ( receivableAcct == null ) {
			throw new IllegalArgumentException("argument <receivableAcct> is null");
		}

		GnuCashWritableCustomerInvoice retval = 
				new GnuCashWritableCustomerInvoiceImpl(this,
													   number, cust,
													   (GnuCashAccountImpl) incomeAcct, 
													   (GnuCashAccountImpl) receivableAcct, 
													   openedDate, postDate, dueDate);

		((org.gnucash.api.write.impl.hlp.FileInvoiceManager) super.invcMgr)
			.addGenerInvoice(retval);
		
		return retval;
	}

	/**
	 * FOR USE BY EXTENSIONS ONLY!
	 * 
	 * @throws WrongOwnerTypeException
	 * @throws IllegalTransactionSplitActionException
	 * @see GnuCashWritableFile#createWritableTransaction()
	 */
	@Override
	public GnuCashWritableVendorBill createWritableVendorBill(final String number, final GnuCashVendor vend,
			final GnuCashAccount expensesAcct, final GnuCashAccount payableAcct, final LocalDate openedDate,
			final LocalDate postDate, final LocalDate dueDate)
			throws WrongOwnerTypeException,
			IllegalTransactionSplitActionException {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}

		if ( expensesAcct == null ) {
			throw new IllegalArgumentException("argument <expensesAcct> is null");
		}

		if ( payableAcct == null ) {
			throw new IllegalArgumentException("argument <payableAcct> is null");
		}

		GnuCashWritableVendorBill retval = 
				new GnuCashWritableVendorBillImpl(this,
												  number, vend,
												  (GnuCashAccountImpl) expensesAcct, (GnuCashAccountImpl) payableAcct, 
												  openedDate, postDate, dueDate);

		((org.gnucash.api.write.impl.hlp.FileInvoiceManager) super.invcMgr)
			.addGenerInvoice(retval);
		
		return retval;
	}

	/**
	 * FOR USE BY EXTENSIONS ONLY!
	 * 
	 * @throws WrongOwnerTypeException
	 * @throws IllegalTransactionSplitActionException
	 * @see GnuCashWritableFile#createWritableTransaction()
	 */
	@Override
	public GnuCashWritableEmployeeVoucher createWritableEmployeeVoucher(final String number, final GnuCashEmployee empl,
			final GnuCashAccount expensesAcct, final GnuCashAccount payableAcct, final LocalDate openedDate,
			final LocalDate postDate, final LocalDate dueDate)
			throws WrongOwnerTypeException,
			IllegalTransactionSplitActionException {
		if ( empl == null ) {
			throw new IllegalArgumentException("argument <empl> is null");
		}

		if ( expensesAcct == null ) {
			throw new IllegalArgumentException("argument <expensesAcct> is null");
		}

		if ( payableAcct == null ) {
			throw new IllegalArgumentException("argument <payableAcct> is null");
		}

		GnuCashWritableEmployeeVoucher retval = 
				new GnuCashWritableEmployeeVoucherImpl(this,
													   number, empl,
													   (GnuCashAccountImpl) expensesAcct, (GnuCashAccountImpl) payableAcct, 
													   openedDate, postDate, dueDate);

		((org.gnucash.api.write.impl.hlp.FileInvoiceManager) super.invcMgr)
			.addGenerInvoice(retval);
		
		return retval;
	}

	/**
	 * FOR USE BY EXTENSIONS ONLY!
	 * 
	 * @throws WrongOwnerTypeException
	 * @throws IllegalTransactionSplitActionException
	 * @see GnuCashWritableFile#createWritableTransaction()
	 */
	@Override
	public GnuCashWritableJobInvoice createWritableJobInvoice(final String number, final GnuCashGenerJob job,
			final GnuCashAccount incExpAcct, final GnuCashAccount recvblPayblAcct, final LocalDate openedDate,
			final LocalDate postDate, final LocalDate dueDate)
			throws WrongOwnerTypeException,
			IllegalTransactionSplitActionException {
		if ( job == null ) {
			throw new IllegalArgumentException("argument <job> is null");
		}

		if ( incExpAcct == null ) {
			throw new IllegalArgumentException("argument <incExpAcct> is null");
		}

		if ( recvblPayblAcct == null ) {
			throw new IllegalArgumentException("argument <recvblPayblAcct> is null");
		}

		GnuCashWritableJobInvoice retval = new GnuCashWritableJobInvoiceImpl(this, number, job,
				(GnuCashAccountImpl) incExpAcct, (GnuCashAccountImpl) recvblPayblAcct, openedDate, postDate, dueDate);

		((org.gnucash.api.write.impl.hlp.FileInvoiceManager) super.invcMgr)
			.addGenerInvoice(retval);
		
		return retval;
	}

	/**
	 * @param invc an invoice to remove
	 */
	@Override
	public void removeGenerInvoice(final GnuCashWritableGenerInvoice invc, boolean withEntries) {
		if ( invc.getPayingTransactions().size() > 0 ) {
			throw new IllegalStateException("cannot remove this invoice! It has payments");
		}

		GCshID invcID = invc.getID();

		if ( withEntries ) {
			if ( invc.getGenerEntries().size() > 0 ) {
				 for ( GnuCashWritableGenerInvoiceEntry entr : invc.getWritableGenerEntries() ) {
					 removeGenerInvoiceEntry(entr);
				 }
			}
		} else {
			if ( invc.getGenerEntries().size() > 0 ) {
				throw new IllegalStateException("cannot remove this invoice! It still has entries, and you did not choose the option to delete those as well");
			}
		}

		GnuCashTransaction postTransaction = invc.getPostTransaction();
		if ( postTransaction != null ) {
			((GnuCashWritableTransaction) postTransaction).remove();
		}
		
		((org.gnucash.api.write.impl.hlp.FileInvoiceManager) super.invcMgr)
			.removeGenerInvoice(invc);
		getRootElement().getGncBook().getBookElements().remove(((GnuCashWritableGenerInvoiceImpl) invc).getJwsdpPeer());
		// this.decrementCountDataFor("gnc:GncInvoice");
		setModified(true);
		
		LOGGER.info("Deleted invoice with ID " + invcID);
	}

	@Override
	public void removeCustomerInvoice(GnuCashWritableCustomerInvoice invc, boolean withEntries) {
		removeGenerInvoice(invc, withEntries);
	}

	@Override
	public void removeVendorBill(GnuCashWritableVendorBill bll, boolean withEntries) {
		removeGenerInvoice(bll, withEntries);
	}

	@Override
	public void removeEmployeeVoucher(GnuCashWritableEmployeeVoucher vch, boolean withEntries) {
		removeGenerInvoice(vch, withEntries);
	}

	@Override
	public void removeJobInvoice(GnuCashWritableJobInvoice invc, boolean withEntries) {
		removeGenerInvoice(invc, withEntries);
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashWritableGenerInvoiceEntry getWritableGenerInvoiceEntryByID(final GCshGenerInvcEntrID invcEntrID) {
		if ( invcEntrID == null ) {
			throw new IllegalArgumentException("argument <invcEntrID> is null");
		}

		if ( !invcEntrID.isSet() ) {
			throw new IllegalArgumentException("argument <invcEntrID> is not set");
		}

		GnuCashGenerInvoiceEntry invcEntr = super.getGenerInvoiceEntryByID(invcEntrID);
		return new GnuCashWritableGenerInvoiceEntryImpl(invcEntr);
	}

	@Override
	public Collection<GnuCashWritableGenerInvoiceEntry> getWritableGenerInvoiceEntries() {
		Collection<GnuCashGenerInvoiceEntry> invcEntrList = getGenerInvoiceEntries();

		if ( invcEntrList == null ) {
			throw new IllegalStateException("getGenerInvoiceEntries() returned null");
		}

		Collection<GnuCashWritableGenerInvoiceEntry> retval = new ArrayList<GnuCashWritableGenerInvoiceEntry>();
		for ( GnuCashGenerInvoiceEntry entry : invcEntrList ) {
			retval.add((GnuCashWritableGenerInvoiceEntry) entry);
		}

		return retval;
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashWritableCustomerInvoiceEntry createWritableCustomerInvoiceEntry(
			final GnuCashWritableCustomerInvoiceImpl invc,
			final GnuCashAccount acct, 
			final FixedPointNumber quant, 
			final FixedPointNumber prc) throws TaxTableNotFoundException {
		if ( invc == null ) {
			throw new IllegalArgumentException("argument <invc> is null");
		}

		if ( acct == null ) {
			throw new IllegalArgumentException("argument <acct> is null");
		}

		GnuCashWritableCustomerInvoiceEntry retval = 
				new GnuCashWritableCustomerInvoiceEntryImpl(invc,
															acct,
															quant, prc);

		((org.gnucash.api.write.impl.hlp.FileInvoiceEntryManager) super.invcEntrMgr)
			.addGenerInvcEntry(retval);
		
		return retval;
	}

	@Override
	public GnuCashWritableVendorBillEntry createWritableVendorBillEntry(
			final GnuCashWritableVendorBillImpl bll, 
			final GnuCashAccount acct,
			final FixedPointNumber quant, 
			final FixedPointNumber prc) throws TaxTableNotFoundException {
		if ( bll == null ) {
			throw new IllegalArgumentException("argument <bll> null");
		}

		if ( acct == null ) {
			throw new IllegalArgumentException("argument <acct> is null");
		}

		GnuCashWritableVendorBillEntry retval = 
				new GnuCashWritableVendorBillEntryImpl(bll,
													   acct,
													   quant, prc);

		((org.gnucash.api.write.impl.hlp.FileInvoiceEntryManager) super.invcEntrMgr)
			.addGenerInvcEntry(retval);
		
		return retval;
	}

	@Override
	public GnuCashWritableEmployeeVoucherEntry createWritableEmployeeVoucher(
			final GnuCashWritableEmployeeVoucherImpl vch,
			final GnuCashAccount acct, 
			final FixedPointNumber quant, 
			final FixedPointNumber prc) throws TaxTableNotFoundException {
		if ( vch == null ) {
			throw new IllegalArgumentException("argument <vch> is null");
		}

		if ( acct == null ) {
			throw new IllegalArgumentException("argument <acct> is null");
		}

		GnuCashWritableEmployeeVoucherEntry retval = 
				new GnuCashWritableEmployeeVoucherEntryImpl(vch,
															acct,
															quant, prc);

		((org.gnucash.api.write.impl.hlp.FileInvoiceEntryManager) super.invcEntrMgr)
			.addGenerInvcEntry(retval);
		
		return retval;
	}

	@Override
	public GnuCashWritableJobInvoiceEntry createWritableJobInvoice(
			final GnuCashWritableJobInvoiceImpl invc, 
			final GnuCashAccount acct,
			final FixedPointNumber quant, 
			final FixedPointNumber prc) throws TaxTableNotFoundException {
		if ( invc == null ) {
			throw new IllegalArgumentException("argument <invc> is null");
		}

		if ( acct == null ) {
			throw new IllegalArgumentException("argument <acct> is null");
		}

		GnuCashWritableJobInvoiceEntry retval = 
				new GnuCashWritableJobInvoiceEntryImpl(invc,
													   acct,
													   quant, prc);

		((org.gnucash.api.write.impl.hlp.FileInvoiceEntryManager) super.invcEntrMgr)
			.addGenerInvcEntry(retval);
		
		return retval;
	}

	@Override
	public void removeGenerInvoiceEntry(GnuCashWritableGenerInvoiceEntry entr) {
		if ( entr.getGenerInvoice().getPayingTransactions().size() > 0 ) {
			throw new IllegalStateException("cannot remove this (generic) invoice entry! It belongs to an invoice that has payments");
		}

		GnuCashTransaction postTransaction = entr.getGenerInvoice().getPostTransaction();
		if ( postTransaction != null ) {
			((GnuCashWritableTransaction) postTransaction).remove();
		}
		
		GCshID entrID = entr.getID();

		((org.gnucash.api.write.impl.hlp.FileInvoiceEntryManager) super.invcEntrMgr)
			.removeGenerInvcEntry(entr);
		getRootElement().getGncBook().getBookElements().remove(((GnuCashWritableGenerInvoiceEntryImpl) entr).getJwsdpPeer());
		// this.decrementCountDataFor("gnc:GncEntry");
		setModified(true);
		
		LOGGER.info("Deleted (generic) invoice entry with ID " + entrID);
	}

	@Override
	public void removeCustomerInvoiceEntry(GnuCashWritableCustomerInvoiceEntry entr) {
		removeGenerInvoiceEntry(entr);
	}

	@Override
	public void removeVendorBillEntry(GnuCashWritableVendorBillEntry entr) {
		removeGenerInvoiceEntry(entr);
	}

	@Override
	public void removeEmployeeVoucherEntry(GnuCashWritableEmployeeVoucherEntry entr) {
		removeGenerInvoiceEntry(entr);
	}

	@Override
	public void removeJobInvoiceEntry(GnuCashWritableJobInvoiceEntry entr) {
		removeGenerInvoiceEntry(entr);
	}

	// ---------------------------------------------------------------

	/**
	 * @see #getCustomerByID(GCshID)
	 */
	@Override
	public GnuCashWritableCustomer getWritableCustomerByID(final GCshCustID custID) {
		if ( custID == null ) {
			throw new IllegalArgumentException("argument <custID> is null");
		}

		if ( !custID.isSet() ) {
			throw new IllegalArgumentException("argument <custID> is not set");
		}

		GnuCashCustomer cust = super.getCustomerByID(custID);
		return new GnuCashWritableCustomerImpl((GnuCashCustomerImpl) cust);
	}

	/**
	 * @see #getCustomers()
	 */
	@Override
	public Collection<GnuCashWritableCustomer> getWritableCustomers() {
		Collection<GnuCashWritableCustomer> result = new ArrayList<GnuCashWritableCustomer>();

		for ( GnuCashCustomer cust : super.getCustomers() ) {
			GnuCashWritableCustomer newCust = new GnuCashWritableCustomerImpl((GnuCashWritableCustomerImpl) cust);
			result.add(newCust);
		}

		return result;
	}

	// ----------------------------

	@Override
	public GnuCashWritableCustomer createWritableCustomer(final String name) {
		GnuCashWritableCustomerImpl cust = new GnuCashWritableCustomerImpl(this);
		cust.setName(name);
		((org.gnucash.api.write.impl.hlp.FileCustomerManager) super.custMgr)
			.addCustomer(cust);
		return cust;
	}

	/**
	 * @param cust the customer to remove
	 */
	@Override
	public void removeCustomer(final GnuCashWritableCustomer cust) {
		((org.gnucash.api.write.impl.hlp.FileCustomerManager) super.custMgr)
			.removeCustomer(cust);
		getRootElement().getGncBook().getBookElements().remove(((GnuCashWritableCustomerImpl) cust).getJwsdpPeer());
		setModified(true);
	}

	// ---------------------------------------------------------------

	/**
	 * @see #getVendorByID(GCshID)
	 */
	@Override
	public GnuCashWritableVendor getWritableVendorByID(final GCshVendID vendID) {
		if ( vendID == null ) {
			throw new IllegalArgumentException("argument <vendID> is null");
		}

		if ( !vendID.isSet() ) {
			throw new IllegalArgumentException("argument <vendID> is not set");
		}

		GnuCashVendor vend = super.getVendorByID(vendID);
		return new GnuCashWritableVendorImpl((GnuCashVendorImpl) vend);
	}

	/**
	 * @see #getVendors()
	 */
	@Override
	public Collection<GnuCashWritableVendor> getWritableVendors() {
		Collection<GnuCashWritableVendor> result = new ArrayList<GnuCashWritableVendor>();

		for ( GnuCashVendor vend : super.getVendors() ) {
			GnuCashWritableVendor newVend = new GnuCashWritableVendorImpl((GnuCashWritableVendorImpl) vend);
			result.add(newVend);
		}

		return result;
	}

	// ----------------------------

	@Override
	public GnuCashWritableVendor createWritableVendor(final String name) {
		GnuCashWritableVendorImpl vend = new GnuCashWritableVendorImpl(this);
		vend.setName(name);
		((org.gnucash.api.write.impl.hlp.FileVendorManager) super.vendMgr)
			.addVendor(vend);
		
		return vend;
	}

	/**
	 * @param vend the vendor to remove
	 */
	@Override
	public void removeVendor(final GnuCashWritableVendor vend) {
		((org.gnucash.api.write.impl.hlp.FileVendorManager) super.vendMgr)
			.removeVendor(vend);
		getRootElement().getGncBook().getBookElements().remove(((GnuCashWritableVendorImpl) vend).getJwsdpPeer());
		setModified(true);
	}

	// ---------------------------------------------------------------

	/**
	 * @see #getEmployeeByID(GCshID)
	 */
	@Override
	public GnuCashWritableEmployee getWritableEmployeeByID(final GCshEmplID emplID) {
		if ( emplID == null ) {
			throw new IllegalArgumentException("argument <emplID> is null");
		}

		if ( !emplID.isSet() ) {
			throw new IllegalArgumentException("argument <emplID> is not set");
		}

		GnuCashEmployee empl = super.getEmployeeByID(emplID);
		return new GnuCashWritableEmployeeImpl((GnuCashEmployeeImpl) empl);
	}

	/**
	 * {@link #getEmployees()}
	 */
	@Override
	public Collection<GnuCashWritableEmployee> getWritableEmployees() {
		Collection<GnuCashWritableEmployee> result = new ArrayList<GnuCashWritableEmployee>();

		for ( GnuCashEmployee empl : super.getEmployees() ) {
			GnuCashWritableEmployee newEmpl = new GnuCashWritableEmployeeImpl((GnuCashWritableEmployeeImpl) empl);
			result.add(newEmpl);
		}

		return result;
	}

	// ----------------------------

	@Override
	public GnuCashWritableEmployee createWritableEmployee(final String userName) {
		GnuCashWritableEmployeeImpl empl = new GnuCashWritableEmployeeImpl(this);
		empl.setUserName(userName);
		((org.gnucash.api.write.impl.hlp.FileEmployeeManager) super.emplMgr)
			.addEmployee(empl);
		
		return empl;
	}

	/**
	 * @param empl the employee to remove
	 */
	@Override
	public void removeEmployee(final GnuCashWritableEmployee empl) {
		((org.gnucash.api.write.impl.hlp.FileEmployeeManager) super.emplMgr)
			.removeEmployee(empl);
		getRootElement().getGncBook().getBookElements().remove(((GnuCashWritableEmployeeImpl) empl).getJwsdpPeer());
		setModified(true);
	}

	// ---------------------------------------------------------------

	/**
	 * @param jobID the id of the job to fetch
	 * @return A modifiable version of the job or null of not found.
	 * 
	 * @see #getGenerJobByID(GCshID)
	 */
	@Override
	public GnuCashWritableGenerJob getWritableGenerJobByID(final GCshGenerJobID jobID) {
		if ( jobID == null ) {
			throw new IllegalArgumentException("argument <jobID> is null");
		}

		if ( !jobID.isSet() ) {
			throw new IllegalArgumentException("argument <jobID> is not set");
		}

		GnuCashGenerJob generJob = super.getGenerJobByID(jobID);
		if ( generJob.getOwnerType() == GnuCashGenerJob.TYPE_CUSTOMER ) {
			GnuCashCustomerJob custJob = super.getCustomerJobByID(jobID);
			return new GnuCashWritableCustomerJobImpl((GnuCashCustomerJobImpl) custJob);
		} else if ( generJob.getOwnerType() == GnuCashGenerJob.TYPE_VENDOR ) {
			GnuCashVendorJob vendJob = super.getVendorJobByID(jobID);
			return new GnuCashWritableVendorJobImpl((GnuCashVendorJobImpl) vendJob);
		}

		return null; // Compiler happy
	}

	/**
	 * @param jnr the job-number to look for.
	 * @return the (first) jobs that have this number or null if not found
	 */
	@Override
	public GnuCashWritableGenerJob getWritableGenerJobByNumber(final String jnr) {
		for ( GnuCashGenerJob gnucashJob : jobMgr.getGenerJobs() ) {
			GnuCashWritableGenerJob job = (GnuCashWritableGenerJob) gnucashJob;
			if ( job.getNumber().equals(jnr) ) {
				return job;
			}
		}
		return null;

	}

	/**
	 * @see #getGenerJobs()
	 */
	@Override
	public Collection<GnuCashWritableGenerJob> getWritableGenerJobs() {
		Collection<GnuCashGenerJob> jobList = getGenerJobs();
		if ( jobList == null ) {
			throw new IllegalStateException("getGenerJobs() returned null");
		}

		Collection<GnuCashWritableGenerJob> retval = new ArrayList<GnuCashWritableGenerJob>();
		for ( GnuCashGenerJob job : jobList ) {
			retval.add((GnuCashWritableGenerJob) job);
		}
		
		return retval;
	}

	// ----------------------------

	@Override
	public GnuCashWritableCustomerJob createWritableCustomerJob(
			final GnuCashCustomer cust, 
			final String number,
			final String name) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}

		GnuCashWritableCustomerJobImpl job = new GnuCashWritableCustomerJobImpl(this, cust, number, name);
		((org.gnucash.api.write.impl.hlp.FileJobManager) super.jobMgr)
			.addGenerJob(job);
		
		return job;
	}

	@Override
	public GnuCashWritableVendorJob createWritableVendorJob(
			final GnuCashVendor vend, 
			final String number,
			final String name) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> null");
		}

		GnuCashWritableVendorJobImpl job = new GnuCashWritableVendorJobImpl(this, vend, number, name);
		((org.gnucash.api.write.impl.hlp.FileJobManager) super.jobMgr)
			.addGenerJob(job);
		
		return job;
	}

	/**
	 * @param job what to remove
	 * 
	 * @see #removeCustomerJob(GnuCashWritableCustomerJobImpl)
	 * @see #removeVendorJob(GnuCashWritableVendorJobImpl)
	 */
	@Override
	public void removeGenerJob(final GnuCashWritableGenerJob job) {
		if ( job == null ) {
			throw new IllegalArgumentException("argument <job> is null");
		}
		
		if ( job.getInvoices().size() > 0 ) {
			throw new IllegalStateException("Job cannot be deleted; there are still customer invoices/vendor bills attached to it");
		}

		((org.gnucash.api.write.impl.hlp.FileJobManager) super.jobMgr)
			.removeGenerJob(job);
		getRootElement().getGncBook().getBookElements().remove(job.getJwsdpPeer());
		setModified(true);
	}

	@Override
	public void removeCustomerJob(final GnuCashWritableCustomerJobImpl job) {
		removeGenerJob(job);
	}

	@Override
	public void removeVendorJob(final GnuCashWritableVendorJobImpl job) {
		removeGenerJob(job);
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrID cmdtyID) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtID> is null");
		}

//		if ( ! cmdtyID.isSet() ) {
//	   	 throw new IllegalArgumentException("commodity ID is not set");
//		}

		GnuCashCommodity cmdty = super.getCommodityByQualifID(cmdtyID);
		return new GnuCashWritableCommodityImpl((GnuCashCommodityImpl) cmdty);
	}

	@Override
	public GnuCashWritableCommodity getWritableCommodityByQualifID(final String nameSpace, final String id) {
		GnuCashCommodity cmdty = super.getCommodityByQualifID(nameSpace, id);
		return new GnuCashWritableCommodityImpl((GnuCashCommodityImpl) cmdty);
	}

	@Override
	public GnuCashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrNameSpace.Exchange exchange,
			String id) {
		GnuCashCommodity cmdty = super.getCommodityByQualifID(exchange, id);
		return new GnuCashWritableCommodityImpl((GnuCashCommodityImpl) cmdty);
	}

	@Override
	public GnuCashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrNameSpace.MIC mic, String id) {
		GnuCashCommodity cmdty = super.getCommodityByQualifID(mic, id);
		return new GnuCashWritableCommodityImpl((GnuCashCommodityImpl) cmdty);
	}

	@Override
	public GnuCashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrNameSpace.SecIdType secIdType,
			String id) {
		GnuCashCommodity cmdty = super.getCommodityByQualifID(secIdType, id);
		return new GnuCashWritableCommodityImpl((GnuCashCommodityImpl) cmdty);
	}

	@Override
	public GnuCashWritableCommodity getWritableCommodityByQualifID(final String qualifID) {
		GnuCashCommodity cmdty = super.getCommodityByQualifID(qualifID);
		return new GnuCashWritableCommodityImpl((GnuCashCommodityImpl) cmdty);
	}

	@Override
	public GnuCashWritableCommodity getWritableCommodityByXCode(final String xCode) {
		GnuCashCommodity cmdty = super.getCommodityByXCode(xCode);
		return new GnuCashWritableCommodityImpl((GnuCashCommodityImpl) cmdty);
	}

	@Override
	public List<GnuCashWritableCommodity> getWritableCommoditiesByName(final String expr) {
		List<GnuCashWritableCommodity> result = new ArrayList<GnuCashWritableCommodity>();

		for ( GnuCashCommodity cmdty : super.getCommoditiesByName(expr) ) {
			GnuCashWritableCommodity newCmdty = new GnuCashWritableCommodityImpl((GnuCashCommodityImpl) cmdty);
			result.add(newCmdty);
		}

		return result;
	}

	@Override
	public List<GnuCashWritableCommodity> getWritableCommoditiesByName(final String expr, final boolean relaxed) {
		List<GnuCashWritableCommodity> result = new ArrayList<GnuCashWritableCommodity>();

		for ( GnuCashCommodity cmdty : super.getCommoditiesByName(expr, relaxed) ) {
			GnuCashWritableCommodity newCmdty = new GnuCashWritableCommodityImpl((GnuCashCommodityImpl) cmdty);
			result.add(newCmdty);
		}

		return result;
	}

	@Override
	public GnuCashWritableCommodity getWritableCommodityByNameUniq(final String expr)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		GnuCashCommodity cmdty = super.getCommodityByNameUniq(expr);
		return new GnuCashWritableCommodityImpl((GnuCashCommodityImpl) cmdty);
	}

	@Override
	public Collection<GnuCashWritableCommodity> getWritableCommodities() {
		Collection<GnuCashWritableCommodity> result = new ArrayList<GnuCashWritableCommodity>();

		for ( GnuCashCommodity cmdty : super.getCommodities() ) {
			GnuCashWritableCommodity newCmdty = new GnuCashWritableCommodityImpl((GnuCashCommodityImpl) cmdty);
			result.add(newCmdty);
		}

		return result;
	}

	// ----------------------------

	@Override
	public GnuCashWritableCommodity createWritableCommodity(
			final GCshCmdtyID cmdtyID,
			final String code, // <-- e.g., ISIN
			final String name) {
		GnuCashWritableCommodityImpl cmdty = new GnuCashWritableCommodityImpl(this, cmdtyID);
		cmdty.setQualifID(cmdtyID);
		cmdty.setName(name);
		cmdty.setXCode(code);
		((org.gnucash.api.write.impl.hlp.FileCommodityManager) super.cmdtyMgr)
			.addCommodity(cmdty);
		
		return cmdty;
	}

	@Override
	public void removeCommodity(final GnuCashWritableCommodity cmdty) throws ObjectCascadeException {
		if ( cmdty == null ) {
			throw new IllegalArgumentException("argument <cmdty> is null");
		}

		if ( cmdty.getQualifID().toString().startsWith(GCshCmdtyCurrNameSpace.CURRENCY + GCshCmdtyCurrID.SEPARATOR) ) {
			throw new IllegalArgumentException("Currency commodities may not be removed");
		}

		if ( cmdty.getQuotes().size() > 0 ) {
			LOGGER.error("removeCommodity: Commodity with ID '" + cmdty.getQualifID() + "' cannot be removed because "
					+ "there are price objects in the Price DB that depend on it");
			throw new ObjectCascadeException();
		}

		if ( cmdty.getTransactionSplits().size() > 0 ) {
			LOGGER.error("removeCommodity: Commodity with ID '" + cmdty.getQualifID() + "' cannot be removed because "
					+ "there are transactions (splits) that depend on it");
			throw new ObjectCascadeException();
		}

		((org.gnucash.api.write.impl.hlp.FileCommodityManager) super.cmdtyMgr)
			.removeCommodity(cmdty);

		getRootElement().getGncBook().getBookElements().remove(((GnuCashWritableCommodityImpl) cmdty).getJwsdpPeer());
		setModified(true);
	}

	// ---------------------------------------------------------------

	/**
	 * Add a new currency.<br/>
	 * If the currency already exists, add a new price-quote for it.
	 *
	 * @param pCmdtySpace        the name space (e.g. "GOODS" or "CURRENCY")
	 * @param pCmdtyID           the currency-name
	 * @param conversionFactor   the conversion-factor from the base-currency (EUR).
	 * @param pCmdtyNameFraction number of decimal-places after the comma
	 * @param pCmdtyName         common name of the new currency
	 */
	@Override
	public void addCurrency(final String pCmdtySpace, final String pCmdtyID, final FixedPointNumber conversionFactor,
			final int pCmdtyNameFraction, final String pCmdtyName) {

		if ( conversionFactor == null ) {
			throw new IllegalArgumentException("argument <conversionFactor> is null");
		}
		
		if ( pCmdtySpace == null ) {
			throw new IllegalArgumentException("argument <pCmdtySpace> is null");
		}
		
		if ( pCmdtyID == null ) {
			throw new IllegalArgumentException("argument <pCmdtyID> is null");
		}
		
		if ( pCmdtyName == null ) {
			throw new IllegalArgumentException("argument <pCmdtyName> is null");
		}
		
		if ( getCurrencyTable().getConversionFactor(pCmdtySpace, pCmdtyID) == null ) {

			// GncCommodity newCurrency =
			// getObjectFactory().createGncV2GncBookGncCommodity();
			GncCommodity newCurrency = createGncGncCommodityType();
			newCurrency.setCmdtyFraction(pCmdtyNameFraction);
			newCurrency.setCmdtySpace(pCmdtySpace);
			newCurrency.setCmdtyId(pCmdtyID);
			newCurrency.setCmdtyName(pCmdtyName);
			newCurrency.setVersion(Const.XML_FORMAT_VERSION);
			getRootElement().getGncBook().getBookElements().add(newCurrency);
			// incrementCountDataFor("commodity");
		}
		
		// add price-quote
		Price.PriceCommodity currency = new Price.PriceCommodity();
		currency.setCmdtySpace(pCmdtySpace);
		currency.setCmdtyId(pCmdtyID);

		Price.PriceCurrency baseCurrency = getObjectFactory().createPricePriceCurrency();
		baseCurrency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
		baseCurrency.setCmdtyId(getDefaultCurrencyID());

		Price newQuote = getObjectFactory().createPrice();
		newQuote.setPriceSource("JGnuCashLib");
		newQuote.setPriceId(getObjectFactory().createPricePriceId());
		newQuote.getPriceId().setType(Const.XML_DATA_TYPE_GUID);
		newQuote.getPriceId().setValue(GCshID.getNew().toString());
		newQuote.setPriceCommodity(currency);
		newQuote.setPriceCurrency(baseCurrency);
		newQuote.setPriceTime(getObjectFactory().createPricePriceTime());
		newQuote.getPriceTime().setTsDate(FilePriceManager.PRICE_QUOTE_DATE_FORMAT.format(new Date()));
		newQuote.setPriceType("last");
		newQuote.setPriceValue(conversionFactor.toGnuCashString());

		List<Object> bookElements = getRootElement().getGncBook().getBookElements();
		for ( Object element : bookElements ) {
			if ( element instanceof GncPricedb ) {
				GncPricedb prices = (GncPricedb) element;
				prices.getPrice().add(newQuote);
				getCurrencyTable().setConversionFactor(pCmdtySpace, pCmdtyID, conversionFactor);
				return;
			}
		}
		throw new IllegalStateException("No priceDB in Book in GnuCash file");
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashWritablePrice getWritablePriceByID(final GCshPrcID prcID) {
		if ( prcID == null ) {
			throw new IllegalArgumentException("argument <prcID> is null");
		}

		if ( !prcID.isSet() ) {
			throw new IllegalArgumentException("argument <prcID> is not set");
		}

		GnuCashPrice prc = super.getPriceByID(prcID);
		return new GnuCashWritablePriceImpl((GnuCashPriceImpl) prc);
	}

	@Override
	public GnuCashWritablePrice getWritablePriceByCmdtyIDDate(final GCshCmdtyID cmdtyID, final LocalDate date) {
		return getWritablePriceByCmdtyCurrIDDate(cmdtyID, date);
	}
	
	@Override
	public GnuCashWritablePrice getWritablePriceByCurrIDDate(final GCshCurrID currID, final LocalDate date) {
		return getWritablePriceByCmdtyCurrIDDate(currID, date);
	}
	
	@Override
	public GnuCashWritablePrice getWritablePriceByCurrDate(final Currency curr, final LocalDate date) {
		GnuCashPrice prc = prcMgr.getPriceByCurrDate(curr, date);
		return new GnuCashWritablePriceImpl((GnuCashPriceImpl) prc);
	}
	
	@Override
    public GnuCashWritablePrice getWritablePriceByCmdtyCurrIDDate(final GCshCmdtyCurrID cmdtyCurrID, final LocalDate date) {
		GnuCashPrice prc = prcMgr.getPriceByCmdtyCurrIDDate(cmdtyCurrID, date);
		return new GnuCashWritablePriceImpl((GnuCashPriceImpl) prc);
    }

    @Override
	public Collection<GnuCashWritablePrice> getWritablePrices() {
		Collection<GnuCashWritablePrice> result = new ArrayList<GnuCashWritablePrice>();

		for ( GnuCashPrice prc : super.getPrices() ) {
			GnuCashWritablePrice newPrc = new GnuCashWritablePriceImpl((GnuCashPriceImpl) prc);
			result.add(newPrc);
		}

		return result;
	}

	// ----------------------------

	@Override
	public GnuCashWritablePrice createWritablePrice(
			final GCshCmdtyCurrID fromCmdtyCurrID,
			final GCshCurrID toCurrID,
			final LocalDate date) {
		GnuCashWritablePrice prc = new GnuCashWritablePriceImpl(this);
	    prc.setFromCmdtyCurrQualifID(fromCmdtyCurrID);
	    prc.setToCurrencyQualifID(toCurrID);
		prc.setDate(date);
		((org.gnucash.api.write.impl.hlp.FilePriceManager) super.prcMgr)
			.addPrice(prc);
		
		return prc;
	}

	@Override
	public void removePrice(final GnuCashWritablePrice prc) {
		((org.gnucash.api.write.impl.hlp.FilePriceManager) super.prcMgr)
			.removePrice(prc);

		// Does not work like that, as prices are one level deeper, in price DB:
		// getRootElement().getGncBook().getBookElements().remove(((GnuCashWritablePriceImpl) prc).getJwsdpPeer());
		// Instead:
		((org.gnucash.api.write.impl.hlp.FilePriceManager) super.prcMgr)
			.removePrice_raw(prc.getID());
		
		setModified(true);
	}

	// ---------------------------------------------------------------

	@Override
	public GCshWritableTaxTable getWritableTaxTableByID(GCshTaxTabID taxTabID) {
		if ( taxTabID == null ) {
			throw new IllegalArgumentException("argument <taxTabID> is null");
		}

		if ( !taxTabID.isSet() ) {
			throw new IllegalArgumentException("argument <taxTabID> is not set");
		}

		GCshTaxTable taxTab = super.getTaxTableByID(taxTabID);
		return new GCshWritableTaxTableImpl((GCshTaxTableImpl) taxTab);
	}

	@Override
	public GCshWritableTaxTable getWritableTaxTableByName(final String name) {
		GCshTaxTable taxTab = super.getTaxTableByName(name);
		return new GCshWritableTaxTableImpl((GCshTaxTableImpl) taxTab);
	}

	/**
	 * @return all TaxTables defined in the book
	 * @see {@link GCshTaxTable}
	 */
	@Override
	public Collection<GCshWritableTaxTable> getWritableTaxTables() {
		Collection<GCshWritableTaxTable> result = new ArrayList<GCshWritableTaxTable>();

		for ( GCshTaxTable taxTab : super.getTaxTables() ) {
			GCshWritableTaxTable newTaxTab = new GCshWritableTaxTableImpl((GCshTaxTableImpl) taxTab);
			result.add(newTaxTab);
		}

		return result;
	}

	// ---------------------------------------------------------------

	@Override
	public GCshWritableBillTerms getWritableBillTermsByID(GCshBllTrmID bllTrmID) {
		if ( bllTrmID == null ) {
			throw new IllegalArgumentException("argument <bllTrmID> is null");
		}

		if ( !bllTrmID.isSet() ) {
			throw new IllegalArgumentException("argument <bllTrmID> is not set");
		}

		GCshBillTerms bllTrm = super.getBillTermsByID(bllTrmID);
		return new GCshWritableBillTermsImpl((GCshBillTermsImpl) bllTrm);
	}

	/**
	 * {@link #getBillTermsByName(String)}
	 */
	@Override
	public GCshWritableBillTerms getWritableBillTermsByName(final String name) {
		GCshBillTerms bllTrm = super.getBillTermsByName(name);
		return new GCshWritableBillTermsImpl((GCshBillTermsImpl) bllTrm);
	}

	/**
	 * @return all TaxTables defined in the book
	 * 
	 * @see #getBillTerms()
	 */
	@Override
	public Collection<GCshWritableBillTerms> getWritableBillTerms() {
		Collection<GCshWritableBillTerms> result = new ArrayList<GCshWritableBillTerms>();

		for ( GCshBillTerms taxTab : super.getBillTerms() ) {
			GCshWritableBillTerms newTaxTab = new GCshWritableBillTermsImpl((GCshBillTermsImpl) taxTab);
			result.add(newTaxTab);
		}

		return result;
	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	public List<GnuCashWritableCustomerInvoice> getPaidWritableInvoicesForCustomer_direct(
			final GnuCashCustomer cust) throws  TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr)
				.getPaidWritableInvoicesForCustomer_direct(cust);
	}

	public List<GnuCashWritableCustomerInvoice> getUnpaidWritableInvoicesForCustomer_direct(
			final GnuCashCustomer cust) throws TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr)
				.getUnpaidWritableInvoicesForCustomer_direct(cust);
	}

	// ----------------------------

	public List<GnuCashWritableVendorBill> getPaidWritableBillsForVendor_direct(final GnuCashVendor vend)
			throws TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getPaidWritableBillsForVendor_direct(vend);
	}

	public List<GnuCashWritableVendorBill> getUnpaidWritableBillsForVendor_direct(final GnuCashVendor vend)
			throws TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr)
				.getUnpaidWritableBillsForVendor_direct(vend);
	}

	// ----------------------------

	public List<GnuCashWritableEmployeeVoucher> getPaidWritableVouchersForEmployee(final GnuCashEmployee empl)
			throws TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getPaidWritableVouchersForEmployee(empl);
	}

	public List<GnuCashWritableEmployeeVoucher> getUnpaidWritableVouchersForEmployee(final GnuCashEmployee empl)
			throws TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getUnpaidWritableVouchersForEmployee(empl);
	}

	// ----------------------------

	public List<GnuCashWritableJobInvoice> getPaidWritableInvoicesForJob(final GnuCashGenerJob job)
			throws TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getPaidWritableInvoicesForJob(job);
	}

	public List<GnuCashWritableJobInvoice> getUnpaidWritableInvoicesForJob(final GnuCashGenerJob job)
			throws TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getUnpaidWritableInvoicesForJob(job);
	}

	// ---------------------------------------------------------------
	// Internal Helpers
	// ---------------------------------------------------------------

	protected GncAccount createGncAccountType() {
		GncAccount retval = getObjectFactory().createGncAccount();
		incrementCountDataFor("account");
		return retval;
	}

	protected GncTransaction createGncTransactionType() {
		GncTransaction retval = getObjectFactory().createGncTransaction();
		incrementCountDataFor("transaction");
		return retval;
	}

	protected GncTransaction.TrnSplits.TrnSplit createGncTransactionSplitType() {
		GncTransaction.TrnSplits.TrnSplit retval = getObjectFactory().createGncTransactionTrnSplitsTrnSplit();
		// Does not apply:
		// incrementCountDataFor();
		return retval;
	}

	// ----------------------------

	@SuppressWarnings("exports")
	public GncAccount.ActLots.GncLot createGncAccountLotType() {
		GncAccount.ActLots.GncLot retval = getObjectFactory().createGncAccountActLotsGncLot();
		// Does not apply:
		// incrementCountDataFor();
		return retval;
	}

	// ----------------------------

	protected GncGncInvoice createGncGncInvoiceType() {
		GncGncInvoice retval = getObjectFactory().createGncGncInvoice();
		incrementCountDataFor("gnc:GncInvoice");
		return retval;
	}

	@SuppressWarnings("exports")
	public GncGncEntry createGncGncEntryType() {
		GncGncEntry retval = getObjectFactory().createGncGncEntry();
		incrementCountDataFor("gnc:GncEntry");
		return retval;
	}

	// ----------------------------

	protected GncGncCustomer createGncGncCustomerType() {
		GncGncCustomer retval = getObjectFactory().createGncGncCustomer();
		incrementCountDataFor("gnc:GncCustomer");
		return retval;
	}

	protected GncGncVendor createGncGncVendorType() {
		GncGncVendor retval = getObjectFactory().createGncGncVendor();
		incrementCountDataFor("gnc:GncVendor");
		return retval;
	}

	protected GncGncEmployee createGncGncEmployeeType() {
		GncGncEmployee retval = getObjectFactory().createGncGncEmployee();
		incrementCountDataFor("gnc:GncEmployee");
		return retval;
	}

	@SuppressWarnings("exports")
	public GncGncJob createGncGncJobType() {
		// ====== <--- sic
		GncGncJob retval = getObjectFactory().createGncGncJob();
		incrementCountDataFor("gnc:GncJob");
		return retval;
	}

	// ----------------------------

	@SuppressWarnings("exports")
	public GncCommodity createGncGncCommodityType() {
		GncCommodity retval = getObjectFactory().createGncCommodity();
		incrementCountDataFor("commodity");
		return retval;
	}

	@SuppressWarnings("exports")
	public Price createGncGncPricedbPriceType() {
		Price retval = getObjectFactory().createPrice();
		incrementCountDataFor("price");
		return retval;
	}

	// ----------------------------

	@SuppressWarnings("exports")
	public GncGncBillTerm.BilltermParent createGncGncBillTermParentType() {
		GncGncBillTerm.BilltermParent retval = getObjectFactory().createGncGncBillTermBilltermParent();
		return retval;
	}

	@SuppressWarnings("exports")
	public GncGncBillTerm.BilltermDays createGncGncBillTermDaysType() {
		GncGncBillTerm.BilltermDays retval = getObjectFactory().createGncGncBillTermBilltermDays();
		return retval;
	}

	@SuppressWarnings("exports")
	public GncGncBillTerm.BilltermProximo createGncGncBillTermProximoType() {
		GncGncBillTerm.BilltermProximo retval = getObjectFactory().createGncGncBillTermBilltermProximo();
		return retval;
	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	private void clean() {
		for ( GnuCashWritableAccount acct : getWritableAccounts() ) {
			((GnuCashWritableAccountImpl) acct).clean();
		}

		for ( GnuCashWritableTransaction trx : getWritableTransactions() ) {
			((GnuCashWritableTransactionImpl) trx).clean();
		}

		// ::TODO: Some funny behavior here
//        for ( GnuCashWritableTransactionSplit splt : getWritableTransactionSplits() ) {
//            ((GnuCashWritableTransactionSplitImpl) splt).clean();
//        }

		// ------------------------

		for ( GnuCashWritableGenerInvoice invc : getWritableGenerInvoices() ) {
			((GnuCashWritableGenerInvoiceImpl) invc).clean();
		}

		for ( GnuCashWritableGenerInvoiceEntry entr : getWritableGenerInvoiceEntries() ) {
			((GnuCashWritableGenerInvoiceEntryImpl) entr).clean();
		}

		// ------------------------

		for ( GnuCashWritableCustomer cust : getWritableCustomers() ) {
			((GnuCashWritableCustomerImpl) cust).clean();
		}

		for ( GnuCashWritableVendor vend : getWritableVendors() ) {
			((GnuCashWritableVendorImpl) vend).clean();
		}

		for ( GnuCashWritableEmployee empl : getWritableEmployees() ) {
			((GnuCashWritableEmployeeImpl) empl).clean();
		}
		
		// NOT GnuCashWritableGenerJob

		// ------------------------

		for ( GnuCashWritableCommodity cmdty : getWritableCommodities() ) {
			((GnuCashWritableCommodityImpl) cmdty).clean();
		}

		// NOT GnuCashWritablePrice

	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	@Override
	public String toString() {
		String result = "GnuCashWritableFileImpl [\n";

		result += "  Stats (raw):\n";
		GCshFileStats stats;
		try {
			stats = new GCshFileStats(this);

			result += "    No. of accounts:                  " + stats.getNofEntriesAccounts(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of transactions:              " + stats.getNofEntriesTransactions(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of transaction splits:        "
					+ stats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW) + "\n";
			result += "    No. of (generic) invoices:        "
					+ stats.getNofEntriesGenerInvoices(GCshFileStats.Type.RAW) + "\n";
			result += "    No. of (generic) invoice entries: "
					+ stats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW) + "\n";
			result += "    No. of customers:                 " + stats.getNofEntriesCustomers(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of vendors:                   " + stats.getNofEntriesVendors(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of employees:                 " + stats.getNofEntriesEmployees(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of (generic) jobs:            " + stats.getNofEntriesGenerJobs(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of commodities:               " + stats.getNofEntriesCommodities(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of tax tables:                " + stats.getNofEntriesTaxTables(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of bill terms:                " + stats.getNofEntriesBillTerms(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of prices:                    " + stats.getNofEntriesPrices(GCshFileStats.Type.RAW)
					+ "\n";
		} catch (Exception e) {
			result += "ERROR\n";
		}

		result += "]";

		return result;
	}

}
