package org.gnucash.api.write.spec.hlp.fil;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.write.impl.spec.GnuCashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucherEntry;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableFile_InvcEntr_Empl {

    GnuCashWritableEmployeeVoucherEntry createWritableEmployeeVoucherEntry(
			GnuCashWritableEmployeeVoucherImpl vch,
			GnuCashAccount account, 
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    // ----------------------------

    void removeEmployeeVoucherEntry(GnuCashWritableEmployeeVoucherEntry entr);

}
