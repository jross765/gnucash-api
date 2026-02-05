package org.gnucash.api.read.hlp;

import org.apache.commons.numbers.fraction.BigFraction;

public interface GnuCashGenerInvoice_Vend_BF {

    BigFraction getVendBllAmountUnpaidWithTaxesRat();

    BigFraction getVendBllAmountPaidWithTaxesRat();

    BigFraction getVendBllAmountPaidWithoutTaxesRat();

    BigFraction getVendBllAmountWithTaxesRat();

    BigFraction getVendBllAmountWithoutTaxesRat();

}
