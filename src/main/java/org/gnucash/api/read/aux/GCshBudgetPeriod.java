package org.gnucash.api.read.aux;

import java.math.BigInteger;

import org.apache.commons.numbers.fraction.BigFraction;

public interface GCshBudgetPeriod {
	
	GCshBudgetAccount getParent();

	// ---------------------------------------------------------------

    BigInteger  getIndex(); // sic, not month, just abstract index

    // ----------------------------
    
    BigFraction getAmount();
    
    String      getAmountFormatted();

}
