package org.gnucash.api.read.spec;

import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.base.basetypes.simple.GCshVendID;

/**
 * A {@link GnuCashGenerJob} that belongs to a {@link GnuCashVendor}
 * <br>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-ap-jobs1.html">GnuCash manual</a>
 * 
 * @see GnuCashCustomerJob
 */
public interface GnuCashVendorJob extends GnuCashGenerJob {

	/**
	 *
	 * @return the vendor this job is from.
	 * 
	 * @see #getVendorID()
	 */
	GnuCashVendor getVendor();

	/**
	 *
	 * @return the id of the vendor this job is from.
	 * 
	 * @see #getVendor()
	 */
	GCshVendID getVendorID();
	
}
