package org.gnucash.api.write.impl.hlp.fil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestFileTransactionManager {

	// ---------------------------------------------------------------

	private GnuCashWritableFileImplTestHelper gcshInFile = null;

	private org.gnucash.api.write.impl.hlp.fil.FileTransactionManager mgr = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestFileTransactionManager.class);
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
		mgr = gcshInFile.getTransactionManager();
		
		assertEquals(ConstTest.Stats.NOF_TRX, mgr.getNofEntriesTransactionMap());
		assertEquals(ConstTest.Stats.NOF_TRX, mgr.getTransactions().size());
	}

	@Test
	public void test02() throws Exception {
		mgr = gcshInFile.getTransactionManager();
		
		List<? extends GnuCashTransaction> trxColl = mgr.getTransactions();
		GCshTrxID trxID = new GCshTrxID("71979c2d99104919899fa249e616bcaa");
		GnuCashTransaction trx = mgr.getTransactionByID(trxID);
		assertTrue(trxColl.contains(trx));
		
		GCshSpltID splt1ID = new GCshSpltID("b65f76a37e5643b1ac2ea2ad9cdf381d");
		GCshSpltID splt2ID = new GCshSpltID("48657aca121b4500baef4078a3982c03");
		GnuCashTransactionSplit splt1 = mgr.getTransactionSplitByID(splt1ID);
		GnuCashTransactionSplit splt2 = mgr.getTransactionSplitByID(splt2ID);
		
		List<GnuCashTransactionSplit> spltColl = mgr.getTransactionSplits();
		assertTrue(spltColl.contains(splt1));
		assertTrue(spltColl.contains(splt2));
	}

	@Test
	public void test03_1() throws Exception {
		mgr = gcshInFile.getTransactionManager();
		
		GCshTrxID trxID = new GCshTrxID("71979c2d99104919899fa249e616bcaa");

		assertEquals(ConstTest.Stats.NOF_TRX, mgr.getTransactions().size());
		GnuCashWritableTransaction trx = gcshInFile.getWritableTransactionByID(trxID);
		gcshInFile.removeTransaction(trx);
		assertEquals(ConstTest.Stats.NOF_TRX - 1, mgr.getTransactions().size());
	}

	@Test
	public void test03_2() throws Exception {
		mgr = gcshInFile.getTransactionManager();
		
		GCshTrxID trxID = new GCshTrxID("71979c2d99104919899fa249e616bcaa");
		GCshSpltID spltID = new GCshSpltID("b65f76a37e5643b1ac2ea2ad9cdf381d");

		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, mgr.getTransactionSplits().size());
		GnuCashWritableTransactionSplit splt = gcshInFile.getWritableTransactionSplitByID(spltID);
		gcshInFile.removeTransactionSplit(splt);
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT - 1, mgr.getTransactionSplits().size());
	}

}
