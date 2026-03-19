package org.gnucash.api.read.spec;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.spec.hlp.invc.SpecInvoiceEntryCommon_BF;
import org.gnucash.api.read.spec.hlp.invc.SpecInvoiceEntryCommon_FP;
import org.gnucash.api.read.spec.hlp.invc.SpecInvoiceEntryCommon_Rest;
import org.gnucash.api.read.spec.hlp.invc.SpecInvoiceEntryCommon_Str;
import org.gnucash.base.basetypes.simple.spec.GCshEmplVchID;

/**
 * One entry (line item) of a {@link GnuCashEmployeeVoucher}
 * 
 * @see GnuCashCustomerInvoiceEntry
 * @see GnuCashVendorBillEntry
 * @see GnuCashJobInvoiceEntry
 * @see GnuCashGenerInvoiceEntry
 */
public interface GnuCashEmployeeVoucherEntry extends GnuCashGenerInvoiceEntry,
													 SpecInvoiceEntryCommon_FP,
													 SpecInvoiceEntryCommon_BF,
													 SpecInvoiceEntryCommon_Str,
													 SpecInvoiceEntryCommon_Rest
{
	
	GCshEmplVchID getVoucherID();

	GnuCashEmployeeVoucher getVoucher();

}
