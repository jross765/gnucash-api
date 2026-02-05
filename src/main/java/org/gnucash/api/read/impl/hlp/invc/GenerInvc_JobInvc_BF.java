package org.gnucash.api.read.impl.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerInvc_JobInvc_BF {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GenerInvc_JobInvc_BF.class);

	// -----------------------------------------------------------------

	public static BigFraction getJobInvcAmountUnpaidWithTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(invc);
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvc_CustInvc_BF.getCustInvcAmountUnpaidWithTaxes(invc);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvc_VendBll_BF.getVendBllAmountUnpaidWithTaxes(invc);

		return null; // Compiler happy
	}

	public static BigFraction getJobInvcAmountPaidWithTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(invc);
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvc_CustInvc_BF.getCustInvcAmountPaidWithTaxes(invc);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvc_VendBll_BF.getVendBllAmountPaidWithTaxes(invc);

		return null; // Compiler happy
	}

	public static BigFraction getJobInvcAmountPaidWithoutTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(invc);
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvc_CustInvc_BF.getCustInvcAmountPaidWithoutTaxes(invc);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvc_VendBll_BF.getVendBllAmountPaidWithoutTaxes(invc);

		return null; // Compiler happy
	}

	public static BigFraction getJobInvcAmountWithTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(invc);
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvc_CustInvc_BF.getCustInvcAmountWithTaxes(invc);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvc_VendBll_BF.getVendBllAmountWithTaxes(invc);

		return null; // Compiler happy
	}

	public static BigFraction getJobInvcAmountWithoutTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(invc);
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvc_CustInvc_BF.getCustInvcAmountWithoutTaxes(invc);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvc_VendBll_BF.getVendBllAmountWithoutTaxes(invc);

		return null; // Compiler happy
	}

}
