package org.gnucash.api.write.hlp.invc;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoiceEntry_Job_FP {

    void setJobInvcPrice(FixedPointNumber prc) throws TaxTableNotFoundException,
	    UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

}
