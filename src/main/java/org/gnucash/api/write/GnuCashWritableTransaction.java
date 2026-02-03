package org.gnucash.api.write;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableAttachment;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.simple.GCshSpltID;

/**
 * Transaction that can be modified.<br/>
 * For PropertyChange-Listeners we support the properties:
 * "description" and "splits".
 * 
 * @see GnuCashTransaction
 */
public interface GnuCashWritableTransaction extends GnuCashTransaction,
													GnuCashWritableObject,
													HasWritableAttachment,
													HasWritableUserDefinedAttributes
{

    /**
     * @param cmdtyCurrID 
     * @param id the new currency

     * @see #getCmdtyCurrID()
     */
    void setCmdtyCurrID(GCshCmdtyID cmdtyCurrID);

    /**
     * The GnuCash file is the top-level class to contain everything.
     * 
     * @return the file we are associated with
     * 
     * @see #getGnuCashFile()
     */
    GnuCashWritableFile getWritableFile();

    /**
     * @param dateEntered the day (time is ignored) that this transaction has been
     *                    entered into the system
     *              
     * @see #getDateEntered()
     * @see #setDatePosted(LocalDate)
     */
    void setDateEntered(LocalDateTime dateEntered); // sic, not LocalDate

    /**
     * @param datePosted the day (time is ignored) that the money was transfered
     * 
     * @see #getDatePosted()
     * @see #setDateEntered(LocalDateTime)
     */
    void setDatePosted(LocalDate datePosted);

    /**
     * 
     * @param desc
     * 
     * @see #getDescription()
     */
    void setDescription(String desc);

    /**
     * 
     * @param string
     * 
     * @see #getNumber()
     */
    void setNumber(String string);

    /**
     *  
     * @param spltID 
     * @return
     * 
     * @see #getSplitByID(GCshSpltID)
     */
    GnuCashWritableTransactionSplit getWritableSplitByID(GCshSpltID spltID);

    /**
     *  
     * @return
     *  
     * @see #getSplits()
     */
    List<GnuCashWritableTransactionSplit> getWritableSplits();

    /**
     * Create a new split, already attached to this transaction.
     * 
     * @param account the account for the new split
     * @return a new split, already attached to this transaction
     *  
     */
    GnuCashWritableTransactionSplit createWritableSplit(GnuCashAccount account);

    /**
     * Removes the given split from this transaction.
     * 
     * @param impl the split to be removed from this transaction
     *  
     */
    void remove(GnuCashWritableTransactionSplit impl);

    /**
     * remove this transaction.
     *  
     */
    void remove();

}
