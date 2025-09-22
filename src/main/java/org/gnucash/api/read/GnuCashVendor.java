package org.gnucash.api.read;

import java.util.List;
import java.util.Locale;

import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.read.hlp.HasAddress;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.base.basetypes.simple.GCshVendID;
import org.gnucash.base.basetypes.simple.aux.GCshBllTrmID;
import org.gnucash.base.basetypes.simple.aux.GCshTaxTabID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * A vendor that can issue jobs and send bills paid by us
 * (and hopefully pay them).
 * <br>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-ap-vendors1.html">GnuCash manual</a>
 *
 * @see GnuCashVendorJob
 * @see GnuCashVendorBill
 * 
 * @see GnuCashCustomer
 * @see GnuCashEmployee
 */
public interface GnuCashVendor extends GnuCashObject,
									   HasUserDefinedAttributes,
									   HasAddress
{
    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshVendID getID();

    /**
     * @return Returns the user-assigned number of this vendor (may contain non-digits)
     */
    String getNumber();

    /**
     * @return Returns the name of the vendor
     */
    String getName();

//    /**
//     * @return Returns the address of this vendor including its name
//     */
//    GCshAddress getAddress();

    /**
     * @return user-defined notes about the vendor (may be null)
     */
    String getNotes();

    // ------------------------------------------------------------

    /**
     * @return 
     * @returns Returns the ID of the default tax table to use with this vendor (may be null). 
     * 
     * @see #getTaxTable()
     */
    GCshTaxTabID getTaxTableID();

    /**
     * @returns Returns The default tax table to use with this vendor (may be null). 
     * 
     * @see #getTaxTableID()
     */
    @SuppressWarnings("javadoc")
    GCshTaxTable getTaxTable();

    // ------------------------------------------------------------

    /**
     * @return Returns the id of the default terms to use with this vendor (may be null).t
     * 
     * @see #getTaxTable()
     */
    GCshBllTrmID getTermsID();

    /**
     * @return Returns the default terms to use with this vendor (may be null). 
     * 
     * @see #getTaxTableID()
     */
    GCshBillTerms getTerms();

    // ------------------------------------------------------------

    /**
     * @return Returns the current number of Unpaid bills.
     *         The date is not checked, so bills that have entered payments in the future are
     *         considered paid.
     */
    int getNofOpenBills();

    // -------------------------------------

    /**
     * @param readVar 
     * @return Returns the sum of payments for invoices to this vendor
     */
    FixedPointNumber getExpensesGenerated(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return Returns the sum of payments for invoices to this vendor
     */
    FixedPointNumber getExpensesGenerated_direct();

    /**
     * @return REturn sthe sum of payments for invoices to this vendor
     */
    FixedPointNumber getExpensesGenerated_viaAllJobs();

    /**
     * @param readVar 
     * @return 
     *  
     * @see #getExpensesGenerated(GnuCashGenerInvoice.ReadVariant readVar) Formatted according to the current locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @param readVar 
     * @param lcl 
     * @return 
     *  
     * @see #getExpensesGenerated(GnuCashGenerInvoice.ReadVariant readVar) Formatted according to the given locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar, Locale lcl);

    // -------------------------------------

    /**
     * @param readVar 
     * @return the sum of left to pay Unpaid invoiced
     */
    FixedPointNumber getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return the sum of left to pay Unpaid invoiced
     *  
     */
    FixedPointNumber getOutstandingValue_direct();

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    FixedPointNumber getOutstandingValue_viaAllJobs();

    /**
     * @param readVar 
     * @return 
     *  
     * @see #getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar) Formatted according to the current locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     *
     * @param readVar 
     * @param lcl 
     * @return 
     *  
     * @see #getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar) Formatted according to the given locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar, Locale lcl);

    // ------------------------------------------------------------

    /**
     * @return Returns the <strong>unmodifiable</strong> collection of jobs that have this 
     *         vendor associated with them.
     */
    List<GnuCashVendorJob> getJobs();

    // ------------------------------------------------------------

    /**
     * @return Returns all bills sent to this vendor, both with
     *         and without job, both paid and unpaid.
     * 
     * @see #getPaidBills_direct()
     * @see #getPaidBills_viaAllJobs()
     */
    List<GnuCashGenerInvoice> getBills();

    /**
     * @return Returns all paid bills sent from this vendor (only those
     *         that were assigned to a job).
     * 
     * @see #getUnpaidBills_direct()
     * @see #getPaidBills_viaAllJobs()
     * @see #getBills()
     */
    List<GnuCashVendorBill>   getPaidBills_direct();

    /**
     * @return Returns all paid invoices sent to this customer (only those
     *         that were assigned directly to that customer, not to a job).
     * 
     * @see #getUnpaidBills_viaAllJobs()
     * @see #getPaidBills_direct()
     * @see #getBills()
     */
    List<GnuCashJobInvoice>   getPaidBills_viaAllJobs();

    /**
     * @return Returns all unpaid bills sent by this vendor (only those
     *         that were assigned to a job).
     * 
     * @see #getPaidBills_direct()
     * @see #getUnpaidBills_viaAllJobs()
     * @see #getBills()
     */
    List<GnuCashVendorBill>   getUnpaidBills_direct();

    /**
     * @return Returns all unpaid bills sent by this vendor (only those
     *         that were assigned directly to that vendor, not to a job).
     * 
     * @see #getPaidBills_viaAllJobs()
     * @see #getUnpaidBills_direct()
     * @see #getBills()
     */
    List<GnuCashJobInvoice>   getUnpaidBills_viaAllJobs();

}
