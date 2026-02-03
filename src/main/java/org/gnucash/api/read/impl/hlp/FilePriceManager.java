package org.gnucash.api.read.impl.hlp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.Const;
import org.gnucash.api.generated.GncPricedb;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.Price;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashPriceImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.simple.GCshPrcID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class FilePriceManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FilePriceManager.class);
    
    // ---------------------------------------------------------------
    
    public static final DateFormat PRICE_QUOTE_DATE_FORMAT = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

    static final int RECURS_DEPTH_MAX = 5; // ::MAGIC

    // ---------------------------------------------------------------
    
    protected GnuCashFileImpl gcshFile;

    protected GncPricedb                   priceDB = null;
    protected Map<GCshPrcID, GnuCashPrice> prcMap  = null;

    // ---------------------------------------------------------------
    
	public FilePriceManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		prcMap = new HashMap<GCshPrcID, GnuCashPrice>();

		initPriceDB(pRootElement);
		List<Price> prices = priceDB.getPrice();
		for ( Price jwsdpPrc : prices ) {
			GnuCashPriceImpl price = createPrice(jwsdpPrc);
			prcMap.put(price.getID(), price);
		}

		LOGGER.debug("init: No. of entries in Price map: " + prcMap.size());
	}

	private void initPriceDB(final GncV2 pRootElement) {
		List<Object> bookElements = pRootElement.getGncBook().getBookElements();
		for ( Object bookElement : bookElements ) {
			if ( bookElement instanceof GncPricedb ) {
				priceDB = (GncPricedb) bookElement;
				return;
			}
		}
	}

	protected GnuCashPriceImpl createPrice(final Price jwsdpPrc) {
		GnuCashPriceImpl prc = new GnuCashPriceImpl(jwsdpPrc, gcshFile);
		LOGGER.debug("Generated new price: " + prc.getID());
		return prc;
	}

	// ---------------------------------------------------------------

	public GncPricedb getPriceDB() {
		return priceDB;
	}

	public GnuCashPrice getPriceByID(GCshPrcID prcID) {
		if ( prcID == null ) {
			throw new IllegalArgumentException("argument <prcID> is null");
		}
		
		if ( ! prcID.isSet() ) {
			throw new IllegalArgumentException("argument <prcID> is not set");
		}
		
		if ( prcMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashPrice retval = prcMap.get(prcID);
		if ( retval == null ) {
			LOGGER.error("getPriceByID: No Price with ID '" + prcID + "'. " + "We know " + prcMap.size() + " prices.");
		}
		
		return retval;
	}

	// ---------------------------------------------------------------
	
	public GnuCashPrice getPriceByCmdtyIDDate(final GCshSecID cmdtyID, final LocalDate date) {
		return getPriceByCmdtyCurrIDDate(cmdtyID, date);
	}
	
	public GnuCashPrice getPriceByCurrIDDate(final GCshCurrID currID, final LocalDate date) {
		return getPriceByCmdtyCurrIDDate(currID, date);
	}
	
	public GnuCashPrice getPriceByCurrDate(final Currency curr, final LocalDate date) {
		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null");
		}
		
		GCshCurrID currID = new GCshCurrID(curr);
		return getPriceByCmdtyCurrIDDate(currID, date);
	}

	public GnuCashPrice getPriceByCmdtyCurrIDDate(final GCshCmdtyID cmdtyCurrID, final LocalDate date) {
		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
		}
		
		if ( date == null ) {
			throw new IllegalArgumentException("argument <date> is null");
		}
		
		for ( GnuCashPrice prc : getPricesByCmdtyCurrID(cmdtyCurrID) ) {
			if ( prc.getDate().equals(date) ) {
				return prc;
			}
		}
		
		return null;
	}

	// ---------------------------------------------------------------

	public List<GnuCashPrice> getPrices() {
		if ( prcMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		ArrayList<GnuCashPrice> temp = new ArrayList<GnuCashPrice>(prcMap.values());
		Collections.sort(temp);
		
		return Collections.unmodifiableList(temp);
	}
	
	public List<GnuCashPrice> getPricesByCmdtyID(final GCshSecID cmdtyID) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}
		
		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}
		
		return getPricesByCmdtyCurrID(cmdtyID);
	}
	
	public List<GnuCashPrice> getPricesByCmdtyCurrID(final GCshCurrID currID) {
		if ( currID == null ) {
			throw new IllegalArgumentException("argument <currID> is null");
		}
		
		if ( ! currID.isSet() ) {
			throw new IllegalArgumentException("argument <currID> is not set");
		}
		
		return getPricesByCmdtyCurrID(currID);
	}
	
	public List<GnuCashPrice> getPricesByCmdtyCurr(final Currency curr) {
		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null");
		}
		
		GCshCurrID currID = new GCshCurrID(curr);
		return getPricesByCmdtyCurrID(currID);
	}
	
	public List<GnuCashPrice> getPricesByCmdtyCurrID(final GCshCmdtyID cmdtyCurrID) {
		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
		}
		
		List<GnuCashPrice> result = new ArrayList<GnuCashPrice>();

		for ( GnuCashPrice prc : getPrices() ) {
			if ( prc.getFromCmdtyCurrQualifID().toString().equals(cmdtyCurrID.toString()) ) {
				result.add(prc);
			}
		}
		
		Collections.sort(result, Collections.reverseOrder()); // descending, i.e. youngest first
		return Collections.unmodifiableList(result);
	}

	// ---------------------------------------------------------------

	public FixedPointNumber getLatestPrice(final GCshCmdtyID cmdtyCurrID) {
		return PriceHelper_FP.getLatestPrice(cmdtyCurrID, 
											 gcshFile, priceDB);
	}

	public FixedPointNumber getLatestPrice(final Currency curr) {
		return PriceHelper_FP.getLatestPrice(curr, 
											 gcshFile, priceDB);
	}

	@Deprecated
	public FixedPointNumber getLatestPrice(final String pCmdtySpace, final String pCmdtyID) {
		GCshCmdtyID cmdtyCurrID = new GCshCmdtyID(pCmdtySpace, pCmdtyID);
		return PriceHelper_FP.getLatestPrice(cmdtyCurrID, 
											 gcshFile, priceDB);
	}

	// ----------------------------
	
	public BigFraction getLatestPriceRat(final GCshCmdtyID cmdtyCurrID) {
		return PriceHelper_BF.getLatestPrice(cmdtyCurrID,
											 gcshFile, priceDB);
	}

	public BigFraction getLatestPriceRat(final Currency curr) {
		return PriceHelper_BF.getLatestPrice(curr,
											 gcshFile, priceDB);
	}

	@Deprecated
	public BigFraction getLatestPriceRat(final String pCmdtySpace, final String pCmdtyID) {
		GCshCmdtyID cmdtyCurrID = new GCshCmdtyID(pCmdtySpace, pCmdtyID);
		return PriceHelper_BF.getLatestPrice(cmdtyCurrID,
											 gcshFile, priceDB);
	}

	// ---------------------------------------------------------------

	private List<Price> getPrices_raw() {
		List<Price> result = new ArrayList<Price>();

		for ( Price jwsdpPrc : priceDB.getPrice() ) {
			result.add(jwsdpPrc);
		}

		return result;
	}

	protected Price getPrice_raw(final GCshPrcID prcID) {
		for ( Price jwsdpPrc : getPrices_raw() ) {
			if ( jwsdpPrc.getPriceId().getValue().equals(prcID.toString()) ) {
				return jwsdpPrc;
			}
		}
		
		return null;
	}

	// ---------------------------------------------------------------

	public int getNofEntriesPriceMap() {
		return prcMap.size();
	}

}
