package org.gnucash.api.pricedb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.ConstTest;
import org.gnucash.api.pricedb.ComplexPriceTable;
import org.gnucash.api.pricedb.SimplePriceTable;
import org.gnucash.api.pricedb.SimpleSecurityQuoteTable;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.gnucash.base.basetypes.complex.GCshSecID_Exchange;
import org.gnucash.base.basetypes.complex.GCshSecID_SecIdType;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestSimpleSecurityQuoteTable {
	private GnuCashFile gcshFile = null;
	private ComplexPriceTable complPriceTab = null;
	private SimplePriceTable simplPriceTab = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestSimpleSecurityQuoteTable.class);
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
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getTabByType(GCshCmdtyID.Type.SECURITY);
		assertNotEquals(null, simplPriceTab);

		assertEquals(4, simplPriceTab.getCodes().size());
		
		List<String> currCodeArr = simplPriceTab.getCodes();
		assertEquals("EURONEXT:SAP",      currCodeArr.get(0));
		assertEquals("EURONEXT:MBG",      currCodeArr.get(1));
		assertEquals("ISIN:FR0000120644", currCodeArr.get(2));
		assertEquals("ISIN:DE000BASF111", currCodeArr.get(3));

		GCshSecID cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "SAP");
		assertEquals(145.0, ((SimpleSecurityQuoteTable) simplPriceTab).getConversionFactor(cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145,   ((SimpleSecurityQuoteTable) simplPriceTab).getConversionFactorRat(cmdtyID).getNumerator().intValue());
		assertEquals(1,     ((SimpleSecurityQuoteTable) simplPriceTab).getConversionFactorRat(cmdtyID).getDenominator().intValue());
		
		cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "MBG");
		assertEquals(11.265, ((SimpleSecurityQuoteTable) simplPriceTab).getConversionFactor(cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2253,   ((SimpleSecurityQuoteTable) simplPriceTab).getConversionFactorRat(cmdtyID).getNumerator().intValue());
		assertEquals(200,    ((SimpleSecurityQuoteTable) simplPriceTab).getConversionFactorRat(cmdtyID).getDenominator().intValue());

		cmdtyID = new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "FR0000120644");
		assertEquals(53.58, ((SimpleSecurityQuoteTable) simplPriceTab).getConversionFactor(cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2679,  ((SimpleSecurityQuoteTable) simplPriceTab).getConversionFactorRat(cmdtyID).getNumerator().intValue());
		assertEquals(50,    ((SimpleSecurityQuoteTable) simplPriceTab).getConversionFactorRat(cmdtyID).getDenominator().intValue());
		
		cmdtyID = new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "DE000BASF111");
		assertEquals(46.3, ((SimpleSecurityQuoteTable) simplPriceTab).getConversionFactor(cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(463,  ((SimpleSecurityQuoteTable) simplPriceTab).getConversionFactorRat(cmdtyID).getNumerator().intValue());
		assertEquals(10,   ((SimpleSecurityQuoteTable) simplPriceTab).getConversionFactorRat(cmdtyID).getDenominator().intValue());
	}

	@Test
	public void test02_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getTabByType(GCshCmdtyID.Type.SECURITY);
		assertNotEquals(null, simplPriceTab);

		GCshSecID_Exchange cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "SAP");
		FixedPointNumber valFP = new FixedPointNumber("101.0");
		BigFraction      valBF = BigFraction.of(101, 1);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(14645, ((SimpleSecurityQuoteTable) simplPriceTab).convertToBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(14645, ((SimpleSecurityQuoteTable) simplPriceTab).convertToBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(1,     ((SimpleSecurityQuoteTable) simplPriceTab).convertToBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());

		cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "MBG");
		assertEquals(1137.765, ((SimpleSecurityQuoteTable) simplPriceTab).convertToBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(227553,   ((SimpleSecurityQuoteTable) simplPriceTab).convertToBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(200,      ((SimpleSecurityQuoteTable) simplPriceTab).convertToBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());
	}

	@Test
	public void test02_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getTabByType(GCshCmdtyID.Type.SECURITY);
		assertNotEquals(null, simplPriceTab);

		GCshSecID_Exchange cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "SAP");
		FixedPointNumber valFP = new FixedPointNumber("14645");
		BigFraction      valBF = BigFraction.of(14645, 1);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101.0, ((SimpleSecurityQuoteTable) simplPriceTab).convertFromBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   ((SimpleSecurityQuoteTable) simplPriceTab).convertFromBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(1,     ((SimpleSecurityQuoteTable) simplPriceTab).convertFromBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());

		cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "MBG");
		valFP = new FixedPointNumber("1137.765");
		valBF = BigFraction.of(227553, 200);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101.0, ((SimpleSecurityQuoteTable) simplPriceTab).convertFromBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   ((SimpleSecurityQuoteTable) simplPriceTab).convertFromBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(1,     ((SimpleSecurityQuoteTable) simplPriceTab).convertFromBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());
	}

	@Test
	public void test03_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getTabByType(GCshCmdtyID.Type.SECURITY);
		assertNotEquals(null, simplPriceTab);

		GCshSecID_SecIdType cmdtyID = new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "FR0000120644");
		FixedPointNumber valFP = new FixedPointNumber("101.0");
		BigFraction      valBF = BigFraction.of(101, 1);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(5411.58, ((SimpleSecurityQuoteTable) simplPriceTab).convertToBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(270579,  ((SimpleSecurityQuoteTable) simplPriceTab).convertToBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(50,      ((SimpleSecurityQuoteTable) simplPriceTab).convertToBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());

		cmdtyID = new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "DE000BASF111");
		assertEquals(4676.3, ((SimpleSecurityQuoteTable) simplPriceTab).convertToBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(46763,  ((SimpleSecurityQuoteTable) simplPriceTab).convertToBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(10,     ((SimpleSecurityQuoteTable) simplPriceTab).convertToBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());
	}

	@Test
	public void test03_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getTabByType(GCshCmdtyID.Type.SECURITY);
		assertNotEquals(null, simplPriceTab);

		GCshSecID_SecIdType cmdtyID = new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "FR0000120644");
		FixedPointNumber valFP = new FixedPointNumber("5411.58");
		BigFraction      valBF = BigFraction.of(270579, 50);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101.0, ((SimpleSecurityQuoteTable) simplPriceTab).convertFromBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   ((SimpleSecurityQuoteTable) simplPriceTab).convertFromBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(1,     ((SimpleSecurityQuoteTable) simplPriceTab).convertFromBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());

		cmdtyID = new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "DE000BASF111");
		valFP = new FixedPointNumber("4676.3");
		valBF = BigFraction.of(46763, 10);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101.0, ((SimpleSecurityQuoteTable) simplPriceTab).convertFromBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   ((SimpleSecurityQuoteTable) simplPriceTab).convertFromBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(1,     ((SimpleSecurityQuoteTable) simplPriceTab).convertFromBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());
	}
}
