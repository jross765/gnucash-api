package org.gnucash.api.read.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;

public interface GnuCashGenerInvoiceEntry_Vend_BF {
  
    BigFraction getVendBllPriceRat();

    // ---------------------------------------------------------------

    BigFraction getVendBllApplicableTaxPercentRat();

    // ---------------------------------------------------------------

    /*
     * This is the vendor bill sum as entered by the user. The user can decide
     * to include or exclude taxes.
     */
    BigFraction getVendBllSumRat();

    BigFraction getVendBllSumInclTaxesRat();

    BigFraction getVendBllSumExclTaxesRat();

}
