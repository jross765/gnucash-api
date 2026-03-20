package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.write.aux.GCshWritableTaxTable;
import org.gnucash.base.basetypes.simple.aux.GCshTaxTabID;

public interface GnuCashWritableFile_TaxTab {

    GCshWritableTaxTable getWritableTaxTableByID(GCshTaxTabID taxTabID);

    GCshWritableTaxTable getWritableTaxTableByName(String name);

    /**
     * @see GnuCashFile#getTaxTables()
     * @return writable versions of all tax tables in the book.
     */
    Collection<GCshWritableTaxTable> getWritableTaxTables();

}
