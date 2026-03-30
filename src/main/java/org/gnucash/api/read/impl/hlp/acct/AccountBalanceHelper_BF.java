package org.gnucash.api.read.impl.hlp.acct;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.pricedb.ComplexPriceTable;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.hlp.AmountFormatter_BF;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public static BigFraction getBalance(final LocalDate date, final GCshCmdtyID cmdtyID,
									     final SimpleAccount acct) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		BigFraction retval = getBalance(date, acct);

		if ( retval == null ) {
			LOGGER.error("getBalance: Could not create balance");
			return null;
		}

		// is conversion needed?
		if ( acct.getCmdtyID().equals(cmdtyID) ) {
			return retval;
		}
	
		ComplexPriceTable priceTab = acct.getGnuCashFile().getCurrencyTable();
		if ( priceTab == null ) {
			LOGGER.error("getBalance: Cannot transfer to given currency because we have no currency-table");
			return null;
		}
	
		retval = priceTab.convertToBaseCurrencyRat(retval, cmdtyID);
		if ( retval == null ) {
			LOGGER.error("getBalance: Cannot transfer " + "from our currency '"
					+ acct.getCmdtyID().toString() + "' to the base-currency!");
			return null;
		}
	
		retval = priceTab.convertFromBaseCurrencyRat(retval, cmdtyID);
		if ( retval == null ) {
			LOGGER.error("getBalance: Cannot transfer " + "from base-currenty to given currency '"
					+ cmdtyID.toString() + "'");
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
		if ( acct.getCmdtyID().getType() == GCshCmdtyID.Type.CURRENCY ) {
			if ( acct.getCmdtyID().getCode().equals(curr.getCurrencyCode()) ) {
				return retval;
			}
		}

		ComplexPriceTable priceTab = acct.getGnuCashFile().getCurrencyTable();

		if ( priceTab == null ) {
			LOGGER.warn("getBalance: Cannot transfer to given currency because we have no currency-table");
			return null;
		}

		retval = priceTab.convertToBaseCurrencyRat(retval, acct.getCmdtyID());
		if ( retval == null ) {
			LOGGER.warn("getBalance: Cannot transfer " + "from our currency '"
					+ acct.getCmdtyID().toString() + "' to the base-currency");
			return null;
		}

		retval = priceTab.convertFromBaseCurrencyRat(retval, new GCshCurrID(curr));
		if ( retval == null ) {
			LOGGER.warn("getBalance: Cannot transfer " + "from base-currenty to given currency '"
					+ curr + "'");
			return null;
		}

		return retval;
	}

	public static BigFraction getBalance(final GnuCashTransactionSplit lastSpltIncl,
										 final SimpleAccount acct) {
		BigFraction balance = BigFraction.ZERO;
	
		for ( GnuCashTransactionSplit splt : acct.getTransactionSplits() ) {
			try {
				// CAUTION: No special logic for action type GnuCashTransactionSplit.Action.SPLIT,
				// as opposed to sister project.
				// CAUTION: BigFraction is immutable
				balance = balance.add(splt.getQuantityRat());
	
				if ( splt.getID().equals( lastSpltIncl.getID() ) ) {
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
		return formatBalance( acct, getBalance(acct), lcl );
	}

	// ---------------------------------------------------------------

	public static BigFraction getBalanceRecursive(final SimpleAccount acct) {
		return getBalanceRecursive(LocalDate.now(), acct);
	}

	public static BigFraction getBalanceRecursive(final LocalDate date,
												  final SimpleAccount acct) {
		return getBalanceRecursive(date, acct.getCmdtyID(), acct);
	}

	public static BigFraction getBalanceRecursive(final LocalDate date, final GCshCmdtyID cmdtyID,
												  final SimpleAccount acct) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

			if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY )
				return getBalanceRecursive(date, new GCshCurrID(cmdtyID.getCode()).getCurrency(), acct);
			else
				return getBalance(date, cmdtyID, acct); // CAUTION: This assumes that under a stock account,
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
				retval = retval.add( child.getBalanceRecursiveRat(date, curr) );
			} catch ( Exception exc ) {
				// Yes, it does happen sometimes!
				LOGGER.error("getBalanceRecursive: Error adding balance for child account " + child.getID());
				throw exc;
			}
		}

		return retval;
	}

	public static BigFraction getBalanceRecursive(final GnuCashTransactionSplit lastSpltIncl,
												  final SimpleAccount acct) {
		BigFraction retval = getBalance(lastSpltIncl, acct);

		if ( retval == null ) {
			retval = BigFraction.ZERO;
		}

		for ( GnuCashAccount child : acct.getChildren() ) {
			try {
				// CAUTION: BigFraction is immutable
				retval = retval.add( child.getBalanceRecursiveRat(lastSpltIncl) );
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
		return formatBalance( acct, getBalanceRecursive(acct), lcl );
	}
	
	// ---------------------------------------------------------------
	// Helpers -- balance pre-computed
	
	public static String formatBalance(SimpleAccount acct, BigFraction blnc) {
		if ( acct == null ) {
			throw new IllegalArgumentException("argument <acct> is null");
		}
		
		if ( blnc == null ) {
			throw new IllegalArgumentException("argument <blnc> is null");
		}
		
		return formatBalance(acct, blnc, Locale.getDefault());
	}
	
	public static String formatBalance(SimpleAccount acct, BigFraction blnc, Locale lcl) {
		if ( acct == null ) {
			throw new IllegalArgumentException("argument <acct> is null");
		}
		
		if ( blnc == null ) {
			throw new IllegalArgumentException("argument <blnc> is null");
		}
		
		if ( lcl == null ) {
			throw new IllegalArgumentException("argument <lcl> is null");
		}
		
		return AmountFormatter_BF.formatAmount( acct.getGnuCashFile(),
												blnc, acct.getCmdtyID(), lcl );
	}

}
