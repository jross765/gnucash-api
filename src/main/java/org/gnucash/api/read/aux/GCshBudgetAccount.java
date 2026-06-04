package org.gnucash.api.read.aux;

import java.util.List;

import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.base.basetypes.simple.GCshAcctID;

public interface GCshBudgetAccount extends GnuCashObject {
	
	GnuCashBudget getParent();

	// ---------------------------------------------------------------

	GCshAcctID getAcctID();
    
	// ---------------------------------------------------------------
    
	boolean hasPeriods();

	List<GCshBudgetPeriod> getPeriods();

}
