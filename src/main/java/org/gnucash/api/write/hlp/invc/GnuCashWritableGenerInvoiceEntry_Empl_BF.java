package org.gnucash.api.write.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.TaxTableNotFoundException;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

public interface GnuCashWritableGenerInvoiceEntry_Empl_BF {

    void setEmplVchPriceRat(BigFraction price)
	    throws TaxTableNotFoundException,
	    IllegalTransactionSplitActionException;

}
