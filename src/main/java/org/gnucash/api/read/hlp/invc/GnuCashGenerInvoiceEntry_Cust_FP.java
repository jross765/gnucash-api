package org.gnucash.api.read.hlp.invc;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Cust_FP {

    FixedPointNumber getCustInvcPrice();

    // ---------------------------------------------------------------
    
    FixedPointNumber getCustInvcApplicableTaxPercent();

    // ---------------------------------------------------------------

    /*
     * This is the customer invoice sum as entered by the user. The user can decide
     * to include or exclude taxes.
     */
    FixedPointNumber getCustInvcSum();

    FixedPointNumber getCustInvcSumInclTaxes();

    FixedPointNumber getCustInvcSumExclTaxes();

}
