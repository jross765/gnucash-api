package org.gnucash.api_ll.read;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;

import org.gnucash.api.ConstTest;
import org.gnucash.api.generated.Slot;
import org.gnucash.api_ll.read.hlp.SlotHandler;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashFile_LL {
	private GnuCashFile_LL gncFile  = null;
	private GnuCashFile_LL gncFile2 = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashFile_LL.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshFileStream = null;
		InputStream gcshFileStream2 = null;
		try {
			gcshFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME_SIMPLE);
			gcshFileStream2 = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME_SIMPLE);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gncFile = new GnuCashFile_LL(gcshFileStream);
			gncFile2 = new GnuCashFile_LL(gcshFileStream2);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		assertEquals(ConstTest.Stats.NOF_ACCT, gncFile.getAccounts().size());
	}

	@Test
	public void test02() throws Exception {
		assertEquals(ConstTest.Stats.NOF_TRX, gncFile.getTransactions().size());
	}

	@Test
	public void test03() throws Exception {
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gncFile.getTransactionSplits().size());
	}

	@Test
	public void test04() throws Exception {
		assertEquals(ConstTest.Stats.NOF_GENER_INVC, gncFile.getGenerInvoices().size());
	}

	@Test
	public void test05() throws Exception {
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR, gncFile.getGenerInvoiceEntries().size());
	}

	// ------------------------------

	@Test
	public void test06() throws Exception {
		assertEquals(ConstTest.Stats.NOF_CUST, gncFile.getCustomers().size());
	}

	@Test
	public void test07() throws Exception {
		assertEquals(ConstTest.Stats.NOF_VEND, gncFile.getVendors().size());
	}

	@Test
	public void test08() throws Exception {
		assertEquals(ConstTest.Stats.NOF_EMPL, gncFile.getEmployees().size());
	}

	@Test
	public void test09() throws Exception {
		assertEquals(ConstTest.Stats.NOF_GENER_JOB, gncFile.getGenerJobs().size());
	}

	// ------------------------------

	@Test
	public void test10() throws Exception {
		// CAUTION: This one is an exception:
		// There is one additional, internal commodity object: 
		// The "template".
		assertEquals(ConstTest.Stats.NOF_CMDTY_ALL + 1, gncFile.getCommodities().size());
	}

	@Test
	public void test11() throws Exception {
		assertEquals(ConstTest.Stats.NOF_PRC, gncFile.getPrices().size());
	}

	// ------------------------------

	@Test
	public void test12() throws Exception {
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gncFile.getTaxTables().size());
	}

	@Test
	public void test13() throws Exception {
		assertEquals(ConstTest.Stats.NOF_BLLTRM, gncFile.getBillTerms().size());
	}

	// -----------------------------------------------------------------

	@Test
	public void test20() {
		assertEquals("CURRENCY", gncFile.getDefaultCurrencyID().getCmdtySpace());
		assertEquals("EUR", gncFile.getDefaultCurrencyID().getCmdtyId());
		assertEquals("EUR", gncFile.getDefaultCurrencyIDStr());
	}

	@Test
	public void test21() {
		// [counter_formats, counters, features, options, remove-color-not-set-slots]
		assertEquals(5, gncFile.getBookSlots().size());
		
		// ---
		
		assertEquals("counter_formats", gncFile.getBookSlots().get(0).getSlotKey());
		assertEquals("frame", gncFile.getBookSlots().get(0).getSlotValue().getType());
		// Slot leaves on level 1:
		List<Object> subSlotsObj = gncFile.getBookSlots().get(0).getSlotValue().getContent();
		assertEquals(17, subSlotsObj.size()); // ::TODO: ??
		List<Slot> subSlots = SlotHandler.convertList( subSlotsObj );
		assertEquals(8, subSlots.size());
		assertEquals(null, SlotHandler.getSlotValCoreList(subSlots, "gncBill"));
		
		// ---
		
		assertEquals("counters", gncFile.getBookSlots().get(1).getSlotKey());
		assertEquals("frame", gncFile.getBookSlots().get(1).getSlotValue().getType());
		// Slot leaves on level 1:
		subSlotsObj = gncFile.getBookSlots().get(1).getSlotValue().getContent();
		assertEquals(17, subSlotsObj.size()); // ::TODO: ??
		subSlots = SlotHandler.convertList( subSlotsObj );
		assertEquals(8, subSlots.size()); // ::TODO: ??
		assertEquals("1", SlotHandler.getSlotValCoreList(subSlots, "gncBill"));

		// ---
		
		assertEquals("features", gncFile.getBookSlots().get(2).getSlotKey());
		assertEquals("frame", gncFile.getBookSlots().get(2).getSlotValue().getType());
		// Slot leaves on level 1:
		subSlotsObj = gncFile.getBookSlots().get(2).getSlotValue().getContent();
		assertEquals(7, subSlotsObj.size()); // ::TODO: ??
		subSlots = SlotHandler.convertList( subSlotsObj );
		assertEquals(3, subSlots.size());
		// CAUTION:
		// That one does not work because the key contains a '.',
		// which is the hierarchy separator
		// (and also because it's a stupidly chosen key, honestly...)
//		assertEquals("Store the register sort and filter settings in .gcm metadata file (requires at least GnuCash 3.3)", 
//					 Slots.getSlotCoreList(subSlots, "Register sort and filter settings stored in .gcm file"));
		// Instead:
		assertEquals("Register sort and filter settings stored in .gcm file", // <-- You call that a "key"? Come on!
				     subSlots.getFirst().getSlotKey());
		assertEquals("Store the register sort and filter settings in .gcm metadata file (requires at least GnuCash 3.3)", 
				     subSlots.getFirst().getSlotValue().getContent().getFirst());

		// ---
		
		assertEquals("options", gncFile.getBookSlots().get(3).getSlotKey());
		assertEquals("frame", gncFile.getBookSlots().get(3).getSlotValue().getType());
		subSlotsObj = gncFile.getBookSlots().get(3).getSlotValue().getContent();
		assertEquals(7, subSlotsObj.size()); // ::TODO: ??
		subSlots = SlotHandler.convertList( subSlotsObj );
		assertEquals(3, subSlots.size());
		// Here, we don't go further with the above method,
		// because the slots leaves are on level 2.

		// ---
		
		assertEquals("remove-color-not-set-slots", gncFile.getBookSlots().get(4).getSlotKey());
		assertEquals("string", gncFile.getBookSlots().get(4).getSlotValue().getType());
		subSlotsObj = gncFile.getBookSlots().get(4).getSlotValue().getContent();
		assertEquals(1, subSlotsObj.size());
		assertEquals("true", subSlotsObj.getFirst());
		// Here, we have a slot leaf on level 0, for once.
	}

	@Test
	public void test22() {
		// [counter_formats, counters, features, options, remove-color-not-set-slots]
		assertEquals(5, gncFile.getBookSlots().size());
		
		assertEquals(null,   SlotHandler.getSlotValCoreList(gncFile.getBookSlots(), "counter_format.gncBill"));
		assertEquals("1",    SlotHandler.getSlotValCoreList(gncFile.getBookSlots(), "counters.gncBill"));
		assertEquals("f",    SlotHandler.getSlotValCoreList(gncFile.getBookSlots(), "options.Accounts.Use Trading Accounts"));
		assertEquals("true", SlotHandler.getSlotValCoreList(gncFile.getBookSlots(), "remove-color-not-set-slots"));
	}

	// ---------------------------------------------------------------
	// The following test cases seem trivial, obvious, superfluous. 
	// I am not so sure about that. I cannot exactly provide a reason
	// right now, but my gut and my experience tell me that these tests
	// are not as trivial and redundant as they seem to be.

	@Test
	public void test23() throws Exception {
		assertEquals(gncFile.toString(), gncFile2.toString());
	}

	@Test
	public void test24() throws Exception {
		assertEquals(gncFile.getAccounts().size(), gncFile2.getAccounts().size());
		assertEquals(gncFile.getTransactions().size(), gncFile2.getTransactions().size());
		assertEquals(gncFile.getTransactionSplits().size(), gncFile2.getTransactionSplits().size());
		assertEquals(gncFile.getGenerInvoices().size(), gncFile2.getGenerInvoices().size());
		assertEquals(gncFile.getGenerInvoiceEntries().size(), gncFile2.getGenerInvoiceEntries().size());
		assertEquals(gncFile.getCustomers().size(), gncFile2.getCustomers().size());
		assertEquals(gncFile.getVendors().size(), gncFile2.getVendors().size());
		assertEquals(gncFile.getEmployees().size(), gncFile2.getEmployees().size());
		assertEquals(gncFile.getGenerJobs().size(), gncFile2.getGenerJobs().size());
		assertEquals(gncFile.getCommodities().size(), gncFile2.getCommodities().size());
		assertEquals(gncFile.getPrices().size(), gncFile2.getPrices().size());
		assertEquals(gncFile.getTaxTables().size(), gncFile2.getTaxTables().size());
		assertEquals(gncFile.getBillTerms().size(), gncFile2.getBillTerms().size());
	}

}
