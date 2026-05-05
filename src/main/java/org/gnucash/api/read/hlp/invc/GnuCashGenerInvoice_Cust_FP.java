package org.gnucash.api.read.hlp.invc;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

@Deprecated
public interface GnuCashGenerInvoice_Cust_FP {

    FixedPointNumber getCustInvcAmountUnpaidWithTaxes();

    FixedPointNumber getCustInvcAmountPaidWithTaxes();

    FixedPointNumber getCustInvcAmountPaidWithoutTaxes();

    FixedPointNumber getCustInvcAmountWithTaxes();

    FixedPointNumber getCustInvcAmountWithoutTaxes();

}
