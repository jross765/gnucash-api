package org.gnucash.api.read.spec.hlp.invc;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashCustomerInvoiceEntry
 * @see GnuCashEmployeeVoucherEntry
 * @see GnuCashVendorBillEntry
 * @see GnuCashJobInvoiceEntry
 */
public interface SpecInvoiceEntryCommon_Str extends GnuCashGenerInvoiceEntry {
	
	String getPriceFormatted();

	// ---------------------------------------------------------------

	String getApplicableTaxPercentFormatted();

	// ---------------------------------------------------------------

	String getSumFormatted();

	String getSumInclTaxesFormatted();

	String getSumExclTaxesFormatted();

}
