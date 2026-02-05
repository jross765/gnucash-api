package org.gnucash.api.read.hlp;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Empl_FP {
    
    FixedPointNumber getEmplVchPrice();

    // ---------------------------------------------------------------

    FixedPointNumber getEmplVchApplicableTaxPercent();

    // ---------------------------------------------------------------

    /*
     * This is the employee voucher sum as entered by the user. The user can decide
     * to include or exclude taxes.
     */
    FixedPointNumber getEmplVchSum();

    FixedPointNumber getEmplVchSumInclTaxes();

    FixedPointNumber getEmplVchSumExclTaxes();

}
