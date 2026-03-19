package org.gnucash.api.read.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.base.basetypes.simple.GCshCustID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashFile_Cust {

    /**
     * @param custID the unique ID of the customer to look for
     * @return the customer or null if it's not found
     */
    GnuCashCustomer getCustomerByID(GCshCustID custID);

    /**
     * warning: this function has to traverse all customers. It is much faster to try
     * getCustomerByID first and to call this method only if the returned account
     * does not have the right name.
     * 
     * @param expr  search expression
     * @return null if not found
     * @see #getCustomerByID(GCshCustID)
     */
    Collection<GnuCashCustomer> getCustomersByName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    Collection<GnuCashCustomer> getCustomersByName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashCustomer getCustomerByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all customers Do not modify the
     *         returned collection!
     */
    Collection<GnuCashCustomer> getCustomers();

}
