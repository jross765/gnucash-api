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

    FixedPointNumber getAmountUnpaidWithTaxes();

    FixedPointNumber getAmountPaidWithTaxes();

    FixedPointNumber getAmountPaidWithoutTaxes();

    FixedPointNumber getAmountWithTaxes();
    
    FixedPointNumber getAmountWithoutTaxes();

}
