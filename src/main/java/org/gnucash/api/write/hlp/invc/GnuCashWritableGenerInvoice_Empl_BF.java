package org.gnucash.api.write.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucherEntry;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

public interface GnuCashWritableGenerInvoice_Empl_BF {

    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * @param acct 
     * @param sglUntPrc 
     * @param qty 
     * @return 
     * 
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableEmployeeVoucherEntry createEmplVchEntryRat(
    		GnuCashAccount acct,
    		BigFraction sglUntPrc,
    		BigFraction qty)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * @param acct 
     * @param sglUntPrc 
     * @param qty 
     * @param taxTabName 
     * @return 
     * 
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableEmployeeVoucherEntry createEmplVchEntryRat(
    		GnuCashAccount acct,
    		BigFraction sglUntPrc,
    		BigFraction qty,
    		String taxTabName)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * create and add a new entry.<br/>
     * @param acct 
     * @param sglUntPrc 
     * @param qty 
     * @param taxTab 
     *
     * @return an entry using the given Tax-Table
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableEmployeeVoucherEntry createEmplVchEntryRat(
    		GnuCashAccount acct,
    		BigFraction sglUntPrc,
    		BigFraction qty,
    		GCshTaxTable taxTab)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

}
