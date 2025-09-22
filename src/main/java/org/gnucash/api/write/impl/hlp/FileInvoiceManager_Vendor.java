package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorBillImpl;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.gnucash.api.write.spec.GnuCashWritableVendorBill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Vendor {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Vendor.class);

	// ---------------------------------------------------------------

	public static List<GnuCashWritableVendorBill> getBills_direct(final FileInvoiceManager invMgr,
			final GnuCashVendor vend) throws TaxTableNotFoundException {
		List<GnuCashWritableVendorBill> retval = new ArrayList<GnuCashWritableVendorBill>();

		for ( GnuCashGenerInvoice invc : invMgr.getGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(vend.getID().toString()) ) {
					GnuCashWritableVendorBillImpl wrtblInvc = new GnuCashWritableVendorBillImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getBills_viaAllJobs(final GnuCashVendor vend) {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashVendorJob job : vend.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getInvoices() ) {
				retval.add((GnuCashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableVendorBill> getPaidBills_direct(final FileInvoiceManager invMgr,
			final GnuCashVendor vend) throws TaxTableNotFoundException {
		List<GnuCashWritableVendorBill> retval = new ArrayList<GnuCashWritableVendorBill>();

		for ( GnuCashWritableGenerInvoice invc : invMgr.getPaidWritableGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(vend.getID().toString()) ) {
					GnuCashWritableVendorBillImpl wrtblInvc = new GnuCashWritableVendorBillImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getPaidBills_viaAllJobs(final GnuCashVendor vend) {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashVendorJob job : vend.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getPaidInvoices() ) {
				retval.add((GnuCashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableVendorBill> getUnpaidBills_direct(final FileInvoiceManager invMgr,
			final GnuCashVendor vend) throws TaxTableNotFoundException {
		List<GnuCashWritableVendorBill> retval = new ArrayList<GnuCashWritableVendorBill>();

		for ( GnuCashWritableGenerInvoice invc : invMgr.getUnpaidWritableGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(vend.getID().toString()) ) {
					GnuCashWritableVendorBillImpl wrtblInvc = new GnuCashWritableVendorBillImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getUnpaidBills_viaAllJobs(final GnuCashVendor vend) {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashVendorJob job : vend.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
				retval.add((GnuCashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

}
