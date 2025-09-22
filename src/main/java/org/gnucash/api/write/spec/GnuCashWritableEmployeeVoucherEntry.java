package org.gnucash.api.write.spec;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucherEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Employee voucher entry that can be modified.
 * 
 * @see GnuCashEmployeeVoucherEntry
 * 
 * @see GnuCashWritableCustomerInvoiceEntry
 * @see GnuCashWritableVendorBillEntry
 * @see GnuCashWritableJobInvoiceEntry
 */
public interface GnuCashWritableEmployeeVoucherEntry extends GnuCashWritableGenerInvoiceEntry, 
															 GnuCashEmployeeVoucherEntry,
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

    // ---------------------------------------------------------------

    /**
     * 
     * @param price
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     * 
     * @see #getPrice()
     * @see #setPrice(FixedPointNumber)
     */
    void setPrice(String price) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * 
     * @param price
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     * 
     * @see #getPrice()
     * @see #setPrice(String)
     */
    void setPrice(FixedPointNumber price) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

}
