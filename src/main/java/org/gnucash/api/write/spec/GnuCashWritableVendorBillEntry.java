package org.gnucash.api.write.spec;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.GnuCashVendorBillEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.spec.hlp.SpecWritableInvoiceEntryCommon_BF;
import org.gnucash.api.write.spec.hlp.SpecWritableInvoiceEntryCommon_FP;
import org.gnucash.api.write.spec.hlp.SpecWritableInvoiceEntryCommon_Rest;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

/**
 * Vendor bill entry  that can be modified.
 * 
 * @see GnuCashVendorBillEntry
 * 
 * @see GnuCashWritableCustomerInvoiceEntry
 * @see GnuCashWritableEmployeeVoucherEntry
 * @see GnuCashWritableJobInvoiceEntry
 */
public interface GnuCashWritableVendorBillEntry extends GnuCashWritableGenerInvoiceEntry,
														GnuCashVendorBillEntry,
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
