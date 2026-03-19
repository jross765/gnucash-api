package org.gnucash.api.read.spec.hlp.invc;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashCustomerInvoice
 * @see GnuCashEmployeeVoucher
 * @see GnuCashVendorBill
 * @see GnuCashJobInvoice
 */
public interface SpecInvoiceCommon_Rest {

    boolean isFullyPaid();

    boolean isNotFullyPaid();

}
