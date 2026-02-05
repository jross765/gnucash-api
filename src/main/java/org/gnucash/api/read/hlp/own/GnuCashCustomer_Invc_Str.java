package org.gnucash.api.read.hlp.own;

import java.util.Locale;

import org.gnucash.api.read.GnuCashGenerInvoice;

public interface GnuCashCustomer_Invc_Str {

    /**
     * @param readVar 
     * @return  
     *  
     * @see #getIncomeGenerated(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    String getIncomeGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @param readVar 
     * @param lcl 
     * @return 
     *  
     * @see #getIncomeGenerated(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    String getIncomeGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar, Locale lcl);

    // -------------------------------------

    /**
     * @param readVar 
     * @return 
     *  
     * @see #getOutstandingValue(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     *
     * @param readVar 
     * @param lcl 
     * @return 
     *  
     * @see #getOutstandingValue(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar, Locale lcl);

}
