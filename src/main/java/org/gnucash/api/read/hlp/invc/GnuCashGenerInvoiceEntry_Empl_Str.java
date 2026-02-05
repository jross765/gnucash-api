package org.gnucash.api.read.hlp.invc;

public interface GnuCashGenerInvoiceEntry_Empl_Str {
    
    String getEmplVchPriceFormatted();

    // ---------------------------------------------------------------

    String getEmplVchApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    String getEmplVchSumFormatted();

    String getEmplVchSumInclTaxesFormatted();

    String getEmplVchSumExclTaxesFormatted();

}
