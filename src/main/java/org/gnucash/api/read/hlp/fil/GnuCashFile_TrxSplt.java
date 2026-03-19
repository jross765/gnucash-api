package org.gnucash.api.read.hlp.fil;

import java.util.List;

import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;

public interface GnuCashFile_TrxSplt {

    /**
     * @param spltID the unique ID of the transaction split to look for
     * @return the transaction split or null if it's not found
     * 
     * @see #getTransactionSplits()
     */
    GnuCashTransactionSplit getTransactionSplitByID(GCshSpltID spltID);

    GnuCashTransactionSplit getTransactionSplitByAcctIDAndTrxID(GCshAcctID acctID, GCshTrxID trxID);

    /**
     * @return list of all transaction splits (ro-objects)
     */
    List<GnuCashTransactionSplit> getTransactionSplits();

    /**
     * @param acctLotID 
     * @return list of all transaction splits (ro-objects)
     *   referencing the given account lot ID (not account ID!).
     */
    List<GnuCashTransactionSplit> getTransactionSplitsByAccountLotID(GCshLotID acctLotID);

    /**
     * @param cmdtyID 
     * @return list of all transaction splits (ro-objects)
     *   denominated in the given commodity. 
     */
    List<GnuCashTransactionSplit> getTransactionSplitsByCmdtyID(GCshCmdtyID cmdtyID);

}
