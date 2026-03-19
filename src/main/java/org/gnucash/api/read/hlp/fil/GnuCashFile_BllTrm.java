package org.gnucash.api.read.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.base.basetypes.simple.aux.GCshBllTrmID;

/**
 * Interface of a top-level class that gives access to a GnuCash file
 * with all its accounts, transactions, etc.
 */
public interface GnuCashFile_BllTrm {

    /**
     * @param bllTrmID id of a tax table
     * @return the identified bill terms or null
     */
    GCshBillTerms getBillTermsByID(GCshBllTrmID bllTrmID);

    /**
     * @param name 
     * @param id name of a bill term
     * @return the identified bill term or null
     */
    GCshBillTerms getBillTermsByName(String name);

    /**
     * @return all bill terms defined in the book
     * @link GnuCashBillTerms
     */
    Collection<GCshBillTerms> getBillTerms();

}
