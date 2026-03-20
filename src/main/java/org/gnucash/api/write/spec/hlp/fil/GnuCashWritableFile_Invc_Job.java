package org.gnucash.api.write.spec.hlp.fil;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

public interface GnuCashWritableFile_Invc_Job {

    /**
     * FOR USE BY EXTENSIONS ONLY
     * @param invoiceNumber 
     * @param job 
     * @param incExpAcct 
     * @param recvblPayblAcct 
     * @param openedDate 
     * @param postDate 
     * @param dueDate 
     * 
     * @return a new invoice with no entries that is already added to this file
*  
     * @throws WrongOwnerTypeException 
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableJobInvoice createWritableJobInvoice(
	    String invoiceNumber, 
	    GnuCashGenerJob job,
	    GnuCashAccount incExpAcct, 
	    GnuCashAccount recvblPayblAcct, 
	    LocalDate openedDate,
	    LocalDate postDate, 
	    LocalDate dueDate)
	    throws WrongOwnerTypeException,
	    IllegalTransactionSplitActionException;

    void removeJobInvoice(GnuCashWritableJobInvoice invc, boolean withEntries);

}
