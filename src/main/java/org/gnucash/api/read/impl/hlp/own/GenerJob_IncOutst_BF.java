package org.gnucash.api.read.impl.hlp.own;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerJob_IncOutst_BF {

	protected static final Logger LOGGER = LoggerFactory.getLogger(GenerJob_IncOutst_BF.class);
	
	// ---------------------------------------------------------------

	public static BigFraction getIncomeGenerated(final GnuCashGenerJob job) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashJobInvoice invcSpec : job.getPaidInvoices() ) {
//				if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_JOB) ) {
//		    		GnuCashJobInvoice invcSpec = new GnuCashJobInvoiceImpl(invcGen);
			GnuCashGenerJob job2 = invcSpec.getGenerJob();
			if ( job2.getID().equals(job.getID()) ) {
				retval = retval.add(invcSpec.getAmountWithoutTaxesRat());
			}
//				} // if invcSpec type
		} // for

		return retval;
	}

	// ----------------------------

	public static BigFraction getOutstandingValue(final GnuCashGenerJob job) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashJobInvoice invcSpec : job.getUnpaidInvoices() ) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_JOB) ) {
//              GnuCashJobInvoice invcSpec = new GnuCashJobInvoiceImpl(invcGen); 
			GnuCashGenerJob job2 = invcSpec.getGenerJob();
			if ( job2.getID().equals(job.getID()) ) {
				retval = retval.add(invcSpec.getAmountUnpaidWithTaxesRat());
			}
//            } // if invcSpec type
		} // for

		return retval;
	}

}
