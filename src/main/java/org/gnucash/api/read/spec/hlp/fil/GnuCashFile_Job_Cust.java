package org.gnucash.api.read.spec.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashFile_Job_Cust {

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

}
