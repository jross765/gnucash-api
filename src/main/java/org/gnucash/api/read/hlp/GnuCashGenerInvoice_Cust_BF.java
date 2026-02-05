package org.gnucash.api.read.hlp;

import org.apache.commons.numbers.fraction.BigFraction;

public interface GnuCashGenerInvoice_Cust_BF {

    BigFraction getCustInvcAmountUnpaidWithTaxesRat();

    BigFraction getCustInvcAmountPaidWithTaxesRat();

    BigFraction getCustInvcAmountPaidWithoutTaxesRat();

    BigFraction getCustInvcAmountWithTaxesRat();

    BigFraction getCustInvcAmountWithoutTaxesRat();

}
