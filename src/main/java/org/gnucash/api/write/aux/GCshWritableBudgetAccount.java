package org.gnucash.api.write.aux;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.aux.GCshBudgetAccount;
import org.gnucash.api.read.aux.GCshBudgetPeriod;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.basetypes.simple.GCshAcctID;

public interface GCshWritableBudgetAccount extends GCshBudgetAccount,
                                                   GnuCashWritableObject
{
	
    void setAcctID(GCshAcctID acctID);

	// ---------------------------------------------------------------
    
    void clearPeriods();
    
    void removePeriod(GCshBudgetPeriod bdgtPrd);

    GCshWritableBudgetPeriod createWritablePeriod(BigInteger idx, BigFraction amt);
    
	List<GCshWritableBudgetPeriod> getWritablePeriods();

}
