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
import org.gnucash.api.write.aux.GCshWritableBudgetPeriod;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.TestGnuCashWritableBudgetImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshBdgtID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGCshWritableBudgetPeriodImpl {
	private static final GCshBdgtID BDGT_1_ID = TestGnuCashWritableBudgetImpl.BDGT_1_ID;

	private static final GCshAcctID ACCT_2_ID = TestGnuCashWritableBudgetImpl.ACCT_2_ID;

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
		return new JUnit4TestAdapter(TestGCshWritableBudgetPeriodImpl.class);
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
		
		GCshWritableBudgetAccount bdgtAcct = bdgt.getWritableAccounts().get(1); // sic, the second
		assertNotEquals(null, bdgtAcct);
		assertEquals(ACCT_2_ID, bdgtAcct.getAcctID());
		assertEquals(bdgt, bdgtAcct.getParent());
		
		assertEquals(12, bdgtAcct.getPeriods().size());
		
		GCshWritableBudgetPeriod bdgtPrd = bdgtAcct.getWritablePeriods().get(0);
		assertNotEquals(null, bdgtPrd);
		assertEquals(bdgt, bdgtAcct.getParent());
		
		assertEquals(0, bdgtPrd.getIndex().intValue());
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
		
		GCshWritableBudgetAccount bdgtAcct = bdgt.getWritableAccounts().get(1); // sic, the second
		assertNotEquals(null, bdgtAcct);
		assertEquals(ACCT_2_ID, bdgtAcct.getAcctID());
		
		assertEquals(12, bdgtAcct.getPeriods().size());
		GCshWritableBudgetPeriod bdgtPrd = bdgtAcct.getWritablePeriods().get(0);
		assertNotEquals(null, bdgtPrd);
		assertEquals(bdgtAcct, bdgtPrd.getParent());
		
		// ----------------------------
		// Modify the object

		try {
			bdgtPrd.setIndex(BigInteger.valueOf(-1));
			assertEquals(0, 1);
		} catch ( IllegalArgumentException | IllegalStateException exc ) {
			assertEquals(0, 0);
		}
		
		bdgtPrd.setIndex(BigInteger.TWO);

		try {
			bdgtPrd.setAmount(BigFraction.of(-3, 1));
			assertEquals(0, 1);
		} catch ( IllegalArgumentException | IllegalStateException exc ) {
			assertEquals(0, 0);
		}
		
		bdgtPrd.setAmount(BigFraction.of(12345, 100));

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(bdgtAcct, bdgtPrd);

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

	private void test02_1_check_memory(GCshWritableBudgetAccount bdgtAcct, 
									   GCshWritableBudgetPeriod bdgtPrd) throws Exception {
		assertEquals(12, bdgtAcct.getPeriods().size()); // unchanged
		
		assertEquals(bdgtAcct, bdgtPrd.getParent()); // unchanged
		assertEquals(2, bdgtPrd.getIndex().intValue()); // changed
		assertEquals(BigFraction.of(12345, 100), bdgtPrd.getAmount()); // changed
		assertEquals("123,45 €", bdgtPrd.getAmountFormatted(Locale.GERMANY)); // changed
		assertEquals("€123.45", bdgtPrd.getAmountFormatted(Locale.US)); // changed
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		
		GnuCashBudget bdgt = gcshOutFile.getBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);
		assertEquals(BDGT_1_ID, bdgt.getID());
		
		GCshBudgetAccount bdgtAcct = bdgt.getAccounts().get(1); // sic, the second
		assertNotEquals(null, bdgtAcct);
		assertEquals(ACCT_2_ID, bdgtAcct.getAcctID());
		
		assertEquals(12, bdgtAcct.getPeriods().size()); // unchanged
		GCshBudgetPeriod bdgtPrd = bdgtAcct.getPeriods().get(0);
		assertNotEquals(null, bdgtPrd);
		assertEquals(bdgtAcct, bdgtPrd.getParent()); // unchanged
		
		assertEquals(2, bdgtPrd.getIndex().intValue()); // changed
		assertEquals(BigFraction.of(12345, 100), bdgtPrd.getAmount()); // changed
		assertEquals("123,45 €", bdgtPrd.getAmountFormatted(Locale.GERMANY)); // changed
		assertEquals("€123.45", bdgtPrd.getAmountFormatted(Locale.US)); // changed
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
		
		GCshWritableBudgetAccount bdgtAcct = bdgt.getWritableAccounts().get(1); // sic, the second
		assertNotEquals(null, bdgtAcct);
		assertEquals(ACCT_2_ID, bdgtAcct.getAcctID());
		
		assertEquals(12, bdgtAcct.getPeriods().size());
		
		// ----------------------------
		// Bare naked object

		GCshWritableBudgetPeriod bdgtPrd = null;
		try {
			bdgtPrd = bdgtAcct.createWritablePeriod(new BigInteger("-1"), BigFraction.of(12345, 100));
			assertEquals(0, 1);
		} catch ( IllegalArgumentException | IllegalStateException exc ) {
			assertEquals(0, 0);
		}

		try {
			bdgtPrd = bdgtAcct.createWritablePeriod(BigInteger.ZERO, BigFraction.of(-3, 1));
			assertEquals(0, 1);
		} catch ( IllegalArgumentException | IllegalStateException exc ) {
			assertEquals(0, 0);
		}

		bdgtPrd = bdgtAcct.createWritablePeriod(BigInteger.ZERO, BigFraction.of(12345, 100));
		assertNotEquals(null, bdgtPrd);
		
		// ----------------------------
		// Check whether the object has actually been modified
		// (in memory, not in the file yet).

		test03_1_check_memory(bdgtAcct, bdgtPrd);

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

	private void test03_1_check_memory(GCshBudgetAccount bdgtAcct, 
									   GCshWritableBudgetPeriod bdgtPrd) throws Exception {
		assertEquals(13, bdgtAcct.getPeriods().size()); // changed
		
		assertEquals(bdgtAcct, bdgtPrd.getParent());
		assertEquals(0, bdgtPrd.getIndex().intValue());
		assertEquals(BigFraction.of(12345, 100), bdgtPrd.getAmount());
		assertEquals("123,45 €", bdgtPrd.getAmountFormatted(Locale.GERMANY));
		assertEquals("€123.45", bdgtPrd.getAmountFormatted(Locale.US));
	}

	private void test03_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		
		GnuCashBudget bdgt = gcshOutFile.getBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);
		assertEquals(BDGT_1_ID, bdgt.getID());
		
		GCshBudgetAccount bdgtAcct = bdgt.getAccounts().get(1); // sic, the second
		assertNotEquals(null, bdgtAcct);
		assertEquals(ACCT_2_ID, bdgtAcct.getAcctID());
		
		assertEquals(13, bdgtAcct.getPeriods().size()); // changed
		
		GCshBudgetPeriod bdgtPrd = bdgtAcct.getPeriods().get( bdgtAcct.getPeriods().size() - 1 );
		assertNotEquals(null, bdgtPrd);
		
		// ---
		
		assertEquals(bdgtAcct, bdgtPrd.getParent());
		assertEquals(0, bdgtPrd.getIndex().intValue());
		assertEquals(BigFraction.of(12345, 100), bdgtPrd.getAmount());
		assertEquals("123,45 €", bdgtPrd.getAmountFormatted(Locale.GERMANY));
		assertEquals("€123.45", bdgtPrd.getAmountFormatted(Locale.US));
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
		
		GCshWritableBudgetAccount bdgtAcct = bdgt.getWritableAccounts().get(1); // sic, the second
		assertNotEquals(null, bdgtAcct);
		assertEquals(ACCT_2_ID, bdgtAcct.getAcctID());
		
		assertEquals(12, bdgtAcct.getPeriods().size());
		
		// ----------------------------
		// Delete the object

		GCshWritableBudgetPeriod bdgtPrd = bdgtAcct.getWritablePeriods().get(0);
		assertNotEquals(null, bdgtPrd);
		bdgtAcct.removePeriod(bdgtPrd);
		
		// ----------------------------
		// Check whether the objects have actually been deleted
		// (in memory, not in the file yet).

		test04_1_check_memory(bdgtAcct, bdgtPrd);

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

	private void test04_1_check_memory(GCshWritableBudgetAccount bdgtAcct,
									   GCshWritableBudgetPeriod bdgtPrd) throws Exception {
		assertEquals(11, bdgtAcct.getPeriods().size()); // changed
		
		// CAUTION / ::TODO
		// Old Object still exists and is unchanged
		assertEquals(0, bdgtPrd.getIndex().intValue()); // unchanged // ::CHECK
		assertEquals(BigFraction.of(1000), bdgtPrd.getAmount()); // unchanged // ::CHECK
		
		// However, the budget period cannot newly be instantiated any more,
		// just as you would expect.
		assertEquals(11, bdgtAcct.getWritablePeriods().size());
		// Iterate through all remaining periods in order to prove
		// that the one we just removed really is not there any more.
		for ( GCshWritableBudgetPeriod bdgtPrd2 : bdgtAcct.getWritablePeriods() ) {
			assertNotEquals(0, bdgtPrd2.getIndex().intValue());
		}
		// Rest: Nothing to do
	}

	private void test04_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		
		// The budget period does not exist any more, just as you would expect.
		// However, no exception is thrown, as opposed to test04_1_check_memory()
		GnuCashBudget bdgt = gcshInFile.getBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);
		assertEquals(BDGT_1_ID, bdgt.getID());
		
		GCshBudgetAccount bdgtAcct = bdgt.getAccount(ACCT_2_ID);
		assertNotEquals(null, bdgtAcct);
		
		assertEquals(11, bdgtAcct.getPeriods().size()); // changed
		
		// Iterate through all remaining periods in order to prove
		// that the one we just removed really is not there any more.
		for ( GCshBudgetPeriod bdgtPrd2 : bdgtAcct.getPeriods() ) {
			assertNotEquals(0, bdgtPrd2.getIndex().intValue());
		}
		// Rest: Nothing to do
	}

	// ------------------------------
	// PART 4.2: Low-Level
	// ------------------------------

	// ::TODO

}
