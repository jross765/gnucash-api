package org.gnucash.api.write.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoiceEntry;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

public interface GnuCashWritableGenerInvoice_Job_BF {

    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * @param acct 
     * @param sglUntPrc 
     * @param qty 
     * @return 
     * 
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableJobInvoiceEntry createJobInvcEntryRat(
    		GnuCashAccount acct,
    		BigFraction sglUntPrc,
    		BigFraction qty) throws TaxTableNotFoundException,
	    UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

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
     * @throws UnknownInvoiceTypeException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableJobInvoiceEntry createJobInvcEntryRat(
    		GnuCashAccount acct,
    		BigFraction sglUntPrc,
    		BigFraction qty,
			String taxTabName) throws  TaxTableNotFoundException, 
    	UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    /**
     * create and add a new entry.<br/>
     * @param acct 
     * @param sglUntPrc 
     * @param qty 
     * @param taxTab 
     *
     * @return an entry using the given Tax-Table
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableJobInvoiceEntry createJobInvcEntryRat(
    		GnuCashAccount acct,
    		BigFraction sglUntPrc,
    		BigFraction qty,
    		GCshTaxTable taxTab) throws  TaxTableNotFoundException, 
    	UnknownInvoiceTypeException, IllegalTransactionSplitActionException;
}
