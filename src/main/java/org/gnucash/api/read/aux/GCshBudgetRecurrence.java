package org.gnucash.api.read.aux;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashBudget;

public interface GCshBudgetRecurrence {
	
	public enum PeriodType {
		DAY,
		WEEK,
		MONTH,
		YEAR
	}
	
	// ---------------------------------------------------------------

	GnuCashBudget getParent();

	// ---------------------------------------------------------------

    int        getMult();
    
    PeriodType getPeriodType();
    
    String     getPeriodTypeStr();
    
    LocalDate  getStart();
    
    String     getStartStr();
    
}
