package org.gnucash.api.write.aux;

import java.math.BigInteger;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.aux.GCshBudgetPeriod;
import org.gnucash.api.write.hlp.GnuCashWritableObject;

public interface GCshWritableBudgetPeriod extends GCshBudgetPeriod,
                                                  GnuCashWritableObject
{
	
    void setIndex(BigInteger idx); // sic, not month, just abstract index

    void setAmount(BigFraction amt);

}
