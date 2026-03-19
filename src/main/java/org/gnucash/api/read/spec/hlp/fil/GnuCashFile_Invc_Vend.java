package org.gnucash.api.read.spec.hlp.fil;

import java.util.List;

import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;

public interface GnuCashFile_Invc_Vend {

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

}
