package org.gnucash.api.write.hlp.fil;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

import org.gnucash.api.write.GnuCashWritablePrice;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.gnucash.base.basetypes.simple.GCshPrcID;

public interface GnuCashWritableFile_Prc {

	/**
	 * @param prcID 
	 * @param prcPrID 
	 * @return A modifiable version of the transaction.
	 */
    GnuCashWritablePrice getWritablePriceByID(GCshPrcID prcID);

    GnuCashWritablePrice getWritablePriceBySecIDDate(GCshSecID secID, LocalDate date);
	
    GnuCashWritablePrice getWritablePriceByCurrIDDate(GCshCurrID currID, LocalDate date);
	
    GnuCashWritablePrice getWritablePriceByCurrDate(Currency curr, LocalDate date);
	
    GnuCashWritablePrice getWritablePriceByCmdtyIDDate(GCshCmdtyID cmdtyID, LocalDate date);
    
	// ----------------------------

	/**
	 * @return writable versions of all prices in the book.
	 * 
	 * @see #getPrices()
	 */
	List<GnuCashWritablePrice> getWritablePrices();

	List<GnuCashWritablePrice> getWritablePricesBySecID(GCshSecID secID);
	
	// ---
	
	List<GnuCashWritablePrice> getWritablePricesByCurrID(GCshCurrID currID);

	List<GnuCashWritablePrice> getWritablePricesByCurr(Currency curr);
	
	// ---
	
	List<GnuCashWritablePrice> getWritablePricesByCmdtyID(GCshCmdtyID cmdtyID);
	
	// ----------------------------

	/**
	 * @param fromCmdtyID 
	 * @param toCurrID 
	 * @param date 
	 * @return a new price object with no splits that is already added to this file
	 */
	GnuCashWritablePrice createWritablePrice(GCshCmdtyID fromCmdtyID, GCshCurrID toCurrID, LocalDate date);

	/**
	 *
	 * @param prc 
	 * @param sec the transaction to remove.
	 */
	void removePrice(GnuCashWritablePrice prc);

}
