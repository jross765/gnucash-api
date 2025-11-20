package org.gnucash.api.currency;

import java.util.List;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface SimplePriceTable {

    FixedPointNumber getConversionFactor(final String code);

    // ::TODO
    // BigFraction getConversionFactorRat(final String code);

    void setConversionFactor(final String code, final FixedPointNumber factor);

    // ::TODO
    // void setConversionFactorRat(final String code, final BigFraction factor);

    // ---------------------------------------------------------------

    boolean convertFromBaseCurrency(FixedPointNumber value, final String code);

    // ::TODO
    // boolean convertFromBaseCurrencyRat(BigFraction value, final String code);
    
    // ---

    boolean convertToBaseCurrency(FixedPointNumber value, final String code);

    // ::TODO
    // boolean convertToBaseCurrencyRat(BigFraction value, final String code);

    // ---------------------------------------------------------------

    List<String> getCurrencies();

    void clear();

}
