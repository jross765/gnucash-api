package org.gnucash.api.write.spec.hlp;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

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
	*/

	// ---------------------------------------------------------------

	/**
	 * 
	 * @param val
	 * @throws TaxTableNotFoundException
	 * @throws IllegalTransactionSplitActionException
	 * 
	 * @see #isTaxable()
	 */
    void setTaxable(boolean val) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * 
     * @param taxTab
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     * 
     * @see #getTaxTable()
     */
    void setTaxTable(GCshTaxTable taxTab) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;
    
}
