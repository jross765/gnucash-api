package org.gnucash.api.read.impl.hlp.own;

import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenerJob_IncOutst_FP {

	protected static final Logger LOGGER = LoggerFactory.getLogger(GenerJob_IncOutst_FP.class);
	
	// ---------------------------------------------------------------

	public static FixedPointNumber getIncomeGenerated(final GnuCashGenerJob job) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashJobInvoice invcSpec : job.getPaidInvoices() ) {
//				if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_JOB) ) {
//		    		GnuCashJobInvoice invcSpec = new GnuCashJobInvoiceImpl(invcGen);
			GnuCashGenerJob job2 = invcSpec.getGenerJob();
			if ( job2.getID().equals(job.getID()) ) {
				retval.add(invcSpec.getAmountWithoutTaxes());
			}
//				} // if invcSpec type
		} // for

		return retval;
	}

	// ----------------------------

	public static FixedPointNumber getOutstandingValue(final GnuCashGenerJob job) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashJobInvoice invcSpec : job.getUnpaidInvoices() ) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_JOB) ) {
//              GnuCashJobInvoice invcSpec = new GnuCashJobInvoiceImpl(invcGen); 
			GnuCashGenerJob job2 = invcSpec.getGenerJob();
			if ( job2.getID().equals(job.getID()) ) {
				retval.add(invcSpec.getAmountUnpaidWithTaxes());
			}
//            } // if invcSpec type
		} // for

		return retval;
	}

}
