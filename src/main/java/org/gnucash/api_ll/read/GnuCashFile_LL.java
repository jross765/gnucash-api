package org.gnucash.api_ll.read;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncBook;
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
import org.gnucash.api.generated.GncIdType;
import org.gnucash.api.generated.GncPricedb;
import org.gnucash.api.generated.GncSchedxaction;
import org.gnucash.api.generated.GncTemplateTransactions;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Price;
import org.gnucash.api.generated.Slot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

// ::TODO
// At the current stage of development, this class only serves as a de-facto
// helper for testing the generated code. It is not actually used for anything 
// that drives functionality.
// It is obvious that there can/should be more one day:
// - Eliminating (or at least reducing) redundancies between GnuCashFileImpl 
//   and this class, i.e., essentially, having the former one use this one's methods,
//   thus really integrating it, so that it is actually used. 
// - Extending and publishing it, so that the user can use a "low-level" API,
//   similar to what we have in the cousin project PyGnuCashLib.
// - Writing a set of simple (read-only) tools that are much more efficient
//   than their high-level equivalents, such as one listing all accounts, etc.
//   Again, just as in the cousin project.
//
// In addition to that, testing the generated code is actually critical here 
// (as opposed to the sister project), because with GnuCash, there is a 
// difference between a "simple" format/schema and a "complex" one.
public class GnuCashFile_LL  
{
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(GnuCashFile_LL.class);

    // ---------------------------------------------------------------
    // ::MAGIC
	
	// Cf. https://en.wikipedia.org/wiki/List_of_file_signatures
	private static final int GZIP_HEADER_LENGTH = 2;
	private static final int GZIP_HEADER_BYTE_1 = 31;
    private static final int GZIP_HEADER_BYTE_2 = -117;

    protected static final String FILE_EXT_ZIPPED_1 = ".gz";
    protected static final String FILE_EXT_ZIPPED_2 = ".gnucash";

    // ---------------------------------------------------------------

	private File file;

	// ----------------------------

	private GncV2      rootElement;
	private GncBook    book;
	private GncPricedb priceDB;

	// ----------------------------

	private volatile ObjectFactory myJAXBFactory;
	private volatile JAXBContext myJAXBContext;

	// ---------------------------------------------------------------

	/**
	 * @param pFile the file to load and initialize from
	 * @throws IOException                   on low level reading-errors
	 *                                       (FileNotFoundException if not found)
	 */
	public GnuCashFile_LL(final File pFile) throws IOException {
		super();
		loadFile(pFile, false);
	}

	public GnuCashFile_LL(final File pFile, final boolean withProgBar) throws IOException {
		super();
		loadFile(pFile, withProgBar);
	}

	/**
	 * @param is the input stream to load and initialize from
	 * @throws IOException                   on low level reading-errors
	 *                                       (FileNotFoundException if not found)
	 */
	public GnuCashFile_LL(final InputStream is) throws IOException {
		super();
		loadInputStream(is, false);
	}

