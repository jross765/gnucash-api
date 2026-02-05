package org.gnucash.api.read.hlp;

import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;

public interface GnuCashGenerInvoice_Empl_Rest {

    GCshTaxedSumImpl[] getEmplVchTaxes();

    // ---------------------------------------------------------------

    boolean isEmplVchFullyPaid();

    boolean isNotEmplVchFullyPaid();

}
