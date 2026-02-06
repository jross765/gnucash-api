package org.gnucash.api.read.spec;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.spec.hlp.SpecInvoiceEntryCommon_BF;
import org.gnucash.api.read.spec.hlp.SpecInvoiceEntryCommon_FP;
import org.gnucash.api.read.spec.hlp.SpecInvoiceEntryCommon_Rest;
import org.gnucash.api.read.spec.hlp.SpecInvoiceEntryCommon_Str;
import org.gnucash.base.basetypes.simple.spec.GCshVendBllID;

/**
 * One entry (line item) of a {@link GnuCashVendorBill}
 * 
 * @see GnuCashCustomerInvoiceEntry
 * @see GnuCashEmployeeVoucherEntry
 * @see GnuCashJobInvoiceEntry
 * @see GnuCashGenerInvoiceEntry
 */
public interface GnuCashVendorBillEntry extends GnuCashGenerInvoiceEntry,
												SpecInvoiceEntryCommon_FP,
												SpecInvoiceEntryCommon_BF,
												SpecInvoiceEntryCommon_Str,
												SpecInvoiceEntryCommon_Rest
{
	
	GCshVendBllID getBillID();

	GnuCashVendorBill getBill();

}
