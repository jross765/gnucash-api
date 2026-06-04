package org.gnucash.api.read.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.Locale;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshBudgetAccount;
import org.gnucash.api.read.aux.GCshBudgetPeriod;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashBudgetImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshBdgtID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGCshBudgetAccountImpl {
	public static final GCshBdgtID BDGT_1_ID = TestGnuCashBudgetImpl.BDGT_1_ID;

	public static final GCshAcctID ACCT_1_ID = TestGnuCashBudgetImpl.ACCT_1_ID;
	public static final GCshAcctID ACCT_2_ID = TestGnuCashBudgetImpl.ACCT_2_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashBudget bdgt = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGCshBudgetAccountImpl.class);
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
		
		GCshBudgetAccount bdgtAcct = bdgt.getAccounts().get(1); // sic, the second
		assertNotEquals(null, bdgtAcct);
		assertEquals(bdgt, bdgtAcct.getParent());
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

}
