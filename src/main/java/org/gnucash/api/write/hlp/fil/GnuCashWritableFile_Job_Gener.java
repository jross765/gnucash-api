package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.write.GnuCashWritableGenerJob;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;

public interface GnuCashWritableFile_Job_Gener {

    /**
     * @see GnuCashFile#getGenerJobByID(GCshGenerJobID)
     * @param jobID the id of the job to fetch
     * @return A modifiable version of the job or null of not found.
     */
    GnuCashWritableGenerJob getWritableGenerJobByID(GCshGenerJobID jobID);

    /**
     * @param jnr the job-number to look for.
     * @return the (first) jobs that have this number or null if not found
     */
    GnuCashWritableGenerJob getWritableGenerJobByNumber(String jnr);

    /**
     * @return all jobs as writable versions.
     */
    Collection<GnuCashWritableGenerJob> getWritableGenerJobs();

    // ----------------------------

    void removeGenerJob(GnuCashWritableGenerJob job);

}
