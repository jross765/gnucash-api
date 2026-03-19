package org.gnucash.api.read.hlp.fil;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.gnucash.base.basetypes.simple.GCshPrcID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashFile_Prc {
	
	// ::EMPTY

	// ---------------------------------------------------------------

    /**
     * @param prcID id of a price
     * @return the identified price or null
     */
    GnuCashPrice getPriceByID(GCshPrcID prcID);

	// ---
	
	GnuCashPrice getPriceBySecIDDate(GCshSecID secID, LocalDate date);
	
	// ---
	
	GnuCashPrice getPriceByCurrIDDate(GCshCurrID currID, LocalDate date);
	
	GnuCashPrice getPriceByCurrDate(Currency curr, LocalDate date);
	
	// ---
	
    GnuCashPrice getPriceByCmdtyIDDate(GCshCmdtyID cmdtyID, LocalDate date);

    // ---------------------------------------------------------------
    
    /**
     * @return all prices defined in the book
     * @link GCshPrice
     */
    List<GnuCashPrice> getPrices();

    // sic: List, not Collection
	List<GnuCashPrice> getPricesBySecID(GCshSecID cmdtyID);
	
	// ---
	
	List<GnuCashPrice> getPricesByCurrID(GCshCurrID currID);
	
	List<GnuCashPrice> getPricesByCurr(Currency curr);
	
	// ---
	
	List<GnuCashPrice> getPricesByCmdtyID(GCshCmdtyID cmdtyID);
	
    /**
     * @param cmdtyID 
     * @param pCmdtySpace the name space for pCmdtyId
     * @param pCmdtyId    the currency-name
     * @return the latest price-quote in the GnuCash file in EURO
     */
    FixedPointNumber getLatestPrice(GCshCmdtyID cmdtyID);

    BigFraction      getLatestPriceRat(GCshCmdtyID cmdtyID);

    @Deprecated
    FixedPointNumber getLatestPrice(String nameSpace, String code);
    
    @Deprecated
    BigFraction      getLatestPriceRat(String nameSpace, String code);
    
}
