package org.gnucash.api.read.hlp.invc;

import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;

public interface GnuCashGenerInvoice_Cust_Rest {

    GCshTaxedSumImpl[] getCustInvcTaxes();

    // ---------------------------------------------------------------

    boolean isCustInvcFullyPaid();

    boolean isNotCustInvcFullyPaid();

}
