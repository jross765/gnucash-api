package org.gnucash.api.write.aux;

import java.time.LocalDate;

import org.gnucash.api.read.aux.GCshBudgetRecurrence;
import org.gnucash.api.write.hlp.GnuCashWritableObject;

public interface GCshWritableBudgetRecurrence extends GCshBudgetRecurrence,
                                                      GnuCashWritableObject
{
	
    void setMult(int mult);
    
    void setPeriodType(PeriodType type);
    
    void setStart(LocalDate date);
    
}
