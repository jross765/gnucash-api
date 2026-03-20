package org.gnucash.api.write.spec.hlp.fil;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.write.impl.spec.GnuCashWritableJobInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoiceEntry;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableFile_InvcEntr_Job {

    GnuCashWritableJobInvoiceEntry createWritableJobInvoiceEntry(
			GnuCashWritableJobInvoiceImpl invc, 
			GnuCashAccount account,
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    // ----------------------------

    void removeJobInvoiceEntry(GnuCashWritableJobInvoiceEntry entr);

}
