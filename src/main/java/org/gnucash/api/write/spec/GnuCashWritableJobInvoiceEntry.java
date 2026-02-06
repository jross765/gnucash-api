package org.gnucash.api.write.spec;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.GnuCashJobInvoiceEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.spec.hlp.SpecWritableInvoiceEntryCommon_BF;
import org.gnucash.api.write.spec.hlp.SpecWritableInvoiceEntryCommon_FP;
import org.gnucash.api.write.spec.hlp.SpecWritableInvoiceEntryCommon_Rest;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

/**
 * Invoice-Entry that can be modified.
 * 
 * @see GnuCashJobInvoiceEntry
 * 
 * @see GnuCashWritableCustomerInvoiceEntry
 * @see GnuCashWritableEmployeeVoucherEntry
 * @see GnuCashWritableVendorBillEntry
 */
public interface GnuCashWritableJobInvoiceEntry extends GnuCashWritableGenerInvoiceEntry,
														GnuCashJobInvoiceEntry,
														SpecWritableInvoiceEntryCommon_FP,
														SpecWritableInvoiceEntryCommon_BF,
														SpecWritableInvoiceEntryCommon_Rest,
                                                        GnuCashWritableObject 
{

	/**
	 * 
	 * @param val
	 * @throws TaxTableNotFoundException
	 * @throws UnknownInvoiceTypeException
	 * @throws IllegalTransactionSplitActionException
	 * 
	 * @see #isTaxable()
	 */
    void setTaxable(boolean val) throws TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    /**
     * 
     * @param taxTab
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException
     * @throws IllegalTransactionSplitActionException
     * 
     * @see #getTaxTable()
     */
    void setTaxTable(GCshTaxTable taxTab) throws TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

}
