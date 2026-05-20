package org.gnucash.api.read.aux;

import java.util.List;

import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.base.basetypes.simple.GCshAcctID;

public interface GCshBudgetAccount {
	
	GnuCashBudget getParent();

	// ---------------------------------------------------------------

    GCshAcctID getAcctID();
    
	// ---------------------------------------------------------------

    List<GCshBudgetPeriod> getPeriods();

}
