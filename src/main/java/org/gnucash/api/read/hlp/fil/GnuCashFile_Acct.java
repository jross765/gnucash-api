package org.gnucash.api.read.hlp.fil;

import java.util.List;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashAccount.Type;
import org.gnucash.base.basetypes.simple.GCshAcctID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashFile_Acct {

    /**
     * @param acctID the unique ID of the account to look for
     * @return the account or null if it's not found
     */
    GnuCashAccount getAccountByID(GCshAcctID acctID);

    /**
     *
     * @param prntAcctID if null, gives all account that have no parent
     * @return all accounts with that parent in no particular order
     */
    List<GnuCashAccount> getAccountsByParentID(GCshAcctID prntAcctID);

    /**
     * warning: this function has to traverse all accounts. If it much faster to try
     * getAccountByID() first and call this method only if the returned account does
     * not have the right name.
     * 
     * @param expr search expression
     *
     * @param name the <strong>unqualified</strong> name to look for
     * @return null if not found
     * @see #getAccountByID(GCshAcctID)
 	 * @see #getAccountsByParentID(GCshAcctID)
	 * @see #getAccountsByName(String, boolean, boolean)
    */
    List<GnuCashAccount> getAccountsByName(String expr);

    /**
	 * @param expr search expression
	 * @param qualif Whether to search for qualified names of unqualified ones
	 * @param relaxed Whether to ignore upper/lower-case letters or not (true: case-insensitive)
	 * @return the qualified or unqualified name to look for, depending on parameter qualif.
     */
    List<GnuCashAccount> getAccountsByName(String expr, boolean qualif, boolean relaxed);

    /**
	 * @param expr search expression
	 * @param qualif
	 * @return read-only account object whose name uniquely matches the expression
	 * @throws NoEntryFoundException
	 * @throws TooManyEntriesFoundException
     */
    GnuCashAccount getAccountByNameUniq(String expr, boolean qualif) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * warning: this function has to traverse all accounts. If it much faster to try
     * getAccountByID() first and call this method only if the returned account does
     * not have the right name.
     *
     * @param name the regular expression of the name to look for
     * @return null if not found
     * @throws NoEntryFoundException 
     * @throws TooManyEntriesFoundException 
     * @see #getAccountByID(GCshAcctID)
     * @see #getAccountsByName(String)
     */
    GnuCashAccount getAccountByNameEx(String name) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param acctID   the ID to look for
     * @param name the name to look for if nothing is found for the ID
     * @return null if not found
     * @throws NoEntryFoundException 
     * @throws TooManyEntriesFoundException 
     * @see #getAccountByID(GCshAcctID)
     * @see #getAccountsByName(String)
     */
    GnuCashAccount getAccountByIDorName(GCshAcctID acctID, String name) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param acctID   the id to look for
     * @param name the regular expression of the name to look for if nothing is
     *             found for the id
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountByID(GCshAcctID)
     * @see #getAccountsByName(String)
     */
    GnuCashAccount getAccountByIDorNameEx(GCshAcctID acctID, String name) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
	 * @param type
     * @return list of read-only account objects of the given type
     */
    List<GnuCashAccount> getAccountsByType(Type type);
    
    /**
     * @param type
     * @param acctName account name
     * @param qualif
     * @param relaxed
     * @return list of read-only account objects of the given type and
     *   matching the other parameters for the name. 
     */
    List<GnuCashAccount> getAccountsByTypeAndName(Type type, String acctName, 
		                                          boolean qualif, boolean relaxed);

    /**
     * @return all accounts
     */
    List<GnuCashAccount> getAccounts();

    /**
     * @return ID of the root account
     * 
     * @see #getRootAccount()
     */
    GCshAcctID getRootAccountID();

    /**
     * @return
     * 
     * @see #getRootAccountID()
     */
    GnuCashAccount getRootAccount();

    /**
     * @return a read-only collection of all accounts that have no parent (the
     *         result is sorted)
     */
    List<? extends GnuCashAccount> getParentlessAccounts();

    /**
     * @return collection of the IDs of all top-level accounts (i.e., 
     * one level under root) 
     */
    List<GCshAcctID> getTopAccountIDs();

    /**
     * @return collection of all top-level accounts (ro-objects) (i.e., 
     * one level under root)
     */
    List<GnuCashAccount> getTopAccounts();

}
