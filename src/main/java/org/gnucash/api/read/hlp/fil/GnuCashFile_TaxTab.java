package org.gnucash.api.read.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.basetypes.simple.aux.GCshTaxTabID;

/**
 * Interface of a top-level class that gives access to a GnuCash file
 * with all its accounts, transactions, etc.
 */
public interface GnuCashFile_TaxTab {

    /**
     * @param taxTabID id of a tax table
     * @return the identified tax table or null
     */
    GCshTaxTable getTaxTableByID(GCshTaxTabID taxTabID);

    /**
     * @param name 
     * @param id name of a tax table
     * @return the identified tax table or null
     */
    GCshTaxTable getTaxTableByName(String name);

    /**
     * @return all tax tables defined in the book
     * @link GnuCashTaxTable
     */
    Collection<GCshTaxTable> getTaxTables();

}
