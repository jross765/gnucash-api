package org.gnucash.api.read.hlp.own;

import org.gnucash.api.read.GnuCashGenerInvoice;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashVendor_Invc_FP {
    
    /**
     * @param readVar 
     * @return the sum of payments for invoices to this vendor
     */
    FixedPointNumber getExpensesGenerated(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return the sum of payments for invoices to this vendor
     */
    FixedPointNumber getExpensesGenerated_direct();

    /**
     * @return the sum of payments for invoices to this vendor
     */
    FixedPointNumber getExpensesGenerated_viaAllJobs();

    // -------------------------------------

    /**
     * @param readVar 
     * @return the sum of left to pay Unpaid invoiced
     */
    FixedPointNumber getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return the sum of left to pay Unpaid invoiced
     *  
     */
    FixedPointNumber getOutstandingValue_direct();

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    FixedPointNumber getOutstandingValue_viaAllJobs();

}
