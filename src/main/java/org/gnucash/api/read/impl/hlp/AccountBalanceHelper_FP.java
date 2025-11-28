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

import org.gnucash.api.currency.ComplexPriceTable;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class AccountBalanceHelper_FP
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountBalanceHelper_FP.class);

	// ---------------------------------------------------------------

	public static FixedPointNumber getBalance(final SimpleAccount acct) {
		return getBalance(LocalDate.now(), acct);
	}


	public static FixedPointNumber getBalance(final LocalDate date,
											  final SimpleAccount acct) {
		return getBalance(date, (List<GnuCashTransactionSplit>) null, acct);
	}

	// The currency will be the one of this account.
	public static FixedPointNumber getBalance(final LocalDate date, List<GnuCashTransactionSplit> after,
											  final SimpleAccount acct) {
		FixedPointNumber balance = new FixedPointNumber();
	
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
			// CAUTION: FixedPointNumber is mutable
			balance.add(splt.getQuantity());
		}
	
		return balance;
	}

	public static FixedPointNumber getBalance(final LocalDate date, final GCshCmdtyCurrID cmdtyCurrID,
											  final SimpleAccount acct) {
		FixedPointNumber retval = getBalance(date, acct);

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
	
		if ( ! priceTab.convertToBaseCurrency(retval, cmdtyCurrID) ) {
			Collection<String> currList = acct.getGnuCashFile().getCurrencyTable()
					.getCurrencies(acct.getCmdtyCurrID().getNameSpace());
			LOGGER.error("getBalance: Cannot transfer " + "from our currency '"
					+ acct.getCmdtyCurrID().toString() + "' to the base-currency " + " \n(we know "
					+ acct.getGnuCashFile().getCurrencyTable().getNameSpaces().size() + " currency-namespaces and "
					+ (currList == null ? "no" : "" + currList.size()) + " currencies in our namespace)");
			return null;
		}
	
		if ( ! priceTab.convertFromBaseCurrency(retval, cmdtyCurrID) ) {
			LOGGER.error("getBalance: Cannot transfer " + "from base-currenty to given currency '"
					+ cmdtyCurrID.toString() + "'");
			return null;
		}
	
		return retval;
	}

	public static FixedPointNumber getBalance(final LocalDate date, final Currency curr,
											  final SimpleAccount acct) {
		FixedPointNumber retval = getBalance(date, acct);

		if ( retval == null ) {
			LOGGER.warn("getBalance: Could not create balance");
			return null;
		}

		if ( curr == null ||
			 retval.equals(new FixedPointNumber()) ) {
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

		if ( ! priceTab.convertToBaseCurrency(retval, acct.getCmdtyCurrID()) ) {
			LOGGER.warn("getBalance: Cannot transfer " + "from our currency '"
					+ acct.getCmdtyCurrID().toString() + "' to the base-currency");
			return null;
		}

		if ( ! priceTab.convertFromBaseCurrency(retval, new GCshCurrID(curr)) ) {
			LOGGER.warn("getBalance: Cannot transfer " + "from base-currenty to given currency '"
					+ curr + "'");
			return null;
		}

		return retval;
	}

	public static FixedPointNumber getBalance(final GnuCashTransactionSplit lastIncludesSplit,
											  final SimpleAccount acct) {
		FixedPointNumber balance = new FixedPointNumber();
	
		for ( GnuCashTransactionSplit splt : acct.getTransactionSplits() ) {
			try {
				// CAUTION: FixedPointNumber is mutable
				balance.add(splt.getQuantity());
	
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
		return cf.format(getBalance(acct).getBigDecimal());
	}

	// ---------------------------------------------------------------

	public static FixedPointNumber getBalanceRecursive(final SimpleAccount acct) {
		return getBalanceRecursive(LocalDate.now(), acct);
	}

	public static FixedPointNumber getBalanceRecursive(final LocalDate date,
													   final SimpleAccount acct) {
		return getBalanceRecursive(date, acct.getCmdtyCurrID(), acct);
	}

	public static FixedPointNumber getBalanceRecursive(final LocalDate date, final GCshCmdtyCurrID cmdtyCurrID,
													   final SimpleAccount acct) {
			if ( cmdtyCurrID.getType() == GCshCmdtyCurrID.Type.CURRENCY )
				return getBalanceRecursive(date, new GCshCurrID(cmdtyCurrID.getCode()).getCurrency(), acct);
			else
				return getBalance(date, cmdtyCurrID, acct); // CAUTION: This assumes that under a stock account,
													        // there are no children (which sounds sensible,
													        // but there might be special cases)
	}

	public static FixedPointNumber getBalanceRecursive(final LocalDate date, final Currency curr,
													   final SimpleAccount acct) {
		FixedPointNumber retval = getBalance(date, curr, acct);

		if ( retval == null ) {
			retval = new FixedPointNumber();
		}

		for ( GnuCashAccount child : acct.getChildren() ) {
			try {
				// CAUTION: FixedPointNumber is mutable
				retval.add(child.getBalanceRecursive(date, curr));
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
		return cf.format(getBalanceRecursive(acct).getBigDecimal());
	}
	
	// ---------------------------------------------------------------
	// Helpers -- balance pre-computed
	
	public static String formatBalance(SimpleAccount acct, FixedPointNumber blnc) {
		Locale lcl = Locale.getDefault();
		return formatBalance(acct, blnc, lcl);
	}
	
	public static String formatBalance(SimpleAccount acct, FixedPointNumber blnc, Locale lcl) {
		NumberFormat nf = acct.getCurrencyFormat(lcl);
    	if ( acct.getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
    		nf.setCurrency(Currency.getInstance(acct.getCmdtyCurrID().getCode()));
    		return nf.format(blnc.getBigDecimal());
    	} else {
    		return nf.format(blnc.getBigDecimal()) + " " + acct.getCmdtyCurrID().getCode().toString();
    	}
	}

}
