package org.gnucash.api.read.hlp;

import java.time.LocalDate;
import java.util.List;

import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.base.basetypes.simple.GCshSpltID;

public interface HasTransactions
{
	
    /**
     * The returned list is sorted by the natural order of the Transaction-Splits.
     *
     * @return all splits
     */
    List<GnuCashTransactionSplit> getTransactionSplits();

    /**
     * @param spltID the split-id to look for
     * @return the identified split or null
     */
    GnuCashTransactionSplit getTransactionSplitByID(GCshSpltID spltID);

    // ----------------------------

    boolean hasTransactions();

    List<GnuCashTransaction> getTransactions();

    List<GnuCashTransaction> getTransactions(LocalDate fromDate, LocalDate toDate);

}
