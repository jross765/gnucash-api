package org.gnucash.api.read.aux;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.hlp.GnuCashObject;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GCshBillTermsDays extends GnuCashObject {

    Integer getDueDays();

    Integer getDiscountDays();

    @Deprecated
    FixedPointNumber getDiscount();

    BigFraction      getDiscountRat();

}
