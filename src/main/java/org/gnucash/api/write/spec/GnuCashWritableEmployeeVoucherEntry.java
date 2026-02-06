package org.gnucash.api.write.spec;

import org.gnucash.api.read.spec.GnuCashEmployeeVoucherEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.spec.hlp.SpecWritableInvoiceEntryCommon_BF;
import org.gnucash.api.write.spec.hlp.SpecWritableInvoiceEntryCommon_FP;
import org.gnucash.api.write.spec.hlp.SpecWritableInvoiceEntryCommon_Rest;

/**
 * Employee voucher entry that can be modified.
 * 
 * @see GnuCashEmployeeVoucherEntry
 * 
 * @see GnuCashWritableCustomerInvoiceEntry
 * @see GnuCashWritableVendorBillEntry
 * @see GnuCashWritableJobInvoiceEntry
 */
public interface GnuCashWritableEmployeeVoucherEntry extends GnuCashWritableGenerInvoiceEntry, 
															 GnuCashEmployeeVoucherEntry,
															 SpecWritableInvoiceEntryCommon_FP,
															 SpecWritableInvoiceEntryCommon_BF,
															 SpecWritableInvoiceEntryCommon_Rest,
                                                             GnuCashWritableObject 
{

	// ::EMPTY

}
