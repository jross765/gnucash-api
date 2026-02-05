package org.gnucash.api.read.hlp.invc;

public interface GnuCashGenerInvoiceEntry_Vend_Str {
  
    String getVendBllPriceFormatted();

    // ---------------------------------------------------------------

    String getVendBllApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    String getVendBllSumFormatted();

    String getVendBllSumInclTaxesFormatted();

    String getVendBllSumExclTaxesFormatted();

}
