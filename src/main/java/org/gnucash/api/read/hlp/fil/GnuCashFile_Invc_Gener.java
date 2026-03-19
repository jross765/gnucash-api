package org.gnucash.api.read.hlp.fil;

import java.util.List;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;

public interface GnuCashFile_Invc_Gener {

    /**
     * @param invcID the unique ID of the (generic) invoice to look for
     * @return the invoice or null if it's not found
     * @see #getUnpaidGenerInvoices()
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer)
     */
    GnuCashGenerInvoice getGenerInvoiceByID(GCshGenerInvcID invcID);

    /**
     * 
     * @param type
     * @return
     */
    List<GnuCashGenerInvoice> getGenerInvoicesByType(GCshOwner.Type type);

    /**
     * @return a (possibly read-only) collection of all invoices Do not modify the
     *         returned collection!
     * @see #getUnpaidGenerInvoices()
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer)
     */
    List<GnuCashGenerInvoice> getGenerInvoices();

    // ----------------------------

    /**
     * @return a (possibly read-only) collection of all invoices that are fully Paid
     *         Do not modify the returned collection!
     *  
     * @see #getUnpaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer)
     */
    List<GnuCashGenerInvoice> getPaidGenerInvoices();

    /**
     * @return a (possibly read-only) collection of all invoices that are not fully
     *         Paid Do not modify the returned collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer)
     */
    List<GnuCashGenerInvoice> getUnpaidGenerInvoices();

}
