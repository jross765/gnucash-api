package org.gnucash.api.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.write.GnuCashWritableCustomer;
import org.gnucash.base.basetypes.simple.GCshCustID;

public interface GnuCashWritableFile_Cust {

    GnuCashWritableCustomer getWritableCustomerByID(GCshCustID custID);

    Collection<GnuCashWritableCustomer> getWritableCustomers();

    // ----------------------------

    GnuCashWritableCustomer createWritableCustomer(String name);

    void removeCustomer(GnuCashWritableCustomer cust);

}
