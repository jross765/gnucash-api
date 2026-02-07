package org.gnucash.api.write;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.gnucash.base.basetypes.complex.GCshCurrID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Price that can be modified.
 * 
 * @see GnuCashPrice
 */
public interface GnuCashWritablePrice extends GnuCashPrice, 
                                              GnuCashWritableObject
{

	/**
	 * 
	 * @param qualifID
	 * 
	 * @see #getFromCmdtyID()
	 */
    void setFromCmdtyID(GCshCmdtyID qualifID);

    /**
     * 
     * @param qualifID
     * 
	 * @see #getFromCmdtyID()
     */
    void setFromSecfID(GCshSecID qualifID);

    /**
     * 
     * @param qualifID
     * 
	 * @see #getFromCmdtyID()
     */
    void setFromCurrID(GCshCurrID qualifID);

    /**
     * 
     * @param cmdty
     * 
     * @see #getFromCommodity()
     */
    void setFromCommodity(GnuCashCommodity cmdty);

    /**
     * 
     * @param code
     * 
     * @see #getFromCurrencyCode()
     */
    void setFromCurrencyCode(String code);

    /**
     * 
     * @param curr
     * 
     * @see #getFromCurrency()
     */
    void setFromCurrency(GnuCashCommodity curr);
    
    // ----------------------------

    /**
     * 
     * @param qualifID
     * 
     * @see #getToCurrID()
     */
    void setToCmdtyID(GCshCmdtyID qualifID);

    /**
     * 
     * @param qualifID
     * 
     * @see #getToCurrID()
     */
    void setToCurrID(GCshCurrID qualifID);

    /**
     * 
     * @param code
     * 
     * @see #getToCurrID()
     */
    void setToCurrencyCode(String code);

    /**
     * 
     * @param curr
     * 
     * @see #getToCurrID()
     */
    void setToCurrency(GnuCashCommodity curr);

    // ----------------------------

    /**
     * 
     * @param date
     * 
     * @see #getDate()
     */
    void setDate(LocalDate date);

    /**
     * 
     * @param dateTime
     * 
     * @see #getDateTime()
     */
    void setDateTime(LocalDateTime dateTime);

    /**
     * 
     * @param src
     * 
     * @see #getSource()
     */
    void setSource(Source src);

    /**
     * 
     * @param type
     * 
     * @see #getType()
     */
    void setType(Type type);

    /**
     * 
     * @param val
     * 
     * @see #getValue()
     */
    void setValue(FixedPointNumber val);

    void setValue(BigFraction val);

}
