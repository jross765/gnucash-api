package org.gnucash.api.read.hlp.own;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

@Deprecated
public interface GnuCashEmployee_Invc_FP {
    
    /**
     * @return the sum of payments for invoices to this client
     */
    @Deprecated
	FixedPointNumber getExpensesGenerated();

    /**
     * @return the sum of payments for invoices to this client
     */
    @Deprecated
	FixedPointNumber getExpensesGenerated_direct();

    // -------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    @Deprecated
	FixedPointNumber getOutstandingValue();

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    @Deprecated
	FixedPointNumber getOutstandingValue_direct();

}
