package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.write.GnuCashWritableVendor;
import org.gnucash.base.basetypes.simple.GCshVendID;

public interface GnuCashWritableFile_Vend {

    GnuCashWritableVendor getWritableVendorByID(GCshVendID vendID);

    Collection<GnuCashWritableVendor> getWritableVendors();

    // ----------------------------

    GnuCashWritableVendor createWritableVendor(String name);

    void removeVendor(GnuCashWritableVendor vend);

}
