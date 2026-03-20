package org.gnucash.api.write.spec.hlp.fil;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerJobImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerJob;

public interface GnuCashWritableFile_Job_Cust {

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

    void removeCustomerJob(GnuCashWritableCustomerJobImpl job);

}
