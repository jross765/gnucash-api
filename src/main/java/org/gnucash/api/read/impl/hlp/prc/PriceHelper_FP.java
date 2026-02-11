package org.gnucash.api.read.impl.hlp.prc;

import java.text.DateFormat;
import java.util.Currency;
import java.util.Date;

import org.gnucash.api.generated.GncPricedb;
import org.gnucash.api.generated.Price;
import org.gnucash.api.generated.Price.PriceCommodity;
import org.gnucash.api.generated.Price.PriceCurrency;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.impl.hlp.fil.FilePriceManager;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class PriceHelper_FP {

    protected static final Logger LOGGER = LoggerFactory.getLogger(PriceHelper_FP.class);
    
	// ---------------------------------------------------------------

    private static final DateFormat PRICE_QUOTE_DATE_FORMAT = FilePriceManager.PRICE_QUOTE_DATE_FORMAT;

    private static final int RECURS_DEPTH_MAX = FilePriceManager.RECURS_DEPTH_MAX;
    
	// ---------------------------------------------------------------

    public static FixedPointNumber getLatestPrice(
			final GCshCmdtyID cmdtyID,
			final GnuCashFile gcshFile,
			final GncPricedb priceDB) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}
		
		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		if ( gcshFile == null ) {
			throw new IllegalArgumentException("argument <gcshFile> is null");
		}

		if ( priceDB == null ) {
			throw new IllegalArgumentException("argument <priceDB> is null");
		}

		return getLatestPrice(cmdtyID,
							  gcshFile, priceDB, 0);
	}

    public static FixedPointNumber getLatestPrice(
			final Currency curr,
			final GnuCashFile gcshFile,
			final GncPricedb priceDB) {
		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null");
		}
		
		if ( gcshFile == null ) {
			throw new IllegalArgumentException("argument <gcshFile> is null");
		}

		if ( priceDB == null ) {
			throw new IllegalArgumentException("argument <priceDB> is null");
		}

		return getLatestPrice(new GCshCurrID(curr),
							  gcshFile, priceDB, 0);
	}

	// ----------------------------

	private static FixedPointNumber getLatestPrice(
			final GCshCmdtyID cmdtyID,
			final GnuCashFile gcshFile,
			final GncPricedb priceDB,
			final int depth) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}
		
		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		if ( gcshFile == null ) {
			throw new IllegalArgumentException("argument <gcshFile> is null");
		}

		if ( priceDB == null ) {
			throw new IllegalArgumentException("argument <priceDB> is null");
		}

		if ( depth < 0 ) {
			throw new IllegalArgumentException("argument <depth> is < 0");
		}

		Date latestDate = null;
		FixedPointNumber latestQuote = null;
		FixedPointNumber factor = FixedPointNumber.ONE.copy(); // factor is used if the quote is not to our base-currency
		final int maxRecursionDepth = RECURS_DEPTH_MAX;

		for ( Price priceQuote : priceDB.getPrice() ) {
			if ( priceQuote == null ) {
				LOGGER.warn("getLatestPrice: GnuCash file contains null price-quotes - there may be a problem with JWSDP");
				continue;
			}

			PriceCommodity fromCmdty = priceQuote.getPriceCommodity();
			PriceCurrency toCurr = priceQuote.getPriceCurrency();

			if ( fromCmdty == null ) {
				LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without from-security/currency: '" + priceQuote.toString() + "'");
				continue;
			}

			if ( toCurr == null ) {
				LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without to-currency: '" + priceQuote.toString() + "'");
				continue;
			}

			try {
				if ( fromCmdty.getCmdtySpace() == null ) {
					LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with from-security/currency without name space: id='" + priceQuote.getPriceId().getValue() + "'");
					continue;
				}

				if ( fromCmdty.getCmdtyId() == null ) {
					LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with from-security/currency without code: id='" + priceQuote.getPriceId().getValue() + "'");
					continue;
				}

				if ( toCurr.getCmdtySpace() == null ) {
					LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with to-currency without name space: id='" + priceQuote.getPriceId().getValue() + "'");
					continue;
				}

				if ( toCurr.getCmdtyId() == null ) {
					LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with to-currency without code: id='" + priceQuote.getPriceId().getValue() + "'");
					continue;
				}

				if ( priceQuote.getPriceTime() == null ) {
					LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without timestamp id='" + priceQuote.getPriceId().getValue() + "'");
					continue;
				}

				if ( priceQuote.getPriceValue() == null ) {
					LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without value id='" + priceQuote.getPriceId().getValue() + "'");
					continue;
				}

				/*
				 * if (priceQuote.getPriceCommodity().getCmdtySpace().equals("FUND") &&
				 * priceQuote.getPriceType() == null) {
				 * LOGGER.warn("getLatestPrice: GnuCash file contains FUND-price-quotes" +
				 * " with no type id='" + priceQuote.getPriceID().getValue() + "'"); continue; }
				 */

				if ( ! ( fromCmdty.getCmdtySpace().equals( cmdtyID.getNameSpace() ) &&
					     fromCmdty.getCmdtyId().equals( cmdtyID.getCode() ) ) ) {
					continue;
				}

				/*
				 * if (priceQuote.getPriceCommodity().getCmdtySpace().equals("FUND") &&
				 * (priceQuote.getPriceType() == null ||
				 * !priceQuote.getPriceType().equals("last") )) {
				 * LOGGER.warn("getLatestPrice: ignoring FUND-price-quote of unknown type '" +
				 * priceQuote.getPriceType() + "' expecting 'last' "); continue; }
				 */

				// BEGIN core
				if ( ! toCurr.getCmdtySpace().equals(GCshCmdtyNameSpace.CURRENCY) ) {
					// is security
					if ( depth > maxRecursionDepth ) {
						LOGGER.warn("getLatestPrice: Ignoring price-quote that is not an ISO4217 currency: '" + toCurr.toString() + "'");
						continue;
					}
					factor = getLatestPrice(new GCshSecID(toCurr.getCmdtySpace(), toCurr.getCmdtyId()), 
											gcshFile, priceDB, 
											depth + 1);
				} else {
					// is currency
					if ( ! toCurr.getCmdtyId().equals( gcshFile.getDefaultCurrencyID() ) ) {
						if ( depth > maxRecursionDepth ) {
							LOGGER.warn("Ignoring price-quote that is not in default currency " + gcshFile.getDefaultCurrencyID() +
									" but in '" + toCurr.toString() + "'");
							continue;
						}
						factor = getLatestPrice(new GCshCurrID(toCurr.getCmdtyId()), 
												gcshFile, priceDB, 
												depth + 1);
					}
				}
				// END core

				Date date = PRICE_QUOTE_DATE_FORMAT.parse(priceQuote.getPriceTime().getTsDate());

				if ( latestDate == null || latestDate.before(date) ) {
					latestDate = date;
					latestQuote = new FixedPointNumber(priceQuote.getPriceValue());
					LOGGER.debug("getLatestPrice: pCmdtyID='" + cmdtyID.toString() + "' converted " + latestQuote + " <= " + priceQuote.getPriceValue());
				}

			} catch (Exception e) {
				LOGGER.error("getLatestPrice: pCmdtyID='" + cmdtyID.toString() + "'! Ignoring a bad price-quote '" + priceQuote + "'", e);
			}
		} // for priceQuote

		LOGGER.debug("getLatestPrice: pCmdtyID='" + cmdtyID.toString() + "' = " + latestQuote + " from " + latestDate);

		if ( latestQuote == null ) {
			return null;
		}

		if ( factor == null ) {
			factor = FixedPointNumber.ONE.copy();
		}

		return factor.multiply(latestQuote);
	}

}
