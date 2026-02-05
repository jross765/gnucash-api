package org.gnucash.api.read.hlp;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Vend_FP {
  
    FixedPointNumber getVendBllPrice();

    // ---------------------------------------------------------------

    FixedPointNumber getVendBllApplicableTaxPercent();

    // ---------------------------------------------------------------

    /*
     * This is the vendor bill sum as entered by the user. The user can decide
     * to include or exclude taxes.
     */
    FixedPointNumber getVendBllSum();

    FixedPointNumber getVendBllSumInclTaxes();

    FixedPointNumber getVendBllSumExclTaxes();

}
