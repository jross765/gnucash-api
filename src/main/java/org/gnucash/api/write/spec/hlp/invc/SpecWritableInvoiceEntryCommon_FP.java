package org.gnucash.api.write.spec.hlp.invc;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashWritableCustomerInvoiceEntry
 * @see GnuCashWritableEmployeeVoucherEntry
 * @see GnuCashWritableVendorBillEntry
 * @see GnuCashWritableJobInvoiceEntry
 */
public interface SpecWritableInvoiceEntryCommon_FP extends GnuCashGenerInvoiceEntry {
	
	void setPrice(FixedPointNumber prc) throws Exception;

}
