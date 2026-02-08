package org.gnucash.api.write;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.write.aux.GCshWritableAccountLot;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Account that can be modified.
 * 
 * @see GnuCashAccount
 */
public interface GnuCashWritableAccount extends GnuCashAccount, 
                                                GnuCashWritableObject,
                                                HasWritableUserDefinedAttributes
{

    /**
	 * The GnuCash file is the top-level class to contain everything.
	 *
     * @return the file we belong to
     */
    GnuCashWritableFile getWritableGnuCashFile();

    /**
     * Change the user-definable name. It should contain no newlines but may contain
     * non-ascii and non-western characters.
     *
     * @param name the new name (not null)
	 * 
	 * @see #getName()
     */
    void setName(String name);

    /**
     * Change the user-definable account-number. It should contain no newlines but
     * may contain non-ascii and non-western characters.
     *
     * @param code the new code (not null)
     */
    void setAccountCode(String code);

    /**
     * @param desc the user-defined description (may contain multiple lines and
     *             non-ascii-characters)
     *             
     * @see #getDescription()
     */
    void setDescription(String desc);

    /**
     * Get the sum of all transaction-splits affecting this account in the given
     * time-frame.
     *
     * @param from when to start, inclusive
     * @param to   when to stop, exclusive.
     * @return the sum of all transaction-splits affecting this account in the given
     *         time-frame.
     * 
	 * @see #getBalanceChange(LocalDate, LocalDate)
     */
    FixedPointNumber getBalanceChange(LocalDate from, LocalDate to);

    BigFraction      getBalanceChangeRat(LocalDate from, LocalDate to);

    /**
     * Set the type of the account (income, ...).
     *
     * @param type the new type.
     * 
     * @see #getType()
     */
    void setType(Type type);

	// ----------------------------

    /**
     * @param cmdtyID 
     * @param id the new currency
     * 
     * @see #getCmdtyID()
     */
    void setCmdtyID(GCshCmdtyID cmdtyID);

    /**
     * @param newPrnt the new account or null to make it a top-level-account
     * 
     * @see #getParentAccount()
     */
    void setParentAccount(GnuCashAccount newPrnt);

    /**
     * If the accountId is invalid, make this a top-level-account.
     * @param newPrntID 
     *
     * @see #getParentAccountID()
     */
    void setParentAccountID(GCshAcctID newPrntID);
    
    // ---------------------------------------------------------------

    /**
     * @param spltID 
     * @return 
     *  
     * @see #getTransactionSplitByID(GCshID)
     */
    GnuCashWritableTransactionSplit getWritableTransactionSplitByID(GCshSpltID spltID);

    /**
     * @return 
     * 
     * @see #getTransactionSplits()
     */
    List<GnuCashWritableTransactionSplit> getWritableTransactionSplits();

    /**
     * Create a new split, already attached to this transaction.
     * 
     * @param account the account for the new split
     * @return a new split, already attached to this transaction
     *  
     */
//    GnuCashWritableTransactionSplit createWritableTransactionSplit();

    // ---------------------------------------------------------------

    /**
     *  
     * @param lotID 
     * @return 
     * 
     * @see #getLotByID(GCshLotID)
     * @see GnuCashAccount#getLotByID(GCshLotID)
     */
    GCshWritableAccountLot getWritableLotByID(GCshLotID lotID);

    /**
     *  
     * @return 
     * 
     * @see #getLots()
     * @see GnuCashAccount#getLots()
     */
    List<GCshWritableAccountLot> getWritableLots();

    /**
     * Create a new split, already attached to this transaction.
     * 
     * @param account the account for the new split
     * @return a new split, already attached to this transaction
     *  
     */
    GCshWritableAccountLot createWritableLot();

    // ---------------------------------------------------------------

    /**
     * Removes the given lot from this account.
     * @param lot 
     * 
     * @param impl the lot to be removed from this account
     *  
     */
    void removeLot(GCshWritableAccountLot lot);

    // ---------------------------------------------------------------
    
    void setHidden();
    
    void unsetHidden();

    /**
     * Remove this account from the system.<br/>
     * Throws IllegalStateException if this account has splits or children.
     */
    void remove();

}
