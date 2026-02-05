package org.gnucash.api.read.hlp.invc;

import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;

public interface GnuCashGenerInvoice_Vend_Rest {

    GCshTaxedSumImpl[] getVendBllTaxes();

    // ---------------------------------------------------------------

    boolean isVendBllFullyPaid();

    boolean isNotVendBllFullyPaid();

}
