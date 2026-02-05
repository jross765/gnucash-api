package org.gnucash.api.read.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;

public interface GnuCashGenerInvoiceEntry_Cust_BF {

    BigFraction getCustInvcPriceRat();

    // ---------------------------------------------------------------
    
    BigFraction getCustInvcApplicableTaxPercentRat();

    // ---------------------------------------------------------------

    /*
     * This is the customer invoice sum as entered by the user. The user can decide
     * to include or exclude taxes.
     */
    BigFraction getCustInvcSumRat();

    BigFraction getCustInvcSumInclTaxesRat();

    BigFraction getCustInvcSumExclTaxesRat();

}
