package org.gnucash.api.read.hlp;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Cust_FP {

    FixedPointNumber getCustInvcAmountUnpaidWithTaxes();

    FixedPointNumber getCustInvcAmountPaidWithTaxes();

    FixedPointNumber getCustInvcAmountPaidWithoutTaxes();

    FixedPointNumber getCustInvcAmountWithTaxes();

    FixedPointNumber getCustInvcAmountWithoutTaxes();

}
