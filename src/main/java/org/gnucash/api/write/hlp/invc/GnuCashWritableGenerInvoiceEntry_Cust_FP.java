package org.gnucash.api.write.hlp.invc;

import org.gnucash.api.read.TaxTableNotFoundException;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoiceEntry_Cust_FP {

//    void setCustInvcPrice(String price)
//	    throws TaxTableNotFoundException,
//	    IllegalTransactionSplitActionException;

    void setCustInvcPrice(FixedPointNumber price)
	    throws TaxTableNotFoundException,
	    IllegalTransactionSplitActionException;

//    void setCustInvcPriceFormatted(String price)
//	    throws TaxTableNotFoundException,
//	    IllegalTransactionSplitActionException;

}
