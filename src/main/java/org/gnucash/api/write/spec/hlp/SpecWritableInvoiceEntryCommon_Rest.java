package org.gnucash.api.write.spec.hlp;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashWritableCustomerInvoiceEntry
 * @see GnuCashWritableEmployeeVoucherEntry
 * @see GnuCashWritableVendorBillEntry
 * @see GnuCashWritableJobInvoiceEntry
 */
public interface SpecWritableInvoiceEntryCommon_Rest extends GnuCashGenerInvoiceEntry {
	
	/*
	 * ::TODO
	 * 
	void setAccountID(GCshAcctID acctID);

	void setAccount(GnuCashAccount acct);

	// ---------------------------------------------------------------

	void setTaxable(boolean val);

	void getTaxTable(GCshTaxTable taxTab);
	*/

}
