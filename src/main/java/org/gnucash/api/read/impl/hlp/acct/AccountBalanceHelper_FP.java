package org.gnucash.api.read.impl.hlp.acct;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.pricedb.ComplexPriceTable;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
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
		FixedPointNumber balance = FixedPointNumber.ZERO.copy();
	
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

	public static FixedPointNumber getBalance(final LocalDate date, final GCshCmdtyID cmdtyID,
											  final SimpleAccount acct) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		FixedPointNumber retval = getBalance(date, acct);

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
	
		retval = priceTab.convertToBaseCurrency(retval, cmdtyID);
		if ( retval == null ) {
			LOGGER.error("getBalance: Cannot transfer " + "from our currency '"
					+ acct.getCmdtyID().toString() + "' to the base-currency!");
			return null;
		}
	
		retval = priceTab.convertFromBaseCurrency(retval, cmdtyID);
		if ( retval == null ) {
			LOGGER.error("getBalance: Cannot transfer " + "from base-currenty to given currency '"
					+ cmdtyID.toString() + "'");
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
			 retval.equals(FixedPointNumber.ZERO.copy()) ) {
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

		retval = priceTab.convertToBaseCurrency(retval, acct.getCmdtyID());
		if ( retval == null ) {
			LOGGER.warn("getBalance: Cannot transfer " + "from our currency '"
					+ acct.getCmdtyID().toString() + "' to the base-currency");
			return null;
		}

		retval = priceTab.convertFromBaseCurrency(retval, new GCshCurrID(curr));
		if ( retval == null ) {
			LOGGER.warn("getBalance: Cannot transfer " + "from base-currenty to given currency '"
					+ curr + "'");
			return null;
		}

		return retval;
	}

	public static FixedPointNumber getBalance(final GnuCashTransactionSplit lastSpltIncl,
											  final SimpleAccount acct) {
		FixedPointNumber balance = FixedPointNumber.ZERO.copy();
	
		for ( GnuCashTransactionSplit splt : acct.getTransactionSplits() ) {
			try {
				// CAUTION: No special logic for action type GnuCashTransactionSplit.Action.SPLIT,
				// as opposed to sister project.
				// CAUTION: FixedPointNumber is mutable
				balance.add(splt.getQuantity());
	
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
		GCshCmdtyID cmdtyID = acct.getCmdtyID();
		if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY ) {
			NumberFormat nf = NumberFormat.getCurrencyInstance(lcl);
			nf.setCurrency(acct.getCurrency());
			return nf.format(getBalance(acct).getBigDecimal());
		} else if ( cmdtyID.getType() == GCshCmdtyID.Type.SECURITY ) {
			GnuCashCommodity cmdty = acct.getGnuCashFile().getCommodityByID(cmdtyID);
			String secSymb = "(sec-symbol)";
			if ( cmdty.getSymbol() != null ) {
				secSymb = cmdty.getSymbol();
			} else if ( cmdty.getXCode() != null ) {
				secSymb = cmdty.getXCode();
			} else {
				secSymb = cmdty.toString();
			}
			NumberFormat nf = NumberFormat.getNumberInstance(lcl);
			return ( nf.format(getBalance(acct).getBigDecimal()) + " " + secSymb );
		}
		
		return "ERROR";
	}

	// ---------------------------------------------------------------

	public static FixedPointNumber getBalanceRecursive(final SimpleAccount acct) {
		return getBalanceRecursive(LocalDate.now(), acct);
	}

	public static FixedPointNumber getBalanceRecursive(final LocalDate date,
													   final SimpleAccount acct) {
		return getBalanceRecursive(date, acct.getCmdtyID(), acct);
	}

	public static FixedPointNumber getBalanceRecursive(final LocalDate date, final GCshCmdtyID cmdtyID,
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

	public static FixedPointNumber getBalanceRecursive(final LocalDate date, final Currency curr,
													   final SimpleAccount acct) {
		FixedPointNumber retval = getBalance(date, curr, acct);

		if ( retval == null ) {
			retval = FixedPointNumber.ZERO.copy();
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

	public static FixedPointNumber getBalanceRecursive(final GnuCashTransactionSplit lastSpltIncl,
			  										   final SimpleAccount acct) {
		FixedPointNumber retval = getBalance(lastSpltIncl, acct);

		if ( retval == null ) {
			retval = FixedPointNumber.ZERO.copy();
		}

		for ( GnuCashAccount child : acct.getChildren() ) {
			try {
				// CAUTION: FixedPointNumber is mutable
				retval.add( child.getBalanceRecursive(lastSpltIncl) );
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
		GCshCmdtyID cmdtyID = acct.getCmdtyID();
		if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY ) {
			NumberFormat cf = NumberFormat.getCurrencyInstance(lcl);
			cf.setCurrency(acct.getCurrency());
			return cf.format(getBalanceRecursive(acct).getBigDecimal());
		} else if ( cmdtyID.getType() == GCshCmdtyID.Type.SECURITY ) {
			GnuCashCommodity cmdty = acct.getGnuCashFile().getCommodityByID(cmdtyID);
			String secSymb = "(sec-symbol)";
			if ( cmdty.getSymbol() != null ) {
				secSymb = cmdty.getSymbol();
			} else if ( cmdty.getXCode() != null ) {
				secSymb = cmdty.getXCode();
			} else {
				secSymb = cmdty.toString();
			}
			NumberFormat nf = NumberFormat.getNumberInstance(lcl);
			return ( nf.format(getBalance(acct).getBigDecimal()) + " " + secSymb );
		}
		
		return "ERROR";
	}
	
	// ---------------------------------------------------------------
	// Helpers -- balance pre-computed
	
	public static String formatBalance(SimpleAccount acct, FixedPointNumber blnc) {
		Locale lcl = Locale.getDefault();
		return formatBalance(acct, blnc, lcl);
	}
	
	public static String formatBalance(SimpleAccount acct, FixedPointNumber blnc, Locale lcl) {
		NumberFormat nf = acct.getCurrencyFormat(lcl);
    	if ( acct.getCmdtyID().getType() == GCshCmdtyID.Type.CURRENCY ) {
    		nf.setCurrency(Currency.getInstance(acct.getCmdtyID().getCode()));
    		return nf.format(blnc.getBigDecimal());
    	} else {
    		return nf.format(blnc.getBigDecimal()) + " " + acct.getCmdtyID().getCode().toString();
    	}
	}

}
