package org.gnucash.api.read.spec.hlp.fil;

import java.util.List;

import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;

public interface GnuCashFile_Invc_Job {

    /**
     * @param job the job to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have fully
     *         been paid and are from the given job Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getPaidInvoicesForJob(GnuCashGenerJob)
     * @see #getUnpaidInvoicesForJob(GnuCashGenerJob)
     */
    List<GnuCashJobInvoice>      getInvoicesForJob(GnuCashGenerJob job);

    /**
     * @param job the job to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have fully
     *         been paid and are from the given job Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidInvoicesForJob(GnuCashGenerJob)
     */

    List<GnuCashJobInvoice>      getPaidInvoicesForJob(GnuCashGenerJob job);

    /**
     * @param job the job to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have not fully
     *         been paid and are from the given job Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getPaidInvoicesForJob(GnuCashGenerJob)
     */
    List<GnuCashJobInvoice>      getUnpaidInvoicesForJob(GnuCashGenerJob job);

}
