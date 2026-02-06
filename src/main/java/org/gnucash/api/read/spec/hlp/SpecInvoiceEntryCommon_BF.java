package org.gnucash.api.read.spec.hlp;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashCustomerInvoiceEntry
 * @see GnuCashEmployeeVoucherEntry
 * @see GnuCashVendorBillEntry
 * @see GnuCashJobInvoiceEntry
 */
public interface SpecInvoiceEntryCommon_BF extends GnuCashGenerInvoiceEntry {
	
	BigFraction getPriceRat();

	// ----------------------------

	BigFraction getApplicableTaxPercentRat();

	// ---------------------------------------------------------------

	BigFraction getSumRat();

	BigFraction getSumInclTaxesRat();

	BigFraction getSumExclTaxesRat();

}
