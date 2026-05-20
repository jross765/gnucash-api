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
		assertEquals("038dea402d134180a6a3ca748c9f6b4d", bdgt.getAccounts().get(0).getAcctID().toString());
		assertEquals("22a7432b85844c88bd023ba3c3ba72aa", bdgt.getAccounts().get(1).getAcctID().toString());
		assertEquals("2b5f38b679e848ee8e397a3a43ed0eb2", bdgt.getAccounts().get(2).getAcctID().toString());
		assertEquals("8a54850637cd4002be59eefc68a3ab80", bdgt.getAccounts().get(3).getAcctID().toString());
		assertEquals("c258aa23358040a08fcfe1efad2a906c", bdgt.getAccounts().get(4).getAcctID().toString());
		
		assertEquals(1,  bdgt.getPeriods(new GCshAcctID("038dea402d134180a6a3ca748c9f6b4d")).size());
		assertEquals(12, bdgt.getPeriods(new GCshAcctID("22a7432b85844c88bd023ba3c3ba72aa")).size());
		assertEquals(2,  bdgt.getPeriods(new GCshAcctID("2b5f38b679e848ee8e397a3a43ed0eb2")).size());
		assertEquals(12, bdgt.getPeriods(new GCshAcctID("8a54850637cd4002be59eefc68a3ab80")).size());
		assertEquals(12, bdgt.getPeriods(new GCshAcctID("c258aa23358040a08fcfe1efad2a906c")).size());

		GCshBudgetAccount bdgtAcct = bdgt.getAccounts().get(0);
		assertEquals(1, bdgtAcct.getPeriods().size());
		// .
		assertEquals(5, bdgtAcct.getPeriods().get(0).getIndex().intValue());
		assertEquals(100.0, bdgtAcct.getPeriods().get(0).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("100,00 €", bdgtAcct.getPeriods().get(0).getAmountFormatted());
		// .
		// The rest is redundant, because there is only one period for this account:
//		assertEquals(5, bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getPeriodIndex().intValue());
//		assertEquals(100.0, bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
//		assertEquals("100,00 €", bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getAmountFormatted());
		
		bdgtAcct = bdgt.getAccounts().get(1);
		assertEquals(12, bdgtAcct.getPeriods().size());
		// .
		assertEquals(0, bdgtAcct.getPeriods().get(0).getIndex().intValue());
		assertEquals(1000.0, bdgtAcct.getPeriods().get(0).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("1.000,00 €", bdgtAcct.getPeriods().get(0).getAmountFormatted());
		// .
		assertEquals(9, bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getIndex().intValue());
		assertEquals(1000.0, bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("1.000,00 €", bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getAmountFormatted());
		
		bdgtAcct = bdgt.getAccounts().get(2);
		assertEquals(2, bdgtAcct.getPeriods().size());
		// .
		assertEquals(2, bdgtAcct.getPeriods().get(0).getIndex().intValue());
		assertEquals(750.0, bdgtAcct.getPeriods().get(0).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("750,00 €", bdgtAcct.getPeriods().get(0).getAmountFormatted());
		// .
		assertEquals(6, bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getIndex().intValue());
		assertEquals(1500.0, bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("1.500,00 €", bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getAmountFormatted());
		
		bdgtAcct = bdgt.getAccounts().get(3);
		assertEquals(12, bdgtAcct.getPeriods().size());
		// .
		assertEquals(0, bdgtAcct.getPeriods().get(0).getIndex().intValue());
		assertEquals(200.0, bdgtAcct.getPeriods().get(0).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("200,00 €", bdgtAcct.getPeriods().get(0).getAmountFormatted());
		// .
		assertEquals(9, bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getIndex().intValue());
		assertEquals(200.0, bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("200,00 €", bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getAmountFormatted());
		
		bdgtAcct = bdgt.getAccounts().get(4);
		assertEquals(12, bdgtAcct.getPeriods().size());
		// .
		assertEquals(0, bdgtAcct.getPeriods().get(0).getIndex().intValue());
		assertEquals(800.0, bdgtAcct.getPeriods().get(0).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("800,00 €", bdgtAcct.getPeriods().get(0).getAmountFormatted());
		// .
		assertEquals(9, bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getIndex().intValue());
		assertEquals(800.0, bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("800,00 €", bdgtAcct.getPeriods().get(bdgtAcct.getPeriods().size() - 1).getAmountFormatted());
	}

}
