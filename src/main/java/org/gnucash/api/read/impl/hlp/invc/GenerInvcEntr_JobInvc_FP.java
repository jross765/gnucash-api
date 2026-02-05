package org.gnucash.api.read.impl.hlp.invc;

import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenerInvcEntr_JobInvc_FP {
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GenerInvcEntr_JobInvc_FP.class);

    // ---------------------------------------------------------------

    public static FixedPointNumber getJobInvcApplicableTaxPercent(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(entr.getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvcEntr_CustInvc_FP.getCustInvcApplicableTaxPercent(entr);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvcEntr_VendBll_FP.getVendBllApplicableTaxPercent(entr);

		return null; // Compiler happy
    }

    // ---------------------------------------------------------------

    public static FixedPointNumber getJobInvcPrice(GnuCashGenerInvoiceEntry entr) {
    	if ( entr.getType() != GCshOwner.Type.JOB )
    		throw new WrongInvoiceTypeException();

    	GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(entr.getGenerInvoice());
    	if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
    		return GenerInvcEntr_CustInvc_FP.getCustInvcPrice(entr);
    	else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
    		return GenerInvcEntr_VendBll_FP.getVendBllPrice(entr);

    	return null; // Compiler happy
    }

    // ----------------------------

    public static FixedPointNumber getJobInvcSum(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(entr.getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvcEntr_CustInvc_FP.getCustInvcSum(entr);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvcEntr_VendBll_FP.getVendBllSum(entr);

		return null; // Compiler happy
    }

    public static FixedPointNumber getJobInvcSumInclTaxes(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(entr.getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvcEntr_CustInvc_FP.getCustInvcSumInclTaxes(entr);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvcEntr_VendBll_FP.getVendBllSumInclTaxes(entr);

		return null; // Compiler happy
    }

    public static FixedPointNumber getJobInvcSumExclTaxes(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(entr.getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvcEntr_CustInvc_FP.getCustInvcSumExclTaxes(entr);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvcEntr_VendBll_FP.getVendBllSumExclTaxes(entr);

		return null; // Compiler happy
    }

}
