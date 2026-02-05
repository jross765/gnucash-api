package org.gnucash.api.read.hlp;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerJob_Invc_FP {

    /**
     * @return the sum of payments for invoices to this client
     */
    FixedPointNumber getIncomeGenerated();

    // ---------------------------------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    FixedPointNumber getOutstandingValue();

}
