package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.gnucash.base.basetypes.simple.GCshAcctID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashWritableFile_Acct {

    GnuCashWritableAccount getWritableAccountByID(GCshAcctID acctID);

    GnuCashWritableAccount getWritableAccountByNameUniq(String name, boolean qualif)
	    throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @param type the type to look for
     * @return A modifiable version of all accounts of the given type.
     */
    Collection<GnuCashWritableAccount> getWritableAccountsByType(GnuCashAccount.Type type);

    /**
     *
     * @return a read-only collection of all accounts that have no parent
     */
    Collection<? extends GnuCashWritableAccount> getWritableParentlessAccounts();

    /**
     *
     * @return a read-only collection of all accounts
     */
    Collection<? extends GnuCashWritableAccount> getWritableAccounts();

    // ----------------------------

    /**
     * @param type 
     * @param cmdtyID 
     * @param parentID 
     * @param name 
     * @return a new account that is already added to this file as a top-level
     *         account
     */
    GnuCashWritableAccount createWritableAccount(GnuCashAccount.Type type,
			  									 GCshCmdtyID cmdtyID,
			  									 GCshAcctID parentID,
			  									 String name);

    /**
     * @param type 
     * @param cmdtyID 
     * @param parentID 
     * @param name 
     * @return a new account that is already added to this file as a top-level
     *         account
     */
    GnuCashWritableAccount createWritableAccount(GnuCashAccount.Type type, 
    											 GCshSecID cmdtyID,
    											 GCshAcctID parentID,
    											 String name);

    /**
     * @param type 
     * @param currID 
     * @param parentID 
     * @param name 
     * @return a new account that is already added to this file as a top-level
     *         account
     */
    GnuCashWritableAccount createWritableAccount(GnuCashAccount.Type type, 
    											 GCshCurrID currID,
    											 GCshAcctID parentID,
    											 String name);
    /**
     * @param acct the account to remove
     */
    void removeAccount(GnuCashWritableAccount acct);

}
