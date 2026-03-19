package org.gnucash.api.read.spec.hlp.fil;

import java.util.List;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;

public interface GnuCashFile_Invc_Cust {

    /**
     * @param cust the customer to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have fully
     *         been paid and are from the given customer. Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer)
     */
    List<GnuCashCustomerInvoice> getInvoicesForCustomer_direct(GnuCashCustomer cust);

    /**
     * @param cust the customer to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have fully
     *         been paid and are from the given customer. Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer)
     */
    List<GnuCashJobInvoice>      getInvoicesForCustomer_viaAllJobs(GnuCashCustomer cust);

    /**
     * @param cust the customer to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have fully
     *         been paid and are from the given customer. Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer)
     */
    List<GnuCashCustomerInvoice> getPaidInvoicesForCustomer_direct(GnuCashCustomer cust);

    /**
     * @param cust the customer to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have fully
     *         been paid and are from the given customer. Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer)
     */
    List<GnuCashJobInvoice>      getPaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer cust);

    /**
     * @param cust the customer to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have not fully
     *         been paid and are from the given customer Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer)
     */
    List<GnuCashCustomerInvoice> getUnpaidInvoicesForCustomer_direct(GnuCashCustomer cust);

    /**
     * @param cust the customer to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have not fully
     *         been paid and are from the given customer Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer)
     */
    List<GnuCashJobInvoice>      getUnpaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer cust);

}
