package org.gnucash.api.read.hlp.fil;

import java.util.List;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;

public interface GnuCashFile_Invc {

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

    // ----------------------------

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

    // ----------------------------

    /**
     * @param vend the vendor to look for (not null)
     * @return a (possibly read-only) collection of all bills that have fully been
     *         paid and are from the given vendor Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getBillsForVendor_viaAllJobs(GnuCashVendor)
     */
    List<GnuCashVendorBill>      getBillsForVendor_direct(GnuCashVendor vend);

    /**
     * @param vend the vendor to look for (not null)
     * @return a (possibly read-only) collection of all bills that have fully been
     *         paid and are from the given vendor Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidBillsForVendor_viaAllJobs(GnuCashVendor)
     */
    List<GnuCashJobInvoice>      getBillsForVendor_viaAllJobs(GnuCashVendor vend);

    /**
     * @param vend the vendor to look for (not null)
     * @return a (possibly read-only) collection of all bills that have fully been
     *         paid and are from the given vendor Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getPaidBillsForVendor_viaAllJobs(GnuCashVendor)
     */
    List<GnuCashVendorBill>      getPaidBillsForVendor_direct(GnuCashVendor vend);

    /**
     * @param vend the vendor to look for (not null)
     * @return a (possibly read-only) collection of all bills that have fully been
     *         paid and are from the given vendor Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidBillsForVendor_viaAllJobs(GnuCashVendor)
     */
    List<GnuCashJobInvoice>      getPaidBillsForVendor_viaAllJobs(GnuCashVendor vend);

    /**
     * @param vend the vendor to look for (not null)
     * @return a (possibly read-only) collection of all bills that have not fully
     *         been paid and are from the given vendor Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidBillsForVendor_viaAllJobs(GnuCashVendor)
     */
    List<GnuCashVendorBill>      getUnpaidBillsForVendor_direct(GnuCashVendor vend);

    /**
     * @param vend the vendor to look for (not null)
     * @return a (possibly read-only) collection of all bills that have not fully
     *         been paid and are from the given vendor Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getPaidBillsForVendor_viaAllJobs(GnuCashVendor)
     */
    List<GnuCashJobInvoice>      getUnpaidBillsForVendor_viaAllJobs(GnuCashVendor vend);

    // ----------------------------

    /**
     * @param empl the employee to look for (not null)
     * @return a (possibly read-only) collection of all vouchers that have fully been
     *         paid and are from the given employee Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getPaidVouchersForEmployee(GnuCashEmployee)
     * @see #getUnpaidVouchersForEmployee(GnuCashEmployee)
     */
    List<GnuCashEmployeeVoucher> getVouchersForEmployee(GnuCashEmployee empl);

    /**
     * @param empl the employee to look for (not null)
     * @return a (possibly read-only) collection of all vouchers that have fully been
     *         paid and are from the given employee Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidVouchersForEmployee(GnuCashEmployee)
     */
    List<GnuCashEmployeeVoucher> getPaidVouchersForEmployee(GnuCashEmployee empl);

    /**
     * @param empl the employee to look for (not null)
     * @return a (possibly read-only) collection of all vouchers that have not fully
     *         been paid and are from the given employee Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getPaidVouchersForEmployee(GnuCashEmployee)
     */
    List<GnuCashEmployeeVoucher> getUnpaidVouchersForEmployee(GnuCashEmployee empl);

    // ----------------------------

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
