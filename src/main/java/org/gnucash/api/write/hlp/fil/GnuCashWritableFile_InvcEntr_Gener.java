package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;

public interface GnuCashWritableFile_InvcEntr_Gener {

    /**
     * @param invcEntrID 
     * @see GnuCashFile#getGenerInvoiceEntryByID(GCshGenerInvcEntrID)
     * @param id the id to look for
     * @return A modifiable version of the invoice entry.
     */
    GnuCashWritableGenerInvoiceEntry getWritableGenerInvoiceEntryByID(GCshGenerInvcEntrID invcEntrID);

    Collection<GnuCashWritableGenerInvoiceEntry> getWritableGenerInvoiceEntries();

    // ----------------------------

    void removeGenerInvoiceEntry(GnuCashWritableGenerInvoiceEntry entr);

}
