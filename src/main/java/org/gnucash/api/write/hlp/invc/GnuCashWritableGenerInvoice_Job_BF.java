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
     * @param singleUnitPrice 
     * @param quantity 
     * @return 
     * 
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableJobInvoiceEntry createJobInvcEntryRat(
    		GnuCashAccount acct,
    		BigFraction singleUnitPrice,
    		BigFraction quantity) throws TaxTableNotFoundException,
	    UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

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
     * @throws UnknownInvoiceTypeException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableJobInvoiceEntry createJobInvcEntryRat(
    		GnuCashAccount acct,
    		BigFraction singleUnitPrice,
    		BigFraction quantity,
			String taxTabName) throws  TaxTableNotFoundException, 
    	UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    /**
     * create and add a new entry.<br/>
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @param taxTab 
     *
     * @return an entry using the given Tax-Table
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableJobInvoiceEntry createJobInvcEntryRat(
    		GnuCashAccount acct,
    		BigFraction singleUnitPrice,
    		BigFraction quantity,
    		GCshTaxTable taxTab) throws  TaxTableNotFoundException, 
    	UnknownInvoiceTypeException, IllegalTransactionSplitActionException;
}
