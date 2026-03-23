package org.gnucash.api.read.impl;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.Const;
import org.gnucash.api.generated.Price;
import org.gnucash.api.generated.Price.PriceCommodity;
import org.gnucash.api.generated.Price.PriceCurrency;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyTypeException;
import org.gnucash.base.basetypes.simple.GCshPrcID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GnuCashPriceImpl extends GnuCashObjectImpl
                              implements GnuCashPrice
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashPriceImpl.class);

	protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
	protected static final DateTimeFormatter DATE_FORMAT_FALLBACK = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	// -----------------------------------------------------------

	/**
	 * The JWSDP-object we are wrapping.
	 */
	protected final Price jwsdpPeer;

	protected ZonedDateTime dateTime;
	protected NumberFormat currencyFormat = null;

	// -----------------------------------------------------------

	/**
	 * @param newPeer the JWSDP-object we are wrapping.
	 * @param gcshFile 
	 */
	@SuppressWarnings("exports")
	public GnuCashPriceImpl(final Price newPeer, final GnuCashFile gcshFile) {
		super(gcshFile);

		this.jwsdpPeer = newPeer;
	}

	// ---------------------------------------------------------------

	/**
	 * @return the JWSDP-object we are wrapping.
	 */
	@SuppressWarnings("exports")
	public Price getJwsdpPeer() {
		return jwsdpPeer;
	}

	// -----------------------------------------------------------

	@Override
	public GCshPrcID getID() {
		if ( jwsdpPeer.getPriceId() == null )
			return null;

		return new GCshPrcID(jwsdpPeer.getPriceId().getValue());
	}

	// ----------------------------

	@Override
	public GCshCmdtyID getFromCmdtyID() {
		if ( jwsdpPeer.getPriceCommodity() == null )
			return null;

		PriceCommodity cmdty = jwsdpPeer.getPriceCommodity();
		if ( cmdty.getCmdtySpace() == null || cmdty.getCmdtyId() == null )
			return null;

		GCshCmdtyID result = new GCshCmdtyID(cmdty.getCmdtySpace(), cmdty.getCmdtyId());

		return result;
	}

	@Override
	public GCshSecID getFromSecID() {
		GCshCmdtyID cmdtyID = getFromCmdtyID();
		return new GCshSecID(cmdtyID);
	}

	@Override
	public GCshCurrID getFromCurrID() {
		GCshCmdtyID cmdtyID = getFromCmdtyID();
		return new GCshCurrID(cmdtyID);
	}

	@Override
	public GnuCashCommodity getFromCommodity() {
		GCshSecID cmdtyID = getFromSecID();
		GnuCashCommodity cmdty = getGnuCashFile().getCommodityByID(cmdtyID);
		return cmdty;
	}

	@Override
	public String getFromCurrencyCode() {
		return getFromCurrID().getCurrency().getCurrencyCode();
	}

	@Override
	public GnuCashCommodity getFromCurrency() {
		GCshCurrID currID = getFromCurrID();
		GnuCashCommodity cmdty = getGnuCashFile().getCommodityByID(currID);
		return cmdty;
	}

	// ----------------------------

	@Override
	public GCshCurrID getToCurrID() {
		if ( jwsdpPeer.getPriceCurrency() == null )
			return null;

		PriceCurrency curr = jwsdpPeer.getPriceCurrency();
		if ( curr.getCmdtySpace() == null || curr.getCmdtyId() == null )
			return null;

		GCshCurrID result = new GCshCurrID(curr.getCmdtySpace(), curr.getCmdtyId());

		return result;
	}

