package org.gnucash.api.write.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Locale;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.aux.GCshBudgetAccount;
import org.gnucash.api.read.aux.GCshBudgetPeriod;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.write.GnuCashWritableBudget;
import org.gnucash.api.write.aux.GCshWritableBudgetAccount;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.TestGnuCashWritableBudgetImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshBdgtID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGCshWritableBudgetAccountImpl {
	private static final GCshBdgtID BDGT_1_ID = TestGnuCashWritableBudgetImpl.BDGT_1_ID;
	// private static final GCshBdgtID BDGT_2_ID = TestGnuCashWritableBudgetImpl.BDGT_2_ID;

	private static final GCshAcctID ACCT_1_ID = TestGnuCashWritableBudgetImpl.ACCT_1_ID;
	private static final GCshAcctID ACCT_2_ID = TestGnuCashWritableBudgetImpl.ACCT_2_ID;
	private static final GCshAcctID ACCT_6_ID = new GCshAcctID("aa8e4dac1bd141468c1eca045598a52b");

	// -----------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	// https://stackoverflow.com/questions/11884141/deleting-file-and-directory-in-junit
	@SuppressWarnings("exports")
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGCshWritableBudgetAccountImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCsh_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshInFileStream = null;
		try {
			gcshInFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME_IN);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshInFile = new GnuCashWritableFileImpl(gcshInFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash in-file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------
	// PART 1: Read existing objects as modifiable ones
	// (and see whether they are fully symmetrical to their read-only
	// counterparts)
	// -----------------------------------------------------------------
	// Cf. TestGnuCashBudget.test01/02
	//
	// Check whether the GnuCashWritableBudget objects returned by
	// GnuCashWritableFileImpl.getWritableBudgetByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getBudgetByID().

	@Test
	public void test01() throws Exception {
		GnuCashWritableBudget bdgt = gcshInFile.getWritableBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);
		assertEquals(BDGT_1_ID, bdgt.getID());
		
		GCshWritableBudgetAccount bdgtAcct = bdgt.getWritableAccounts().get(1); // sic, the second one
		assertNotEquals(null, bdgtAcct);
		
		assertEquals(ACCT_2_ID, bdgtAcct.getAcctID());

		assertEquals(12, bdgtAcct.getPeriods().size());
		
		GCshBudgetPeriod bdgtPrd = bdgtAcct.getPeriods().get(0);
		assertEquals(0, bdgtPrd.getIndex().intValue());
		assertEquals(1000.0, bdgtPrd.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("1.000,00 €", bdgtPrd.getAmountFormatted(Locale.GERMANY));
		assertEquals("€1,000.00", bdgtPrd.getAmountFormatted(Locale.US));

		bdgtPrd = bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1);
		assertEquals(9, bdgtPrd.getIndex().intValue());
		assertEquals(1000.0, bdgtPrd.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("1.000,00 €", bdgtPrd.getAmountFormatted(Locale.GERMANY));
		assertEquals("€1,000.00", bdgtPrd.getAmountFormatted(Locale.US));
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableBudget objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		GnuCashWritableBudget bdgt = gcshInFile.getWritableBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);
		assertEquals(BDGT_1_ID, bdgt.getID());
		
		GCshWritableBudgetAccount bdgtAcct = bdgt.getWritableAccounts().get(0);
		assertNotEquals(null, bdgtAcct);
		assertEquals(ACCT_1_ID, bdgtAcct.getAcctID());

		assertEquals(1, bdgtAcct.getPeriods().size()); // unchanged
		
		// ----------------------------
		// Modify the object

		bdgtAcct.setAcctID(ACCT_6_ID);

		// ::TODO modify period entries

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(bdgtAcct);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test02_1_check_persisted(outFile);
	}

//	@Test
//	public void test02_2() throws Exception {
//		// ::TODO
//	}
	
	// ---------------------------------------------------------------

	private void test02_1_check_memory(GCshWritableBudgetAccount bdgtAcct) throws Exception {
		assertEquals(ACCT_6_ID, bdgtAcct.getAcctID()); // changed
		
		assertEquals(1, bdgtAcct.getPeriods().size());
		
		GCshBudgetPeriod bdgtPrd = bdgtAcct.getPeriods().get(0);
		assertEquals(5, bdgtPrd.getIndex().intValue()); // unchanged
		assertEquals(100.0, bdgtPrd.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE); // unchanged
		assertEquals("100,00 €", bdgtPrd.getAmountFormatted(Locale.GERMANY)); // unchanged
		assertEquals("€100.00", bdgtPrd.getAmountFormatted(Locale.US)); // unchanged
		// Rest is redundant, because just one period in this account
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		
		GnuCashBudget bdgt = gcshOutFile.getBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);
		assertEquals(BDGT_1_ID, bdgt.getID());
		
		GCshBudgetAccount bdgtAcct = bdgt.getAccounts().get(0);
		assertNotEquals(null, bdgtAcct);
		
		assertEquals(ACCT_6_ID, bdgtAcct.getAcctID()); // changed
		
		assertEquals(1, bdgtAcct.getPeriods().size()); // unchanged
		
		GCshBudgetPeriod bdgtPrd = bdgtAcct.getPeriods().get(0);
		assertEquals(5, bdgtPrd.getIndex().intValue()); // unchanged
		assertEquals(100.0, bdgtPrd.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE); // unchanged
		assertEquals("100,00 €", bdgtPrd.getAmountFormatted(Locale.GERMANY)); // unchanged
		assertEquals("€100.00", bdgtPrd.getAmountFormatted(Locale.US)); // unchanged
		// Rest is redundant, because just one period in this account
	}

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 3.1: High-Level
	// ------------------------------

	@Test
	public void test03_1() throws Exception {
		GnuCashWritableBudget bdgt = gcshInFile.getWritableBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);
		assertEquals(BDGT_1_ID, bdgt.getID());
		
		assertEquals(5, bdgt.getAccounts().size());
		
		// ----------------------------
		// Bare naked object

		GCshWritableBudgetAccount bdgtAcct = bdgt.createWritableAccount(ACCT_6_ID);
		assertNotEquals(null, bdgtAcct);
		
		assertEquals(ACCT_6_ID, bdgtAcct.getAcctID());
		assertEquals(0, bdgtAcct.getPeriods().size());

		// ----------------------------
		// Modify the object
		
		bdgtAcct.createWritablePeriod(new BigInteger("0"), BigFraction.of(100));
		bdgtAcct.createWritablePeriod(new BigInteger("1"), BigFraction.of(200));
		bdgtAcct.createWritablePeriod(new BigInteger("2"), BigFraction.of(300));

		// ----------------------------
		// Check whether the object has actually been modified
		// (in memory, not in the file yet).

		test03_1_check_memory(bdgt, bdgtAcct);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test03_1_check_persisted(outFile);
	}

	// ---------------------------------------------------------------

	private void test03_1_check_memory(GnuCashWritableBudget bdgt, 
									   GCshWritableBudgetAccount bdgtAcct) throws Exception {
		assertEquals(6, bdgt.getAccounts().size()); // changed
		
		assertEquals(ACCT_6_ID, bdgtAcct.getAcctID());
		
		assertEquals(3, bdgtAcct.getPeriods().size());

		GCshBudgetPeriod bdgtPrd = bdgtAcct.getPeriods().get(0);
		assertEquals(0, bdgtPrd.getIndex().intValue());
		assertEquals(100.0, bdgtPrd.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);

		bdgtPrd = bdgtAcct.getPeriods().get(1);
		assertEquals(1, bdgtPrd.getIndex().intValue());
		assertEquals(200.0, bdgtPrd.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);

		bdgtPrd = bdgtAcct.getPeriods().get(2);
		assertEquals(2, bdgtPrd.getIndex().intValue());
		assertEquals(300.0, bdgtPrd.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	private void test03_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		
		GnuCashBudget bdgt = gcshOutFile.getBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);
		assertEquals(BDGT_1_ID, bdgt.getID());
		
		assertEquals(6, bdgt.getAccounts().size()); // changed
		
		GCshBudgetAccount bdgtAcct = bdgt.getAccounts().get( bdgt.getAccounts().size() - 1 );
		assertNotEquals(null, bdgtAcct);
		
		// ---
		
		assertEquals(ACCT_6_ID, bdgtAcct.getAcctID());
		
		assertEquals(3, bdgtAcct.getPeriods().size());

		GCshBudgetPeriod bdgtPrd = bdgtAcct.getPeriods().get(0);
		assertEquals(0, bdgtPrd.getIndex().intValue());
		assertEquals(100.0, bdgtPrd.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);

		bdgtPrd = bdgtAcct.getPeriods().get(1);
		assertEquals(1, bdgtPrd.getIndex().intValue());
		assertEquals(200.0, bdgtPrd.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);

		bdgtPrd = bdgtAcct.getPeriods().get(2);
		assertEquals(2, bdgtPrd.getIndex().intValue());
		assertEquals(300.0, bdgtPrd.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	// ------------------------------
	// PART 3.2: Low-Level
	// ------------------------------

	// ::TODO

	// -----------------------------------------------------------------
	// PART 4: Delete objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 4.1: High-Level
	// ------------------------------

	@Test
	public void test04_1() throws Exception {
		GnuCashWritableBudget bdgt = gcshInFile.getWritableBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);
		assertEquals(BDGT_1_ID, bdgt.getID());
		
		assertEquals(5, bdgt.getAccounts().size());
		
		// ----------------------------
		// Delete the object

		GCshWritableBudgetAccount bdgtAcct = bdgt.getWritableAccount(ACCT_1_ID);
		assertNotEquals(null, bdgtAcct);
		bdgt.removeAccount(bdgtAcct);

		// ----------------------------
		// Check whether the objects have actually been deleted
		// (in memory, not in the file yet).

		test04_1_check_memory(bdgt, bdgtAcct);

		// ----------------------------
		// Now, check whether the deletions have been written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test04_1_check_persisted(outFile);
	}

	// ---------------------------------------------------------------

	private void test04_1_check_memory(GnuCashWritableBudget bdgt,
									   GCshWritableBudgetAccount bdgtAcct) throws Exception {
		assertEquals(4, bdgt.getAccounts().size()); // changed
		
		// CAUTION / ::TODO
		// Old Object still exists and is unchanged
		assertEquals(ACCT_1_ID, bdgtAcct.getAcctID()); // unchanged
		
		assertEquals(1, bdgtAcct.getPeriods().size()); // unchanged
		
		// However, the budget account cannot newly be instantiated any more,
		// just as you would expect.
		try {
			GCshWritableBudgetAccount bdgtAcctNow = bdgt.getWritableAccount(ACCT_1_ID);
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
	}

	private void test04_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);

		// The budget account does not exist any more, just as you would expect.
		// However, no exception is thrown, as opposed to test04_1_check_memory()
		GnuCashWritableBudget bdgt = gcshInFile.getWritableBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);
		assertEquals(BDGT_1_ID, bdgt.getID());
		
		assertEquals(4, bdgt.getAccounts().size()); // changed
		
		GCshBudgetAccount bdgtAcct = bdgt.getAccount(ACCT_1_ID);
		assertEquals(null, bdgtAcct); // sic
	}

	// ------------------------------
	// PART 4.2: Low-Level
	// ------------------------------

	// ::TODO

}
