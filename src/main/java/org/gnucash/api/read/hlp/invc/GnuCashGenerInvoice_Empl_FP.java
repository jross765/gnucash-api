package org.gnucash.api.read.hlp.invc;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

@Deprecated
public interface GnuCashGenerInvoice_Empl_FP {

    FixedPointNumber getEmplVchAmountUnpaidWithTaxes();

    FixedPointNumber getEmplVchAmountPaidWithTaxes();

    FixedPointNumber getEmplVchAmountPaidWithoutTaxes();

    FixedPointNumber getEmplVchAmountWithTaxes();

    FixedPointNumber getEmplVchAmountWithoutTaxes();

}
