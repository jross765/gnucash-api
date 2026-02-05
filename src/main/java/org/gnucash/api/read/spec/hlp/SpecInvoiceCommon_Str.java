package org.gnucash.api.read.spec.hlp;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashCustomerInvoice
 * @see GnuCashEmployeeVoucher
 * @see GnuCashVendorBill
 * @see GnuCashJobInvoice
 */
public interface SpecInvoiceCommon_Str {

    public String getAmountUnpaidWithTaxesFormatted();

    public String getAmountPaidWithTaxesFormatted();

    public String getAmountPaidWithoutTaxesFormatted();

    public String getAmountWithTaxesFormatted();

    public String getAmountWithoutTaxesFormatted();

}
