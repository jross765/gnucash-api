package org.gnucash.api.write.hlp.fil;

import java.time.LocalDate;
import java.util.Collection;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableJobInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorBillImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoiceEntry;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucherEntry;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoiceEntry;
import org.gnucash.api.write.spec.GnuCashWritableVendorBill;
import org.gnucash.api.write.spec.GnuCashWritableVendorBillEntry;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableFile_Invc {

    /**
     * @param invcID 
     * @param id the id to look for
     * @return A modifiable version of the invoice.
     *
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     */
    GnuCashWritableGenerInvoice getWritableGenerInvoiceByID(GCshGenerInvcID invcID);

    /**
     * 
     * @return
     * 
     * @see #getGenerInvoices()
     */
    Collection<GnuCashWritableGenerInvoice> getWritableGenerInvoices();

    // ----------------------------

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

    void removeGenerInvoice(GnuCashWritableGenerInvoice invc, boolean withEntries);

    void removeCustomerInvoice(GnuCashWritableCustomerInvoice invc, boolean withEntries);

    void removeVendorBill(GnuCashWritableVendorBill bll, boolean withEntries);

    void removeEmployeeVoucher(GnuCashWritableEmployeeVoucher vch, boolean withEntries);

    void removeJobInvoice(GnuCashWritableJobInvoice invc, boolean withEntries);

    // ---------------------------------------------------------------

    /**
     * @param invcEntrID 
     * @see GnuCashFile#getGenerInvoiceEntryByID(GCshGenerInvcEntrID)
     * @param id the id to look for
     * @return A modifiable version of the invoice entry.
     */
    GnuCashWritableGenerInvoiceEntry getWritableGenerInvoiceEntryByID(GCshGenerInvcEntrID invcEntrID);

    Collection<GnuCashWritableGenerInvoiceEntry> getWritableGenerInvoiceEntries();

    // ----------------------------

    GnuCashWritableCustomerInvoiceEntry createWritableCustomerInvoiceEntry(
			GnuCashWritableCustomerInvoiceImpl invc,
			GnuCashAccount account, 
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    GnuCashWritableVendorBillEntry createWritableVendorBillEntry(
			GnuCashWritableVendorBillImpl bll, 
			GnuCashAccount account,
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    GnuCashWritableEmployeeVoucherEntry createWritableEmployeeVoucher(
			GnuCashWritableEmployeeVoucherImpl vch,
			GnuCashAccount account, 
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    GnuCashWritableJobInvoiceEntry createWritableJobInvoice(
			GnuCashWritableJobInvoiceImpl invc, 
			GnuCashAccount account,
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    void removeGenerInvoiceEntry(GnuCashWritableGenerInvoiceEntry entr);

    void removeCustomerInvoiceEntry(GnuCashWritableCustomerInvoiceEntry entr);

    void removeVendorBillEntry(GnuCashWritableVendorBillEntry entr);

    void removeEmployeeVoucherEntry(GnuCashWritableEmployeeVoucherEntry entr);

    void removeJobInvoiceEntry(GnuCashWritableJobInvoiceEntry entr);

}
