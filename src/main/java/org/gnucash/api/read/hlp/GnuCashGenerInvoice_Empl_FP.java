package org.gnucash.api.read.hlp;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Empl_FP {

    FixedPointNumber getEmplVchAmountUnpaidWithTaxes();

    FixedPointNumber getEmplVchAmountPaidWithTaxes();

    FixedPointNumber getEmplVchAmountPaidWithoutTaxes();

    FixedPointNumber getEmplVchAmountWithTaxes();

    FixedPointNumber getEmplVchAmountWithoutTaxes();

}
