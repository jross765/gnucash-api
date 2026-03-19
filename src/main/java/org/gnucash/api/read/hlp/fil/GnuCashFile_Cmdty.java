package org.gnucash.api.read.hlp.fil;

import java.util.List;

import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashFile_Cmdty {

    /**
     * @param cmdtyCurrID 
     * @param id the unique ID of the currency/security/commodity to look for
     * @return the currency/security/commodity or null if it's not found
     */
    GnuCashCommodity getCommodityByID(GCshCmdtyID cmdtyCurrID);

    @Deprecated
    GnuCashCommodity getCommodityByQualifID(GCshCmdtyID cmdtyCurrID);

    /**
     * @param nameSpace
     * @param code
     * @return
     */
    GnuCashCommodity getCommodityByNamSpcCode(String nameSpace, String code);

    @Deprecated
    GnuCashCommodity getCommodityByQualifID(String nameSpace, String code);

    /**
     * @param exchange
     * @param code
     * @return
     */
    GnuCashCommodity getCommodityByNamSpcCode(GCshCmdtyNameSpace.Exchange exchange, String code);

    @Deprecated
    GnuCashCommodity getCommodityByQualifID(GCshCmdtyNameSpace.Exchange exchange, String code);

    /**
     * @param mic
     * @param code
     * @return
     */
    GnuCashCommodity getCommodityByNamSpcCode(GCshCmdtyNameSpace.MIC mic, String code);

    @Deprecated
    GnuCashCommodity getCommodityByQualifID(GCshCmdtyNameSpace.MIC mic, String code);

    /**
     * @param secIdType
     * @param code
     * @return
     */
    GnuCashCommodity getCommodityByNamSpcCode(GCshCmdtyNameSpace.SecIdType secIdType, String code);

    @Deprecated
    GnuCashCommodity getCommodityByQualifID(GCshCmdtyNameSpace.SecIdType secIdType, String code);

    /**
     * @param cmdtyIDStr the unique ID of the currency/security/commodity to look for
     * @return the currency/security/commodity or null if it's not found
     */
    @Deprecated
    GnuCashCommodity getCommodityByQualifID(String cmdtyIDStr);

    /**
     * @param xCode the unique X-code of the currency/security/commodity to look for
     * @return the currency/security/commodity or null if it's not found
     */
    GnuCashCommodity getCommodityByXCode(String xCode);

    /**
     * warning: this function has to traverse all currencies/securities/commodities. If it much faster to try
     * getCommodityByID first and only call this method if the returned account does
     * not have the right name.
     * @param expr search expression
     * @return null if not found
     * @see #getCommodityByID(GCshCmdtyID)
     */
    List<GnuCashCommodity> getCommoditiesByName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    List<GnuCashCommodity> getCommoditiesByName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashCommodity getCommodityByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all currencies/securities/commodities Do not modify the
     *         returned collection!
     */
    List<GnuCashCommodity> getCommodities();

}
