package org.gnucash.api.currency;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface SimplePriceTable {

    /**
     * @param code
     * @return conversion factor from currency specified by
     *         code to base currency
     */
    FixedPointNumber getConversionFactor(String code);

    /**
     * @param code
     * @return
     */
    BigFraction      getConversionFactorRat(String code);

    /**
     * @param code
     * @param factor
     */
    void setConversionFactor(String code, FixedPointNumber factor);

    /**
     * @param code
     * @param factor
     */
    void setConversionFactorRat(String code, BigFraction factor);

    // ---------------------------------------------------------------

    /**
     * @param value
     * @param code
     * @return
     */
    FixedPointNumber convertFromBaseCurrency(FixedPointNumber value, String code);

    /**
     * @param value
     * @param code
     * @return
     */
    BigFraction      convertFromBaseCurrencyRat(BigFraction value, String code);
    
    // ---

    /**
     * @param value
     * @param code
     * @return
     */
    FixedPointNumber convertToBaseCurrency(FixedPointNumber value, String code);

    /**
     * @param value
     * @param code
     * @return
     */
    BigFraction      convertToBaseCurrencyRat(BigFraction value, String code);

    // ---------------------------------------------------------------

    List<String> getCodes();

    void clear();

}
