package org.gnucash.api.read.hlp;

public interface GnuCashGenerInvoiceEntry_Job_Str {

    String getJobInvcPriceFormatted();

    // ---------------------------------------------------------------

    String getJobInvcApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    String getJobInvcSumFormatted();

    String getJobInvcSumInclTaxesFormatted();

    String getJobInvcSumExclTaxesFormatted();

}