	public GnuCashFile_LL(final InputStream is, final boolean withProgBar) throws IOException {
		super();
		loadInputStream(is, withProgBar);
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Internal method, just sets this.file .
	 *
	 * @param pFile the file loaded
	 */
	protected void setFile(final File pFile) {
		if ( pFile == null ) {
			throw new IllegalArgumentException("argument <pFile> is null");
		}
		file = pFile;
	}

	// ----------------------------

	/**
	 * loads the file and calls setRootElement.
	 *
	 * @param pFile the file to read
	 * @throws IOException on low level reading-errors (FileNotFoundException if not found)
	 * @see #setRootElement(GncV2)
	 */
	protected void loadFile(final File pFile, boolean withProgBar) throws IOException {
		long start = System.currentTimeMillis();

		if ( pFile == null ) {
			throw new IllegalArgumentException("argument <pFile> is null");
		}

		if ( ! pFile.exists() ) {
			throw new IllegalArgumentException("File '" + pFile.getAbsolutePath() + "' does not exist");
		}

		setFile(pFile);

		InputStream in = new FileInputStream(pFile);
		if ( pFile.getName().endsWith(FILE_EXT_ZIPPED_1) ||
		     pFile.getName().endsWith(FILE_EXT_ZIPPED_2) ) {
			in = new BufferedInputStream(in);
			in = new GZIPInputStream(in);
		} else {
			// determine if it's gzipped by the magic bytes
			byte[] magic = new byte[GZIP_HEADER_LENGTH];
			in.read(magic);
			in.close();

			in = new FileInputStream(pFile);
			in = new BufferedInputStream(in);
			if ( magic[0] == GZIP_HEADER_BYTE_1 && 
				 magic[1] == GZIP_HEADER_BYTE_2 ) {
				in = new GZIPInputStream(in);
			}
		}

		loadInputStream(in, withProgBar);

		long end = System.currentTimeMillis();
		LOGGER.info("loadFile: Took " + (end - start) + " ms (total) ");
	}

	protected void loadInputStream(InputStream in, boolean withProgBar) throws UnsupportedEncodingException, IOException {
		long start = System.currentTimeMillis();

		InputStreamReader reader = new InputStreamReader(in, "utf-8");
		try {
			JAXBContext myContext = getJAXBContext();
			if ( myContext == null ) {
				LOGGER.error("loadInputStream: JAXB context cannot be found/generated");
				throw new IOException("JAXB context cannot be found/generated");
			}
			Unmarshaller unmarshaller = myContext.createUnmarshaller();

			GncV2 obj = (GncV2) unmarshaller.unmarshal(new InputSource(new BufferedReader(reader)));
			long start2 = System.currentTimeMillis();
			
			setRootElement(obj, withProgBar);
			initBook();
			initPriceDB();
			
			long end = System.currentTimeMillis();
			LOGGER.info("loadInputStream: Took " + (end - start) + " ms (total), " + (start2 - start)
					+ " ms (jaxb-loading), " + (end - start2) + " ms (building facades)");

		} catch (JAXBException e) {
			LOGGER.error("loadInputStream: " + e.getMessage(), e);
			System.err.println(">>>");
			System.err.println(e.getMessage());
			System.err.println("<<<");
			throw new IllegalStateException(e);
		} finally {
			reader.close();
		}
	}

	// ---------------------------------------------------------------

	private void initBook() {
		if ( rootElement == null ) {
			throw new IllegalStateException("root element is null");
		}
		
		book = rootElement.getGncBook();
	}
	
	private void initPriceDB() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncPricedb ) {
				priceDB = (GncPricedb) bookElement;
				return;
			}
		}
	}
	
	// ---------------------------------------------------------------

	/**
	 * Get count data for specific type.
	 *
	 * @param type the type to set it for
	 * 
	 * @return 
	 */
	public int getCountDataFor(final String type) {
		if ( type == null ) {
			throw new IllegalArgumentException("argument <type> is null");
		}

		if ( type.isBlank() ) {
			throw new IllegalArgumentException("argument <type> is blank");
		}

		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		for ( GncCountData gncCountData : book.getGncCountData() ) {
			if ( type.equals(gncCountData.getCdType()) ) {
				return gncCountData.getValue();
			}
		}

		throw new IllegalArgumentException("Unknown type '" + type + "'");
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public GncAccount.ActCommodity getDefaultCurrencyID() {
    	try {
    		return getRootAccount().getActCommodity();
    	} catch ( Exception exc ) {
    		GncAccount.ActCommodity cmdty = new GncAccount.ActCommodity();
    		cmdty.setCmdtySpace( "CURRENCY" ); // ::MAGIC
    		cmdty.setCmdtyId( Const.DEFAULT_CURRENCY );
    		return cmdty;
    	}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getDefaultCurrencyIDStr() {
		return getDefaultCurrencyID().getCmdtyId();
	}

	// ---------------------------------------------------------------

	/**
	 * @see #getAccountsByParentID(GncAccount.ActId)
	 */
	public GncAccount getAccountByID(final GncAccount.ActId acctID) {
		for ( GncAccount acct : getAccounts() ) {
			if ( acct.getActId().getValue().equals( acctID.getValue() ) )
				return acct;
		}
		
		return null;
	}

	/**
	 * @param prntAcctID if null, gives all account that have no parent
	 * @return the sorted collection of children of that account
	 * 
	 * @see #getAccountByID(org.gnucash.api.generated.GncAccount.ActId)
	 */
	public List<GncAccount> getAccountsByParentID(final GncAccount.ActParent prntAcctID) {
		ArrayList<GncAccount> result = new ArrayList<GncAccount>();
		
		for ( GncAccount acct : getAccounts() ) {
			if ( acct.getActParent().getValue().equals( prntAcctID.getValue() ) )
				result.add( acct );
		}
		
		return result;
	}

	public List<GncAccount> getAccountsByName(final String name) {
		ArrayList<GncAccount> result = new ArrayList<GncAccount>();
		
		for ( GncAccount acct : getAccounts() ) {
			if ( acct.getActName().contains( name.trim() ) )
				result.add( acct );
		}
		
		return result;
	}

//	/**
//	 * @see GncFile#getAccountsByName(java.lang.String)
//	 */
//	@Override
//	public List<GncAccount> getAccountsByName(final String expr, boolean qualif, boolean relaxed) {
//		return acctMgr.getAccountsByName(expr, qualif, relaxed);
//	}
//
//	@Override
//	public GncAccount getAccountByNameUniq(final String name, final boolean qualif)
//			throws NoEntryFoundException, TooManyEntriesFoundException {
//		return acctMgr.getAccountByNameUniq(name, qualif);
//	}

//	/**
//	 * warning: this function has to traverse all accounts. If it much faster to try
//	 * getAccountByID first and only call this method if the returned account does
//	 * not have the right name.
//	 *
//	 * @param nameRegEx the regular expression of the name to look for
//	 * @return null if not found
//	 * @throws TooManyEntriesFoundException
//	 * @throws NoEntryFoundException
//	 * @see #getAccountByID(GCshAcctID)
//	 * @see #getAccountsByName(String)
//	 */
//	@Override
//	public GncAccount getAccountByNameEx(final String nameRegEx)
//			throws NoEntryFoundException, TooManyEntriesFoundException {
//		return acctMgr.getAccountByNameEx(nameRegEx);
//	}

//	/**
//	 * First try to fetch the account by id, then fall back to traversing all
//	 * accounts to get if by it's name.
//	 *
//	 * @param acctID the id to look for
//	 * @param name   the name to look for if nothing is found for the id
//	 * @return null if not found
//	 * @throws TooManyEntriesFoundException
//	 * @throws NoEntryFoundException
//	 * @see #getAccountByID(GCshAcctID)
//	 * @see #getAccountsByName(String)
//	 */
//	@Override
//	public GncAccount getAccountByIDorName(final GCshAcctID acctID, final String name)
//			throws NoEntryFoundException, TooManyEntriesFoundException {
//		return acctMgr.getAccountByIDorName(acctID, name);
//	}

//	/**
//	 * First try to fetch the account by id, then fall back to traversing all
//	 * accounts to get if by it's name.
//	 *
//	 * @param acctID the id to look for
//	 * @param name   the regular expression of the name to look for if nothing is
//	 *               found for the id
//	 * @return null if not found
//	 * @throws TooManyEntriesFoundException
//	 * @throws NoEntryFoundException
//	 * @see #getAccountByID(GCshAcctID)
//	 * @see #getAccountsByName(String)
//	 */
//	@Override
//	public GncAccount getAccountByIDorNameEx(final GCshAcctID acctID, final String name)
//			throws NoEntryFoundException, TooManyEntriesFoundException {
//		return acctMgr.getAccountByIDorNameEx(acctID, name);
//	}

	public List<GncAccount> getAccountsByType(final String typeStr) {
		ArrayList<GncAccount> result = new ArrayList<GncAccount>();
		
		for ( GncAccount acct : getAccounts() ) {
			if ( acct.getActType().equals( typeStr ) )
				result.add( acct );
		}
		
		return result;
	}

//	public List<GncAccount> getAccountsByTypeAndName(String typeStr, String acctName, boolean qualif,
//			boolean relaxed) {
//		return acctMgr.getAccountsByTypeAndName(type, acctName, qualif, relaxed);
//	}

	/**
	 * @return a read-only collection of all accounts
	 */
	public List<GncAccount> getAccounts() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		ArrayList<GncAccount> result = new ArrayList<GncAccount>();
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncAccount ) {
				result.add( (GncAccount) bookElement );
			}
		}
		
		return result;
	}

	/**
	 * @return a read-only collection of all accounts that have no parent (the
	 *         result is sorted)
	 */
	public GncAccount.ActId getRootAccountID() {
		return getRootAccount().getActId();
	}

	/**
	 * @return a read-only collection of all accounts that have no parent (the
	 *         result is sorted)
	 */
	public GncAccount getRootAccount() {
		List<GncAccount> acctList = getAccountsByType("ROOT"); // ::MAGIC
		
		if ( acctList.size() == 0 ) {
			throw new IllegalStateException("No account of type 'ROOT' found");
		}

		if ( acctList.size() > 1 ) {
			throw new IllegalStateException("Too many accounts of type 'ROOT' found");
		}
		
		return acctList.getFirst();
	}

