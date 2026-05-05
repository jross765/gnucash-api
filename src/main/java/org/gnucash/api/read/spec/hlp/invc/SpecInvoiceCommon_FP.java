package org.gnucash.api.read.spec.hlp.invc;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashCustomerInvoice
 * @see GnuCashEmployeeVoucher
 * @see GnuCashVendorBill
 * @see GnuCashJobInvoice
 */
@Deprecated
public interface SpecInvoiceCommon_FP {

    @Deprecated
	FixedPointNumber getAmountUnpaidWithTaxes();

    @Deprecated
	FixedPointNumber getAmountPaidWithTaxes();

    @Deprecated
	FixedPointNumber getAmountPaidWithoutTaxes();

    @Deprecated
	FixedPointNumber getAmountWithTaxes();
    
    @Deprecated
	FixedPointNumber getAmountWithoutTaxes();

}
