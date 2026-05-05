package org.gnucash.api.read.hlp.own;

import org.gnucash.api.read.GnuCashGenerInvoice;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

@Deprecated
public interface GnuCashCustomer_Invc_FP {
	
    /**
     * @param readVar 
     * @return Returns the sum of payments for invoices to this customer
     * 
     * @see #getIncomeGenerated_direct()
     * @see #getIncomeGenerated_viaAllJobs()
     *  
     */
    @Deprecated
	FixedPointNumber getIncomeGenerated(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return Returns the sum of payments for invoices to this customer
     *  
     * @see #getIncomeGenerated_viaAllJobs()
     * @see #getIncomeGenerated(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    @Deprecated
	FixedPointNumber getIncomeGenerated_direct();

    /**
     * @return Returns the sum of payments for invoices to this customer
     *  
     * @see #getIncomeGenerated_direct()
     * @see #getIncomeGenerated(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    @Deprecated
	FixedPointNumber getIncomeGenerated_viaAllJobs();

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
