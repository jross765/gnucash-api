package org.gnucash.api.read.hlp.invc;

public interface GnuCashGenerInvoice_Job_Str {
    
    String getJobInvcAmountUnpaidWithTaxesFormatted();

    String getJobInvcAmountPaidWithTaxesFormatted();

    String getJobInvcAmountPaidWithoutTaxesFormatted();

    String getJobInvcAmountWithTaxesFormatted();

    String getJobInvcAmountWithoutTaxesFormatted();

}
