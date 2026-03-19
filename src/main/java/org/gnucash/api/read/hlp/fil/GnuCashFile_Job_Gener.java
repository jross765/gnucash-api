package org.gnucash.api.read.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashFile_Job_Gener {

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

}
