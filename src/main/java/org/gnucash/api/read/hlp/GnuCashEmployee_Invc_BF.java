package org.gnucash.api.read.hlp;

import org.apache.commons.numbers.fraction.BigFraction;

public interface GnuCashEmployee_Invc_BF {
    
    /**
     * @return the sum of payments for invoices to this client
     */
    BigFraction getExpensesGeneratedRat();

    /**
     * @return the sum of payments for invoices to this client
     */
    BigFraction getExpensesGeneratedRat_direct();

    // -------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    BigFraction getOutstandingValueRat();

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    BigFraction getOutstandingValueRat_direct();

}
