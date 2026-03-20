package org.gnucash.api.write.spec.hlp.fil;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoice;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

public interface GnuCashWritableFile_Invc_Cust {

    /**
     * FOR USE BY EXTENSIONS ONLY
     * @param invoiceNumber 
     * @param cust 
     * @param incomeAcct 
     * @param receivableAcct 
     * @param openedDate 
     * @param postDate 
     * @param dueDate 
     * 
     * @return a new invoice with no entries that is already added to this file
*  
     * @throws WrongOwnerTypeException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableCustomerInvoice createWritableCustomerInvoice(
	    String invoiceNumber, 
	    GnuCashCustomer cust,
	    GnuCashAccount incomeAcct, 
	    GnuCashAccount receivableAcct, 
	    LocalDate openedDate,
	    LocalDate postDate, 
	    LocalDate dueDate)
	    throws WrongOwnerTypeException,
	    IllegalTransactionSplitActionException;

    void removeCustomerInvoice(GnuCashWritableCustomerInvoice invc, boolean withEntries);

}
