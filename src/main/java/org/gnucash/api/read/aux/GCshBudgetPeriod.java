package org.gnucash.api.read.aux;

import java.math.BigInteger;
import java.util.Locale;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.hlp.GnuCashObject;

public interface GCshBudgetPeriod extends GnuCashObject {
	
	GCshBudgetAccount getParent();

	// ---------------------------------------------------------------

    BigInteger  getIndex(); // sic, not month, just abstract index

    // ----------------------------
    
    BigFraction getAmount();
    
    String      getAmountFormatted();

    String      getAmountFormatted(Locale lcl);

}
