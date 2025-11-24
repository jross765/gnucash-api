package org.gnucash.api.read;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.read.aux.GCshAcctLot;
import org.gnucash.api.read.aux.GCshAcctReconInfo;
import org.gnucash.api.read.hlp.HasTransactions;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * A GnuCash account satisfies the "normal" definition of the term in 
 * accounting (<a href="https://en.wikipedia.org/wiki/Account_(bookkeeping)">Wikipedia</a>).
 * <br>
 * You can also see it as a collection of transactions that start or end there. 
 * <br>
 * An account has a balance.  
 * <br>
 * All accounts taken together define the so-called chart of accounts,
 * organized in a tree (the top node of the tree being the root account). 
 * That means that each account may have a parent-account as well as one or 
 * several child-accounts.
 * <br>
 * Cf. <a href="https://lists.gnucash.org/docs/C/gnucash-manual/acct-types.html">GnuCash manual</a>
 */
public interface GnuCashAccount extends Comparable<GnuCashAccount>,
										HasTransactions,
										HasUserDefinedAttributes
{

    // For the following types cf.:
    // https://github.com/GnuCash/gnucash/blob/stable/libgnucash/engine/Account.h
    //
    // Examples (from German accounting):
    //
    // - TYPE_BANK = "BANK"; Girokonto, Tagesgeldkonto
    // - TYPE_CASH = "CASH"; Kasse
    // - TYPE_CREDIT = "CREDIT"; "Kreditkarte"
    // - TYPE_ASSET = "ASSET"; Vermögensgegenstaende, "1. Forderungen aus
    // Lieferungen und Leistungen"
    // - TYPE_LIABILITY = "LIABILITY"; Verbindlichkeiten ggueber Lieferanten
    // - TYPE_STOCK = "STOCK"; Aktie
    // - TYPE_MUTUAL = "MUTUAL"; Investment-Fonds
    // - TYPE_CURRENCY = "CURRENCY";
    // - TYPE_INCOME = "INCOME"; "Umsatzerloese 16% USt"
    // - TYPE_EXPENSE = "EXPENSE"; "private Ausgaben"
    // - TYPE_EQUITY = "EQUITY"; "Anfangsbestand"
    // - TYPE_RECEIVABLE = "RECEIVABLE"; "Forderungen aus Lieferungen und
    // Leistungen"
    // - TYPE_PAYABLE = "PAYABLE"; "Verbindlichkeiten ggueber Lieferant xyz"
    // - TYPE_ROOT = "ROOT"; guess ;-)
    // - TYPE_TRADING = "TRADING";

    public enum Type {
    	BANK,
    	CASH,
    	CREDIT,
    	ASSET,
    	LIABILITY,
    	STOCK,
    	MUTUAL,
    	CURRENCY,
    	INCOME,
    	EXPENSE,
    	EQUITY,
    	RECEIVABLE,
    	PAYABLE,
    	ROOT,
    	TRADING
    }
    
    // -----------------------------------------------------------------
    
    public static String SEPARATOR = ":";

    // -----------------------------------------------------------------

    @SuppressWarnings("exports")
    GncAccount getJwsdpPeer();

    // -----------------------------------------------------------------

    /**
     * @return the unique id for that account (not meaningfull to human users)
     */
    GCshAcctID getID();

    /**
     * @return a user-defined description to acompany the name of the account. Can
     *         encompass many lines.
     */
    String getDescription();

    /**
     * @return the account-number
     */
    String getCode();

    /**
     * @return user-readable name of this account. Does not contain the name of
     *         parent-accounts
     */
    String getName();

    /**
     * get name including the name of the parent.accounts.
     *
     * @return e.g. "Aktiva::test::test2"
     */
    String getQualifiedName();

    // ---------------------------------------------------------------

    /**
     * @return null if the account is below the root
     */
    GCshAcctID getParentAccountID();
    
    /**
     * @return the parent-account we are a child of or null if we are a top-level
     *         account
     */
    GnuCashAccount getParentAccount();

    boolean isRootAccount();

    // ----------------------------

    /**
     * The returned collection is never null and is sorted by Account-Name.
     *
     * @return all child-accounts (only one level, no grand-children etc.)
     * 
     * @see #getChildrenRecursive()
     * @see #isChildAccountRecursive(GnuCashAccount)
     */
    List<GnuCashAccount> getChildren();

    /**
     * 
     * @return all child accounts including their children, grand-children etc.
     * 
     * @see #getChildren()
     * @see #isChildAccountRecursive(GnuCashAccount)
     */
    List<GnuCashAccount> getChildrenRecursive();

    /**
     * @param account the account to test
     * 
     * @return true if this is a child of us or any child's or us.
     * 
     * @see #getChildren()
     * #see #getChildrenRecursive()
     */
    boolean isChildAccountRecursive(GnuCashAccount account);

    // ----------------------------

    /**
     * 
     * @return
     */
    Type getType();

    /**
     * 
     * @return commodity/currency ID
     */
    GCshCmdtyCurrID getCmdtyCurrID();

    // -----------------------------------------------------------------

    /**
     * Gets the last transaction-split before the given date.
     *
     * @param date if null, the last split of all time is returned
     * @return the last transaction-split before the given date
     */
    GnuCashTransactionSplit getLastSplitBeforeRecursive(LocalDate date);

    // ----------------------------

    /**
     * @return true if ${@link #hasTransactions()} is true for this or any
     *         sub-accounts
     */
    boolean hasTransactionsRecursive();

    // -----------------------------------------------------------------

    /**
     * @param lot 
     * @param split split to add to this transaction
     */
    void addLot(GCshAcctLot lot);

    /**
     * @return true if ${@link #getTransactionSplits()}.size() &gt; 0
     */
    boolean hasLots();

    /**
     * @return all lots
     */
    List<GCshAcctLot> getLots();

    /**
     * @param acctLotID the lot-id to look for
     * @return the identified lot or null
     */
    GCshAcctLot getLotByID(GCshLotID acctLotID);

    // -----------------------------------------------------------------

    /**
     * same as getBalance(new Date()).<br/>
     * ignores transactions after the current date+time<br/>
     * Be aware that the result is in the currency of this account!
     *
     * @return the balance
     */
    FixedPointNumber getBalance();

    BigFraction      getBalanceRat();
    
    /**
     * Be aware that the result is in the currency of this account!
     *
     * @param date if non-null transactions after this date are ignored in the
     *             calculation
     * @return the balance formatted using the current locale
     */
    FixedPointNumber getBalance(LocalDate date);

    BigFraction      getBalanceRat(LocalDate date);

    /**
     * Be aware that the result is in the currency of this account!
     *
     * @param date  if non-null transactions after this date are ignored in the
     *              calculation
     * @param after splits that are after date are added here.
     * @return the balance formatted using the current locale
     */
    FixedPointNumber getBalance(LocalDate date, List<GnuCashTransactionSplit> after);

    BigFraction      getBalanceRat(LocalDate date, List<GnuCashTransactionSplit> after);

	FixedPointNumber getBalance(LocalDate date, GCshCmdtyCurrID cmdtyCurrID);
	
	BigFraction      getBalanceRat(LocalDate date, GCshCmdtyCurrID cmdtyCurrID);
	
	FixedPointNumber getBalance(LocalDate date, Currency currency);
	
	BigFraction      getBalanceRat(LocalDate date, Currency currency);
	
    /**
     * @param lastIncludesSplit last split to be included
     * @return the balance up to and including the given split
     */
    FixedPointNumber getBalance(GnuCashTransactionSplit lastIncludesSplit);

    // ----------------------------

    /**
     * same as getBalance(new Date()). ignores transactions after the current
     * date+time
     *
     * @return the balance formatted using the current locale
     */
    String getBalanceFormatted();

    /**
     * same as getBalance(new Date()). ignores transactions after the current
     * date+time
     *
     * @param lcl the locale to use (does not affect the currency)
     * @return the balance formatted using the given locale
     */
    String getBalanceFormatted(Locale lcl);

    // ----------------------------

    /**
     * same as getBalanceRecursive(new Date()).<br/>
     * ignores transactions after the current date+time<br/>
     * Be aware that the result is in the currency of this account!
     *
     * @return the balance including sub-accounts
     */
    FixedPointNumber getBalanceRecursive();

    BigFraction      getBalanceRecursiveRat();

    /**
     * Gets the balance including all sub-accounts.
     *
     * @param date if non-null transactions after this date are ignored in the
     *             calculation
     * @return the balance including all sub-accounts
     */
    FixedPointNumber getBalanceRecursive(LocalDate date);

    BigFraction      getBalanceRecursiveRat(LocalDate date);

    /**
     * Ignores accounts for which this conversion is not possible.
     *
     * @param date     ignores transactions after the given date
     * @param curr 
     * @return Gets the balance including all sub-accounts.
     * @see GnuCashAccount#getBalanceRecursive(LocalDate)
     */
    FixedPointNumber getBalanceRecursive(LocalDate date, Currency curr);

    BigFraction      getBalanceRecursiveRat(LocalDate date, Currency curr);

    /**
     * Ignores accounts for which this conversion is not possible.
     *
     * @param date              ignores transactions after the given date
     * @param secCurrID 
     * @return Gets the balance including all sub-accounts.
     * @see GnuCashAccount#getBalanceRecursive(LocalDate)
     */
    FixedPointNumber getBalanceRecursive(LocalDate date, GCshCmdtyCurrID secCurrID);

    BigFraction      getBalanceRecursiveRat(LocalDate date, GCshCmdtyCurrID secCurrID);

    // ----------------------------

    /**
     * same as getBalanceRecursive(new Date()). ignores transactions after the
     * current date+time
     *
     * @return the balance including sub-accounts formatted using the current locale
     */
    String getBalanceRecursiveFormatted();

    String getBalanceRecursiveFormatted(Locale lcl);

    // ---------------------------------------------------------------
    
    GCshAcctReconInfo getReconcileInfo();

    // ---------------------------------------------------------------

    void printTree(StringBuilder buffer, String prefix, String childrenPrefix);
}
