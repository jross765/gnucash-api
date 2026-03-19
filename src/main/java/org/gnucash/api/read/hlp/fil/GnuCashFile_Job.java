package org.gnucash.api.read.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashFile_Job {

    /**
     * @param jobID the unique ID of the job to look for
     * @return the job or null if it's not found
     */
    GnuCashGenerJob getGenerJobByID(GCshGenerJobID jobID);

    /**
     * @param type 
     * @return list of (generic) jobs (ro-objects) of the given 
     * owner type.
     */
    Collection<GnuCashGenerJob> getGenerJobsByType(GCshOwner.Type type);

    /**
     * @param expr
     * @return list of (generic) jobs (ro-objects) whose names
     * match the given expression.
     */
    Collection<GnuCashGenerJob> getGenerJobsByName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return list of (generic) jobs (ro-objects) whose names
     * match the given criteria.
     */
    Collection<GnuCashGenerJob> getGenerJobsByName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashGenerJob getGenerJobByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all jobs Do not modify the
     *         returned collection!
     */
    Collection<GnuCashGenerJob> getGenerJobs();

    // ----------------------------

    /**
     * @param custID the unique ID of the customer job to look for
     * @return the job or null if it's not found
     */
    GnuCashCustomerJob getCustomerJobByID(GCshGenerJobID custID);

    /**
     * @param expr search expression
     * @return
     */
    Collection<GnuCashCustomerJob> getCustomerJobsByName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    Collection<GnuCashCustomerJob> getCustomerJobsByName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashCustomerJob getCustomerJobByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all customer jobs Do not modify the
     *         returned collection!
     */
    Collection<GnuCashCustomerJob> getCustomerJobs();

    // ----------------------------

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
