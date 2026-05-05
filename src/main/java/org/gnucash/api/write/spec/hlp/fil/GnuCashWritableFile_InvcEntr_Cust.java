package org.gnucash.api.write.spec.hlp.fil;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoiceEntry;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableFile_InvcEntr_Cust {

	@Deprecated
    GnuCashWritableCustomerInvoiceEntry createWritableCustomerInvoiceEntry(
			GnuCashWritableCustomerInvoiceImpl invc,
			GnuCashAccount account, 
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    GnuCashWritableCustomerInvoiceEntry createWritableCustomerInvoiceEntry(
			GnuCashWritableCustomerInvoiceImpl invc,
			GnuCashAccount account, 
			BigFraction quantity, 
			BigFraction price) throws TaxTableNotFoundException;

    // ----------------------------

    void removeCustomerInvoiceEntry(GnuCashWritableCustomerInvoiceEntry entr);

}
