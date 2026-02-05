package org.gnucash.api.read.hlp.own;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashEmployee_Invc_FP {
    
    /**
     * @return the sum of payments for invoices to this client
     */
    FixedPointNumber getExpensesGenerated();

    /**
     * @return the sum of payments for invoices to this client
     */
    FixedPointNumber getExpensesGenerated_direct();

    // -------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    FixedPointNumber getOutstandingValue();

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    FixedPointNumber getOutstandingValue_direct();

}
