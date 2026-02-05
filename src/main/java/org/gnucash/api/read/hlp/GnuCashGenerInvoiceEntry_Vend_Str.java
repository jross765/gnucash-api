package org.gnucash.api.read.hlp;

public interface GnuCashGenerInvoiceEntry_Vend_Str {
  
    String getVendBllPriceFormatted();

    // ---------------------------------------------------------------

    String getVendBllApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    String getVendBllSumFormatted();

    String getVendBllSumInclTaxesFormatted();

    String getVendBllSumExclTaxesFormatted();

}
