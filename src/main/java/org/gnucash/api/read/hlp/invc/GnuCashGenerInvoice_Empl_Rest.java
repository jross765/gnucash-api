package org.gnucash.api.read.hlp.invc;

import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;

public interface GnuCashGenerInvoice_Empl_Rest {

    GCshTaxedSumImpl[] getEmplVchTaxes();

    // ---------------------------------------------------------------

    boolean isEmplVchFullyPaid();

    boolean isNotEmplVchFullyPaid();

}
