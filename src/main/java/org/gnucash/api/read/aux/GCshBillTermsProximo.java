package org.gnucash.api.read.aux;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.hlp.GnuCashObject;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GCshBillTermsProximo extends GnuCashObject {

    Integer getDueDay();

    Integer getDiscountDay();

    @Deprecated
    FixedPointNumber getDiscount();

    BigFraction      getDiscountRat();

}
