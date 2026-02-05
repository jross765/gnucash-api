package org.gnucash.api.read.hlp.own;

import org.apache.commons.numbers.fraction.BigFraction;

public interface GnuCashGenerJob_Invc_BF {

    /**
     * @return the sum of payments for invoices to this client
     */
    BigFraction getIncomeGeneratedRat();

    // ---------------------------------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    BigFraction getOutstandingValueRat();

}
