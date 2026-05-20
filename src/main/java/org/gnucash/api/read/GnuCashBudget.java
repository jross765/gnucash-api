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
    
    int getNofPeriods();
    
    GCshBudgetRecurrence getRecurrence();

    // ---------------------------------------------------------------
    
    List<GCshBudgetAccount> getAccounts();
	
    List<GCshBudgetPeriod>  getPeriods(GCshAcctID acctID);
	
}
