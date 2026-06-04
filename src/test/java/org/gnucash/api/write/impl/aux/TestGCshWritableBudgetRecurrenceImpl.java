package org.gnucash.api.write.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.aux.GCshBudgetRecurrence;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.write.GnuCashWritableBudget;
import org.gnucash.api.write.aux.GCshWritableBudgetRecurrence;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.TestGnuCashWritableBudgetImpl;
import org.gnucash.base.basetypes.simple.GCshBdgtID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGCshWritableBudgetRecurrenceImpl {
	private static final GCshBdgtID BDGT_1_ID = TestGnuCashWritableBudgetImpl.BDGT_1_ID;

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
		return new JUnit4TestAdapter(TestGCshWritableBudgetRecurrenceImpl.class);
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
		
		GCshWritableBudgetRecurrence bdgtRecurr = bdgt.getWritableRecurrence();
		assertNotEquals(null, bdgtRecurr);
		assertEquals(bdgt, bdgtRecurr.getParent());
		
		assertEquals(1, bdgtRecurr.getMult());
		assertEquals(GCshBudgetRecurrence.PeriodType.MONTH, bdgtRecurr.getPeriodType());
		assertEquals("month", bdgtRecurr.getPeriodTypeStr());
		assertEquals(LocalDate.of(2026, 1, 1), bdgtRecurr.getStart());
		assertEquals("2026-01-01", bdgtRecurr.getStartStr());
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
		
		GCshWritableBudgetRecurrence bdgtRecurr = bdgt.getWritableRecurrence();
		assertNotEquals(null, bdgtRecurr);
		assertEquals(bdgt, bdgtRecurr.getParent());
		
		// ----------------------------
		// Modify the object

		try {
			bdgtRecurr.setMult(-1);
			assertEquals(0, 1);
		} catch ( IllegalArgumentException | IllegalStateException exc ) {
			assertEquals(0, 0);
		}
		
		bdgtRecurr.setMult(6);

		bdgtRecurr.setPeriodType(GCshBudgetRecurrence.PeriodType.YEAR);
		
		bdgtRecurr.setStart(LocalDate.of(2027, 5, 1));

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(bdgt, bdgtRecurr);

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

	private void test02_1_check_memory(GnuCashWritableBudget bdgt, 
									   GCshWritableBudgetRecurrence bdgtRecurr) throws Exception {
		assertNotEquals(null, bdgt.getRecurrence());
		
		assertEquals(bdgt, bdgtRecurr.getParent()); // unchanged
		assertEquals(6, bdgtRecurr.getMult()); // changed
		assertEquals(GCshBudgetRecurrence.PeriodType.YEAR, bdgtRecurr.getPeriodType()); // changed
		assertEquals(LocalDate.of(2027, 5, 1), bdgtRecurr.getStart()); // changed
		assertTrue(bdgtRecurr.getStartStr().startsWith( "2027-05-01" )); // changed
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		
		GnuCashBudget bdgt = gcshOutFile.getBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);
		assertEquals(BDGT_1_ID, bdgt.getID());
		
		GCshBudgetRecurrence bdgtRecurr = bdgt.getRecurrence();
		assertNotEquals(null, bdgtRecurr);
		assertEquals(bdgt, bdgtRecurr.getParent()); // unchanged
		assertEquals(6, bdgtRecurr.getMult()); // changed
		assertEquals(GCshBudgetRecurrence.PeriodType.YEAR, bdgtRecurr.getPeriodType()); // changed
		assertEquals(LocalDate.of(2027, 5, 1), bdgtRecurr.getStart()); // changed
		assertTrue(bdgtRecurr.getStartStr().startsWith( "2027-05-01" )); // changed
	}

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 3.1: High-Level
	// ------------------------------

	// N/A

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

	// N/A

	// ------------------------------
	// PART 4.2: Low-Level
	// ------------------------------

	// ::TODO

}
