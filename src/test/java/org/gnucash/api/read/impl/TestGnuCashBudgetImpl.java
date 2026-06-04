package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.time.LocalDate;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshBudgetAccount;
import org.gnucash.api.read.aux.GCshBudgetRecurrence;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshBdgtID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashBudgetImpl {
	public static final GCshBdgtID BDGT_1_ID = new GCshBdgtID("66d6d03d772f4825bab86986a54bf295");
	// public static final GCshBdgtID BDGT_2_ID = new GCshBdgtID("xyz");

	public static final GCshAcctID ACCT_1_ID = new GCshAcctID("038dea402d134180a6a3ca748c9f6b4d");
	public static final GCshAcctID ACCT_2_ID = new GCshAcctID("22a7432b85844c88bd023ba3c3ba72aa");
	public static final GCshAcctID ACCT_3_ID = new GCshAcctID("2b5f38b679e848ee8e397a3a43ed0eb2");
	public static final GCshAcctID ACCT_4_ID = new GCshAcctID("8a54850637cd4002be59eefc68a3ab80");
	public static final GCshAcctID ACCT_5_ID = new GCshAcctID("c258aa23358040a08fcfe1efad2a906c");

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashBudget bdgt = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashBudgetImpl.class);
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
	public void test01_1() throws Exception {
		bdgt = gcshFile.getBudgetByID(BDGT_1_ID);
		assertNotEquals(null, bdgt);

		assertEquals(BDGT_1_ID, bdgt.getID());
		assertEquals("Budget 2026", bdgt.getName());
		assertEquals("Erstellt: 18.05.2026", bdgt.getDescription());
		
		assertEquals(1, bdgt.getRecurrence().getMult());
		assertEquals(GCshBudgetRecurrence.PeriodType.MONTH, bdgt.getRecurrence().getPeriodType());
		assertEquals(LocalDate.of(2026, 1, 1), bdgt.getRecurrence().getStart());
		
		assertEquals(12, bdgt.getNofPeriods());
		
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

}
