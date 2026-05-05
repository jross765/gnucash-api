package org.gnucash.api.read.hlp.own;

import org.gnucash.api.read.GnuCashGenerInvoice;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

@Deprecated
public interface GnuCashVendor_Invc_FP {
    
    /**
     * @param readVar 
     * @return the sum of payments for invoices to this vendor
     */
    @Deprecated
	FixedPointNumber getExpensesGenerated(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return the sum of payments for invoices to this vendor
     */
    @Deprecated
	FixedPointNumber getExpensesGenerated_direct();

    /**
     * @return the sum of payments for invoices to this vendor
     */
    @Deprecated
	FixedPointNumber getExpensesGenerated_viaAllJobs();

    // -------------------------------------

    /**
     * @param readVar 
     * @return the sum of left to pay Unpaid invoiced
     */
    @Deprecated
	FixedPointNumber getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return the sum of left to pay Unpaid invoiced
     *  
     */
    @Deprecated
	FixedPointNumber getOutstandingValue_direct();

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    @Deprecated
	FixedPointNumber getOutstandingValue_viaAllJobs();

}
