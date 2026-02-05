package org.gnucash.api.read.hlp;

import org.apache.commons.numbers.fraction.BigFraction;

public interface GnuCashGenerInvoice_Vend_BF {

    BigFraction getVendBllAmountUnpaidWithTaxes();

    BigFraction getVendBllAmountPaidWithTaxes();

    BigFraction getVendBllAmountPaidWithoutTaxes();

    BigFraction getVendBllAmountWithTaxes();

    BigFraction getVendBllAmountWithoutTaxes();

}
