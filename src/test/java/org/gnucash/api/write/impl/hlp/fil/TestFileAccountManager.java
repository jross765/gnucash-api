package org.gnucash.api.write.impl.hlp.fil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Collection;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.impl.TestGnuCashAccountImpl;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestFileAccountManager {

	// ---------------------------------------------------------------

	private GnuCashWritableFileImplTestHelper gcshInFile = null;

	private org.gnucash.api.write.impl.hlp.fil.FileAccountManager mgr = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestFileAccountManager.class);
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
		mgr = gcshInFile.getAccountManager();
		
		assertEquals(ConstTest.Stats.NOF_ACCT, mgr.getNofEntriesAccountMap());
		assertEquals(ConstTest.Stats.NOF_ACCT, mgr.getAccounts().size());
	}

	@Test
	public void test02() throws Exception {
		mgr = gcshInFile.getAccountManager();
		
		Collection<GnuCashAccount> acctColl = mgr.getAccounts();
		GCshAcctID acctID = new GCshAcctID("bbf77a599bd24a3dbfec3dd1d0bb9f5c");
		GnuCashAccount acct = mgr.getAccountByID(acctID);
		assertTrue(acctColl.contains(acct));
	}

	@Test
	public void test03() throws Exception {
		mgr = gcshInFile.getAccountManager();
		
		GCshAcctID acctID = new GCshAcctID("6a5db2c70fc04b4fa2e455d374c103cf");
		
		assertEquals(ConstTest.Stats.NOF_ACCT, mgr.getAccounts().size());
		GnuCashWritableAccount acct = gcshInFile.getWritableAccountByID(acctID);
		gcshInFile.removeAccount(acct);
		assertEquals(ConstTest.Stats.NOF_ACCT - 1, mgr.getAccounts().size());
		
		// has splits:
		acctID = TestGnuCashAccountImpl.ACCT_1_ID;
		acct = gcshInFile.getWritableAccountByID(acctID);
		try {
			gcshInFile.removeAccount(acct);
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		assertEquals(ConstTest.Stats.NOF_ACCT - 1, mgr.getAccounts().size());
	}

	@Test
	public void test04() throws Exception {
		mgr = gcshInFile.getAccountManager();
		
		GCshAcctID acctID = TestGnuCashAccountImpl.ACCT_1_ID;
		GnuCashAccount acct = mgr.getAccountByID(acctID);
		
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFile.getTransactionSplits().size());
		assertEquals(10, acct.getTransactionSplits().size());
		
		GnuCashWritableTransactionSplit splt = gcshInFile.getWritableTransactionSplitByID(new GCshSpltID("b6a88c1d918e465892488c561e02831a"));
		gcshInFile.removeTransactionSplit(splt);
		
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT - 1, gcshInFile.getTransactionSplits().size());
		assertEquals(9, acct.getTransactionSplits().size());
	}

}
