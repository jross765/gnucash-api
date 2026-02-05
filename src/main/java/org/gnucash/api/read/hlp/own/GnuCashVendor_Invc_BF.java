package org.gnucash.api.read.hlp.own;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashGenerInvoice;

public interface GnuCashVendor_Invc_BF {
    
    /**
     * @param readVar 
     * @return the sum of payments for invoices to this vendor
     */
    BigFraction getExpensesGeneratedRat(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return the sum of payments for invoices to this vendor
     */
    BigFraction getExpensesGeneratedRat_direct();

    /**
     * @return the sum of payments for invoices to this vendor
     */
    BigFraction getExpensesGeneratedRat_viaAllJobs();

    // -------------------------------------

    /**
     * @param readVar 
     * @return the sum of left to pay Unpaid invoiced
     */
    BigFraction getOutstandingValueRat(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return the sum of left to pay Unpaid invoiced
     *  
     */
    BigFraction getOutstandingValueRat_direct();

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    BigFraction getOutstandingValueRat_viaAllJobs();

}
