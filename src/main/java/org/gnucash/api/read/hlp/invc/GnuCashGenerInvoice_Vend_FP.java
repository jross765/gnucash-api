package org.gnucash.api.read.hlp.invc;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Vend_FP {

    FixedPointNumber getVendBllAmountUnpaidWithTaxes();

    FixedPointNumber getVendBllAmountPaidWithTaxes();

    FixedPointNumber getVendBllAmountPaidWithoutTaxes();

    FixedPointNumber getVendBllAmountWithTaxes();

    FixedPointNumber getVendBllAmountWithoutTaxes();

}
