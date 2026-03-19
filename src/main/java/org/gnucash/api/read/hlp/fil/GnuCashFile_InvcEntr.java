package org.gnucash.api.read.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;

public interface GnuCashFile_InvcEntr {

    /**
     * @param entrID the unique ID of the (generic) invoice entry to look for
     * @return the invoice entry or null if it's not found
     * 
     * @see #GGshCashFile_Invc.getUnpaidGenerInvoices()
     * @see #GGshCashFile_Invc.getPaidGenerInvoices()
     */
    GnuCashGenerInvoiceEntry getGenerInvoiceEntryByID(GCshGenerInvcEntrID entrID);

    /**
     * @return list of all (generic) invoices (ro-objects)
     */
    Collection<GnuCashGenerInvoiceEntry> getGenerInvoiceEntries();
    
}
