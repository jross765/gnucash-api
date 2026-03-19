package org.gnucash.api.read.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.base.basetypes.simple.GCshEmplID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashFile_Empl {

    /**
     * @param emplID the unique ID of the employee to look for
     * @return the employee or null if it's not found
     */
    GnuCashEmployee getEmployeeByID(GCshEmplID emplID);

    /**
     * warning: this function has to traverse all employees. It is much faster to
     * getEmployeeByID first and to call this method only if the returned account
     * does not have the right name.
     * 
     * @param expr  search expression
     * @return null if not found
     * @see #getEmployeeByID(GCshEmplID)
     */
    Collection<GnuCashEmployee> getEmployeesByUserName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    Collection<GnuCashEmployee> getEmployeesByUserName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashEmployee getEmployeeByUserNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;


    /**
     * @return a (possibly read-only) collection of all employees Do not modify the
     *         returned collection!
     */
    Collection<GnuCashEmployee> getEmployees();

}
