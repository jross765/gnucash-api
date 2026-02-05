package org.gnucash.api.read.hlp.invc;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.basetypes.simple.GCshAcctID;

public interface GnuCashGenerInvoiceEntry_Empl_Rest {
    
    GCshAcctID getEmplVchAccountID() throws AccountNotFoundException;

    // ---------------------------------------------------------------

    boolean isEmplVchTaxable();

    public GCshTaxTable getEmplVchTaxTable() throws TaxTableNotFoundException;

}
