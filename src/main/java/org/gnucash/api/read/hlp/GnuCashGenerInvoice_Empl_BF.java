package org.gnucash.api.read.hlp;

import org.apache.commons.numbers.fraction.BigFraction;

public interface GnuCashGenerInvoice_Empl_BF {

    BigFraction getEmplVchAmountUnpaidWithTaxesRat();

    BigFraction getEmplVchAmountPaidWithTaxesRat();

    BigFraction getEmplVchAmountPaidWithoutTaxesRat();

    BigFraction getEmplVchAmountWithTaxesRat();

    BigFraction getEmplVchAmountWithoutTaxesRat();

}
