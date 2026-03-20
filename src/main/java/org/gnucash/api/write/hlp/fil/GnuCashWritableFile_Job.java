package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.write.GnuCashWritableGenerJob;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerJobImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorJobImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerJob;
import org.gnucash.api.write.spec.GnuCashWritableVendorJob;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;

public interface GnuCashWritableFile_Job {

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

    /**
     * @param cust 
     * @param number 
     * @param name 
     * @return a new customer job with no values that is already added to this file
     */
    GnuCashWritableCustomerJob createWritableCustomerJob(
	    GnuCashCustomer cust, 
	    String number,
	    String name);

    /**
     * @param vend 
     * @param number 
     * @param name 
     * @return a new vendor job with no values that is already added to this file
     */
    GnuCashWritableVendorJob createWritableVendorJob(
	    GnuCashVendor vend, 
	    String number, 
	    String name);

    void removeGenerJob(GnuCashWritableGenerJob job);

    void removeCustomerJob(GnuCashWritableCustomerJobImpl job);

    void removeVendorJob(GnuCashWritableVendorJobImpl job);

}
