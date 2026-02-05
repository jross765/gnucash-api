package org.gnucash.api.read.hlp.invc;

public interface GnuCashGenerInvoiceEntry_Cust_Str {

    String getCustInvcPriceFormatted();

    // ---------------------------------------------------------------
    
    String getCustInvcApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    String getCustInvcSumFormatted();

    String getCustInvcSumInclTaxesFormatted();

    String getCustInvcSumExclTaxesFormatted();

}
