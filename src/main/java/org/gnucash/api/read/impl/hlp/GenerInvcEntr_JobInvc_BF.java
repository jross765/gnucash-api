package org.gnucash.api.read.impl.hlp;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerInvcEntr_JobInvc_BF {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerInvcEntr_JobInvc_BF.class);

    // ---------------------------------------------------------------

    public static BigFraction getJobInvcApplicableTaxPercent(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(entr.getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvcEntr_CustInvc_BF.getCustInvcApplicableTaxPercent(entr);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvcEntr_VendBll_BF.getVendBllApplicableTaxPercent(entr);

		return null; // Compiler happy
    }

    // ---------------------------------------------------------------

    public static BigFraction getJobInvcPrice(GnuCashGenerInvoiceEntry entr) {
    	if ( entr.getType() != GCshOwner.Type.JOB )
    		throw new WrongInvoiceTypeException();

    	GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(entr.getGenerInvoice());
    	if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
    		return GenerInvcEntr_CustInvc_BF.getCustInvcPrice(entr);
    	else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
    		return GenerInvcEntr_VendBll_BF.getVendBllPrice(entr);

    	return null; // Compiler happy
    }

    // ----------------------------

    public static BigFraction getJobInvcSum(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(entr.getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvcEntr_CustInvc_BF.getCustInvcSum(entr);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvcEntr_VendBll_BF.getVendBllSum(entr);

		return null; // Compiler happy
    }

    public static BigFraction getJobInvcSumInclTaxes(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(entr.getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvcEntr_CustInvc_BF.getCustInvcSumInclTaxes(entr);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvcEntr_VendBll_BF.getVendBllSumInclTaxes(entr);

		return null; // Compiler happy
    }

    public static BigFraction getJobInvcSumExclTaxes(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(entr.getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return GenerInvcEntr_CustInvc_BF.getCustInvcSumExclTaxes(entr);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return GenerInvcEntr_VendBll_BF.getVendBllSumExclTaxes(entr);

		return null; // Compiler happy
    }

}
