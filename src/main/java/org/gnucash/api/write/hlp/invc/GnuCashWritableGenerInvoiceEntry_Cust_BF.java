package org.gnucash.api.write.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.TaxTableNotFoundException;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

public interface GnuCashWritableGenerInvoiceEntry_Cust_BF {

    void setCustInvcPriceRat(BigFraction price)
	    throws TaxTableNotFoundException,
	    IllegalTransactionSplitActionException;

}
