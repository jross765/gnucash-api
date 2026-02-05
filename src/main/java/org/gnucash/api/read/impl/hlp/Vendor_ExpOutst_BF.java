package org.gnucash.api.read.impl.hlp;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vendor_ExpOutst_BF {
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Vendor_ExpOutst_BF.class);

    // ---------------------------------------------------------------

	public static BigFraction getExpensesGenerated(final GnuCashVendor vend, GnuCashGenerInvoice.ReadVariant readVar) {
		if ( readVar == GnuCashGenerInvoice.ReadVariant.DIRECT ) {
			return getExpensesGenerated_direct(vend);
		} else if ( readVar == GnuCashGenerInvoice.ReadVariant.VIA_JOB ) {
			return getExpensesGenerated_viaAllJobs(vend);
		}

		return null; // Compiler happy
    }

	public static BigFraction getExpensesGenerated_direct(final GnuCashVendor vend) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashVendorBill bllSpec : vend.getPaidBills_direct() ) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//		      GnuCashVendorBill bllSpec = new GnuCashVendorBillImpl(invcGen); 
			GnuCashVendor vend2 = bllSpec.getVendor();
			if ( vend2.getID().equals(vend.getID()) ) {
				retval = retval.add(bllSpec.getAmountWithoutTaxesRat());
			}
//            } // if bllSpec type
		} // for

		return retval;
    }

	public static BigFraction getExpensesGenerated_viaAllJobs(final GnuCashVendor vend) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashJobInvoice bllSpec : vend.getPaidBills_viaAllJobs() ) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//		      GnuCashVendorBill bllSpec = new GnuCashVendorBillImpl(invcGen); 
			GnuCashVendor vend2 = bllSpec.getVendor();
			if ( vend2.getID().equals(vend.getID()) ) {
				retval = retval.add(bllSpec.getAmountWithoutTaxesRat());
			}
//            } // if bllSpec type
		} // for

		return retval;
    }

    // -------------------------------------

	public static BigFraction getOutstandingValue(final GnuCashVendor vend, GnuCashGenerInvoice.ReadVariant readVar) {
		if ( readVar == GnuCashGenerInvoice.ReadVariant.DIRECT ) {
			return getOutstandingValue_direct(vend);
		} else if ( readVar == GnuCashGenerInvoice.ReadVariant.VIA_JOB ) {
			return getOutstandingValue_viaAllJobs(vend);
		}

		return null; // Compiler happy
    }

	public static BigFraction getOutstandingValue_direct(final GnuCashVendor vend) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashVendorBill bllSpec : vend.getUnpaidBills_direct() ) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//              GnuCashVendorBill bllSpec = new GnuCashVendorBillImpl(invcGen); 
			GnuCashVendor vend2 = bllSpec.getVendor();
			if ( vend2.getID().equals(vend.getID()) ) {
				retval = retval.add(bllSpec.getAmountUnpaidWithTaxesRat());
			}
//            } // if bllSpec type
		} // for

		return retval;
    }

	public static BigFraction getOutstandingValue_viaAllJobs(final GnuCashVendor vend) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashJobInvoice bllSpec : vend.getUnpaidBills_viaAllJobs() ) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//              GnuCashVendorBill bllSpec = new GnuCashVendorBillImpl(invcGen); 
			GnuCashVendor vend2 = bllSpec.getVendor();
			if ( vend2.getID().equals(vend.getID()) ) {
				retval = retval.add(bllSpec.getAmountUnpaidWithTaxesRat());
			}
//            } // if bllSpec type
		} // for

		return retval;
    }
    
}
