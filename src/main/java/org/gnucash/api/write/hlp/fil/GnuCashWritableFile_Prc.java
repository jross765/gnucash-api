package org.gnucash.api.write.hlp.fil;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;

import org.gnucash.api.write.GnuCashWritablePrice;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.gnucash.base.basetypes.simple.GCshPrcID;

public interface GnuCashWritableFile_Prc {

    GnuCashWritablePrice getWritablePriceByID(GCshPrcID prcID);

    GnuCashWritablePrice getWritablePriceBySecIDDate(GCshSecID cmdtyID, LocalDate date);
	
    GnuCashWritablePrice getWritablePriceByCurrIDDate(GCshCurrID currID, LocalDate date);
	
    GnuCashWritablePrice getWritablePriceByCurrDate(Currency curr, LocalDate date);
	
    GnuCashWritablePrice getWritablePriceByCmdtyIDDate(GCshCmdtyID cmdtyID, LocalDate date);
    
    Collection<GnuCashWritablePrice> getWritablePrices();

    // ----------------------------

    /**
     * @param fromCmdtyID 
     * @param toCurrID 
     * @param date 
     * @return a new price object with no values that is already added to this file
     */
    GnuCashWritablePrice createWritablePrice(GCshCmdtyID fromCmdtyID, GCshCurrID toCurrID,
											 LocalDate date);

    /**
     * @param prc the price to remove
     */
    void removePrice(GnuCashWritablePrice prc);

}
