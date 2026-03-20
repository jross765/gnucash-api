package org.gnucash.api.write.spec;

import org.gnucash.api.read.spec.GnuCashJobInvoiceEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.spec.hlp.invc.SpecWritableInvoiceEntryCommon_BF;
import org.gnucash.api.write.spec.hlp.invc.SpecWritableInvoiceEntryCommon_FP;
import org.gnucash.api.write.spec.hlp.invc.SpecWritableInvoiceEntryCommon_Rest;

/**
 * Invoice-Entry that can be modified.
 * 
 * @see GnuCashJobInvoiceEntry
 * 
 * @see GnuCashWritableCustomerInvoiceEntry
 * @see GnuCashWritableEmployeeVoucherEntry
 * @see GnuCashWritableVendorBillEntry
 */
public interface GnuCashWritableJobInvoiceEntry extends GnuCashWritableGenerInvoiceEntry,
														GnuCashJobInvoiceEntry,
														SpecWritableInvoiceEntryCommon_FP,
														SpecWritableInvoiceEntryCommon_BF,
														SpecWritableInvoiceEntryCommon_Rest,
                                                        GnuCashWritableObject 
{

	// ::EMPTY

}
