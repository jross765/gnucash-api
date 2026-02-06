package org.gnucash.api.read.spec.hlp;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.basetypes.simple.GCshAcctID;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashCustomerInvoiceEntry
 * @see GnuCashEmployeeVoucherEntry
 * @see GnuCashVendorBillEntry
 * @see GnuCashJobInvoiceEntry
 */
public interface SpecInvoiceEntryCommon_Rest extends GnuCashGenerInvoiceEntry {
	
	GCshAcctID getAccountID() throws AccountNotFoundException;

	GnuCashAccount getAccount() throws AccountNotFoundException;

	// ---------------------------------------------------------------

	boolean isTaxable();

	GCshTaxTable getTaxTable() throws TaxTableNotFoundException;

}
