package org.gnucash.api.write.hlp.invc;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;

public interface GnuCashWritableGenerInvoiceEntry_Vend_Rest {

    /**
     * @param val
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    void setVendBllTaxable(boolean val) throws TaxTableNotFoundException;

    /**
     * @param tax the new tax table to use. Null sets isTaxable to false.
     * @throws TaxTableNotFoundException
     */
    void setVendBllTaxTable(GCshTaxTable tax) throws TaxTableNotFoundException;

}
