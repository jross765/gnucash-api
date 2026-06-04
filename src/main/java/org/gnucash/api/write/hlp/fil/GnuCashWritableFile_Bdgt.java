package org.gnucash.api.write.hlp.fil;

import java.util.List;

import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.write.GnuCashWritableBudget;
import org.gnucash.api.write.ObjectCascadeException;
import org.gnucash.base.basetypes.simple.GCshBdgtID;

public interface GnuCashWritableFile_Bdgt {

    GnuCashWritableBudget getWritableBudgetByID(GCshBdgtID bdgtID);

    /**
     * @see GnuCashFile#getBudgets()
     * @return writable versions of all transactions in the book.
     */
    List<GnuCashWritableBudget> getWritableBudgets();

    // ----------------------------

    /**
     * @return a new budget with no splits that is already added to this file
     * 
     */
    GnuCashWritableBudget createWritableBudget(String name);

    /**
     *
     * @param impl the budget to remove.
     * 
     */
    void removeBudget(GnuCashWritableBudget bdgt) throws ObjectCascadeException;

}
