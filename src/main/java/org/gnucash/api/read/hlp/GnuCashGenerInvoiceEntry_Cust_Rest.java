package org.gnucash.api.read.hlp;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.basetypes.simple.GCshAcctID;

public interface GnuCashGenerInvoiceEntry_Cust_Rest {

    GCshAcctID getCustInvcAccountID() throws AccountNotFoundException;

    // ---------------------------------------------------------------

    boolean isCustInvcTaxable();

    public GCshTaxTable getCustInvcTaxTable() throws TaxTableNotFoundException;

}
