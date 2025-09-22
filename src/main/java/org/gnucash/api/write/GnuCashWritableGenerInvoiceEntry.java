package org.gnucash.api.write;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.write.hlp.GnuCashWritableGenerInvoiceEntry_Cust;
import org.gnucash.api.write.hlp.GnuCashWritableGenerInvoiceEntry_Empl;
import org.gnucash.api.write.hlp.GnuCashWritableGenerInvoiceEntry_Job;
import org.gnucash.api.write.hlp.GnuCashWritableGenerInvoiceEntry_Vend;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Invoice-Entry that can be modified.
 * 
 * @see GnuCashGenerInvoiceEntry
 */
public interface GnuCashWritableGenerInvoiceEntry extends GnuCashGenerInvoiceEntry,
                                                          GnuCashWritableGenerInvoiceEntry_Cust,
                                                          GnuCashWritableGenerInvoiceEntry_Vend,
                                                          GnuCashWritableGenerInvoiceEntry_Empl,
                                                          GnuCashWritableGenerInvoiceEntry_Job,
                                                          GnuCashWritableObject,
                                                          HasWritableUserDefinedAttributes
{

    /**
     * @see GnuCashGenerInvoiceEntry#getGenerInvoice() .
     */
    GnuCashWritableGenerInvoice getGenerInvoice();

    /**
     * 
     * @param date
     * 
     * @see #getDate()
     */
    void setDate(LocalDate date);

    /**
     * Set the description-text.
     *
     * @param desc the new description
     * 
     * @see #getDescription()
     */
    void setDescription(String desc);

    // ---------------------------------------------------------------

    /**
	 * Wrapper for {@link #setActionStr(String)}.
	 * 
     * @param act
     * 
     * @see #getAction()
     * @see #setActionStr(String)
     */
    void setAction(Action act);
    
    /**
     * <b>Using this method is discouraged.</b>
     * Use {@link #setAction(org.gnucash.api.read.GnuCashGenerInvoiceEntry.Action)}
     * whenever possible/applicable instead.
     * <br>
     * Cf. the comment in {@link #getActionStr()} and {@link #getAction()} for the reason
     * (and why we still have this method in the interface).
     * 
     * @param act
     * 
     * @see #getActionStr()
     * @see #setAction(org.gnucash.api.read.GnuCashGenerInvoiceEntry.Action)
     */
    void setActionStr(String act);

    /**
     * 
     * @param quantity
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     * 
     * @see #getQuantity()
     * @see #setQuantity(FixedPointNumber)
     * @see #setQuantityFormatted(String)
     */
    void setQuantity(String quantity)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * 
     * @param quantity
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     * 
     * @see #getQuantity()
     * @see #setQuantity(String)
     * @see #setQuantityFormatted(String)
     */
    void setQuantity(FixedPointNumber quantity)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * 
     * @param n
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     * 
     * @see #getQuantity()
     * @see #setQuantity(FixedPointNumber)
     * @see #setQuantity(String)
     */
    void setQuantityFormatted(String n)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    void remove() throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

}
