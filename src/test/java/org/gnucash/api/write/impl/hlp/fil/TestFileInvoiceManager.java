package org.gnucash.api.write.impl.hlp.fil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Collection;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.impl.TestGnuCashGenerInvoiceImpl;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestFileInvoiceManager {

	// ---------------------------------------------------------------

	private GnuCashWritableFileImplTestHelper gcshInFile = null;

	private org.gnucash.api.write.impl.hlp.fil.FileInvoiceManager mgr = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestFileInvoiceManager.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshInFileStream = null;
		try {
			gcshInFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME_IN);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshInFile = new GnuCashWritableFileImplTestHelper(gcshInFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash in-file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------
	
	@Test
	public void test01() throws Exception {
		mgr = gcshInFile.getInvoiceManager();
		
		assertEquals(ConstTest.Stats.NOF_GENER_INVC, mgr.getNofEntriesGenerInvoiceMap());
		assertEquals(ConstTest.Stats.NOF_GENER_INVC, mgr.getGenerInvoices().size());
	}

	@Test
	public void test02() throws Exception {
		mgr = gcshInFile.getInvoiceManager();
		
		Collection<GnuCashGenerInvoice> invcColl = mgr.getGenerInvoices();
		GCshGenerInvcID invcID = TestGnuCashGenerInvoiceImpl.GENER_INVC_1_ID;
		GnuCashGenerInvoice invc = mgr.getGenerInvoiceByID(invcID);
		assertTrue(invcColl.contains(invc));
	}

	@Test
	public void test03() throws Exception {
		mgr = gcshInFile.getInvoiceManager();
		
		GCshGenerInvcID invcID = TestGnuCashGenerInvoiceImpl.GENER_INVC_4_ID;
		
		assertEquals(ConstTest.Stats.NOF_GENER_INVC, mgr.getGenerInvoices().size());
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(invcID);
		gcshInFile.removeGenerInvoice(invc, true);
		assertEquals(ConstTest.Stats.NOF_GENER_INVC - 1, mgr.getGenerInvoices().size());
		
		// has payment transactions:
		invcID = TestGnuCashGenerInvoiceImpl.GENER_INVC_6_ID;
		invc = gcshInFile.getWritableGenerInvoiceByID(invcID);
		try {
			gcshInFile.removeGenerInvoice(invc, true);
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		assertEquals(ConstTest.Stats.NOF_GENER_INVC - 1, mgr.getGenerInvoices().size());
	}

}
