package org.gnucash.api.read.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.base.basetypes.simple.GCshVendID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashFile_Vend {

    /**
     * @param vendID the unique ID of the vendor to look for
     * @return the vendor or null if it's not found
     */
    GnuCashVendor getVendorByID(GCshVendID vendID);

    /**
     * warning: this function has to traverse all vendors. It is much faster to try
     * getVendorByID first and to call this method only if the returned account
     * does not have the right name.
     * 
     * @param expr  search expression
     * @return null if not found
     * @see #getVendorByID(GCshVendID)
     */
    Collection<GnuCashVendor> getVendorsByName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    Collection<GnuCashVendor> getVendorsByName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashVendor getVendorByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all vendors Do not modify the
     *         returned collection!
     */
    Collection<GnuCashVendor> getVendors();

}
