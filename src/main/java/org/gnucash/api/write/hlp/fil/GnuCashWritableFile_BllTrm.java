package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.write.aux.GCshWritableBillTerms;
import org.gnucash.base.basetypes.simple.aux.GCshBllTrmID;

public interface GnuCashWritableFile_BllTrm {

    GCshWritableBillTerms getWritableBillTermsByID(GCshBllTrmID bllTrmID);

    GCshWritableBillTerms getWritableBillTermsByName(String name);

    /**
     * @see GnuCashFile#getBillTerms()
     * @return writable versions of all bill terms in the book.
     */
    Collection<GCshWritableBillTerms> getWritableBillTerms();

}
