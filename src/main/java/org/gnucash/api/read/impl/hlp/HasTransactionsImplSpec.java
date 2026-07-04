package org.gnucash.api.read.impl.hlp;

import org.gnucash.api.read.GnuCashTransactionSplit;

// *Internal* interface 
// (as opposed to HasTransactionsImplSpec in package "read.hlp")
public interface HasTransactionsImplSpec
{
	
    void addTransactionSplit(GnuCashTransactionSplit splt);

    void removeTransactionSplit(GnuCashTransactionSplit splt);

    void removeTransactionSplit(GnuCashTransactionSplit splt, boolean strict);

}
