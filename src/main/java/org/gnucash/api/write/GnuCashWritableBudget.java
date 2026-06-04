package org.gnucash.api.write;

import java.util.List;

import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.aux.GCshBudgetAccount;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.api.write.aux.GCshWritableBudgetAccount;
import org.gnucash.api.write.aux.GCshWritableBudgetPeriod;
import org.gnucash.api.write.aux.GCshWritableBudgetRecurrence;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.basetypes.simple.GCshAcctID;

/**
 * Budget that can be modified.
 * 
 * @see GnuCashBudget
 */
public interface GnuCashWritableBudget extends GnuCashBudget,
                                               GnuCashWritableObject,
                                               HasUserDefinedAttributes
{

    void remove() throws ObjectCascadeException;

	// ---------------------------------------------------------------

    void setName(String name);
    
    void setDescription(String descr);
    
    GCshWritableBudgetRecurrence getWritableRecurrence();

    void setRecurrence(GCshWritableBudgetRecurrence recurr);

    // ---------------------------------------------------------------
    
    void clearAccounts();
    
    void removeAccount(GCshBudgetAccount bdgtAcct);
    
    GCshWritableBudgetAccount createWritableAccount(GCshAcctID acct);
    
	List<GCshWritableBudgetAccount> getWritableAccounts();

	GCshWritableBudgetAccount getWritableAccount(GCshAcctID acctID);

    // ---------------------------------------------------------------
    
    void clearPeriods(GCshAcctID acctID);
    
	List<GCshWritableBudgetPeriod> getWritablePeriods(GCshAcctID acctID);

}
