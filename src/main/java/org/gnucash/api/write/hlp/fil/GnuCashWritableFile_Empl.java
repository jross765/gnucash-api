package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.write.GnuCashWritableEmployee;
import org.gnucash.base.basetypes.simple.GCshEmplID;

public interface GnuCashWritableFile_Empl {

    GnuCashWritableEmployee getWritableEmployeeByID(GCshEmplID emplID);

    Collection<GnuCashWritableEmployee> getWritableEmployees();

    // ----------------------------

    GnuCashWritableEmployee createWritableEmployee(String userName);

    void removeEmployee(GnuCashWritableEmployee empl);

}
