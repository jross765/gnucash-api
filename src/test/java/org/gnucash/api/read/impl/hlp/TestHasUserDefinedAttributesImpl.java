package org.gnucash.api.read.impl.hlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_Exchange;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_SecIdType;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestHasUserDefinedAttributesImpl {
	public static final GCshAcctID ACCT_1_ID = new GCshAcctID( "b3741e92e3b9475b9d5a2dc8254a8111" ); // SAP
	public static final GCshAcctID ACCT_2_ID = new GCshAcctID( "7ee6fe4de6db46fd957f3513c9c6f983" ); // Depots
	public static final GCshAcctID ACCT_3_ID = new GCshAcctID( "3b0e56552514420da0e2cec300f64ce6" ); // Aktiva

	public static final GCshTrxID TRX_1_ID = new GCshTrxID( "18a45dfc8a6868c470438e27d6fe10b2" );
	public static final GCshTrxID TRX_2_ID = new GCshTrxID( "cc9fe6a245df45ba9b494660732a7755" );
	public static final GCshTrxID TRX_3_ID = new GCshTrxID( "d465b802d5c940c9bba04b87b63ba23f" );

	public static final GCshGenerInvcID INVC_2_ID = new GCshGenerInvcID( "8de4467c17e04bb2895fb68cc07fc4df" );
	public static final GCshGenerInvcID INVC_3_ID = new GCshGenerInvcID( "169331c9860642cf84b04f3e3151058a" );

	public static final GCshCmdtyID_Exchange  CMDTY_1_ID = new GCshCmdtyID_Exchange( GCshCmdtyCurrNameSpace.Exchange.EURONEXT, "MBG" );
	public static final GCshCmdtyID_SecIdType CMDTY_2_ID = new GCshCmdtyID_SecIdType( GCshCmdtyCurrNameSpace.SecIdType.ISIN, "DE000BASF111" );

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestHasUserDefinedAttributesImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshFileStream = null;
		try {
			gcshFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshFile = new GnuCashFileImpl(gcshFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------
	// Book
	// -----------------------------------------------------------------

	// Several slots
	@Test
	public void test_book_01() throws Exception {
		assertNotEquals(null, gcshFile.getUserDefinedAttributeKeys());

		assertEquals(5, gcshFile.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_BOOK_COUNTER_FORMATS, gcshFile.getUserDefinedAttributeKeys().get(0));
		assertEquals(ConstTest.SLOT_KEY_BOOK_COUNTERS, gcshFile.getUserDefinedAttributeKeys().get(1));
		assertEquals(ConstTest.SLOT_KEY_BOOK_FEATURES, gcshFile.getUserDefinedAttributeKeys().get(2));
		assertEquals(ConstTest.SLOT_KEY_BOOK_OPTIONS, gcshFile.getUserDefinedAttributeKeys().get(3));
		assertEquals(ConstTest.SLOT_KEY_BOOK_REMOVE_COLOR_NOT_SETS_SLOTS, gcshFile.getUserDefinedAttributeKeys().get(4));

		assertEquals(null, gcshFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_COUNTER_FORMATS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"gncBill"));
		assertEquals(null, gcshFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_COUNTER_FORMATS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"gncCustomer"));
		// etc.

		assertEquals("1", gcshFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_COUNTERS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"gncBill"));
		assertEquals("3", gcshFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_COUNTERS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"gncCustomer"));
		// etc.

		// CAUTION: This one does not work because the key contains a dot ("."),
		// which we have chosen as the hierarchy-separator 
		// (it's a very badly chosen key, b.t.w.).
		//    	assertEquals("Store the register sort and filter settings in .gcm metadata file (requires at least GnuCash 3.3)", 
		//    				 gcshFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_FEATURES + 
		//    			                                      HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
		//                                                    "Register sort and filter settings stored in .gcm file"));
		assertEquals("Use a dedicated opening balance account identified by an 'equity-type' slot (requires at least Gnucash 4.3)", 
				gcshFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_FEATURES + 
						HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
						"Use a dedicated opening balance account identified by an 'equity-type' slot"));

		assertEquals("f", gcshFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_OPTIONS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"Accounts"+ 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"Use Trading Accounts"));
		assertEquals("83b1859fd415421cb24f8c72eb755fcc", 
				gcshFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_OPTIONS + 
						HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
						"Business.Default Customer TaxTable"));
		assertEquals(null, gcshFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_OPTIONS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"Tax" + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"Tax Number"));

		assertEquals("true", gcshFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_REMOVE_COLOR_NOT_SETS_SLOTS));
	}

	// -----------------------------------------------------------------
	// Account
	// -----------------------------------------------------------------

	// No slots
	@Test
	public void test_acct_01() throws Exception {
		GnuCashAccount acct = gcshFile.getAccountByID(ACCT_1_ID);
		assertNotEquals(null, acct);

		assertEquals(1, acct.getUserDefinedAttributeKeys().size());
		assertEquals("lot-mgmt", acct.getUserDefinedAttributeKeys().get(0));
	}

	// One slot
	@Test
	public void test_acct_02() throws Exception {
		GnuCashAccount acct = gcshFile.getAccountByID(ACCT_2_ID);
		assertNotEquals(null, acct);

		assertNotEquals(null, acct.getUserDefinedAttributeKeys());
		assertEquals(1, acct.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_ACCT_PLACEHOLDER, acct.getUserDefinedAttributeKeys().get(0));
		assertEquals("true", acct.getUserDefinedAttribute(ConstTest.SLOT_KEY_ACCT_PLACEHOLDER));
	}

	// Account, several slots
	// Such a case does not exist, at least not with "organic" data

	// -----------------------------------------------------------------
	// Transaction
	// -----------------------------------------------------------------

	// No slots
	@Test
	public void test_trx_01() throws Exception {
		GnuCashTransaction trx = gcshFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, trx);

		assertEquals(null, trx.getUserDefinedAttributeKeys());
	}

	// One slot
	@Test
	public void test_trx_02() throws Exception {
		GnuCashTransaction trx = gcshFile.getTransactionByID(TRX_2_ID);
		assertNotEquals(null, trx);

		assertNotEquals(null, trx.getUserDefinedAttributeKeys());
		assertEquals(1, trx.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_TRX_DATE_POSTED, trx.getUserDefinedAttributeKeys().get(0));
		assertEquals("2023-07-01", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_TRX_DATE_POSTED));
	}

	// Several slots
	@Test
	public void test_trx_03() throws Exception {
		GnuCashTransaction trx = gcshFile.getTransactionByID(TRX_3_ID);
		assertNotEquals(null, trx);

		assertNotEquals(null, trx.getUserDefinedAttributeKeys());
		assertEquals(2, trx.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_ASSOC_URI, trx.getUserDefinedAttributeKeys().get(0));
		assertEquals(ConstTest.SLOT_KEY_TRX_DATE_POSTED, trx.getUserDefinedAttributeKeys().get(1));
		assertEquals("https://my.transaction.link.01", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_ASSOC_URI));
		assertEquals("2023-10-01", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_TRX_DATE_POSTED));
	}

	// -----------------------------------------------------------------
	// Transaction Split
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Invoice
	// -----------------------------------------------------------------

	// No slots
	// Such a case does not exist, at least not with "organic" data

	// One slot
	@Test
	public void test_invc_02() throws Exception {
		GnuCashGenerInvoice invc = gcshFile.getGenerInvoiceByID(INVC_2_ID);
		assertNotEquals(null, invc);

		assertNotEquals(null, invc.getUserDefinedAttributeKeys());
		assertEquals(1, invc.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE, invc.getUserDefinedAttributeKeys().get(0));
		assertEquals("0", invc.getUserDefinedAttribute(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE));
	}

	// Several slots
	@Test
	public void test_invc_03() throws Exception {
		GnuCashGenerInvoice trx = gcshFile.getGenerInvoiceByID(INVC_3_ID);
		assertNotEquals(null, trx);

		assertNotEquals(null, trx.getUserDefinedAttributeKeys());
		assertEquals(2, trx.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_ASSOC_URI, trx.getUserDefinedAttributeKeys().get(0));
		assertEquals(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE, trx.getUserDefinedAttributeKeys().get(1));
		assertEquals("https://my.job.invoice.link.01", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_ASSOC_URI));
		assertEquals("0", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE));
	}

	// -----------------------------------------------------------------
	// Invoice Entry
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Commodity
	// -----------------------------------------------------------------

	// No slots
	@Test
	public void test_cmdty_01() throws Exception {
		GnuCashCommodity cmdty = gcshFile.getCommodityByQualifID(CMDTY_1_ID);
		assertNotEquals(null, cmdty);

		assertEquals(null, cmdty.getUserDefinedAttributeKeys());
	}

	// One slot
	@Test
	public void test_cmdty_02() throws Exception {
		GnuCashCommodity cmdty = gcshFile.getCommodityByQualifID(CMDTY_2_ID);
		assertNotEquals(null, cmdty);

		assertNotEquals(null, cmdty.getUserDefinedAttributeKeys());
		assertEquals(1, cmdty.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_CMDTY_USER_SYMBOL, cmdty.getUserDefinedAttributeKeys().get(0));
		assertEquals("BAS", cmdty.getUserDefinedAttribute(ConstTest.SLOT_KEY_CMDTY_USER_SYMBOL));
	}

	// Several slots
	// There are none with several slots, at least not "organically"

	// -----------------------------------------------------------------
	// Customer
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Vendor
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Employee
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Stats/Meta
	// -----------------------------------------------------------------

	//    @Test
	//    public void test_meta() throws Exception {
	//    	for ( GnuCashTransactionSplit elt : gcshFile.getTransactionSplits() ) {
	//    		if ( elt.getUserDefinedAttributeKeys() != null ) {
	//    			if ( elt.getUserDefinedAttributeKeys().size() == 1 ) {
	//    				System.err.println("yyy splt: " + elt.getID() );
	//    			}
	//    		}
	//    	}
	//
	//    	for ( GnuCashGenerInvoiceEntry elt : gcshFile.getGenerInvoiceEntries() ) {
	//    		if ( elt.getUserDefinedAttributeKeys() != null ) {
	//    			if ( elt.getUserDefinedAttributeKeys().size() == 1 ) {
	//    				System.err.println("yyy invc-entr: " + elt.getID() );
	//    			}
	//    		}
	//    	}
	//
	//    	for ( GnuCashCommodity elt : gcshFile.getCommodities() ) {
	//    		if ( elt.getUserDefinedAttributeKeys() != null ) {
	//    			if ( elt.getUserDefinedAttributeKeys().size() == 1 ) {
	//    				System.err.println("yyy cmdty: " + elt.getQualifID() );
	//    			}
	//    		}
	//    	}
	//
	//    	for ( GnuCashCustomer elt : gcshFile.getCustomers() ) {
	//    		if ( elt.getUserDefinedAttributeKeys() != null ) {
	//    			if ( elt.getUserDefinedAttributeKeys().size() == 1 ) {
	//    				System.err.println("yyy cust: " + elt.getID() );
	//    			}
	//    		}
	//    	}
	//
	//    	for ( GnuCashVendor elt : gcshFile.getVendors() ) {
	//    		if ( elt.getUserDefinedAttributeKeys() != null ) {
	//    			if ( elt.getUserDefinedAttributeKeys().size() == 1 ) {
	//    				System.err.println("yyy vend: " + elt.getID() );
	//    			}
	//    		}
	//    	}
	//
	//    	for ( GnuCashEmployee elt : gcshFile.getEmployees() ) {
	//    		if ( elt.getUserDefinedAttributeKeys() != null ) {
	//    			if ( elt.getUserDefinedAttributeKeys().size() == 1 ) {
	//    				System.err.println("yyy empl: " + elt.getID() );
	//    			}
	//    		}
	//    	}
	//
	//    }

}
