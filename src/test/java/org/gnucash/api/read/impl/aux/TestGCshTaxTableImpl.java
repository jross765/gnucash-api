package org.gnucash.api.read.impl.aux;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Collection;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.aux.GCshTaxTabID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGCshTaxTableImpl {
	// DE
	// Note the funny parent/child pair.
	public static final GCshTaxTabID TAXTABLE_DE_1_1_ID = new GCshTaxTabID("3c9690f9f31b4cd0baa936048b833c06"); // DE_USt_Std
	// "parent"
	public static final GCshTaxTabID TAXTABLE_DE_1_2_ID = new GCshTaxTabID("cba6011c826f426fbc4a1a72c3d6c8ee"); // DE_USt_Std
	// "child"
	public static final GCshTaxTabID TAXTABLE_DE_2_ID = new GCshTaxTabID("c518af53a93c4a5cb3e2161b7b358e68"); // DE_USt_red

	// FR
	public static final GCshTaxTabID TAXTABLE_FR_1_ID = new GCshTaxTabID("de4c17d1eb0e4f088ba73d4c697032f0"); // FR_TVA_Std
	public static final GCshTaxTabID TAXTABLE_FR_2_ID = new GCshTaxTabID("e279d5cc81204f1bb6cf672ef3357c0c"); // FR_TVA_red

	// UK
	public static final GCshTaxTabID TAXTABLE_UK_1_ID = new GCshTaxTabID("0bc4e576896a4fb4a2779dcf310f82f1"); // UK_VAT_Std
	public static final GCshTaxTabID TAXTABLE_UK_2_ID = new GCshTaxTabID("9d33a0082d9241ac89aa8e907f30d1db"); // UK_VAT_red

	public static final GCshAcctID TAX_ACCT_ID = new GCshAcctID("1a5b06dada56466197edbd15e64fd425"); // Root
	// Account::Fremdkapital::Steuerverbindl

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GCshTaxTable taxTab = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGCshTaxTableImpl.class);
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

	@Test
	public void test01() throws Exception {
		Collection<GCshTaxTable> taxTableList = gcshFile.getTaxTables();

		assertEquals(7, taxTableList.size());

		// ::TODO: Sort array for predictability
		Object[] taxTableArr = taxTableList.toArray();

		assertEquals(TAXTABLE_UK_2_ID, ((GCshTaxTable) taxTableArr[0]).getID());
		assertEquals(TAXTABLE_DE_1_2_ID, ((GCshTaxTable) taxTableArr[1]).getID());
		assertEquals(TAXTABLE_UK_1_ID, ((GCshTaxTable) taxTableArr[2]).getID());
		assertEquals(TAXTABLE_DE_1_1_ID, ((GCshTaxTable) taxTableArr[3]).getID());
		assertEquals(TAXTABLE_DE_2_ID, ((GCshTaxTable) taxTableArr[4]).getID());
		assertEquals(TAXTABLE_FR_1_ID, ((GCshTaxTable) taxTableArr[5]).getID());
		assertEquals(TAXTABLE_FR_2_ID, ((GCshTaxTable) taxTableArr[6]).getID());
	}

	@Test
	public void test02_1_1() throws Exception {
		taxTab = gcshFile.getTaxTableByID(TAXTABLE_DE_1_1_ID);

		assertEquals(TAXTABLE_DE_1_1_ID, taxTab.getID());
		assertEquals("DE_USt_Std", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(19.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
					 ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test02_1_2() throws Exception {
		taxTab = gcshFile.getTaxTableByName("DE_USt_Std");

		assertEquals(TAXTABLE_DE_1_1_ID, taxTab.getID());
		assertEquals("DE_USt_Std", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(19.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
					 ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test02_2_1() throws Exception {
		taxTab = gcshFile.getTaxTableByID(TAXTABLE_DE_1_2_ID);

		assertEquals(TAXTABLE_DE_1_2_ID, taxTab.getID());
		assertEquals("USt_Std", taxTab.getName()); // sic, old name w/o prefix "DE_"
		assertEquals(TAXTABLE_DE_1_1_ID, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(19.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test02_2_2() throws Exception {
		taxTab = gcshFile.getTaxTableByName("USt_Std");

		assertEquals(TAXTABLE_DE_1_2_ID, taxTab.getID());
		assertEquals("USt_Std", taxTab.getName()); // sic, old name w/o prefix "DE_"
		assertEquals(TAXTABLE_DE_1_1_ID, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(19.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
					 ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test03_1() throws Exception {
		taxTab = gcshFile.getTaxTableByID(TAXTABLE_DE_2_ID);

		assertEquals(TAXTABLE_DE_2_ID, taxTab.getID());
		assertEquals("DE_USt_red", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(7.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
					 ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test03_2() throws Exception {
		taxTab = gcshFile.getTaxTableByName("DE_USt_red");

		assertEquals(TAXTABLE_DE_2_ID, taxTab.getID());
		assertEquals("DE_USt_red", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(7.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
					 ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test04_1() throws Exception {
		taxTab = gcshFile.getTaxTableByID(TAXTABLE_FR_1_ID);

		assertEquals(TAXTABLE_FR_1_ID, taxTab.getID());
		assertEquals("FR_TVA_Std", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(20.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
					 ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test04_2() throws Exception {
		taxTab = gcshFile.getTaxTableByName("FR_TVA_Std");

		assertEquals(TAXTABLE_FR_1_ID, taxTab.getID());
		assertEquals("FR_TVA_Std", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(20.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
					 ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test05_1() throws Exception {
		taxTab = gcshFile.getTaxTableByID(TAXTABLE_FR_2_ID);

		assertEquals(TAXTABLE_FR_2_ID, taxTab.getID());
		assertEquals("FR_TVA_red", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(10.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
					 ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test05_2() throws Exception {
		taxTab = gcshFile.getTaxTableByName("FR_TVA_red");

		assertEquals(TAXTABLE_FR_2_ID, taxTab.getID());
		assertEquals("FR_TVA_red", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(10.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
					 ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}
}
