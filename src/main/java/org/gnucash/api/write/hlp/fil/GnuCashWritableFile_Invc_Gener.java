package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;

public interface GnuCashWritableFile_Invc_Gener {

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

    void removeGenerInvoice(GnuCashWritableGenerInvoice invc, boolean withEntries);

}
