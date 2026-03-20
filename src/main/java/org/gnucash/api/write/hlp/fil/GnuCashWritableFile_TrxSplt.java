package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.base.basetypes.simple.GCshSpltID;

public interface GnuCashWritableFile_TrxSplt {

    /**
     * @param spltID
     * @return
     * 
     * #see {@link #getTransactionSplitByID(GCshSpltID)}
     */
    GnuCashWritableTransactionSplit getWritableTransactionSplitByID(GCshSpltID spltID);

    /**
     * @return
     * 
     * @see #getTransactionSplits()
     */
    Collection<GnuCashWritableTransactionSplit> getWritableTransactionSplits();

}
