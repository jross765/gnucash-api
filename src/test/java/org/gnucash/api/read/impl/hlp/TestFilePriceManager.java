package org.gnucash.api.read.impl.hlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Collection;
import java.util.Currency;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshSecID_Exchange;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.simple.GCshPrcID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestFilePriceManager {

	public final static GCshPrcID PRC_1_ID = new GCshPrcID("037c268b47fb46d385360b1c9788a459");
	public final static GCshPrcID PRC_2_ID = new GCshPrcID("3206bcc27c4242b88f7570788646c13a");
	
	// ---------------------------------------------------------------

	private GnuCashFileImplTestHelper gcshFile = null;

	private FilePriceManager mgr = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestFilePriceManager.class);
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
			gcshFile = new GnuCashFileImplTestHelper(gcshInFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash in-file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------
	
	@Test
	public void test01() throws Exception {
		mgr = gcshFile.getPriceManager();
		
		assertEquals(ConstTest.Stats.NOF_PRC, mgr.getNofEntriesPriceMap());
		assertEquals(ConstTest.Stats.NOF_PRC, mgr.getPrices().size());
	}

	@Test
	public void test02() throws Exception {
		mgr = gcshFile.getPriceManager();
		Collection<GnuCashPrice> prcColl = mgr.getPrices();
		
		GnuCashPrice prc = mgr.getPriceByID(PRC_1_ID);
		assertTrue(prcColl.contains(prc));
		
		prc = mgr.getPriceByID(PRC_2_ID);
		assertTrue(prcColl.contains(prc));
	}

	@Test
	public void test03_1() throws Exception {
		mgr = gcshFile.getPriceManager();
		
		GCshSecID_Exchange cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "MBG");
		assertEquals(11.265, mgr.getLatestPrice(cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2253,   mgr.getLatestPriceRat(cmdtyID).getNumerator().intValue());
		assertEquals(200,    mgr.getLatestPriceRat(cmdtyID).getDenominator().intValue());
	}

	@Test
	public void test03_2() throws Exception {
		mgr = gcshFile.getPriceManager();
		
		GCshCurrID currID = new GCshCurrID(Currency.getInstance("EUR") );
		assertEquals(null, mgr.getLatestPrice(currID));    // ::CHECK
		assertEquals(null, mgr.getLatestPriceRat(currID)); // ::CHECK
		
		currID = new GCshCurrID(Currency.getInstance("USD") );
		assertEquals(0.93, mgr.getLatestPrice(currID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(93,   mgr.getLatestPriceRat(currID).getNumerator().intValue());
		assertEquals(100,  mgr.getLatestPriceRat(currID).getDenominator().intValue());
	}

	@Test
	public void test03_3() throws Exception {
		mgr = gcshFile.getPriceManager();
		
		assertEquals(11.265, mgr.getLatestPrice(GCshCmdtyNameSpace.Exchange.EURONEXT.toString(), "MBG").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2253,   mgr.getLatestPriceRat(GCshCmdtyNameSpace.Exchange.EURONEXT.toString(), "MBG").getNumerator().intValue());
		assertEquals(200,    mgr.getLatestPriceRat(GCshCmdtyNameSpace.Exchange.EURONEXT.toString(), "MBG").getDenominator().intValue());

		assertEquals(null, mgr.getLatestPrice(GCshCmdtyNameSpace.CURRENCY, "EUR"));    // ::CHECK
		assertEquals(null, mgr.getLatestPriceRat(GCshCmdtyNameSpace.CURRENCY, "EUR")); // ::CHECK
		
		assertEquals(0.93, mgr.getLatestPrice(GCshCmdtyNameSpace.CURRENCY, "USD").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(93,   mgr.getLatestPriceRat(GCshCmdtyNameSpace.CURRENCY, "USD").getNumerator().intValue());
		assertEquals(100,  mgr.getLatestPriceRat(GCshCmdtyNameSpace.CURRENCY, "USD").getDenominator().intValue());
	}

	@Test
	public void test04() throws Exception {
		mgr = gcshFile.getPriceManager();
		
		GnuCashPrice prc = mgr.getPriceByID(PRC_1_ID);
		assertEquals("CURRENCY:USD", prc.getFromCmdtyCurrQualifID().toString());
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
		assertEquals(1.07527, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(100,     prc.getValueRat().getNumerator().intValue());
		assertEquals(93,      prc.getValueRat().getDenominator().intValue());
		
		prc = mgr.getPriceByID(PRC_2_ID);
		assertEquals("EURONEXT:MBG", prc.getFromCmdtyCurrQualifID().toString());
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
		assertEquals(11.265, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2253,   prc.getValueRat().getNumerator().intValue());
		assertEquals(200,    prc.getValueRat().getDenominator().intValue());
	}

}
