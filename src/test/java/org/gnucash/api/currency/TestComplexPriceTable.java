package org.gnucash.api.currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.List;

import org.gnucash.api.ConstTest;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_Exchange;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_SecIdType;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.impl.GnuCashFileImpl;
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

		assertEquals(3, complPriceTab.getNameSpaces().size());

		List<String> nameSpaceArr = complPriceTab.getNameSpaces();
		assertEquals(GCshCmdtyCurrNameSpace.CURRENCY, nameSpaceArr.get(0));
		assertEquals(GCshCmdtyCurrNameSpace.Exchange.EURONEXT.toString(), nameSpaceArr.get(1));
		assertEquals(GCshCmdtyCurrNameSpace.SecIdType.ISIN.toString(), nameSpaceArr.get(2));
	}

	@Test
	public void test02_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		assertEquals(145.0, complPriceTab
				.getConversionFactor(GCshCmdtyCurrNameSpace.Exchange.EURONEXT.toString(), "SAP").doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(11.265, complPriceTab
				.getConversionFactor(GCshCmdtyCurrNameSpace.Exchange.EURONEXT.toString(), "MBG").doubleValue(),
				ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test02_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		assertEquals(53.58, complPriceTab
				.getConversionFactor(GCshCmdtyCurrNameSpace.SecIdType.ISIN.toString(), "FR0000120644").doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(46.3, complPriceTab
				.getConversionFactor(GCshCmdtyCurrNameSpace.SecIdType.ISIN.toString(), "DE000BASF111").doubleValue(),
				ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test03_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		FixedPointNumber val = new FixedPointNumber("101.0");
		assertEquals(true, complPriceTab.convertToBaseCurrency(val,
				new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.EURONEXT, "SAP")));
		assertEquals(14645, val.doubleValue(), ConstTest.DIFF_TOLERANCE);

		val = new FixedPointNumber("101.0");
		assertEquals(true, complPriceTab.convertToBaseCurrency(val,
				new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.EURONEXT, "MBG")));
		assertEquals(1137.765, val.doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test03_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		FixedPointNumber val = new FixedPointNumber("14645");
		assertEquals(true, complPriceTab.convertFromBaseCurrency(val,
				new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.EURONEXT, "SAP")));
		assertEquals(101.0, val.doubleValue(), ConstTest.DIFF_TOLERANCE);

		val = new FixedPointNumber("1137.765");
		assertEquals(true, complPriceTab.convertFromBaseCurrency(val,
				new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.EURONEXT, "MBG")));
		assertEquals(101.0, val.doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test03_3() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		FixedPointNumber val = new FixedPointNumber("101.0");
		assertEquals(true, complPriceTab.convertToBaseCurrency(val,
				new GCshCmdtyID_SecIdType(GCshCmdtyCurrNameSpace.SecIdType.ISIN, "FR0000120644")));
		assertEquals(5411.58, val.doubleValue(), ConstTest.DIFF_TOLERANCE);

		val = new FixedPointNumber("101.0");
		assertEquals(true, complPriceTab.convertToBaseCurrency(val,
				new GCshCmdtyID_SecIdType(GCshCmdtyCurrNameSpace.SecIdType.ISIN, "DE000BASF111")));
		assertEquals(4676.3, val.doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test03_4() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		FixedPointNumber val = new FixedPointNumber("5411.58");
		assertEquals(true, complPriceTab.convertFromBaseCurrency(val,
				new GCshCmdtyID_SecIdType(GCshCmdtyCurrNameSpace.SecIdType.ISIN, "FR0000120644")));
		assertEquals(101.0, val.doubleValue(), ConstTest.DIFF_TOLERANCE);

		val = new FixedPointNumber("4676.3");
		assertEquals(true, complPriceTab.convertFromBaseCurrency(val,
				new GCshCmdtyID_SecIdType(GCshCmdtyCurrNameSpace.SecIdType.ISIN, "DE000BASF111")));
		assertEquals(101.0, val.doubleValue(), ConstTest.DIFF_TOLERANCE);
	}
}
