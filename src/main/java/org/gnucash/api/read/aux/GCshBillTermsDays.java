package org.gnucash.api.read.aux;

import org.apache.commons.numbers.fraction.BigFraction;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GCshBillTermsDays {

    Integer getDueDays();

    Integer getDiscountDays();

    @Deprecated
    FixedPointNumber getDiscount();

    BigFraction      getDiscountRat();

}
