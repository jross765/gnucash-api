package org.gnucash.api.read.hlp;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashGenerInvoice;

public interface GnuCashCustomer_Invc_BF {
	
    /**
     * @param readVar 
     * @return Returns the sum of payments for invoices to this customer
     * 
     * @see #getIncomeGeneratedRat_direct()
     * @see #getIncomeGeneratedRat_viaAllJobs()
     *  
     */
    BigFraction getIncomeGeneratedRat(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return Returns the sum of payments for invoices to this customer
     *  
     * @see #getIncomeGeneratedRat_viaAllJobs()
     * @see #getIncomeGeneratedRat(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    BigFraction getIncomeGeneratedRat_direct();

    /**
     * @return Returns the sum of payments for invoices to this customer
     *  
     * @see #getIncomeGeneratedRat_direct()
     * @see #getIncomeGeneratedRat(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    BigFraction getIncomeGeneratedRat_viaAllJobs();

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
