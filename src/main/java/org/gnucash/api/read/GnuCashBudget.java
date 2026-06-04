package org.gnucash.api.read;

import java.util.List;

import org.gnucash.api.read.aux.GCshBudgetAccount;
import org.gnucash.api.read.aux.GCshBudgetPeriod;
import org.gnucash.api.read.aux.GCshBudgetRecurrence;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshBdgtID;

public interface GnuCashBudget extends GnuCashObject,
                                       HasUserDefinedAttributes
{

    GCshBdgtID getID();
    
    String getName();
    
    String getDescription();
    
    /**
     * @return the <b>maximum</b> number of periods for <b>all</b> accounts.
     * <p>
     * If you want the <b>actual</b> number of periods for a <b>specific</b> account,
     * use getPeriods().size().
     */
    int getNofPeriods(); 
    
    GCshBudgetRecurrence getRecurrence();

    // ---------------------------------------------------------------
    
    boolean hasAccounts();
    
    List<GCshBudgetAccount> getAccounts();
	
    GCshBudgetAccount getAccount(GCshAcctID acctID);
	
    // ---------------------------------------------------------------
    
    boolean hasPeriods(GCshAcctID acctID);
    
    List<GCshBudgetPeriod>  getPeriods(GCshAcctID acctID);
	
}
