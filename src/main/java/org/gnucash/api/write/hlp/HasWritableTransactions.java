package org.gnucash.api.write.hlp;

import java.time.LocalDate;
import java.util.List;

import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.base.basetypes.simple.GCshSpltID;

public interface HasWritableTransactions
{
	
    List<GnuCashWritableTransactionSplit> getWritableTransactionSplits();

    GnuCashWritableTransactionSplit getWritableTransactionSplitByID(GCshSpltID spltID);
    
    // ---------------------------------------------------------------

    List<GnuCashWritableTransaction> getWritableTransactions();

    List<GnuCashWritableTransaction> getWritableTransactions(LocalDate fromDate, LocalDate toDate);

}
