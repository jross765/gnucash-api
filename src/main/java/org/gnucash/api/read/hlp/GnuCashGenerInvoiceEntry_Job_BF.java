package org.gnucash.api.read.hlp;

import org.apache.commons.numbers.fraction.BigFraction;

public interface GnuCashGenerInvoiceEntry_Job_BF {

    BigFraction getJobInvcPriceRat();

    // ---------------------------------------------------------------

    BigFraction getJobInvcApplicableTaxPercentRat();

    // ---------------------------------------------------------------

    /*
     * This is the vendor bill sum as entered by the user. The user can decide to
     * include or exclude taxes.
     */
    BigFraction getJobInvcSumRat();

    BigFraction getJobInvcSumInclTaxesRat();

    BigFraction getJobInvcSumExclTaxesRat();

}
