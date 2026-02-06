package org.gnucash.api.write.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

public interface GnuCashWritableGenerInvoiceEntry_Job_BF {

    void setJobInvcPriceRat(BigFraction prc) throws TaxTableNotFoundException,
	    UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

}
