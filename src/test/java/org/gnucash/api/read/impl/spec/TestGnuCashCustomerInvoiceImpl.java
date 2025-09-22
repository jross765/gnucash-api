package org.gnucash.api.read.impl.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerInvoiceEntry;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashCustomerInvoiceImpl {
	private static final GCshGenerInvcID CUST_INVC_1_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_1_ID;
	private static final GCshGenerInvcID CUST_INVC_6_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_6_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashGenerInvoice invcGen = null;
	private GnuCashCustomerInvoice invcSpec = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashCustomerInvoiceImpl.class);
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
	public void test01_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(CUST_INVC_1_ID);
		assertNotEquals(null, invcGen);
		invcSpec = new GnuCashCustomerInvoiceImpl(invcGen);
		assertNotEquals(null, invcSpec);

		assertEquals(true, invcSpec instanceof GnuCashCustomerInvoiceImpl);
		assertEquals(CUST_INVC_1_ID, invcSpec.getID());
		assertEquals(GCshOwner.Type.CUSTOMER, invcSpec.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT));
		assertEquals("R1730", invcSpec.getNumber());
		assertEquals("Alles ohne Steuern / voll bezahlt", invcSpec.getDescription());

		assertEquals("2023-07-29T10:59Z", invcSpec.getDateOpened().toString());
		assertEquals("2023-07-29T10:59Z", invcSpec.getDatePosted().toString());
	}

	@Test
	public void test02_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(CUST_INVC_1_ID);
		assertNotEquals(null, invcGen);
		invcSpec = new GnuCashCustomerInvoiceImpl(invcGen);
		assertNotEquals(null, invcSpec);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(2, invcGen.getGenerEntries().size());
		assertEquals(2, invcSpec.getGenerEntries().size());
		assertEquals(2, invcSpec.getEntries().size());

		TreeSet entrList = new TreeSet(); // sort elements of HashSet
		entrList.addAll(invcSpec.getEntries());
		assertEquals("92e54c04b66f4682a9afb48e27dfe397",
				((GnuCashCustomerInvoiceEntry) entrList.toArray()[0]).getID().toString());
		assertEquals("3c67a99b5fe34387b596bb1fbab21a74",
				((GnuCashCustomerInvoiceEntry) entrList.toArray()[1]).getID().toString());
	}

	@Test
	public void test03_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(CUST_INVC_1_ID);
		assertNotEquals(null, invcGen);
		invcSpec = new GnuCashCustomerInvoiceImpl(invcGen);
		assertNotEquals(null, invcSpec);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(1327.60, invcGen.getCustInvcAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1327.60, invcSpec.getCustInvcAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1327.60, invcSpec.getAmountWithoutTaxes().doubleValue(),
				ConstTest.DIFF_TOLERANCE);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(1327.60, invcGen.getCustInvcAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1327.60, invcSpec.getCustInvcAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1327.60, invcSpec.getAmountWithTaxes().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test04_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(CUST_INVC_1_ID);
		assertNotEquals(null, invcGen);
		invcSpec = new GnuCashCustomerInvoiceImpl(invcGen);
		assertNotEquals(null, invcSpec);

		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals("c97032ba41684b2bb5d1391c9d7547e9", invcGen.getPostTransaction().getID().toString());
		assertEquals("c97032ba41684b2bb5d1391c9d7547e9", invcSpec.getPostTransaction().getID().toString());

		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(1, invcGen.getPayingTransactions().size());
		assertEquals(1, invcSpec.getPayingTransactions().size());

		List<GnuCashTransaction> trxList = (List<GnuCashTransaction>) invcSpec.getPayingTransactions();
		Collections.sort(trxList);
		assertEquals("29557cfdf4594eb68b1a1b710722f991",
				((GnuCashTransaction) trxList.toArray()[0]).getID().toString());

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(true, invcGen.isCustInvcFullyPaid());
		assertEquals(true, invcSpec.isCustInvcFullyPaid());
		assertEquals(true, invcSpec.isFullyPaid());
	}

	@Test
	public void test05() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(CUST_INVC_6_ID);
		assertNotEquals(null, invcGen);
		invcSpec = new GnuCashCustomerInvoiceImpl(invcGen);
		assertNotEquals(null, invcSpec);

		assertEquals("https://my.customer.invoice.link.01", invcGen.getURL());
		assertEquals(invcGen.getURL(), invcSpec.getURL());
	}
}