//	/**
//	 * @return a read-only collection of all accounts that have no parent (the
//	 *         result is sorted)
//	 */
//	@Override
//	public List<? extends GncAccount> getParentlessAccounts() {
//		return acctMgr.getParentlessAccounts();
//	}
//
//	@Override
//	public List<GCshAcctID> getTopAccountIDs() {
//		return acctMgr.getTopAccountIDs();
//	}
//
//	@Override
//	public List<GncAccount> getTopAccounts() {
//		return acctMgr.getTopAccounts();
//	}

	// ---------------------------------------------------------------

	public GncTransaction getTransactionByID(final GncTransaction.TrnId trxID) {
		for ( GncTransaction trx : getTransactions() ) {
			if ( trx.getTrnId().getValue().equals( trxID.toString() ) )
				return trx;
		}
		
		return null;
	}

	public List<GncTransaction> getTransactions() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		ArrayList<GncTransaction> result = new ArrayList<GncTransaction>();
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncTransaction ) {
				result.add( (GncTransaction) bookElement );
			}
		}
		
		return result;
	}

//	public List<? extends GncTransaction> getTransactions(final GncTransaction.TrnDatePosted fromDate, final GncTransaction.TrnDatePosted toDate) {
//		ArrayList<GncTransaction> result = new ArrayList<GncTransaction>();
//		
//		for ( GncTransaction trx : getTransactions() ) {
//			 if ( ( trx.getTrnDatePosted().getTsDate().isEqual( fromDate.getTsDate() ) ||
//				    trx.getTrnDatePosted().getTsDate().isAfter( fromDate.getTsDate() ) ) 
//					 &&
//			      ( trx.getTrnDatePosted().getTsDate().isEqual( toDate.getTsDate() ) ||
//					trx.getTrnDatePosted().getTsDate().isBefore( toDate.getTsDate() ) ) ) {
//				 result.add(trx);
//			 }
//		}
//		
//		return Collections.unmodifiableList(result);
//	}

	// ---------------------------------------------------------------

	public GncTransaction.TrnSplits.TrnSplit getTransactionSplitByID(final GncTransaction.TrnSplits.TrnSplit.SplitId spltID) {
		if ( spltID == null ) {
			throw new IllegalArgumentException("argument <spltID> is null");
		}

		if ( ! spltID.getValue().isEmpty() ) {
			throw new IllegalArgumentException("argument <spltID> is not set");
		}

		for ( GncTransaction.TrnSplits.TrnSplit splt : getTransactionSplits() ) {
			if ( splt.getSplitId().getValue().equals( spltID.getValue() ) ) {
				return splt;
			}
		}
		
		return null;
	}

    public GncTransaction.TrnSplits.TrnSplit getTransactionSplitByAcctIDAndTrxID(final GncTransaction.TrnSplits.TrnSplit.SplitAccount acctID, final GncTransaction.TrnId trxID) {
		if ( acctID == null ) {
			throw new IllegalArgumentException("argument <acctID> is null");
		}

		if ( ! acctID.getValue().equals("") ) {
			throw new IllegalArgumentException("argument <acctID> is not set");
		}

		if ( trxID == null ) {
			throw new IllegalArgumentException("argument <trxID> is null");
		}

		if ( ! trxID.getValue().equals("") ) {
			throw new IllegalArgumentException("argument <trxID> is not set");
		}

		GncTransaction trx = getTransactionByID(trxID);
		if ( trx == null ) {
			LOGGER.error("getTransactionSplitByAcctIDAndTrxID: No transaction with ID " + trxID);
			return null;
		}

		for ( GncTransaction.TrnSplits.TrnSplit splt : trx.getTrnSplits().getTrnSplit() ) {
			if ( splt.getSplitAccount().equals(acctID) )
				return splt;
		}

		LOGGER.error("getTransactionSplitByAcctIDAndTrxID: No transaction split with account-ID " + acctID + " and transaction-ID " + trxID);
		return null;
    }

	public List<GncTransaction.TrnSplits.TrnSplit> getTransactionSplits() {
		ArrayList<GncTransaction.TrnSplits.TrnSplit> result = new ArrayList<GncTransaction.TrnSplits.TrnSplit>();
		
		for ( GncTransaction trx : getTransactions() ) {
			for ( GncTransaction.TrnSplits.TrnSplit splt : trx.getTrnSplits().getTrnSplit() ) {
				result.add( splt );
			}
		}
		
		return result;
	}

	// ----------------------------

