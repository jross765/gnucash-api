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

public class TestSimpleCurrencyQuoteTable {
	private GnuCashFile gcshFile = null;
	private ComplexPriceTable complPriceTab = null;
	private SimplePriceTable simplPriceTab = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestSimpleCurrencyQuoteTable.class);
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

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.CURRENCY);
		assertNotEquals(null, simplPriceTab);

		assertEquals(2, simplPriceTab.getCodes().size());
		
		List<String> currCodeArr = simplPriceTab.getCodes();
		assertEquals(ConstTest.DEFAULT_CURRENCY, currCodeArr.get(0));
		assertEquals("USD", currCodeArr.get(1));

		assertEquals(0.93, simplPriceTab.getConversionFactor("USD").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(93,   simplPriceTab.getConversionFactorRat("USD").getNumerator().intValue());
		assertEquals(100,  simplPriceTab.getConversionFactorRat("USD").getDenominator().intValue());
	}

	@Test
	public void test02_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.CURRENCY);
		assertNotEquals(null, simplPriceTab);

		FixedPointNumber valFP = new FixedPointNumber("101.0");
		BigFraction      valBF = BigFraction.of(101, 1);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(93.93, simplPriceTab.convertToBaseCurrency(valFP, "USD").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(9393,  simplPriceTab.convertToBaseCurrencyRat(valBF, "USD").getNumerator().intValue());
		assertEquals(100,   simplPriceTab.convertToBaseCurrencyRat(valBF, "USD").getDenominator().intValue());
	}

	@Test
	public void test02_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.CURRENCY);
		assertNotEquals(null, simplPriceTab);

		FixedPointNumber valFP = new FixedPointNumber("93.93");
		BigFraction      valBF = BigFraction.of(9393, 100);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101.0, simplPriceTab.convertFromBaseCurrency(valFP, "USD").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   simplPriceTab.convertFromBaseCurrencyRat(valBF, "USD").getNumerator().intValue());
		assertEquals(1,     simplPriceTab.convertFromBaseCurrencyRat(valBF, "USD").getDenominator().intValue());
	}
}
