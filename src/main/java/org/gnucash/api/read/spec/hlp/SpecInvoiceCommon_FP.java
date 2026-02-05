package org.gnucash.api.read.spec.hlp;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashCustomerInvoice
 * @see GnuCashEmployeeVoucher
 * @see GnuCashVendorBill
 * @see GnuCashJobInvoice
 */
public interface SpecInvoiceCommon_FP {

    public FixedPointNumber getAmountUnpaidWithTaxes();

    public FixedPointNumber getAmountPaidWithTaxes();

    public FixedPointNumber getAmountPaidWithoutTaxes();

    public FixedPointNumber getAmountWithTaxes();
    
    public FixedPointNumber getAmountWithoutTaxes();

}
