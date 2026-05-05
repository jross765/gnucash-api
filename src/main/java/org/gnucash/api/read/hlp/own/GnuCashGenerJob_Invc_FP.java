package org.gnucash.api.read.hlp.own;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

@Deprecated
public interface GnuCashGenerJob_Invc_FP {

    /**
     * @return the sum of payments for invoices to this client
     */
    @Deprecated
	FixedPointNumber getIncomeGenerated();

    // ---------------------------------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    @Deprecated
	FixedPointNumber getOutstandingValue();

}
