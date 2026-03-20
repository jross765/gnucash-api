package org.gnucash.api.write.spec.hlp.fil;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.write.spec.GnuCashWritableVendorBill;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

public interface GnuCashWritableFile_Invc_Vend {

    /**
     * FOR USE BY EXTENSIONS ONLY
     * @param invoiceNumber 
     * @param vend 
     * @param expensesAcct 
     * @param payableAcct 
     * @param openedDate 
     * @param postDate 
     * @param dueDate 
     * 
     * @return a new invoice with no entries that is already added to this file
*  
     * @throws WrongOwnerTypeException 
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableVendorBill createWritableVendorBill(
	    String invoiceNumber, 
	    GnuCashVendor vend,
	    GnuCashAccount expensesAcct, 
	    GnuCashAccount payableAcct, 
	    LocalDate openedDate,
	    LocalDate postDate, 
	    LocalDate dueDate)
	    throws WrongOwnerTypeException,
	    IllegalTransactionSplitActionException;

    void removeVendorBill(GnuCashWritableVendorBill bll, boolean withEntries);

}
