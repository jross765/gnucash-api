package org.gnucash.api.write;

import java.time.LocalDate;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.api.write.hlp.invc.GnuCashWritableGenerInvoiceEntry_Cust_BF;
import org.gnucash.api.write.hlp.invc.GnuCashWritableGenerInvoiceEntry_Cust_FP;
import org.gnucash.api.write.hlp.invc.GnuCashWritableGenerInvoiceEntry_Cust_Rest;
import org.gnucash.api.write.hlp.invc.GnuCashWritableGenerInvoiceEntry_Empl_BF;
import org.gnucash.api.write.hlp.invc.GnuCashWritableGenerInvoiceEntry_Empl_FP;
import org.gnucash.api.write.hlp.invc.GnuCashWritableGenerInvoiceEntry_Empl_Rest;
import org.gnucash.api.write.hlp.invc.GnuCashWritableGenerInvoiceEntry_Job_BF;
import org.gnucash.api.write.hlp.invc.GnuCashWritableGenerInvoiceEntry_Job_FP;
import org.gnucash.api.write.hlp.invc.GnuCashWritableGenerInvoiceEntry_Job_Rest;
import org.gnucash.api.write.hlp.invc.GnuCashWritableGenerInvoiceEntry_Vend_BF;
import org.gnucash.api.write.hlp.invc.GnuCashWritableGenerInvoiceEntry_Vend_FP;
import org.gnucash.api.write.hlp.invc.GnuCashWritableGenerInvoiceEntry_Vend_Rest;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Invoice-Entry that can be modified.
 * 
 * @see GnuCashGenerInvoiceEntry
 */
public interface GnuCashWritableGenerInvoiceEntry extends GnuCashGenerInvoiceEntry,
                                                          GnuCashWritableGenerInvoiceEntry_Cust_FP,
                                                          GnuCashWritableGenerInvoiceEntry_Cust_BF,
                                                          GnuCashWritableGenerInvoiceEntry_Cust_Rest,
                                                          GnuCashWritableGenerInvoiceEntry_Vend_FP,
                                                          GnuCashWritableGenerInvoiceEntry_Vend_BF,
                                                          GnuCashWritableGenerInvoiceEntry_Vend_Rest,
                                                          GnuCashWritableGenerInvoiceEntry_Empl_FP,
                                                          GnuCashWritableGenerInvoiceEntry_Empl_BF,
                                                          GnuCashWritableGenerInvoiceEntry_Empl_Rest,
                                                          GnuCashWritableGenerInvoiceEntry_Job_FP,
                                                          GnuCashWritableGenerInvoiceEntry_Job_BF,
                                                          GnuCashWritableGenerInvoiceEntry_Job_Rest,
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
     * @see #setQuantityRat(BigFraction)
     */
    void setQuantity(FixedPointNumber quantity)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * 
     * @param quantity
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     * 
     * @see #getQuantity()
     * @see #setQuantity(FixedPointNumber)
     */
    void setQuantityRat(BigFraction quantity)
    	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    void remove() throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

}
