package org.gnucash.api.read.impl.hlp;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.currency.ComplexPriceTable;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber; // sic

public class AccountBalanceHelper_BF
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountBalanceHelper_BF.class);

	// ---------------------------------------------------------------

	public static BigFraction getBalance(final SimpleAccount acct) {
		return getBalance(LocalDate.now(), acct);
	}

	public static BigFraction getBalance(final LocalDate date,
										 final SimpleAccount acct) {
		return getBalance(date, (List<GnuCashTransactionSplit>) null, acct);
	}

	// The currency will be the one of this account.
	public static BigFraction getBalance(final LocalDate date, List<GnuCashTransactionSplit> after,
										 final SimpleAccount acct) {
		BigFraction balance = BigFraction.ZERO;
	
		for ( GnuCashTransactionSplit splt : acct.getTransactionSplits() ) {
			if ( date != null && 
				 after != null ) {
				LocalDateTime startOfDay = date.atStartOfDay();
				ZonedDateTime startOfDay_zdt = startOfDay.atZone(ZoneId.systemDefault());
				if ( splt.getTransaction().getDatePosted().isAfter(startOfDay_zdt) ) {
					after.add(splt);
					continue;
				}
			}
	
			// the currency of the quantity is the one of the account
			// CAUTION: No special logic for action type GnuCashTransactionSplit.Action.SPLIT,
			// as opposed to sister project.
			// CAUTION: BigFraction is immutable
			balance = balance.add(splt.getQuantityRat());
		}
	
		return balance;
	}

	public static BigFraction getBalance(final LocalDate date, final GCshCmdtyCurrID cmdtyCurrID,
									     final SimpleAccount acct) {
		BigFraction retval = getBalance(date, acct);

		if ( retval == null ) {
			LOGGER.error("getBalance: Could not create balance");
			return null;
		}

		// is conversion needed?
		if ( acct.getCmdtyCurrID().equals(cmdtyCurrID) ) {
			return retval;
		}
	
		ComplexPriceTable priceTab = acct.getGnuCashFile().getCurrencyTable();
	
		if ( priceTab == null ) {
			LOGGER.error("getBalance: Cannot transfer "
					+ "to given currency because we have no currency-table");
			return null;
		}
	
		if ( ! priceTab.convertToBaseCurrency(FixedPointNumber.of(retval), cmdtyCurrID) ) {
			Collection<String> currList = acct.getGnuCashFile().getCurrencyTable()
					.getCurrencies(acct.getCmdtyCurrID().getNameSpace());
			LOGGER.error("getBalance: Cannot transfer " + "from our currency '"
					+ acct.getCmdtyCurrID().toString() + "' to the base-currency " + " \n(we know "
					+ acct.getGnuCashFile().getCurrencyTable().getNameSpaces().size() + " currency-namespaces and "
					+ (currList == null ? "no" : "" + currList.size()) + " currencies in our namespace)");
			return null;
		}
	
		if ( ! priceTab.convertFromBaseCurrency(FixedPointNumber.of(retval), cmdtyCurrID) ) {
			LOGGER.error("getBalance: Cannot transfer " + "from base-currenty to given currency '"
					+ cmdtyCurrID.toString() + "'");
			return null;
		}
	
		return retval;
	}

	public static BigFraction getBalance(final LocalDate date, final Currency curr,
										 final SimpleAccount acct) {
		BigFraction retval = getBalance(date, acct);

		if ( retval == null ) {
			LOGGER.warn("getBalance: Could not create balance");
			return null;
		}

		if ( curr == null ||
			 retval.equals(BigFraction.ZERO) ) {
			return retval;
		}

		// is conversion needed?
		if ( acct.getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
			if ( acct.getCmdtyCurrID().getCode().equals(curr.getCurrencyCode()) ) {
				return retval;
			}
		}

		ComplexPriceTable priceTab = acct.getGnuCashFile().getCurrencyTable();

		if ( priceTab == null ) {
			LOGGER.warn("getBalance: Cannot transfer "
					+ "to given currency because we have no currency-table");
			return null;
		}

		// BEGIN ::TODO: 
		// Works, but is ugly.
		// Have that symmetrical with FP-variant
		FixedPointNumber hlp = FixedPointNumber.of(retval);
		if ( ! priceTab.convertToBaseCurrency(hlp, acct.getCmdtyCurrID()) ) {
			LOGGER.warn("getBalance: Cannot transfer " + "from our currency '"
					+ acct.getCmdtyCurrID().toString() + "' to the base-currency");
			return null;
		}

		if ( ! priceTab.convertFromBaseCurrency(hlp, new GCshCurrID(curr)) ) {
			LOGGER.warn("getBalance: Cannot transfer " + "from base-currenty to given currency '"
					+ curr + "'");
			return null;
		}
		retval = hlp.toBigFraction();
		// END ::TODO

		return retval;
	}

	public static BigFraction getBalance(final GnuCashTransactionSplit lastIncludesSplit,
										 final SimpleAccount acct) {
		BigFraction balance = BigFraction.ZERO;
	
		for ( GnuCashTransactionSplit splt : acct.getTransactionSplits() ) {
			try {
				// CAUTION: BigFraction is immutable
				balance = balance.add(splt.getQuantityRat());
	
				if ( splt == lastIncludesSplit ) {
					break;
				}
			} catch ( Exception exc ) {
				// Yes, it does happen!
				LOGGER.error("getBalance: Could not add Split " + splt.getID() + 
						     " of Transaction " + splt.getTransactionID());
			}
		}
	
		return balance;
	}

	// ----------------------------

	public static String getBalanceFormatted(final SimpleAccount acct) {
		Locale lcl = Locale.getDefault();
		return getBalanceFormatted(lcl, acct);
	}

	public static String getBalanceFormatted(final Locale lcl,
											 final SimpleAccount acct) {
		NumberFormat cf = NumberFormat.getCurrencyInstance(lcl);
		cf.setCurrency(acct.getCurrency());
		return cf.format(getBalance(acct).bigDecimalValue());
	}

	// ---------------------------------------------------------------

	public static BigFraction getBalanceRecursive(final SimpleAccount acct) {
		return getBalanceRecursive(LocalDate.now(), acct);
	}

	public static BigFraction getBalanceRecursive(final LocalDate date,
												  final SimpleAccount acct) {
		return getBalanceRecursive(date, acct.getCmdtyCurrID(), acct);
	}

	public static BigFraction getBalanceRecursive(final LocalDate date, final GCshCmdtyCurrID cmdtyCurrID,
												  final SimpleAccount acct) {
			if ( cmdtyCurrID.getType() == GCshCmdtyCurrID.Type.CURRENCY )
				return getBalanceRecursive(date, new GCshCurrID(cmdtyCurrID.getCode()).getCurrency(), acct);
			else
				return getBalance(date, cmdtyCurrID, acct); // CAUTION: This assumes that under a stock account,
													        // there are no children (which sounds sensible,
													        // but there might be special cases)
	}

	public static BigFraction getBalanceRecursive(final LocalDate date, final Currency curr,
												  final SimpleAccount acct) {
		BigFraction retval = getBalance(date, curr, acct);

		if ( retval == null ) {
			retval = BigFraction.ZERO;
		}

		for ( GnuCashAccount child : acct.getChildren() ) {
			try {
				// CAUTION: BigFraction is immutable
				retval = retval.add(child.getBalanceRecursiveRat(date, curr));
			} catch ( Exception exc ) {
				// Yes, it does happen sometimes!
				LOGGER.error("getBalanceRecursive: Error adding balance for child account " + child.getID());
				throw exc;
			}
		}

		return retval;
	}

	// ----------------------------

	public static String getBalanceRecursiveFormatted(final SimpleAccount acct) {
		Locale lcl = Locale.getDefault();
		return getBalanceRecursiveFormatted(lcl, acct);
	}

	public static String getBalanceRecursiveFormatted(final Locale lcl,
													  final SimpleAccount acct) {
		NumberFormat cf = NumberFormat.getCurrencyInstance(lcl);
		cf.setCurrency(acct.getCurrency());
		return cf.format(getBalanceRecursive(acct).bigDecimalValue());
	}
}
