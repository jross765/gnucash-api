package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.aux.GCshBudgetAccount;
import org.gnucash.api.read.aux.GCshBudgetRecurrence;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashBudgetImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.write.GnuCashWritableBudget;
import org.gnucash.api.write.aux.GCshWritableBudgetAccount;
import org.gnucash.api.write.aux.GCshWritableBudgetRecurrence;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshBdgtID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashWritableBudgetImpl {
	public static final GCshBdgtID BDGT_1_ID = TestGnuCashBudgetImpl.BDGT_1_ID;
	// public static final GCshBdgtID BDGT_2_ID = TestGnuCashBudgetImpl.BDGT_2_ID;

	public static final GCshAcctID ACCT_1_ID = TestGnuCashBudgetImpl.ACCT_1_ID;
	public static final GCshAcctID ACCT_2_ID = TestGnuCashBudgetImpl.ACCT_2_ID;
	public static final GCshAcctID ACCT_3_ID = TestGnuCashBudgetImpl.ACCT_3_ID;
	public static final GCshAcctID ACCT_4_ID = TestGnuCashBudgetImpl.ACCT_4_ID;
	public static final GCshAcctID ACCT_5_ID = TestGnuCashBudgetImpl.ACCT_5_ID;

	// -----------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	private GCshFileStats gcshInFileStats = null;
	private GCshFileStats gcshOutFileStats = null;

	private GCshBdgtID newBdgtID = null;

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
		return new JUnit4TestAdapter(TestGnuCashWritableBudgetImpl.class);
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
		assertEquals("Budget 2026", bdgt.getName());

		assertEquals(bdgt, bdgt.getRecurrence().getParent()); // changed
		assertEquals(1, bdgt.getRecurrence().getMult()); // changed ::CHECK
		assertEquals(GCshBudgetRecurrence.PeriodType.MONTH, bdgt.getRecurrence().getPeriodType()); // changed
		assertEquals(LocalDate.of(2026, 1, 1), bdgt.getRecurrence().getStart()); // changed
		
		assertEquals(5, bdgt.getAccounts().size());
		assertEquals(ACCT_1_ID, bdgt.getAccounts().get(0).getAcctID());
		assertEquals(ACCT_2_ID, bdgt.getAccounts().get(1).getAcctID());
		assertEquals(ACCT_3_ID, bdgt.getAccounts().get(2).getAcctID());
		assertEquals(ACCT_4_ID, bdgt.getAccounts().get(3).getAcctID());
		assertEquals(ACCT_5_ID, bdgt.getAccounts().get(4).getAcctID());

		assertEquals(1,  bdgt.getPeriods(ACCT_1_ID).size());
		assertEquals(12, bdgt.getPeriods(ACCT_2_ID).size());
		assertEquals(2,  bdgt.getPeriods(ACCT_3_ID).size());
		assertEquals(12, bdgt.getPeriods(ACCT_4_ID).size());
		assertEquals(12, bdgt.getPeriods(ACCT_5_ID).size());

		GCshBudgetAccount bdgtAcct = bdgt.getAccounts().get(0);
		assertEquals(bdgt, bdgtAcct.getParent());
		assertEquals(1, bdgtAcct.getPeriods().size());
		
		bdgtAcct = bdgt.getAccounts().get(1);
		assertEquals(bdgt, bdgtAcct.getParent());
		assertEquals(12, bdgtAcct.getPeriods().size());
		
		bdgtAcct = bdgt.getAccounts().get(2);
		assertEquals(bdgt, bdgtAcct.getParent());
		assertEquals(2, bdgtAcct.getPeriods().size());
		
		bdgtAcct = bdgt.getAccounts().get(3);
		assertEquals(bdgt, bdgtAcct.getParent());
		assertEquals(12, bdgtAcct.getPeriods().size());
		
		bdgtAcct = bdgt.getAccounts().get(4);
		assertEquals(bdgt, bdgtAcct.getParent());
		assertEquals(12, bdgtAcct.getPeriods().size());
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableBudget objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_BDGT, gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_BDGT, gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.CACHE));

		GnuCashWritableBudget bdgt = gcshInFile.getWritableBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);

		assertEquals(BDGT_1_ID, bdgt.getID());

		// ----------------------------
		// Modify the object

		bdgt.setName("Improved Budget 3000");
		bdgt.setDescription("Tralala!");
		bdgt.setNofPeriods(13);
		
		GCshWritableBudgetRecurrence recurr = bdgt.getWritableRecurrence();
		recurr.setMult(2);
		recurr.setPeriodType(GCshBudgetRecurrence.PeriodType.MONTH);
		recurr.setStart(LocalDate.of(2026, 1, 1));
		bdgt.setRecurrence(recurr);

		// ::TODO modify entries

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(bdgt);

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

	private void test02_1_check_memory(GnuCashWritableBudget bdgt) throws Exception {
		assertEquals(ConstTest.Stats.NOF_BDGT, gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_BDGT, gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.CACHE));

		assertEquals(BDGT_1_ID, bdgt.getID()); // unchanged
		assertEquals("Improved Budget 3000", bdgt.getName()); // changed
		assertEquals("Tralala!", bdgt.getDescription()); // changed
		assertEquals(13, bdgt.getNofPeriods()); // changed
		
		assertEquals(bdgt, bdgt.getRecurrence().getParent()); // changed
		assertEquals(2, bdgt.getRecurrence().getMult()); // changed
		assertEquals(GCshBudgetRecurrence.PeriodType.MONTH, bdgt.getRecurrence().getPeriodType()); // changed
		assertEquals(LocalDate.of(2026, 1, 1), bdgt.getRecurrence().getStart()); // changed
		
		assertEquals(5, bdgt.getAccounts().size()); // unchanged
		assertEquals(ACCT_1_ID, bdgt.getAccounts().get(0).getAcctID()); // unchanged
		assertEquals(ACCT_2_ID, bdgt.getAccounts().get(1).getAcctID()); // unchanged
		assertEquals(ACCT_3_ID, bdgt.getAccounts().get(2).getAcctID()); // unchanged
		assertEquals(ACCT_4_ID, bdgt.getAccounts().get(3).getAcctID()); // unchanged
		assertEquals(ACCT_5_ID, bdgt.getAccounts().get(4).getAcctID()); // unchanged

		assertEquals(1,  bdgt.getPeriods(ACCT_1_ID).size()); // unchanged
		assertEquals(12, bdgt.getPeriods(ACCT_2_ID).size()); // unchanged
		assertEquals(2,  bdgt.getPeriods(ACCT_3_ID).size()); // unchanged
		assertEquals(12, bdgt.getPeriods(ACCT_4_ID).size()); // unchanged
		assertEquals(12, bdgt.getPeriods(ACCT_5_ID).size()); // unchanged

		GCshBudgetAccount bdgtAcct = bdgt.getAccounts().get(0);
		assertEquals(bdgt, bdgtAcct.getParent()); // unchanged
		assertEquals(1, bdgtAcct.getPeriods().size()); // unchanged
		
		bdgtAcct = bdgt.getAccounts().get(1);
		assertEquals(bdgt, bdgtAcct.getParent()); // unchanged
		assertEquals(12, bdgtAcct.getPeriods().size()); // unchanged
		
		bdgtAcct = bdgt.getAccounts().get(2);
		assertEquals(bdgt, bdgtAcct.getParent()); // unchanged
		assertEquals(2, bdgtAcct.getPeriods().size()); // unchanged
		
		bdgtAcct = bdgt.getAccounts().get(3);
		assertEquals(bdgt, bdgtAcct.getParent()); // unchanged
		assertEquals(12, bdgtAcct.getPeriods().size()); // unchanged
		
		bdgtAcct = bdgt.getAccounts().get(4);
		assertEquals(bdgt, bdgtAcct.getParent()); // unchanged
		assertEquals(12, bdgtAcct.getPeriods().size()); // unchanged
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_BDGT, gcshOutFileStats.getNofEntriesBudgets(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_BDGT, gcshOutFileStats.getNofEntriesBudgets(GCshFileStats.Type.CACHE));

		GnuCashBudget bdgt = gcshOutFile.getBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);

		assertEquals(BDGT_1_ID, bdgt.getID()); // unchanged
		assertEquals("Improved Budget 3000", bdgt.getName()); // changed
		assertEquals("Tralala!", bdgt.getDescription()); // changed
		assertEquals(13, bdgt.getNofPeriods()); // changed
		
		assertEquals(bdgt, bdgt.getRecurrence().getParent()); // changed
		assertEquals(2, bdgt.getRecurrence().getMult()); // changed
		assertEquals(GCshBudgetRecurrence.PeriodType.MONTH, bdgt.getRecurrence().getPeriodType()); // changed
		assertEquals(LocalDate.of(2026, 1, 1), bdgt.getRecurrence().getStart()); // changed
		
		assertEquals(5, bdgt.getAccounts().size()); // unchanged
		assertEquals(ACCT_1_ID, bdgt.getAccounts().get(0).getAcctID()); // unchanged
		assertEquals(ACCT_2_ID, bdgt.getAccounts().get(1).getAcctID()); // unchanged
		assertEquals(ACCT_3_ID, bdgt.getAccounts().get(2).getAcctID()); // unchanged
		assertEquals(ACCT_4_ID, bdgt.getAccounts().get(3).getAcctID()); // unchanged
		assertEquals(ACCT_5_ID, bdgt.getAccounts().get(4).getAcctID()); // unchanged

		assertEquals(1,  bdgt.getPeriods(ACCT_1_ID).size()); // unchanged
		assertEquals(12, bdgt.getPeriods(ACCT_2_ID).size()); // unhanged
		assertEquals(2,  bdgt.getPeriods(ACCT_3_ID).size()); // unchanged
		assertEquals(12, bdgt.getPeriods(ACCT_4_ID).size()); // unchanged
		assertEquals(12, bdgt.getPeriods(ACCT_5_ID).size()); // unchanged

		GCshBudgetAccount bdgtAcct = bdgt.getAccounts().get(0);
		assertEquals(bdgt, bdgtAcct.getParent()); // unchanged
		assertEquals(1, bdgtAcct.getPeriods().size()); // unchanged
		
		bdgtAcct = bdgt.getAccounts().get(1);
		assertEquals(bdgt, bdgtAcct.getParent()); // unchanged
		assertEquals(12, bdgtAcct.getPeriods().size()); // unchanged
		
		bdgtAcct = bdgt.getAccounts().get(2);
		assertEquals(bdgt, bdgtAcct.getParent()); // unchanged
		assertEquals(2, bdgtAcct.getPeriods().size()); // unchanged
		
		bdgtAcct = bdgt.getAccounts().get(3);
		assertEquals(bdgt, bdgtAcct.getParent()); // unchanged
		assertEquals(12, bdgtAcct.getPeriods().size()); // unchanged
		
		bdgtAcct = bdgt.getAccounts().get(4);
		assertEquals(bdgt, bdgtAcct.getParent()); // unchanged
		assertEquals(12, bdgtAcct.getPeriods().size()); // unchanged
	}

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 3.1: High-Level
	// ------------------------------

	@Test
	public void test03_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_BDGT, gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_BDGT, gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.CACHE));

		// ----------------------------
		// Bare naked object

		GnuCashWritableBudget bdgt = gcshInFile.createWritableBudget("The ultimate budget -- once and for all");
		assertNotEquals(null, bdgt);
		newBdgtID = bdgt.getID();
		assertEquals(true, newBdgtID.isSet());

		// ----------------------------
		// Modify the object
		
		bdgt.setDescription("My big fat budget");
		bdgt.setNofPeriods(12);

		GCshWritableBudgetRecurrence recurr = bdgt.getWritableRecurrence();
		recurr.setMult(1);
		recurr.setPeriodType(GCshBudgetRecurrence.PeriodType.MONTH);
		recurr.setStart(LocalDate.of(2026, 1, 1));
		bdgt.setRecurrence(recurr);

		// Add accounts (without periods)

		GCshWritableBudgetAccount bdgtAcct1 = bdgt.createWritableAccount(ACCT_1_ID);

		GCshWritableBudgetAccount bdgtAcct2 = bdgt.createWritableAccount(ACCT_2_ID);

		GCshWritableBudgetAccount bdgtAcct3 = bdgt.createWritableAccount(ACCT_4_ID);

		// ----------------------------
		// Check whether the object has actually been modified
		// (in memory, not in the file yet).

		test03_1_check_memory(bdgt);

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

	private void test03_1_check_memory(GnuCashWritableBudget bdgt) throws Exception {
		assertEquals(ConstTest.Stats.NOF_BDGT + 1, gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.RAW));
		// CAUTION: The counter has not been updated yet.
		// This is on purpose
		// ::TODO
		// assertEquals(ConstTest.Stats.NOF_BDGT,
		// gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_BDGT + 1, gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.CACHE));

		assertEquals("The ultimate budget -- once and for all", bdgt.getName());
		
		assertEquals(bdgt, bdgt.getRecurrence().getParent()); // changed
		assertEquals(1, bdgt.getRecurrence().getMult()); // changed
		assertEquals(GCshBudgetRecurrence.PeriodType.MONTH, bdgt.getRecurrence().getPeriodType()); // changed
		assertEquals(null, bdgt.getRecurrence().getStart()); // changed ::CHECK
		
		assertEquals(3, bdgt.getAccounts().size());
		assertEquals(ACCT_1_ID, bdgt.getAccounts().get(0).getAcctID());
		assertEquals(ACCT_2_ID, bdgt.getAccounts().get(1).getAcctID());
		assertEquals(ACCT_4_ID, bdgt.getAccounts().get(2).getAcctID());

		assertEquals(0, bdgt.getPeriods(ACCT_1_ID).size());
		assertEquals(0, bdgt.getPeriods(ACCT_2_ID).size());
		assertEquals(0, bdgt.getPeriods(ACCT_4_ID).size());

		GCshBudgetAccount bdgtAcct = bdgt.getAccounts().get(0);
		assertEquals(bdgt, bdgtAcct.getParent());
		assertEquals(0, bdgtAcct.getPeriods().size());
		
		bdgtAcct = bdgt.getAccounts().get(1);
		assertEquals(bdgt, bdgtAcct.getParent());
		assertEquals(0, bdgtAcct.getPeriods().size());
		
		bdgtAcct = bdgt.getAccounts().get(2);
		assertEquals(bdgt, bdgtAcct.getParent());
		assertEquals(0, bdgtAcct.getPeriods().size());
	}

	private void test03_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		// Here, all 3 stats variants must have been updated
		assertEquals(ConstTest.Stats.NOF_BDGT + 1, gcshOutFileStats.getNofEntriesBudgets(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_BDGT + 1, gcshOutFileStats.getNofEntriesBudgets(GCshFileStats.Type.CACHE));

		GnuCashBudget bdgt = gcshOutFile.getBudgetByID(newBdgtID);
		assertNotEquals(null, bdgt);

		assertEquals("The ultimate budget -- once and for all", bdgt.getName());
		
		assertEquals(bdgt, bdgt.getRecurrence().getParent());
		assertEquals(1, bdgt.getRecurrence().getMult());
		assertEquals(GCshBudgetRecurrence.PeriodType.MONTH, bdgt.getRecurrence().getPeriodType());
		assertEquals(null, bdgt.getRecurrence().getStart());
		
		assertEquals(3, bdgt.getAccounts().size());
		assertEquals(ACCT_1_ID, bdgt.getAccounts().get(0).getAcctID());
		assertEquals(ACCT_2_ID, bdgt.getAccounts().get(1).getAcctID());
		assertEquals(ACCT_4_ID, bdgt.getAccounts().get(2).getAcctID());

		assertEquals(0, bdgt.getPeriods(ACCT_1_ID).size());
		assertEquals(0, bdgt.getPeriods(ACCT_2_ID).size());
		assertEquals(0, bdgt.getPeriods(ACCT_4_ID).size());

		GCshBudgetAccount bdgtAcct = bdgt.getAccounts().get(0);
		assertEquals(bdgt, bdgtAcct.getParent());
		assertEquals(0, bdgtAcct.getPeriods().size());
		
		bdgtAcct = bdgt.getAccounts().get(1);
		assertEquals(bdgt, bdgtAcct.getParent());
		assertEquals(0, bdgtAcct.getPeriods().size());
		
		bdgtAcct = bdgt.getAccounts().get(2);
		assertEquals(bdgt, bdgtAcct.getParent());
		assertEquals(0, bdgtAcct.getPeriods().size());
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

//	@Test
//	public void test04_1() throws Exception {
//		gcshInFileStats = new GCshFileStats(gcshInFile);
//
//		assertEquals(ConstTest.Stats.NOF_BDGT, gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.RAW));
//		assertEquals(ConstTest.Stats.NOF_BDGT, gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.CACHE));
//
//
//		// ----------------------------
//		// Delete the object
//
//		// Variant 1
//		GnuCashWritableBudget bdgt1 = gcshInFile.getWritableBudgetByID(BDGT_1_ID);
//		assertNotEquals(null, bdgt1);
//		gcshInFile.removeBudget(bdgt1);
//
//		// Variant 2
//		GnuCashWritableBudget bdgt2 = gcshInFile.getWritableBudgetByID(BDGT_2_ID);
//		bdgt2.remove();
//
//		// ----------------------------
//		// Check whether the objects have actually been deleted
//		// (in memory, not in the file yet).
//
//		test04_1_check_memory(bdgt1, bdgt2);
//
//		// ----------------------------
//		// Now, check whether the deletions have been written to the
//		// output file, then re-read from it, and whether is is what
//		// we expect it is.
//
//		File outFile = folder.newFile(ConstTest.GCsh_FILENAME_OUT);
//		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
//		// + outFile.getPath() + "'");
//		outFile.delete(); // sic, the temp. file is already generated (empty),
//		// and the GnuCash file writer does not like that.
//		gcshInFile.writeFile(outFile);
//
//		test04_1_check_persisted(outFile);
//	}
//
//	// ---------------------------------------------------------------
//
//	private void test04_1_check_memory(GnuCashWritableBudget bdgt1,
//									   GnuCashWritableBudget bdgt2) throws Exception {
//		assertEquals(ConstTest.Stats.NOF_BDGT - 2, gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.RAW));
//		assertEquals(ConstTest.Stats.NOF_BDGT - 2, gcshInFileStats.getNofEntriesBudgets(GCshFileStats.Type.CACHE));
//
//		// ---
//		// First transaction:
//		
//		// CAUTION / ::TODO
//		// Old Object still exists and is unchanged
//		// Exception: no splits any more
//		// Don't know what to do about this oddity right now,
//		// but it needs to be addressed at some point.
//		assertEquals(0.0, bdgt1.getBalance().getBigDecimal().doubleValue(), ConstTest.DIFF_TOLERANCE); // unchanged
//		assertEquals("", bdgt1.getMemo()); // unchanged
//		assertEquals("2023-01-01", bdgt1.getDatePosted().toString()); // unchanged
//		assertEquals("2023-01-01", bdgt1.getDatePostedFormatted());
//		assertEquals("2023-11-03", bdgt1.getDateEntered().toString()); // unchanged
//		assertEquals("2023-11-03", bdgt1.getDateEnteredFormatted());
//		assertEquals(0, bdgt1.getSplitsCount()); // changed
//		
//		// However, the transaction cannot newly be instantiated any more,
//		// just as you would expect.
//		try {
//			GnuCashWritableBudget bdgt1Now = gcshInFile.getWritableBudgetByID(BDGT_1_ID);
//			assertEquals(1, 0);
//		} catch ( Exception exc ) {
//			assertEquals(0, 0);
//		}
//		
//		// ---
//		// Second transaction, same as above:
//		
//		// CAUTION / ::TODO
//		// Cf. above.
//		assertEquals(BDGT_2_ID, bdgt2.getID()); // unchanged
//		assertEquals(0.0, bdgt2.getBalance().getBigDecimal().doubleValue(), ConstTest.DIFF_TOLERANCE); // unchanged
//		assertEquals("", bdgt2.getMemo()); // unchanged
//		assertEquals("2023-01-03", bdgt2.getDatePosted().toString()); // unchanged
//		assertEquals("2023-01-03", bdgt2.getDatePostedFormatted());
//		assertEquals("2023-10-14", bdgt2.getDateEntered().toString()); // unchanged
//		assertEquals("2023-10-14", bdgt2.getDateEnteredFormatted());
//		assertEquals(0, bdgt2.getSplitsCount()); // changed
//		
//		// Cf. above.
//		try {
//			GnuCashWritableBudget bdgt2Now = gcshInFile.getWritableBudgetByID(BDGT_2_ID);
//			assertEquals(1, 0);
//		} catch ( Exception exc ) {
//			assertEquals(0, 0);
//		}
//	}
//
//	private void test04_1_check_persisted(File outFile) throws Exception {
//		gcshOutFile = new GnuCashFileImpl(outFile);
//		gcshOutFileStats = new GCshFileStats(gcshOutFile);
//
//		assertEquals(ConstTest.Stats.NOF_BDGT - 2, gcshOutFileStats.getNofEntriesBudgets(GCshFileStats.Type.RAW));
//		assertEquals(ConstTest.Stats.NOF_BDGT - 2, gcshOutFileStats.getNofEntriesBudgets(GCshFileStats.Type.CACHE));
//
//		// ---
//		// First transaction:
//		
//		// The transaction does not exist any more, just as you would expect.
//		// However, no exception is thrown, as opposed to test04_1_check_memory()
//		GnuCashBudget bdgt1 = gcshOutFile.getBudgetByID(BDGT_1_ID);
//		assertEquals(null, bdgt1); // sic
//
//		// ---
//		// Second transaction, same as above:
//		
//		// Cf. above
//		GnuCashBudget bdgt2 = gcshOutFile.getBudgetByID(BDGT_2_ID);
//		assertEquals(null, bdgt2); // sic
//	}

	// ------------------------------
	// PART 4.2: Low-Level
	// ------------------------------

	// ::TODO

}
