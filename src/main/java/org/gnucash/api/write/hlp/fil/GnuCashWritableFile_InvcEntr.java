package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableJobInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorBillImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoiceEntry;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucherEntry;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoiceEntry;
import org.gnucash.api.write.spec.GnuCashWritableVendorBillEntry;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableFile_InvcEntr {

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

    GnuCashWritableEmployeeVoucherEntry createWritableEmployeeVoucherEntry(
			GnuCashWritableEmployeeVoucherImpl vch,
			GnuCashAccount account, 
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    GnuCashWritableJobInvoiceEntry createWritableJobInvoiceEntry(
			GnuCashWritableJobInvoiceImpl invc, 
			GnuCashAccount account,
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    // ----------------------------

    void removeGenerInvoiceEntry(GnuCashWritableGenerInvoiceEntry entr);

    void removeCustomerInvoiceEntry(GnuCashWritableCustomerInvoiceEntry entr);

    void removeVendorBillEntry(GnuCashWritableVendorBillEntry entr);

    void removeEmployeeVoucherEntry(GnuCashWritableEmployeeVoucherEntry entr);

    void removeJobInvoiceEntry(GnuCashWritableJobInvoiceEntry entr);

}
