package org.gnucash.api.read.hlp.own;

import java.util.Locale;

public interface GnuCashGenerJob_Invc_Str {

    /**
     * @return Formatted according to the system's locale's currency-format
     *  
     * @see #getIncomeGeneratedFormatted(Locale)
     */
    String getIncomeGeneratedFormatted();

    /**
     * @param lcl 
     * @return Formatted according to the given locale's currency format
     *  
     * @see #getIncomeGenerated() 
     */
    String getIncomeGeneratedFormatted(Locale lcl);
    
    // ----------------------------

    /**
     * @param lcl 
     * @return Formatted according to the system's locale's currency-format
     *  
     * @see #getOutstandingValueFormatted(Locale)
    */
    String getOutstandingValueFormatted();

    /**
     * @param lcl 
     * @return Formatted according to the given locale's currency format
     *  
     * @see #getOutstandingValue() 
     */
    String getOutstandingValueFormatted(Locale lcl);

}
