package org.gnucash.api.write.hlp.fil;

import java.util.Collection;
import java.util.List;

import org.gnucash.api.write.GnuCashWritableCommodity;
import org.gnucash.api.write.ObjectCascadeException;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableFile_Cmdty {

    GnuCashWritableCommodity getWritableCommodityByQualifID(GCshCmdtyID cmdtyID);

    GnuCashWritableCommodity getWritableCommodityByQualifID(String nameSpace, String id);

    GnuCashWritableCommodity getWritableCommodityByQualifID(GCshCmdtyNameSpace.Exchange exchange, String id);

    GnuCashWritableCommodity getWritableCommodityByQualifID(GCshCmdtyNameSpace.MIC mic, String id);

    GnuCashWritableCommodity getWritableCommodityByQualifID(GCshCmdtyNameSpace.SecIdType secIdType, String id);

    GnuCashWritableCommodity getWritableCommodityByQualifID(String qualifID);

    GnuCashWritableCommodity getWritableCommodityByXCode(String xCode);

    List<GnuCashWritableCommodity> getWritableCommoditiesByName(String expr);

    List<GnuCashWritableCommodity> getWritableCommoditiesByName(String expr, boolean relaxed);
    
    GnuCashWritableCommodity getWritableCommodityByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;
    
    Collection<GnuCashWritableCommodity> getWritableCommodities();

    // ----------------------------

    /**
     * @param qualifID Technical commodity ID, fully qualified (e.g., containing the 'name space').
     *                 <strong>Caution:</strong> GnuCash, for whatever reason, has no internal
     *                 technical UUID for commodities (as opposed to all other entities). This is
     *                 why, in this lib, we use the 'qualified' commodity ID as sort of pseudo-technical-
     *                 ID, which is very similar to the code, but declared 'technical' nonetheless.
	 * @param code  Commodity code (<strong>not</strong> the technical ID (which effectively
	 *              is very similar, but declared technical nontheless),
	 *              but the business ID, such as ISIN, CUSIP, etc. 
	 *              A ticker will also work, but it is <strong>not</strong> recommended,
	 *              as tickers typically are not unique, and there is a separate field
	 *              for it. 
	 * @param name  Security name
     * @return a new commodity with no values that is already added to this file
     */
    GnuCashWritableCommodity createWritableCommodity(GCshSecID cmdtyID, String code, String name);

    GnuCashWritableCommodity createWritableCommodity(GCshCurrID currID, String code, String name);

    // ---------------------------------------------------------------

    /**
     * Add a new currency.<br/>
     * If the currency already exists, add a new price-quote for it.
     * 
     * @param pCmdtySpace        the name space (e.g. "GOODS" or "CURRENCY")
     * @param pCmdtyId           the currency-name
     * @param conversionFactor   the conversion-factor from the base-currency (EUR).
     * @param pCmdtyNameFraction number of decimal-places after the comma
     * @param pCmdtyName         common name of the new currency
     */
    // ::TODO: either change ID (currency doesn't need name space supplied)
    // or change name to addCommodity and add addCurrency() to module "Spec. Ent."
    void addCurrency(String pCmdtySpace, String pCmdtyId, FixedPointNumber conversionFactor,
	    int pCmdtyNameFraction, String pCmdtyName);

    /**
     * @param cmdty the commodity to remove
     * @throws ObjectCascadeException
     */
    void removeCommodity(GnuCashWritableCommodity cmdty) throws ObjectCascadeException;

}
