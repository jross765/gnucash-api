package org.gnucash.api.currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestSimpleCommodityQuoteTable {
	private GnuCashFile gcshFile = null;
	private ComplexPriceTable complPriceTab = null;
	private SimplePriceTable simplPriceTab = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestSimpleCommodityQuoteTable.class);
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
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.Exchange.EURONEXT.toString());
		assertNotEquals(null, simplPriceTab);

		assertEquals(2, simplPriceTab.getCodes().size());
		
		List<String> currCodeArr = simplPriceTab.getCodes();
		assertEquals("SAP", currCodeArr.get(0));
		assertEquals("MBG", currCodeArr.get(1));

		assertEquals(145.0, simplPriceTab.getConversionFactor("SAP").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145,   simplPriceTab.getConversionFactorRat("SAP").getNumerator().intValue());
		assertEquals(1,     simplPriceTab.getConversionFactorRat("SAP").getDenominator().intValue());
		
		assertEquals(11.265, simplPriceTab.getConversionFactor("MBG").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2253,   simplPriceTab.getConversionFactorRat("MBG").getNumerator().intValue());
		assertEquals(200,    simplPriceTab.getConversionFactorRat("MBG").getDenominator().intValue());
	}

	@Test
	public void test01_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.SecIdType.ISIN.toString());
		assertNotEquals(null, simplPriceTab);

		assertEquals(2, simplPriceTab.getCodes().size());
		
		List<String> currCodeArr = simplPriceTab.getCodes();
		assertEquals("FR0000120644", currCodeArr.get(0));
		assertEquals("DE000BASF111", currCodeArr.get(1));

		assertEquals(53.58, simplPriceTab.getConversionFactor("FR0000120644").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2679,  simplPriceTab.getConversionFactorRat("FR0000120644").getNumerator().intValue());
		assertEquals(50,    simplPriceTab.getConversionFactorRat("FR0000120644").getDenominator().intValue());
		
		assertEquals(46.3, simplPriceTab.getConversionFactor("DE000BASF111").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(463,  simplPriceTab.getConversionFactorRat("DE000BASF111").getNumerator().intValue());
		assertEquals(10,   simplPriceTab.getConversionFactorRat("DE000BASF111").getDenominator().intValue());
	}

	@Test
	public void test02_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.Exchange.EURONEXT.toString());
		assertNotEquals(null, simplPriceTab);

		FixedPointNumber valFP = new FixedPointNumber("101.0");
		BigFraction      valBF = BigFraction.of(101, 1);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(14645, simplPriceTab.convertToBaseCurrency(valFP, "SAP").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(14645, simplPriceTab.convertToBaseCurrencyRat(valBF, "SAP").getNumerator().intValue());
		assertEquals(1,     simplPriceTab.convertToBaseCurrencyRat(valBF, "SAP").getDenominator().intValue());

		assertEquals(1137.765, simplPriceTab.convertToBaseCurrency(valFP, "MBG").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(227553,   simplPriceTab.convertToBaseCurrencyRat(valBF, "MBG").getNumerator().intValue());
		assertEquals(200,      simplPriceTab.convertToBaseCurrencyRat(valBF, "MBG").getDenominator().intValue());
	}

	@Test
	public void test02_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.Exchange.EURONEXT.toString());
		assertNotEquals(null, simplPriceTab);

		FixedPointNumber valFP = new FixedPointNumber("14645");
		BigFraction      valBF = BigFraction.of(14645, 1);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101.0, simplPriceTab.convertFromBaseCurrency(valFP, "SAP").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   simplPriceTab.convertFromBaseCurrencyRat(valBF, "SAP").getNumerator().intValue());
		assertEquals(1,     simplPriceTab.convertFromBaseCurrencyRat(valBF, "SAP").getDenominator().intValue());

		valFP = new FixedPointNumber("1137.765");
		valBF = BigFraction.of(227553, 200);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101.0, simplPriceTab.convertFromBaseCurrency(valFP, "MBG").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   simplPriceTab.convertFromBaseCurrencyRat(valBF, "MBG").getNumerator().intValue());
		assertEquals(1,     simplPriceTab.convertFromBaseCurrencyRat(valBF, "MBG").getDenominator().intValue());
	}

	@Test
	public void test03_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.SecIdType.ISIN.toString());
		assertNotEquals(null, simplPriceTab);

		FixedPointNumber valFP = new FixedPointNumber("101.0");
		BigFraction      valBF = BigFraction.of(101, 1);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(5411.58, simplPriceTab.convertToBaseCurrency(valFP, "FR0000120644").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(270579,  simplPriceTab.convertToBaseCurrencyRat(valBF, "FR0000120644").getNumerator().intValue());
		assertEquals(50,      simplPriceTab.convertToBaseCurrencyRat(valBF, "FR0000120644").getDenominator().intValue());

		assertEquals(4676.3, simplPriceTab.convertToBaseCurrency(valFP, "DE000BASF111").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(46763,  simplPriceTab.convertToBaseCurrencyRat(valBF, "DE000BASF111").getNumerator().intValue());
		assertEquals(10,     simplPriceTab.convertToBaseCurrencyRat(valBF, "DE000BASF111").getDenominator().intValue());
	}

	@Test
	public void test03_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.SecIdType.ISIN.toString());
		assertNotEquals(null, simplPriceTab);

		FixedPointNumber valFP = new FixedPointNumber("5411.58");
		BigFraction      valBF = BigFraction.of(270579, 50);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101.0, simplPriceTab.convertFromBaseCurrency(valFP, "FR0000120644").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   simplPriceTab.convertFromBaseCurrencyRat(valBF, "FR0000120644").getNumerator().intValue());
		assertEquals(1,     simplPriceTab.convertFromBaseCurrencyRat(valBF, "FR0000120644").getDenominator().intValue());

		valFP = new FixedPointNumber("4676.3");
		valBF = BigFraction.of(46763, 10);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101.0, simplPriceTab.convertFromBaseCurrency(valFP, "DE000BASF111").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   simplPriceTab.convertFromBaseCurrencyRat(valBF, "DE000BASF111").getNumerator().intValue());
		assertEquals(1,     simplPriceTab.convertFromBaseCurrencyRat(valBF, "DE000BASF111").getDenominator().intValue());
	}
}
