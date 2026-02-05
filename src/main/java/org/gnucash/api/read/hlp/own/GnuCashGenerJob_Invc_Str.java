package org.gnucash.api.read.hlp.own;

import java.util.Locale;

public interface GnuCashGenerJob_Invc_Str {

    /**
     * @return 
     *  
     * @see #getIncomeGenerated() Formatted according to the current locale's
     *      currency-format
     */
    String getIncomeGeneratedFormatted();

    /**
     * @param lcl 
     * @return 
     *  
     * @see #getIncomeGenerated() Formatted according to the given locale's
     *      currency-format
     */
    String getIncomeGeneratedFormatted(Locale lcl);

    /**
     *
     * @param lcl 
     * @return 
     *  
     * @see #getOutstandingValue() Formatted according to the given locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(Locale lcl);

}
