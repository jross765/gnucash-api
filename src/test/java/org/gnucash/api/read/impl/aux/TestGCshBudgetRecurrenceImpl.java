package org.gnucash.api.read.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.time.LocalDate;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshBudgetRecurrence;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashBudgetImpl;
import org.gnucash.base.basetypes.simple.GCshBdgtID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGCshBudgetRecurrenceImpl {
	public static final GCshBdgtID BDGT_1_ID = TestGnuCashBudgetImpl.BDGT_1_ID;
	// public static final GCshBdgtID BDGT_2_ID = new GCshBdgtID("xyz");

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashBudget bdgt = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGCshBudgetRecurrenceImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCsh_FILENAME);
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

	@Test
	public void test01() throws Exception {
		bdgt = gcshFile.getBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);
		assertEquals(BDGT_1_ID, bdgt.getID());
		
		GCshBudgetRecurrence recurr = bdgt.getRecurrence();
		assertNotEquals(null, recurr);
		assertEquals(bdgt, recurr.getParent());
		
		assertEquals(1, recurr.getMult());
		assertEquals(GCshBudgetRecurrence.PeriodType.MONTH, recurr.getPeriodType());
		assertEquals("month", recurr.getPeriodTypeStr());
		assertEquals(LocalDate.of(2026, 1, 1), recurr.getStart());
		assertEquals("2026-01-01", recurr.getStartStr());
	}

}
