package org.gnucash.api.pricedb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.ConstTest;
import org.gnucash.api.pricedb.ComplexPriceTable;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshSecID_Exchange;
import org.gnucash.base.basetypes.complex.GCshSecID_SecIdType;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestComplexPriceTable {
	private GnuCashFile gcshFile = null;
	private ComplexPriceTable complPriceTab = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestComplexPriceTable.class);
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

		assertEquals(2, complPriceTab.getTabTypes().size());

		List<GCshCmdtyID.Type> nameSpaceArr = complPriceTab.getTabTypes();
		assertEquals(GCshCmdtyID.Type.CURRENCY, nameSpaceArr.get(0));
		assertEquals(GCshCmdtyID.Type.SECURITY, nameSpaceArr.get(1));
	}

	@Test
	public void test02_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		GCshSecID_Exchange cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "SAP");
		assertEquals(145.0, complPriceTab.getConversionFactor(cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145,   complPriceTab.getConversionFactorRat(cmdtyID).getNumerator().intValue());
		assertEquals(1,     complPriceTab.getConversionFactorRat(cmdtyID).getDenominator().intValue());

		cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "MBG");
		assertEquals(11.265, complPriceTab.getConversionFactor(cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2253,   complPriceTab.getConversionFactorRat(cmdtyID).getNumerator().intValue());
		assertEquals(200,    complPriceTab.getConversionFactorRat(cmdtyID).getDenominator().intValue());
	}

	@Test
	public void test02_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		GCshSecID_SecIdType cmdtyID = new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "FR0000120644");
		assertEquals(53.58, complPriceTab.getConversionFactor(cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2679,  complPriceTab.getConversionFactorRat(cmdtyID).getNumerator().intValue());
		assertEquals(50,    complPriceTab.getConversionFactorRat(cmdtyID).getDenominator().intValue());
		
		cmdtyID = new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "DE000BASF111");
		assertEquals(46.3, complPriceTab.getConversionFactor(cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(463,  complPriceTab.getConversionFactorRat(cmdtyID).getNumerator().intValue());
		assertEquals(10,   complPriceTab.getConversionFactorRat(cmdtyID).getDenominator().intValue());
	}

	@Test
	public void test03_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		FixedPointNumber valFP = new FixedPointNumber("101.0");
		BigFraction      valBF = BigFraction.of(101, 1);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		GCshSecID_Exchange cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "SAP");
		assertEquals(14645, complPriceTab.convertToBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(14645, complPriceTab.convertToBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(1,     complPriceTab.convertToBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());

		cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "MBG");
		assertEquals(1137.765, complPriceTab.convertToBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(227553,   complPriceTab.convertToBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(200,      complPriceTab.convertToBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());
	}

	@Test
	public void test03_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		FixedPointNumber valFP = new FixedPointNumber("14645");
		BigFraction      valBF = BigFraction.of(14645, 1);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		GCshSecID_Exchange cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "SAP");
		assertEquals(101.0, complPriceTab.convertFromBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   complPriceTab.convertFromBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(1,     complPriceTab.convertFromBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());

		valFP = new FixedPointNumber("1137.765");
		valBF = BigFraction.of(1137765, 1000);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		cmdtyID = new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "MBG");
		assertEquals(101.0, complPriceTab.convertFromBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   complPriceTab.convertFromBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(1,     complPriceTab.convertFromBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());
	}

	@Test
	public void test04_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		FixedPointNumber valFP = new FixedPointNumber("101.0");
		BigFraction      valBF = BigFraction.of(101, 1);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		GCshSecID_SecIdType cmdtyID = new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "FR0000120644");
		assertEquals(5411.58, complPriceTab.convertToBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(270579,  complPriceTab.convertToBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(50,      complPriceTab.convertToBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());

		cmdtyID = new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "DE000BASF111");
		assertEquals(4676.3, complPriceTab.convertToBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(46763,  complPriceTab.convertToBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(10,     complPriceTab.convertToBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());
	}

	@Test
	public void test04_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		FixedPointNumber valFP = new FixedPointNumber("5411.58");
		BigFraction      valBF = BigFraction.of(541158, 100);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		GCshSecID_SecIdType cmdtyID = new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "FR0000120644");
		assertEquals(101.0, complPriceTab.convertFromBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   complPriceTab.convertFromBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(1,     complPriceTab.convertFromBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());

		valFP = new FixedPointNumber("4676.3");
		valBF = BigFraction.of(46763, 10);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		cmdtyID = new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "DE000BASF111");
		assertEquals(101.0, complPriceTab.convertFromBaseCurrency(valFP, cmdtyID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   complPriceTab.convertFromBaseCurrencyRat(valBF, cmdtyID).getNumerator().intValue());
		assertEquals(1,     complPriceTab.convertFromBaseCurrencyRat(valBF, cmdtyID).getDenominator().intValue());
	}


	@Test
	public void test05_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		FixedPointNumber valFP = new FixedPointNumber("101.0");
		BigFraction      valBF = BigFraction.of(101, 1);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		GCshCurrID currID = new GCshCurrID("USD");
		assertEquals(93.93, complPriceTab.convertToBaseCurrency(valFP, currID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(9393,  complPriceTab.convertToBaseCurrencyRat(valBF, currID).getNumerator().intValue());
		assertEquals(100,   complPriceTab.convertToBaseCurrencyRat(valBF, currID).getDenominator().intValue());
	}

	@Test
	public void test05_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		FixedPointNumber valFP = new FixedPointNumber("93.93");
		BigFraction      valBF = BigFraction.of(9393, 100);
		assertEquals(valFP.doubleValue(), valBF.doubleValue(), ConstTest.DIFF_TOLERANCE);
		GCshCurrID currID = new GCshCurrID("USD");
		assertEquals(101.0, complPriceTab.convertFromBaseCurrency(valFP, currID).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(101,   complPriceTab.convertFromBaseCurrencyRat(valBF, currID).getNumerator().intValue());
		assertEquals(1,     complPriceTab.convertFromBaseCurrencyRat(valBF, currID).getDenominator().intValue());
	}
}
