package org.gnucash.api.write.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.write.spec.GnuCashWritableVendorBillEntry;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

public interface GnuCashWritableGenerInvoice_Vend_BF {

    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @return 
     * 
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableVendorBillEntry createVendBllEntryRat(
    		GnuCashAccount acct,
    		BigFraction singleUnitPrice,
    		BigFraction quantity)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @param taxTabName 
     * @return 
     * 
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableVendorBillEntry createVendBllEntryRat(
    		GnuCashAccount acct,
    		BigFraction singleUnitPrice,
    		BigFraction quantity,
    		String taxTabName)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * create and add a new entry.<br/>
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @param taxTab 
     *
     * @return an entry using the given Tax-Table
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableVendorBillEntry createVendBllEntryRat(
    		GnuCashAccount acct,
    		BigFraction singleUnitPrice,
    		BigFraction quantity,
    		GCshTaxTable taxTab)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

}
