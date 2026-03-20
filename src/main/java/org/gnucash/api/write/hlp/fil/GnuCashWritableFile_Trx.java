package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.base.basetypes.simple.GCshTrxID;

public interface GnuCashWritableFile_Trx {

    GnuCashWritableTransaction getWritableTransactionByID(GCshTrxID trxID);

    /**
     * @see GnuCashFile#getTransactions()
     * @return writable versions of all transactions in the book.
     */
    Collection<? extends GnuCashWritableTransaction> getWritableTransactions();

    // ----------------------------

    /**
     * @return a new transaction with no splits that is already added to this file
     * 
     */
    GnuCashWritableTransaction createWritableTransaction();

    /**
     *
     * @param impl the transaction to remove.
     * 
     */
    void removeTransaction(GnuCashWritableTransaction impl);

}
