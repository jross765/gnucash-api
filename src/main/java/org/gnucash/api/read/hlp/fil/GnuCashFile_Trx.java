package org.gnucash.api.read.hlp.fil;

import java.time.LocalDate;
import java.util.List;

import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.base.basetypes.simple.GCshTrxID;

public interface GnuCashFile_Trx {

    /**
     * @param trxID the unique ID of the transaction to look for
     * @return the transaction or null if it's not found
     */
    GnuCashTransaction getTransactionByID(GCshTrxID trxID);

    /**
     * @return a (possibly read-only) collection of all transactions Do not modify
     *         the returned collection!
     * 
     * @see #getTransactions(LocalDate, LocalDate)
     */
    List<? extends GnuCashTransaction> getTransactions();

    /**
     * 
     * @param fromDate
     * @param toDate
     * @return
     * 
     * @see #getTransactions()
     */
    List<? extends GnuCashTransaction> getTransactions(LocalDate fromDate, LocalDate toDate);

}
