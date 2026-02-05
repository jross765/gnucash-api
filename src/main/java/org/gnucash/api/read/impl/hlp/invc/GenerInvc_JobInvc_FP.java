package org.gnucash.api.read.impl.hlp.invc;

import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenerInvc_JobInvc_FP {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GenerInvc_JobInvc_FP.class);

	// -----------------------------------------------------------------

	public static FixedPointNumber getJobInvcAmountUnpaidWithTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(invc);
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvc_CustInvc_FP.getCustInvcAmountUnpaidWithTaxes(invc);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvc_VendBll_FP.getVendBllAmountUnpaidWithTaxes(invc);

		return null; // Compiler happy
	}

	public static FixedPointNumber getJobInvcAmountPaidWithTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(invc);
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvc_CustInvc_FP.getCustInvcAmountPaidWithTaxes(invc);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvc_VendBll_FP.getVendBllAmountPaidWithTaxes(invc);

		return null; // Compiler happy
	}

	public static FixedPointNumber getJobInvcAmountPaidWithoutTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(invc);
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvc_CustInvc_FP.getCustInvcAmountPaidWithoutTaxes(invc);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvc_VendBll_FP.getVendBllAmountPaidWithoutTaxes(invc);

		return null; // Compiler happy
	}

	public static FixedPointNumber getJobInvcAmountWithTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(invc);
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvc_CustInvc_FP.getCustInvcAmountWithTaxes(invc);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvc_VendBll_FP.getVendBllAmountWithTaxes(invc);

		return null; // Compiler happy
	}

	public static FixedPointNumber getJobInvcAmountWithoutTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(invc);
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvc_CustInvc_FP.getCustInvcAmountWithoutTaxes(invc);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvc_VendBll_FP.getVendBllAmountWithoutTaxes(invc);

		return null; // Compiler happy
	}

}
