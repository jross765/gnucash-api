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
public interface SpecInvoiceEntryCommon_FP extends GnuCashGenerInvoiceEntry {
	
	FixedPointNumber getPrice();

	// ----------------------------

	FixedPointNumber getApplicableTaxPercent();

	// ---------------------------------------------------------------

	FixedPointNumber getSum();

	FixedPointNumber getSumInclTaxes();

	FixedPointNumber getSumExclTaxes();

}
