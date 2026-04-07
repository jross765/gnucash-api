package org.gnucash.api.read.impl.hlp.acct;

import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.aux.GCshAcctLot;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/*
 * This is a base-class that helps implementing the GnuCashAccount
 * interface with its extensive number of convenience-methods.
 */
public abstract class SimpleAccount extends GnuCashObjectImpl 
									implements GnuCashAccount 
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAccount.class);

	// ---------------------------------------------------------------

	public SimpleAccount(final GnuCashFile gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * The returned list is sorted by the natural order of the Transaction-Splits.
	 */
	@Override
	public List<GnuCashTransaction> getTransactions() {
		List<GnuCashTransaction> retval = new ArrayList<GnuCashTransaction>();

		for ( GnuCashTransactionSplit splt : getTransactionSplits() ) {
			retval.add(splt.getTransaction());
		}

		// retval.sort(Comparator.reverseOrder()); // not necessary 

		return retval;
	}

	@Override
	public List<GnuCashTransaction> getTransactions(final LocalDate fromDate, final LocalDate toDate) {
		List<GnuCashTransaction> retval = new ArrayList<GnuCashTransaction>();

		for ( GnuCashTransaction trx : getTransactions() ) {
			 if ( ( trx.getDatePosted().toLocalDate().isEqual( fromDate ) ||
				    trx.getDatePosted().toLocalDate().isAfter( fromDate ) ) &&
			      ( trx.getDatePosted().toLocalDate().isEqual( toDate ) ||
					trx.getDatePosted().toLocalDate().isBefore( toDate ) ) ) {
				 retval.add(trx);
			 }
		}

		// retval.sort(Comparator.reverseOrder()); // not necessary 
		
		return retval;
	}

	@Override
	public boolean isChildAccountRecursive(final GnuCashAccount account) {
		if ( this == account ) {
			return true;
		}

		for ( GnuCashAccount child : getChildren() ) {
			if ( this == child ) {
				return true;
			}
			if ( child.isChildAccountRecursive(account) ) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		return getQualifiedName();
	}

	/**
	 * Get name including the name of the parent accounts.
	 */
	@Override
	public String getQualifiedName() {
		GnuCashAccount acc = getParentAccount();

		if ( acc == null || 
			 acc.getID() == getID() ) {
			if ( getParentAccountID() == null || 
				 ! getParentAccountID().isSet() ) {
				return getName();
			}

			return "UNKNOWN" + SEPARATOR + getName();
		} else {
			return acc.getQualifiedName() + SEPARATOR + getName();
		}
	}

	@Override
	public GnuCashAccount getParentAccount() {
		if ( isRootAccount() )
			return null;

		GCshAcctID parentID = getParentAccountID();
		if ( parentID == null ) {
			return null;
		}

		return getGnuCashFile().getAccountByID(parentID);
	}

	@Override
	public boolean isRootAccount() {
		if ( getType() == Type.ROOT )
			return true;
		else
			return false;
	}

	// ---------------------------------------------------------------

	@Override
	public FixedPointNumber getBalance() {
		return AccountBalanceHelper_FP.getBalance(this);
	}

	@Override
	public BigFraction getBalanceRat() {
		return AccountBalanceHelper_BF.getBalance(this);
	}
	
	// ---

	@Override
	public FixedPointNumber getBalance(final LocalDate date) {
		return AccountBalanceHelper_FP.getBalance(date, this);
	}

	@Override
	public BigFraction getBalanceRat(final LocalDate date) {
		return AccountBalanceHelper_BF.getBalance(date, this);
	}

	// ---

	@Override
	public FixedPointNumber getBalance(final LocalDate date, List<GnuCashTransactionSplit> after) {
		return AccountBalanceHelper_FP.getBalance(date, after, this);
	}

	public BigFraction getBalanceRat(final LocalDate date, List<GnuCashTransactionSplit> after) {
		return AccountBalanceHelper_BF.getBalance(date, after, this);
	}

	// ---

	@Override
	public FixedPointNumber getBalance(final LocalDate date, final GCshCmdtyID cmdtyCurrID) {
		return AccountBalanceHelper_FP.getBalance(date, cmdtyCurrID, this);
	}

	@Override
	public BigFraction getBalanceRat(final LocalDate date, final GCshCmdtyID cmdtyCurrID) {
		return AccountBalanceHelper_BF.getBalance(date, cmdtyCurrID, this);
	}

	// ---

	@Override
	public FixedPointNumber getBalance(final LocalDate date, final GCshSecID cmdtyID) {
		return AccountBalanceHelper_FP.getBalance(date, cmdtyID, this);
	}

	@Override
	public BigFraction getBalanceRat(final LocalDate date, final GCshSecID cmdtyID) {
		return AccountBalanceHelper_BF.getBalance(date, cmdtyID, this);
	}

	// ---

	@Override
	public FixedPointNumber getBalance(final LocalDate date, final GCshCurrID currID) {
		return AccountBalanceHelper_FP.getBalance(date, currID, this);
	}

	@Override
	public BigFraction getBalanceRat(final LocalDate date, final GCshCurrID currID) {
		return AccountBalanceHelper_BF.getBalance(date, currID, this);
	}

	// ---

	@Override
	public FixedPointNumber getBalance(final LocalDate date, final Currency curr) {
		return AccountBalanceHelper_FP.getBalance(date, curr, this);
	}

	@Override
	public BigFraction getBalanceRat(final LocalDate date, final Currency curr) {
		return AccountBalanceHelper_BF.getBalance(date, curr, this);
	}

	// ---

	@Override
	public FixedPointNumber getBalance(final GnuCashTransactionSplit lastSpltIncl) {
		return AccountBalanceHelper_FP.getBalance(lastSpltIncl, this);
	}

	@Override
	public BigFraction getBalanceRat(final GnuCashTransactionSplit lastSpltIncl) {
		return AccountBalanceHelper_BF.getBalance(lastSpltIncl, this);
	}

	// ----------------------------

	@Override
	public String getBalanceFormatted() {
		return AccountBalanceHelper_FP.getBalanceFormatted(this);
	}

	@Override
	public String getBalanceFormatted(final Locale lcl) {
		return AccountBalanceHelper_FP.getBalanceFormatted(lcl, this);
	}
	
	// ---------------------------------------------------------------

	@Override
	public FixedPointNumber getBalanceRecursive() {
		return AccountBalanceHelper_FP.getBalanceRecursive(this);
	}

	@Override
	public BigFraction getBalanceRecursiveRat() {
		return AccountBalanceHelper_BF.getBalanceRecursive(this);
	}

	// ---

	@Override
	public FixedPointNumber getBalanceRecursive(final LocalDate date) {
		return AccountBalanceHelper_FP.getBalanceRecursive(date, this);
	}

	@Override
	public BigFraction getBalanceRecursiveRat(final LocalDate date) {
		return AccountBalanceHelper_BF.getBalanceRecursive(date, this);
	}

	// ---

	@Override
	public FixedPointNumber getBalanceRecursive(final LocalDate date, final GCshCmdtyID cmdtyID) {
		return AccountBalanceHelper_FP.getBalanceRecursive(date, cmdtyID, this);
	}

	@Override
	public BigFraction getBalanceRecursiveRat(final LocalDate date, final GCshCmdtyID cmdtyID) {
		return AccountBalanceHelper_BF.getBalanceRecursive(date, cmdtyID, this);
	}

	// ---

	@Override
	public FixedPointNumber getBalanceRecursive(final LocalDate date, final GCshSecID secID) {
		return AccountBalanceHelper_FP.getBalanceRecursive(date, secID, this);
	}
	
	@Override
	public BigFraction getBalanceRecursiveRat(final LocalDate date, final GCshSecID secID) {
		return AccountBalanceHelper_BF.getBalanceRecursive(date, secID, this);
	}
	
	// ---

	@Override
	public FixedPointNumber getBalanceRecursive(final LocalDate date, final Currency curr) {
		return AccountBalanceHelper_FP.getBalanceRecursive(date, curr, this);
	}

	@Override
	public BigFraction getBalanceRecursiveRat(final LocalDate date, final Currency curr) {
		return AccountBalanceHelper_BF.getBalanceRecursive(date, curr, this);
	}

	// ---

	@Override
	public FixedPointNumber getBalanceRecursive(final GnuCashTransactionSplit lastSpltIncl) {
		return AccountBalanceHelper_FP.getBalanceRecursive(lastSpltIncl, this);
	}

	@Override
	public BigFraction getBalanceRecursiveRat(final GnuCashTransactionSplit lastSpltIncl) {
		return AccountBalanceHelper_BF.getBalanceRecursive(lastSpltIncl, this);
	}

	// ----------------------------

	@Override
	public String getBalanceRecursiveFormatted() {
		return AccountBalanceHelper_FP.getBalanceRecursiveFormatted(this);
	}

	@Override
	public String getBalanceRecursiveFormatted(final Locale lcl) {
		return AccountBalanceHelper_FP.getBalanceRecursiveFormatted(lcl, this);
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashTransactionSplit getLastSplitBeforeRecursive(final LocalDate date) {
		if ( date == null ) {
			throw new IllegalArgumentException("argument <date> is null");
		}

		GnuCashTransactionSplit lastSplit = null;

		for ( GnuCashTransactionSplit split : getTransactionSplits() ) {
			if ( date == null || 
				 split.getTransaction().getDatePosted()
				 	.isBefore(ChronoZonedDateTime.from(date.atStartOfDay())) ) {
				if ( lastSplit == null ||
					 split.getTransaction().getDatePosted()
						.isAfter(lastSplit.getTransaction().getDatePosted()) ) {
					lastSplit = split;
				}
			}
		}

		for ( GnuCashAccount account : getChildren() ) {
			GnuCashTransactionSplit split = account.getLastSplitBeforeRecursive(date);
			if ( split != null && 
				 split.getTransaction() != null ) {
				if ( lastSplit == null ||
					 split.getTransaction().getDatePosted()
						.isAfter(lastSplit.getTransaction().getDatePosted()) ) {
					lastSplit = split;
				}
			}
		}

		return lastSplit;
	}
	
	// ----------------------------

	@Override
	public boolean hasTransactions() {
		if ( this.getTransactionSplits() == null ) {
			return false;
		}
		
		return this.getTransactionSplits().size() > 0;
	}

	@Override
	public boolean hasTransactionsRecursive() {
		if ( this.hasTransactions() ) {
			return true;
		}

		for ( GnuCashAccount child : getChildren() ) {
			if ( child.hasTransactionsRecursive() ) {
				return true;
			}
		}

		return false;
	}

	// ----------------------------

	@Override
	public boolean hasLots() {
		if ( this.getLots() == null ) {
			return false;
		}
			
		return this.getLots().size() > 0;
	}
    
	// ----------------------------

	/**
	 * @return null if we are no currency but e.g. a fund
	 */
	public Currency getCurrency() {
		if ( getCmdtyID().getType() != GCshCmdtyID.Type.CURRENCY ) {
			throw new IllegalStateException("Account security/currency is not of type " + GCshCmdtyID.Type.CURRENCY);
		}

		String gcshCurrID = getCmdtyID().getCode();
		return Currency.getInstance(gcshCurrID);
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashTransactionSplit getTransactionSplitByID(final GCshSpltID spltID) {
		if ( spltID == null ) {
			throw new IllegalArgumentException("argument <spltID> is null");
		}

		if ( ! spltID.isSet() ) {
			throw new IllegalArgumentException("argument <spltID> is not set");
		}

		for ( GnuCashTransactionSplit split : getTransactionSplits() ) {
			if ( spltID.equals(split.getID()) ) {
				return split;
			}

		}

		return null;
	}

	@Override
	public GCshAcctLot getLotByID(final GCshLotID acctLotID) {
		if ( acctLotID == null ) {
			throw new IllegalArgumentException("argument <acctLotID> is null");
		}

		if ( ! acctLotID.isSet() ) {
			throw new IllegalArgumentException("argument <acctLotID> is not set");
		}

		for ( GCshAcctLot lot : getLots() ) {
			if ( acctLotID.equals(lot.getID()) ) {
				return lot;
			}

		}

		return null;
	}

    // -----------------------------------------------------------------

	@Override
	public int compareTo(final GnuCashAccount otherAcct) {
		int i = compareToByQualifiedName(otherAcct);
		if ( i != 0 ) {
			return i;
		}

		i = compareToByID(otherAcct);
		if ( i != 0 ) {
			return i;
		}

		return ("" + hashCode()).compareTo("" + otherAcct.hashCode());
	}

	private int compareToByID(final GnuCashAccount otherAcct) {
		return getID().toString().compareTo(otherAcct.getID().toString());
	}

	@SuppressWarnings("unused")
	private int compareToByCode(final GnuCashAccount otherAcct) {
		return getCode().toString().compareTo(otherAcct.getCode().toString());
	}

	@SuppressWarnings("unused")
	private int compareToByName(final GnuCashAccount otherAcct) {
		return getName().compareTo(otherAcct.getName());
	}

	private int compareToByQualifiedName(final GnuCashAccount otherAcct) {
		return getQualifiedName().compareTo(otherAcct.getQualifiedName());
	}

    // -----------------------------------------------------------------

	/*
	 * Helper used in ${@link #compareTo(Object)} to compare names starting with a
	 * number.
	 */
	@SuppressWarnings("unused")
	private Long startsWithNumber(final String s) {
		if ( s == null ) {
			throw new IllegalArgumentException("null string given");
		}

//		if ( s.isBlank() ) {
//			throw new IllegalArgumentException("empty string given");
//		}

		int digitCount = 0;
		for ( int i = 0; i < s.length() && Character.isDigit(s.charAt(i)); i++ ) {
			digitCount++;
		}
		
		if ( digitCount == 0 ) {
			return null;
		}
		
		return Long.valueOf(s.substring(0, digitCount));
	}

}