//	public List<GncTransaction.TrnSplits.TrnSplit> getTransactionSplitsByAccountLotID(final GCshLotID acctLotID) {
//		return trxMgr.getTransactionSplitsByAccountLotID(acctLotID);
//	}
//
//	public List<GncTransaction.TrnSplits.TrnSplit> getTransactionSplitsByCmdtyID(final GCshCmdtyID cmdtyID) {
//		return trxMgr.getTransactionSplitsByCmdtyID(cmdtyID);
//	}

	// ---------------------------------------------------------------

	public GncGncInvoice getGenerInvoiceByID(final String invcID) {
		for ( GncGncInvoice invc : getGenerInvoices() ) {
			if ( invc.getInvoiceId().equals( invcID ) )
				return invc;
		}
		
		return null;
	}

	public List<GncGncInvoice> getGenerInvoices() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		ArrayList<GncGncInvoice> result = new ArrayList<GncGncInvoice>();
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncGncInvoice ) {
				result.add( (GncGncInvoice) bookElement );
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------

	public GncGncEntry getGenerInvoiceEntryByID(final GncGncEntry.EntryGuid entrID) {
		for ( GncGncEntry invc : getGenerInvoiceEntries() ) {
			if ( invc.getEntryGuid().getValue().equals( entrID.getValue() ) )
				return invc;
		}
		
		return null;
	}

	public List<GncGncEntry> getGenerInvoiceEntriesByInvcID(final GncGncEntry.EntryInvoice invcID) {
		ArrayList<GncGncEntry> result = new ArrayList<GncGncEntry>();
		
		for ( GncGncEntry entr : getGenerInvoiceEntries() ) {
			if ( entr.getEntryInvoice().getValue().equals( invcID.getValue() ) )
				result.add( entr );
		}
		
		return result;
	}

	public List<GncGncEntry> getGenerInvoiceEntries() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		ArrayList<GncGncEntry> result = new ArrayList<GncGncEntry>();
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncGncEntry ) {
				result.add( (GncGncEntry) bookElement );
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------

	public GncGncCustomer getCustomerByID(final String custID) {
		for ( GncGncCustomer cust : getCustomers() ) {
			if ( cust.getCustId().equals( custID ) )
				return cust;
		}
		
		return null;
	}

//	public Collection<GncGncCustomer> getCustomersByName(final String name) {
//		return custMgr.getCustomersByName(name);
//	}
//
//	public Collection<GncGncCustomer> getCustomersByName(final String expr, boolean relaxed) {
//		return custMgr.getCustomersByName(expr, relaxed);
//	}
//
//	public GncGncCustomer getCustomerByNameUniq(final String name)
//			throws NoEntryFoundException, TooManyEntriesFoundException {
//		return custMgr.getCustomerByNameUniq(name);
//	}

	public Collection<GncGncCustomer> getCustomers() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		ArrayList<GncGncCustomer> result = new ArrayList<GncGncCustomer>();
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncGncCustomer ) {
				result.add( (GncGncCustomer) bookElement );
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------

	public GncGncVendor getVendorByID(final String vendID) {
		for ( GncGncVendor vend : getVendors() ) {
			if ( vend.getVendorId().equals( vendID ) )
				return vend;
		}
		
		return null;
	}

//	public Collection<GncGncVendor> getVendorsByName(final String name) {
//		return vendMgr.getVendorsByName(name);
//	}
//
//	public Collection<GncGncVendor> getVendorsByName(final String expr, final boolean relaxed) {
//		return vendMgr.getVendorsByName(expr, relaxed);
//	}
//
//	public GncGncVendor getVendorByNameUniq(final String name)
//			throws NoEntryFoundException, TooManyEntriesFoundException {
//		return vendMgr.getVendorByNameUniq(name);
//	}

	public Collection<GncGncVendor> getVendors() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		ArrayList<GncGncVendor> result = new ArrayList<GncGncVendor>();
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncGncVendor ) {
				result.add( (GncGncVendor) bookElement );
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------

	public GncGncEmployee getEmployeeByID(final String emplID) {
		for ( GncGncEmployee vend : getEmployees() ) {
			if ( vend.getEmployeeId().equals( emplID ) )
				return vend;
		}
		
		return null;
	}

//	public Collection<GncGncEmployee> getEmployeesByUserName(final String userName) {
//		return emplMgr.getEmployeesByUserName(userName);
//	}
//
//	public Collection<GncGncEmployee> getEmployeesByUserName(final String expr, boolean relaxed) {
//		return emplMgr.getEmployeesByUserName(expr, relaxed);
//	}
//
//	public GncGncEmployee getEmployeeByUserNameUniq(final String userName)
//			throws NoEntryFoundException, TooManyEntriesFoundException {
//		return emplMgr.getEmployeeByUserNameUniq(userName);
//	}

	public Collection<GncGncEmployee> getEmployees() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		ArrayList<GncGncEmployee> result = new ArrayList<GncGncEmployee>();
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncGncEmployee ) {
				result.add( (GncGncEmployee) bookElement );
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------

	public GncGncJob getGenerJobByID(final String jobID) {
		for ( GncGncJob job : getGenerJobs() ) {
			if ( job.getJobId().equals( jobID ) )
				return job;
		}
		
		return null;
	}

//	public Collection<GncGncJob> getGenerJobsByName(final String name) {
//		return jobMgr.getGenerJobsByName(name);
//	}
//
//	public Collection<GncGncJob> getGenerJobsByName(final String expr, final boolean relaxed) {
//		return jobMgr.getGenerJobsByName(expr, relaxed);
//	}
//
//	public GncGncJob getGenerJobByNameUniq(final String name)
//			throws NoEntryFoundException, TooManyEntriesFoundException {
//		return jobMgr.getGenerJobByNameUniq(name);
//	}

	public Collection<GncGncJob> getGenerJobs() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		ArrayList<GncGncJob> result = new ArrayList<GncGncJob>();
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncGncJob ) {
				result.add( (GncGncJob) bookElement );
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------

	public GncCommodity getCommodityByID(final String nameSpace, final String code) {
		for ( GncCommodity cmdty : getCommodities() ) {
			if ( cmdty.getCmdtySpace().equals( nameSpace ) &&
				 cmdty.getCmdtyId().equals( code ) )
			{
				return cmdty;
			}
		}
		
		return null;
	}

	public GncCommodity getCommodityByXCode(final String xCode) {
		for ( GncCommodity cmdty : getCommodities() ) {
			if ( cmdty.getCmdtyXcode().equals( xCode ) ) {
				return cmdty;
			}
		}
		
		return null;
	}

//	public List<GncCommodity> getCommoditiesByName(final String expr) {
//		return cmdtyMgr.getCommoditiesByName(expr);
//	}
//
//	public List<GncCommodity> getCommoditiesByName(final String expr, final boolean relaxed) {
//		return cmdtyMgr.getCommoditiesByName(expr, relaxed);
//	}
//
//	public GncCommodity getCommodityByNameUniq(final String expr)
//			throws NoEntryFoundException, TooManyEntriesFoundException {
//		return cmdtyMgr.getCommodityByNameUniq(expr);
//	}

	public List<GncCommodity> getCommodities() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		ArrayList<GncCommodity> result = new ArrayList<GncCommodity>();
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncCommodity ) {
				result.add( (GncCommodity) bookElement );
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------
	
	public GncBudget getBudgetByID(GncIdType bdgtID) {
		for ( GncBudget bdgt : getBudgets() ) {
			if ( bdgt.getBgtId().getValue().equals( bdgtID.getValue() ) )
				return bdgt;
		}
		
		return null;
	}

//	public Collection<GncBudget> getBudgetsByName(final final String expr) {
//		return bdgtMgr.getBudgetsByName(expr);
//	}
//
//	public Collection<GncBudget> getBudgetsByName(final String expr, final boolean relaxed) {
//		return bdgtMgr.getBudgetsByName(expr, relaxed);
//	}
//
//	public GncBudget getBudgetByNameUniq(final String expr) throws NoEntryFoundException, TooManyEntriesFoundException {
//		return bdgtMgr.getBudgetByNameUniq(expr);
//	}

	public Collection<GncBudget> getBudgets() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		ArrayList<GncBudget> result = new ArrayList<GncBudget>();
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncBudget ) {
				result.add( (GncBudget) bookElement );
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------

	/**
	 * @param taxTabID ID of a tax table
	 * @return the identified tax table or null
	 */
	public GncGncTaxTable getTaxTableByID(final GncGncTaxTable.TaxtableGuid taxTabID) {
		for ( GncGncTaxTable taxTab : getTaxTables() ) {
			if ( taxTab.getTaxtableGuid().getValue().equals( taxTabID.getValue() ) )
				return taxTab;
		}
		
		return null;
	}

//	/**
//	 * @param name Name of a tax table
//	 * @return the identified tax table or null
//	 */
//	public GncGncTaxTable getTaxTableByName(final String name) {
//		return taxTabMgr.getTaxTableByName(name);
//	}

	/**
	 * @return all TaxTables defined in the book
	 */
	public Collection<GncGncTaxTable> getTaxTables() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		ArrayList<GncGncTaxTable> result = new ArrayList<GncGncTaxTable>();
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncGncTaxTable ) {
				result.add( (GncGncTaxTable) bookElement );
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------

	/**
	 * @param bllTrmID ID of a bill terms item
	 * @return the identified bill terms item or null
	 */
	public GncGncBillTerm getBillTermsByID(final GncGncBillTerm.BilltermGuid bllTrmID) {
		for ( GncGncBillTerm bllTrm : getBillTerms() ) {
			if ( bllTrm.getBilltermGuid().getValue().equals( bllTrmID.getValue() ) )
				return bllTrm;
		}
		
		return null;
	}

//	/**
//	 * @param name Name of a bill terms item
//	 * @return the identified bill-terms item or null
//	 */
//	public GncGncBillTerm getBillTermsByName(final String name) {
//		return bllTrmMgr.getBillTermsByName(name);
//	}

	/**
	 * @return all TaxTables defined in the book
	 */
	public Collection<GncGncBillTerm> getBillTerms() {
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		ArrayList<GncGncBillTerm> result = new ArrayList<GncGncBillTerm>();
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncGncBillTerm ) {
				result.add( (GncGncBillTerm) bookElement );
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public Price getPriceByID(final Price.PriceId prcID) {
		for ( Price prc : getPrices() ) {
			if ( prc.getPriceId().getValue().equals( prcID.getValue() ) )
				return prc;
		}
		
		return null;
	}
	
	public Price getPriceByCmdtyDate(final Price.PriceCommodity cmdtyID, final Price.PriceTime date) {
		for ( Price prc : getPrices() ) {
			if ( prc.getPriceCommodity().getCmdtySpace().equals( cmdtyID.getCmdtySpace() ) &&
				 prc.getPriceCommodity().getCmdtyId().equals( cmdtyID.getCmdtyId() ) &&
				 prc.getPriceTime().getTsDate().equals( date.getTsDate() ))
				return prc;
		}
		
		return null;
	}
	
	public Price getPriceByCmdtyDate(final Price.PriceCurrency currID, final Price.PriceTime date) {
		for ( Price prc : getPrices() ) {
			if ( prc.getPriceCommodity().getCmdtySpace().equals( currID.getCmdtySpace() ) &&
				 prc.getPriceCommodity().getCmdtyId().equals( currID.getCmdtyId() ) &&
				 prc.getPriceTime().getTsDate().equals( date.getTsDate() ))
				return prc;
		}
		
		return null;
	}
	
	// ::TODO: Fallunterscheidung Commodity / Currency
//	public Price getPriceByCmdtyDate(final String cmdtyNameSpace, final String cmdtyCode, final LocalDate date) {
//		for ( Price prc : getPrices() ) {
//			if ( prc.getPriceCommodity().getValue().equals( prcID.getValue() ) )
//				return prc;
//		}
//		
//		return null;
//	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<Price> getPrices() {
		if ( priceDB == null ) {
			throw new IllegalStateException("price DB is null");
		}
		
		ArrayList<Price> result = new ArrayList<Price>();
		
		for ( Price prc : priceDB.getPrice() ) {
			result.add( (Price) prc );
		}
		
		return result;
	}

	// -----------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public List<Slot> getBookSlots() {
		if ( rootElement == null ) {
			throw new IllegalStateException("root element is null");
		}
		
		return book.getBookSlots().getSlot();
	}

	// ---------------------------------------------------------------

	/**
	 * @return the underlying JAXB-element
	 */
	public GncV2 getRootElement() {
		return rootElement;
	}

	/**
	 * Set the new root-element and load all accounts, transactions,... from it.
	 *
	 * @param rootElt the new root-element
	 */
	protected void setRootElement(final GncV2 rootElt, boolean withProgBar) {
		if ( rootElt == null ) {
			throw new IllegalArgumentException("argument <rootElt> is null");
		}

		this.rootElement = rootElt;
	}

	protected void checkForUnknownBookElts()
	{
		if ( book == null ) {
			throw new IllegalStateException("book is null");
		}
		
		for ( Object bookElement : book.getBookElements() ) {
			if ( bookElement instanceof GncTransaction ) {
				continue;
			} else if ( bookElement instanceof GncSchedxaction ) {
				continue;
			} else if ( bookElement instanceof GncTemplateTransactions ) {
				continue;
			} else if ( bookElement instanceof GncAccount ) {
				continue;
			} else if ( bookElement instanceof GncTransaction ) {
				continue;
			} else if ( bookElement instanceof GncGncInvoice ) {
				continue;
			} else if ( bookElement instanceof GncGncEntry ) {
				continue;
			} else if ( bookElement instanceof GncGncCustomer ) {
				continue;
			} else if ( bookElement instanceof GncGncVendor ) {
				continue;
			} else if ( bookElement instanceof GncGncEmployee ) {
				continue;
			} else if ( bookElement instanceof GncGncJob ) {
				continue;
			} else if ( bookElement instanceof GncCommodity ) {
				continue;
			} else if ( bookElement instanceof GncBudget ) {
				continue;
			} else if ( bookElement instanceof GncGncTaxTable ) {
				continue;
			} else if ( bookElement instanceof GncGncBillTerm ) {
				continue;
			} else if ( bookElement instanceof GncPricedb ) {
				continue;
			}

			throw new IllegalArgumentException("<gnc:book> contains unknown element [" + bookElement.getClass().getName() + "]");
		}
	}
	
	public GncBook getBook() {
		return book;
	}

	public GncPricedb getPriceDB() {
		return priceDB;
	}

	// ---------------------------------------------------------------

	/**
	 * @return the jaxb object-factory used to create new peer-objects to extend
	 *         this
	 */
	public ObjectFactory getObjectFactory() {
		if ( myJAXBFactory == null ) {
			myJAXBFactory = new ObjectFactory();
		}
		return myJAXBFactory;
	}

	/**
	 * @return the JAXB-context
	 */
	protected JAXBContext getJAXBContext() {
		if ( myJAXBContext == null ) {
			try {
				myJAXBContext = JAXBContext.newInstance("org.gnucash.api.generated", this.getClass().getClassLoader());
			} catch (JAXBException e) {
				LOGGER.error("getJAXBContext: " + e.getMessage(), e);
			}
		}
		return myJAXBContext;
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public GnuCashFile_LL getGncFile() {
		return this;
	}

    // ---------------------------------------------------------------
    
    public void dump() {
    	dump(System.out);
    }
    
    public void dump(PrintStream strm) {
    	strm.println("GNUCASH FILE (LOW-LEVEL)");
    	
    	// ------------------------

    	strm.println("");
    	strm.println("CONTENTS:"); 

    	strm.println("");
    	strm.println("Accounts:");
    	for ( GncAccount acct : getAccounts() ) {
    		strm.println(" - " + acct.toString());
    	}

    	strm.println("");
    	strm.println("Transactions:");
    	for ( GncTransaction trx : getTransactions() ) {
    		strm.println(" - " + trx.toString());
        	for ( GncTransaction.TrnSplits.TrnSplit splt : trx.getTrnSplits().getTrnSplit() ) {
        		strm.println("   o " + splt.toString());
        	}
    	}

    	strm.println("");
    	strm.println("(Generic) Invoices:");
    	for ( GncGncInvoice invc : getGenerInvoices() ) {
    		strm.println(" - " + invc.toString());
    		GncGncEntry.EntryInvoice invcID = new GncGncEntry.EntryInvoice();
    		invcID.setType( Const.XML_DATA_TYPE_GUID );
    		invcID.setValue( invc.getInvoiceId() );
        	for ( GncGncEntry entr : getGenerInvoiceEntriesByInvcID(invcID) ) {
        		strm.println("   o " + entr.toString());
        	}
    	}

    	strm.println("");
    	strm.println("Customers:");
    	for ( GncGncCustomer cust : getCustomers() ) {
    		strm.println(" - " + cust.toString());
    	}

    	strm.println("");
    	strm.println("Vendors:");
    	for ( GncGncVendor vend : getVendors() ) {
    		strm.println(" - " + vend.toString());
    	}

    	strm.println("");
    	strm.println("Employees:");
    	for ( GncGncEmployee empl : getEmployees() ) {
    		strm.println(" - " + empl.toString());
    	}

    	strm.println("");
    	strm.println("(Generic) Jobs:");
    	for ( GncGncJob job : getGenerJobs() ) {
    		strm.println(" - " + job.toString());
    	}

    	strm.println("");
    	strm.println("Commodities:");
    	for ( GncCommodity cmdty : getCommodities() ) {
    		strm.println(" - " + cmdty.toString());
    	}

    	strm.println("");
    	strm.println("Budgets:");
    	for ( GncBudget bdgt : getBudgets() ) {
    		strm.println(" - " + bdgt.toString());
    	}

    	strm.println("");
    	strm.println("Tax Tables:");
    	for ( GncGncTaxTable taxTab : getTaxTables() ) {
    		strm.println(" - " + taxTab.toString());
    	}

    	strm.println("");
    	strm.println("Bill Terms:");
    	for ( GncGncBillTerm bllTrm : getBillTerms() ) {
    		strm.println(" - " + bllTrm.toString());
    	}

    	strm.println("");
    	strm.println("Prices:");
    	for ( Price prc : getPrices() ) {
    		strm.println(" - " + prc.toString());
    	}
    }

	// ---------------------------------------------------------------

	public String toString() {
		String result = "GnuCashFile_LL [\n";

		result += "NOT IMPLEMENTED YET";
		
		result += "]";

		return result;
	}

}
