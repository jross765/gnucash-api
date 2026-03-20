package org.gnucash.api.write.spec;

import org.gnucash.api.read.spec.GnuCashCustomerInvoiceEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.spec.hlp.invc.SpecWritableInvoiceEntryCommon_BF;
import org.gnucash.api.write.spec.hlp.invc.SpecWritableInvoiceEntryCommon_FP;
import org.gnucash.api.write.spec.hlp.invc.SpecWritableInvoiceEntryCommon_Rest;

/**
 * Customer invoice entry that can be modified.
 * 
 * @see GnuCashCustomerInvoiceEntry
 * 
 * @see GnuCashWritableEmployeeVoucherEntry
 * @see GnuCashWritableVendorBillEntry
 * @see GnuCashWritableJobInvoiceEntry
 */
public interface GnuCashWritableCustomerInvoiceEntry extends GnuCashWritableGenerInvoiceEntry,
                                                             GnuCashCustomerInvoiceEntry,
                                                             SpecWritableInvoiceEntryCommon_FP,
                                                             SpecWritableInvoiceEntryCommon_BF,
                                                             SpecWritableInvoiceEntryCommon_Rest,
                                                             GnuCashWritableObject 
{

	// ::EMPTY

}
