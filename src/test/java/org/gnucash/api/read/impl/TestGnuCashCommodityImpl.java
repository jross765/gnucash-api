package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.List;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshSecID_Exchange;
import org.gnucash.base.basetypes.complex.GCshSecID_SecIdType;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashCommodityImpl {
	// Mercedes-Benz Group AG
	public static final GCshCmdtyNameSpace.Exchange CMDTY_1_EXCH = GCshCmdtyNameSpace.Exchange.EURONEXT;
	public static final String CMDTY_1_CODE = "MBG";
	public static final String CMDTY_1_ISIN = "DE0007100000";

	// SAP SE
	public static final GCshCmdtyNameSpace.Exchange CMDTY_2_EXCH = GCshCmdtyNameSpace.Exchange.EURONEXT;
	public static final String CMDTY_2_CODE = "SAP";
	public static final String CMDTY_2_ISIN = "DE0007164600";

	// AstraZeneca Plc
	// Note that in the SecIDType variants, the ISIN/CUSIP/SEDOL/WKN/whatever
	// is stored twice in the object, redundantly
	public static final GCshCmdtyNameSpace.SecIdType CMDTY_3_SECIDTYPE = GCshCmdtyNameSpace.SecIdType.ISIN;
	public static final String CMDTY_3_CODE = "GB0009895292";
	public static final String CMDTY_3_ISIN = CMDTY_3_CODE;

	// Coca Cola
	public static final GCshCmdtyNameSpace.SecIdType CMDTY_4_SECIDTYPE = GCshCmdtyNameSpace.SecIdType.ISIN;
	public static final String CMDTY_4_CODE = "US1912161007";
	public static final String CMDTY_4_ISIN = CMDTY_4_CODE;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashCommodity cmdty = null;

	private GCshCmdtyID cmdtyCurrID1 = null;
	private GCshCmdtyID cmdtyCurrID2 = null;
	private GCshCmdtyID cmdtyCurrID3 = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashCommodityImpl.class);
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

		// ---

		cmdtyCurrID1 = new GCshSecID_Exchange(CMDTY_1_EXCH, CMDTY_1_CODE);
		cmdtyCurrID2 = new GCshSecID_Exchange(CMDTY_2_EXCH, CMDTY_2_CODE);
		cmdtyCurrID3 = new GCshSecID_SecIdType(CMDTY_3_SECIDTYPE, CMDTY_3_CODE);
	}

	// -----------------------------------------------------------------

	@Test
	public void test00() throws Exception {
		// Cf. TestCmdtyCurrID -- let's just double-check
		assertEquals(CMDTY_1_EXCH.toString() + GCshCmdtyID.SEPARATOR + CMDTY_1_CODE, cmdtyCurrID1.toString());
		assertEquals(CMDTY_2_EXCH.toString() + GCshCmdtyID.SEPARATOR + CMDTY_2_CODE, cmdtyCurrID2.toString());
		assertEquals(CMDTY_3_SECIDTYPE.toString() + GCshCmdtyID.SEPARATOR + CMDTY_3_CODE, cmdtyCurrID3.toString());
	}

	// ------------------------------

	@Test
	public void test01_1() throws Exception {
		cmdty = gcshFile.getCommodityByNamSpcCode(CMDTY_1_EXCH, CMDTY_1_CODE);
		assertNotEquals(null, cmdty);

		assertEquals(cmdtyCurrID1.toString(), cmdty.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID1, cmdty.getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, cmdty.getQualifID()); // not trivial!
		assertEquals(CMDTY_1_ISIN, cmdty.getXCode());
		assertEquals("Mercedes-Benz Group AG", cmdty.getName());
	}

	@Test
	public void test01_2() throws Exception {
		cmdty = gcshFile.getCommodityByID(cmdtyCurrID1);
		assertNotEquals(null, cmdty);

		assertEquals(cmdtyCurrID1.toString(), cmdty.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID1, cmdty.getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, cmdty.getQualifID()); // not trivial!
		assertEquals(CMDTY_1_ISIN, cmdty.getXCode());
		assertEquals("Mercedes-Benz Group AG", cmdty.getName());
		
		// ---

		cmdty = gcshFile.getCommodityByQualifID(cmdtyCurrID1.toString());
		assertNotEquals(null, cmdty);

		assertEquals(cmdtyCurrID1.toString(), cmdty.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID1, cmdty.getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, cmdty.getQualifID()); // not trivial!
		assertEquals(CMDTY_1_ISIN, cmdty.getXCode());
		assertEquals("Mercedes-Benz Group AG", cmdty.getName());
	}

	@Test
	public void test01_3() throws Exception {
		cmdty = gcshFile.getCommodityByXCode(CMDTY_1_ISIN);
		assertNotEquals(null, cmdty);

		assertEquals(cmdtyCurrID1.toString(), cmdty.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID1, cmdty.getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, cmdty.getQualifID()); // not trivial!
		assertEquals(CMDTY_1_ISIN, cmdty.getXCode());
		assertEquals("Mercedes-Benz Group AG", cmdty.getName());
	}

	@Test
	public void test01_4() throws Exception {
		List<GnuCashCommodity> cmdtyList = gcshFile.getCommoditiesByName("mercedes");
		assertNotEquals(null, cmdtyList);
		assertEquals(1, cmdtyList.size());

		assertEquals(cmdtyCurrID1.toString(), cmdtyList.get(0).getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID1, cmdtyList.get(0).getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, 
		//	        ((GnuCashCommodity) cmdtyList.toArray()[0]).getQualifID()); // not trivial!
		assertEquals(CMDTY_1_ISIN, cmdtyList.get(0).getXCode());
		assertEquals("Mercedes-Benz Group AG", ((GnuCashCommodity) cmdtyList.toArray()[0]).getName());

		cmdtyList = gcshFile.getCommoditiesByName("BENZ");
		assertNotEquals(null, cmdtyList);
		assertEquals(1, cmdtyList.size());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID1, cmdtyList.get(0).getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, 
		//	         cmdtyList.get(0).getQualifID());

		cmdtyList = gcshFile.getCommoditiesByName(" MeRceDeS-bEnZ  ");
		assertNotEquals(null, cmdtyList);
		assertEquals(1, cmdtyList.size());
		assertEquals(cmdtyCurrID1.toString(), cmdtyList.get(0).getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID1, cmdtyList.get(0).getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, 
		//	         cmdtyList.get(0).getQualifID()); // not trivial!
	}

	// ------------------------------

	@Test
	public void test02_1() throws Exception {
		cmdty = gcshFile.getCommodityByNamSpcCode(CMDTY_3_SECIDTYPE.toString(), CMDTY_3_CODE);
		assertNotEquals(null, cmdty);

		assertEquals(cmdtyCurrID3.toString(), cmdty.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID3, cmdty.getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, cmdty.getQualifID()); // not trivial!
		assertEquals(CMDTY_3_ISIN, cmdty.getXCode());
		assertEquals("AstraZeneca Plc", cmdty.getName());
	}

	@Test
	public void test02_2() throws Exception {
		cmdty = gcshFile.getCommodityByID(cmdtyCurrID3);
		assertNotEquals(null, cmdty);

		assertEquals(cmdtyCurrID3.toString(), cmdty.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID3, cmdty.getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, cmdty.getQualifID()); // not trivial!
		assertEquals(CMDTY_3_ISIN, cmdty.getXCode());
		assertEquals("AstraZeneca Plc", cmdty.getName());
		
		// ---
		
		cmdty = gcshFile.getCommodityByQualifID(cmdtyCurrID3.toString());
		assertNotEquals(null, cmdty);

		assertEquals(cmdtyCurrID3.toString(), cmdty.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID3, cmdty.getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, cmdty.getQualifID()); // not trivial!
		assertEquals(CMDTY_3_ISIN, cmdty.getXCode());
		assertEquals("AstraZeneca Plc", cmdty.getName());
	}

	@Test
	public void test02_3() throws Exception {
		cmdty = gcshFile.getCommodityByXCode(CMDTY_3_ISIN);
		assertNotEquals(null, cmdty);

		assertEquals(cmdtyCurrID3.toString(), cmdty.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID3, cmdty.getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, cmdty.getQualifID()); // not trivial!
		assertEquals(CMDTY_3_ISIN, cmdty.getXCode());
		assertEquals("AstraZeneca Plc", cmdty.getName());
	}

	@Test
	public void test02_4() throws Exception {
		List<GnuCashCommodity> cmdtyList = gcshFile.getCommoditiesByName("astra");
		assertNotEquals(null, cmdtyList);
		assertEquals(1, cmdtyList.size());

		assertEquals(cmdtyCurrID3.toString(), ((GnuCashCommodity) cmdtyList.toArray()[0]).getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID3, ((GnuCashCommodity) cmdtyList.toArray()[0]).getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, 
		//	        ((GnuCashCommodity) cmdtyList.toArray()[0]).getQualifID()); // not trivial!
		assertEquals(CMDTY_3_ISIN, ((GnuCashCommodity) cmdtyList.toArray()[0]).getXCode());
		assertEquals("AstraZeneca Plc", ((GnuCashCommodity) cmdtyList.toArray()[0]).getName());

		cmdtyList = gcshFile.getCommoditiesByName("BENZ");
		assertNotEquals(null, cmdtyList);
		assertEquals(1, cmdtyList.size());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID3, ((GnuCashCommodity) cmdtyList.toArray()[0]).getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, 
		//	         ((GnuCashCommodity) cmdtyList.toArray()[0]).getQualifID());

		cmdtyList = gcshFile.getCommoditiesByName(" aStrAzENeCA  ");
		assertNotEquals(null, cmdtyList);
		assertEquals(1, cmdtyList.size());
		assertEquals(cmdtyCurrID3.toString(), ((GnuCashCommodity) cmdtyList.toArray()[0]).getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(cmdtyCurrID3, ((GnuCashCommodity) cmdtyList.toArray()[0]).getQualifID());
		// ::TODO: Convert to CommodityID_Exchange, then it should be equal
		//    assertEquals(cmdtyCurrID1, 
		//	         ((GnuCashCommodity) cmdtyList.toArray()[0]).getQualifID()); // not trivial!
	}
}
