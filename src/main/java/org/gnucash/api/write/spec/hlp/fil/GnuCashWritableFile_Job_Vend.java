package org.gnucash.api.write.spec.hlp.fil;

import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorJobImpl;
import org.gnucash.api.write.spec.GnuCashWritableVendorJob;

public interface GnuCashWritableFile_Job_Vend {

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

    void removeVendorJob(GnuCashWritableVendorJobImpl job);

}
