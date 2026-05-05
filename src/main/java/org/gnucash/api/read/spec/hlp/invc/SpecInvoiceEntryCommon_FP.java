package org.gnucash.api.read.spec.hlp.invc;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashCustomerInvoiceEntry
 * @see GnuCashEmployeeVoucherEntry
 * @see GnuCashVendorBillEntry
 * @see GnuCashJobInvoiceEntry
 */
@Deprecated
public interface SpecInvoiceEntryCommon_FP extends GnuCashGenerInvoiceEntry {
	
	@Deprecated
	FixedPointNumber getPrice();

	// ----------------------------

	@Deprecated
	FixedPointNumber getApplicableTaxPercent();

	// ---------------------------------------------------------------

	@Deprecated
	FixedPointNumber getSum();

	@Deprecated
	FixedPointNumber getSumInclTaxes();

	@Deprecated
	FixedPointNumber getSumExclTaxes();

}
