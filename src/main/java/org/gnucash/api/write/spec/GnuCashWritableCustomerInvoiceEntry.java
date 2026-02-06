package org.gnucash.api.write.spec;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.GnuCashCustomerInvoiceEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.spec.hlp.SpecWritableInvoiceEntryCommon_BF;
import org.gnucash.api.write.spec.hlp.SpecWritableInvoiceEntryCommon_FP;
import org.gnucash.api.write.spec.hlp.SpecWritableInvoiceEntryCommon_Rest;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

/**
 * Customer invoice entry that can be modified.
 * 
 * @see GnuCashCustomerInvoiceEntry
 * 
 * @see GnuCashWritableEmployeeVoucherEntry
 * @see GnuCashWritableVendorBillEntry
 * @see GnuCashWritableJobInvoiceEntry
 */
public interface GnuCashWritableCustomerInvoiceEntry extends GnuCashWritableGenerInvoiceEntry,
                                                             GnuCashCustomerInvoiceEntry,
                                                             SpecWritableInvoiceEntryCommon_FP,
                                                             SpecWritableInvoiceEntryCommon_BF,
                                                             SpecWritableInvoiceEntryCommon_Rest,
                                                             GnuCashWritableObject 
{

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
