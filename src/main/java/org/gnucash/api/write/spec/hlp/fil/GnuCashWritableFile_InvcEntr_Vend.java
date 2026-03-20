package org.gnucash.api.write.spec.hlp.fil;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorBillImpl;
import org.gnucash.api.write.spec.GnuCashWritableVendorBillEntry;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableFile_InvcEntr_Vend {

    GnuCashWritableVendorBillEntry createWritableVendorBillEntry(
			GnuCashWritableVendorBillImpl bll, 
			GnuCashAccount account,
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    // ----------------------------

    void removeVendorBillEntry(GnuCashWritableVendorBillEntry entr);

}
