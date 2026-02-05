package org.gnucash.api.read.hlp.own;

import java.util.Locale;

public interface GnuCashEmployee_Invc_Str {

    /**
     * @return 
     *  
     * @see #getExpensesGenerated() Formatted according to the current locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted();

    /**
     * @param lcl 
     * @return 
     *  
     * @see #getExpensesGenerated() Formatted according to the given locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(Locale lcl);

    // -------------------------------------

    /**
     * @return 
     *  
     * @see #getOutstandingValue() Formatted according to the current locale's
     *      currency-format
     */
    String getOutstandingValueFormatted();

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
