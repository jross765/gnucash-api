package org.gnucash.api.read.spec.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashFile_Job_Vend {

    /**
     * @param vendID the unique ID of the vendor job to look for
     * @return the job or null if it's not found
     */
    GnuCashVendorJob getVendorJobByID(GCshGenerJobID vendID);

    /**
     * @param expr search expression
     * @return
     */
    Collection<GnuCashVendorJob> getVendorJobsByName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    Collection<GnuCashVendorJob> getVendorJobsByName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashVendorJob getVendorJobByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all vendor jobs Do not modify the
     *         returned collection!
     */
    Collection<GnuCashVendorJob> getVendorJobs();

}
