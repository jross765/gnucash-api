package org.gnucash.api.read.hlp.own;

import java.util.Locale;

import org.gnucash.api.read.GnuCashGenerInvoice;

public interface GnuCashVendor_Invc_Str {

    /**
     * @param readVar 
     * @return 
     *  
     * @see #getExpensesGenerated(GnuCashGenerInvoice.ReadVariant readVar) Formatted according to the current locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @param readVar 
     * @param lcl 
     * @return 
     *  
     * @see #getExpensesGenerated(GnuCashGenerInvoice.ReadVariant readVar) Formatted according to the given locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar, Locale lcl);

    // -------------------------------------

    /**
     * @param readVar 
     * @return 
     *  
     * @see #getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar) Formatted according to the current locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     *
     * @param readVar 
     * @param lcl 
     * @return 
     *  
     * @see #getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar) Formatted according to the given locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar, Locale lcl);

}
