package org.gnucash.api.read.hlp.invc;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Job_FP {

    FixedPointNumber getJobInvcPrice();

    // ---------------------------------------------------------------

    FixedPointNumber getJobInvcApplicableTaxPercent();

    // ---------------------------------------------------------------

    /*
     * This is the vendor bill sum as entered by the user. The user can decide to
     * include or exclude taxes.
     */
    FixedPointNumber getJobInvcSum();

    FixedPointNumber getJobInvcSumInclTaxes();

    FixedPointNumber getJobInvcSumExclTaxes();

}
