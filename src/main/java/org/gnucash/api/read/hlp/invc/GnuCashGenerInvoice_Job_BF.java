package org.gnucash.api.read.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;

public interface GnuCashGenerInvoice_Job_BF {
    
    BigFraction getJobInvcAmountUnpaidWithTaxesRat();

    BigFraction getJobInvcAmountPaidWithTaxesRat();

    BigFraction getJobInvcAmountPaidWithoutTaxesRat();

    BigFraction getJobInvcAmountWithTaxesRat();

    BigFraction getJobInvcAmountWithoutTaxesRat();

}
