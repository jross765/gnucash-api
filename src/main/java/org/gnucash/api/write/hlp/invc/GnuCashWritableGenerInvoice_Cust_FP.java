package org.gnucash.api.write.hlp.invc;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoiceEntry;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoice_Cust_FP {

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
    GnuCashWritableCustomerInvoiceEntry createCustInvcEntry(
    		GnuCashAccount acct,
    		FixedPointNumber sglUntPrc, 
    		FixedPointNumber qty)
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
    GnuCashWritableCustomerInvoiceEntry createCustInvcEntry(
    		GnuCashAccount acct,
    		FixedPointNumber sglUntPrc, 
    		FixedPointNumber qty, 
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
    GnuCashWritableCustomerInvoiceEntry createCustInvcEntry(
    		GnuCashAccount acct,
    		FixedPointNumber sglUntPrc,
    		FixedPointNumber qty,
    		GCshTaxTable taxTab)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException;
}
