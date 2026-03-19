package org.gnucash.api.read.spec;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.spec.hlp.invc.SpecInvoiceEntryCommon_BF;
import org.gnucash.api.read.spec.hlp.invc.SpecInvoiceEntryCommon_FP;
import org.gnucash.api.read.spec.hlp.invc.SpecInvoiceEntryCommon_Rest;
import org.gnucash.api.read.spec.hlp.invc.SpecInvoiceEntryCommon_Str;
import org.gnucash.base.basetypes.simple.spec.GCshJobInvcID;

/**
 * One entry (line item) of a {@link GnuCashJobInvoice}
 * 
 * @see GnuCashCustomerInvoiceEntry
 * @see GnuCashEmployeeVoucherEntry
 * @see GnuCashVendorBillEntry
 * @see GnuCashGenerInvoiceEntry
 */
public interface GnuCashJobInvoiceEntry extends GnuCashGenerInvoiceEntry,
												SpecInvoiceEntryCommon_FP,
												SpecInvoiceEntryCommon_BF,
												SpecInvoiceEntryCommon_Str,
												SpecInvoiceEntryCommon_Rest
{
	
	GCshJobInvcID getInvoiceID();

	GnuCashJobInvoice getInvoice();

}
