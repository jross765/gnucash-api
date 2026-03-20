package org.gnucash.api.write.spec;

import org.gnucash.api.read.spec.GnuCashVendorBillEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.spec.hlp.invc.SpecWritableInvoiceEntryCommon_BF;
import org.gnucash.api.write.spec.hlp.invc.SpecWritableInvoiceEntryCommon_FP;
import org.gnucash.api.write.spec.hlp.invc.SpecWritableInvoiceEntryCommon_Rest;

/**
 * Vendor bill entry  that can be modified.
 * 
 * @see GnuCashVendorBillEntry
 * 
 * @see GnuCashWritableCustomerInvoiceEntry
 * @see GnuCashWritableEmployeeVoucherEntry
 * @see GnuCashWritableJobInvoiceEntry
 */
public interface GnuCashWritableVendorBillEntry extends GnuCashWritableGenerInvoiceEntry,
														GnuCashVendorBillEntry,
														SpecWritableInvoiceEntryCommon_FP,
														SpecWritableInvoiceEntryCommon_BF,
														SpecWritableInvoiceEntryCommon_Rest,
                                                        GnuCashWritableObject 
{

	// ::EMPTY

}