//	@Override
//	public GnuCashCommodity getToCurrency() {
//		if ( getToCurrID() == null )
//			return null;
//
//		GnuCashCommodity cmdty = getGnuCashFile().getCommodityByID(getToCurrID());
//
//		return cmdty;
//	}

	@Override
	public Currency getToCurrency() {
		if ( getToCurrID().getType() != GCshCmdtyID.Type.CURRENCY ) {
			throw new IllegalStateException("Price security/currency is not of type " + GCshCmdtyID.Type.CURRENCY);
		}

		String gcshCurrID = getToCurrID().getCode();
		return Currency.getInstance(gcshCurrID);
	}

	@Override
	public String getToCurrencyCode() {
		if ( jwsdpPeer.getPriceCurrency() == null )
			return null;

		PriceCurrency curr = jwsdpPeer.getPriceCurrency();
		if ( curr.getCmdtySpace() == null || curr.getCmdtyId() == null )
			return null;

		if ( !curr.getCmdtySpace().equals(GCshCmdtyNameSpace.CURRENCY) )
			throw new InvalidCmdtyTypeException();

		return curr.getCmdtyId();
	}

	// ----------------------------

	/**
	 * @return The currency-format to use for formating.
	 */
	private NumberFormat getCurrencyFormat() {
		if ( currencyFormat == null ) {
			currencyFormat = NumberFormat.getCurrencyInstance();
		}

//	// the currency may have changed
//	if ( ! getCurrencyQualifID().getType().equals(CmdtyID.Type.CURRENCY) )
//	    throw new InvalidCmdtyTypeException();

		Currency curr = Currency.getInstance(getToCurrencyCode());
		currencyFormat.setCurrency(curr);

		return currencyFormat;
	}

	@Override
	public LocalDate getDate() {
		if ( jwsdpPeer.getPriceTime() == null )
			return null;

		String dateStr = jwsdpPeer.getPriceTime().getTsDate();
		try {
			return getZonedDateTime().toLocalDate();
		} catch (Exception exc) {
			LOGGER.error("getDate: unparsable date '" + dateStr + "' (1st try)");
//	    IllegalStateException ex = new IllegalStateException("unparsable date '" + dateStr + "' (1st try)");
//	    ex.initCause(e);
//	    throw ex;
			try {
				return LocalDate.parse(dateStr, DATE_FORMAT_FALLBACK);
			} catch (Exception exc2) {
				LOGGER.error("getDate: unparsable date '" + dateStr + "' (2nd try)");
				IllegalStateException ex2 = new IllegalStateException("unparsable date '" + dateStr + "' (2nd try)");
				ex2.initCause(exc2);
				throw ex2;
			}
		}
	}

	@Override
	public LocalDateTime getDateTime() {
		if ( jwsdpPeer.getPriceTime() == null )
			return null;

		// String dateStr = jwsdpPeer.getPriceTime().getTsDate();
		try {
			return getZonedDateTime().toLocalDateTime();
		} catch (Exception exc) {
			// LOGGER.error("getDateTime: unparsable date-time '" + dateStr + "'");
			throw exc;
		}
	}

	private ZonedDateTime getZonedDateTime() {
		if ( jwsdpPeer.getPriceTime() == null )
			return null;

		String dateStr = jwsdpPeer.getPriceTime().getTsDate();
		try {
			return ZonedDateTime.parse(dateStr, DATE_FORMAT);
		} catch (Exception exc) {
			LOGGER.error("getZonedDateTime: unparsable zoned date-time '" + dateStr + "'");
			throw exc;
		}
	}

	@Override
	public Source getSource() {
		return Source.valueOff(getSourceStr());
	}

	public String getSourceStr() {
		if ( jwsdpPeer.getPriceSource() == null )
			return null;

		return jwsdpPeer.getPriceSource();
	}

	@Override
	public Type getType() {
		return Type.valueOff(getTypeStr());
	}

	public String getTypeStr() {
		if ( jwsdpPeer.getPriceType() == null )
			return null;

		return jwsdpPeer.getPriceType();
	}

	@Override
	public FixedPointNumber getValue() {
		if ( jwsdpPeer.getPriceValue() == null )
			return null;

		return new FixedPointNumber(jwsdpPeer.getPriceValue());
	}

	@Override
	public BigFraction getValueRat() {
		if ( jwsdpPeer.getPriceValue() == null )
			return null;

		return BigFraction.parse(jwsdpPeer.getPriceValue());
	}

	@Override
	public String getValueFormatted() {
		Locale lcl = Locale.getDefault();
		return getValueFormatted(lcl);
	}

	public String getValueFormatted(final Locale lcl) {
		NumberFormat nf = NumberFormat.getCurrencyInstance(lcl);
		nf.setCurrency(getToCurrency());
		return nf.format(getValue().getBigDecimal());
	}

    // -----------------------------------------------------------------

    @Override
	public int compareTo(final GnuCashPrice otherPrc) {
		int i = getFromCmdtyID().toString().compareTo(otherPrc.getFromCmdtyID().toString());
		if ( i != 0 ) {
			return i;
		}

		i = getDate().compareTo(otherPrc.getDate());
		if ( i != 0 ) {
			return i;
		}

		i = getSource().toString().compareTo(otherPrc.getSource().toString()); // sic, not getSourceStr()
		if ( i != 0 ) {
			return i;
		}
		
		return ("" + hashCode()).compareTo("" + otherPrc.hashCode());
	}
	
    // -----------------------------------------------------------------

	public NumberFormat getToCurrencyFormat() {
		return getToCurrencyFormat(Locale.getDefault());
	}
	
	public NumberFormat getToCurrencyFormat(Locale lcl) {
		currencyFormat = NumberFormat.getCurrencyInstance(lcl);
		Currency curr = getToCurrency();
		currencyFormat.setCurrency(curr);

		return currencyFormat;
	}
	
    // -----------------------------------------------------------------

	@Override
	public String toString() {
		String result = "GnuCashPriceImpl [";

		result += "id=" + getID();

		try {
			result += ", from-sec-curr-qualif-id='" + getFromCmdtyID() + "'";
		} catch (InvalidCmdtyTypeException e) {
			result += ", from-sec-curr-qualif-id=" + "ERROR";
		}

		try {
			result += ", to-curr-qualif-id='" + getToCurrID() + "'";
		} catch (Exception e) {
			result += ", to-curr-qualif-id=" + "ERROR";
		}

		result += ", date=" + getDate();

		try {
			result += ", value=" + getValueFormatted();
		} catch (Exception e) {
			result += ", value=" + "ERROR";
		}

		result += ", type=" + getType();
		result += ", source=" + getSource();

		result += "]";

		return result;
	}

}
