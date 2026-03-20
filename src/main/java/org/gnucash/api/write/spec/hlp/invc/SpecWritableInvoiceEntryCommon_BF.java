package org.gnucash.api.write.spec.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashWritableCustomerInvoiceEntry
 * @see GnuCashWritableEmployeeVoucherEntry
 * @see GnuCashWritableVendorBillEntry
 * @see GnuCashWritableJobInvoiceEntry
 */
public interface SpecWritableInvoiceEntryCommon_BF extends GnuCashGenerInvoiceEntry {
	
	void setPrice(BigFraction prc) throws Exception;

}
