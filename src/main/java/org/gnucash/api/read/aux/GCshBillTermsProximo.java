package org.gnucash.api.read.aux;

import org.apache.commons.numbers.fraction.BigFraction;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GCshBillTermsProximo {

    Integer getDueDay();

    Integer getDiscountDay();

    @Deprecated
    FixedPointNumber getDiscount();

    BigFraction      getDiscountRat();

}
