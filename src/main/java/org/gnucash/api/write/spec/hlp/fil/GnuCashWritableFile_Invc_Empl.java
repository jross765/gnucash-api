package org.gnucash.api.write.spec.hlp.fil;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

public interface GnuCashWritableFile_Invc_Empl {

    /**
     * FOR USE BY EXTENSIONS ONLY
     * @param invoiceNumber 
     * @param empl 
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
    GnuCashWritableEmployeeVoucher createWritableEmployeeVoucher(
	    String invoiceNumber, 
	    GnuCashEmployee empl,
	    GnuCashAccount expensesAcct, 
	    GnuCashAccount payableAcct, 
	    LocalDate openedDate,
	    LocalDate postDate, 
	    LocalDate dueDate)
	    throws WrongOwnerTypeException,
	    IllegalTransactionSplitActionException;

    void removeEmployeeVoucher(GnuCashWritableEmployeeVoucher vch, boolean withEntries);

}
