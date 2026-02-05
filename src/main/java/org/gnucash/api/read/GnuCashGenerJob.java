package org.gnucash.api.read;

import java.util.List;

import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.GncGncJob.JobOwner;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.hlp.GnuCashGenerJob_Invc_FP;
import org.gnucash.api.read.hlp.GnuCashGenerJob_Invc_Str;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;
import org.gnucash.base.basetypes.simple.GCshID;

/**
 * This class represents a generic job.
 * <br>
 * Please note: In GnuCash lingo, a "job" is a technical umbrella term comprising:
 * <ul>
 *   <li>a customer job ({@link GnuCashCustomerJob})</li>
 *   <li>a vendor job   ({@link GnuCashVendorJob})</li>
 * </ul>
 * This is the reason why here, we call the invoice "generic" in order to avoid
 * misunderstandings. 
 * <br>
 * It normally should be avoided to use it directly; instead, use one of its 
 * specialized variants.
 *  
 * @see GnuCashCustomerJob
 * @see GnuCashVendorJob
 */
public interface GnuCashGenerJob extends GnuCashObject,
                                         GnuCashGenerJob_Invc_FP,
                                         GnuCashGenerJob_Invc_Str
{

    public static final GCshOwner.Type TYPE_CUSTOMER = GCshOwner.Type.CUSTOMER;
    public static final GCshOwner.Type TYPE_VENDOR   = GCshOwner.Type.VENDOR;

    // -----------------------------------------------------------------

    @SuppressWarnings("exports")
    GncGncJob getJwsdpPeer();

    // -----------------------------------------------------------------

    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshGenerJobID getID();

    GCshOwner.Type getType();
    
    // -----------------------------------------------------------------

    /**
     * @return true if the job is still active
     */
    boolean isActive();

    /**
     *
     * @return the user-defines number of this job (may contain non-digits)
     */
    String getNumber();

    /**
     *
     * @return the name the user gave to this job.
     */
    String getName();

    // ----------------------------

    /**
     * Not used.
     * 
     * @return CUSTOMETYPE_CUSTOMER
     */
    GCshOwner.Type getOwnerType();

    /**
     *
     * @return the id of the customer this job is from.
     */
    GCshID getOwnerID();
    
    // ---------------------------------------------------------------

    /**
     * Date is not checked so invoiced that have entered payments in the future are
     * considered Paid.
     * 
     * @return the current number of Unpaid invoices
     *  
     */
    int getNofOpenInvoices();

    // ---------------------------------------------------------------

    List<GnuCashJobInvoice> getInvoices();

    List<GnuCashJobInvoice> getPaidInvoices();

    List<GnuCashJobInvoice> getUnpaidInvoices();

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    JobOwner getOwnerPeerObj();

}
