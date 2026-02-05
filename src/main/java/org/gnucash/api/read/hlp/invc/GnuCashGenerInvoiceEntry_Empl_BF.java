package org.gnucash.api.read.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;

public interface GnuCashGenerInvoiceEntry_Empl_BF {
    
    BigFraction getEmplVchPriceRat();

    // ---------------------------------------------------------------

    BigFraction getEmplVchApplicableTaxPercentRat();

    // ---------------------------------------------------------------

    /*
     * This is the employee voucher sum as entered by the user. The user can decide
     * to include or exclude taxes.
     */
    BigFraction getEmplVchSumRat();

    BigFraction getEmplVchSumInclTaxesRat();

    BigFraction getEmplVchSumExclTaxesRat();

}
