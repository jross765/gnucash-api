package org.gnucash.api.read.impl.hlp;

import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class Vendor_ExpOutst_FP {
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Vendor_ExpOutst_FP.class);

    // ---------------------------------------------------------------

	public static FixedPointNumber getExpensesGenerated(final GnuCashVendor vend, GnuCashGenerInvoice.ReadVariant readVar) {
		if ( readVar == GnuCashGenerInvoice.ReadVariant.DIRECT ) {
			return getExpensesGenerated_direct(vend);
		} else if ( readVar == GnuCashGenerInvoice.ReadVariant.VIA_JOB ) {
			return getExpensesGenerated_viaAllJobs(vend);
		}

		return null; // Compiler happy
    }

	public static FixedPointNumber getExpensesGenerated_direct(final GnuCashVendor vend) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashVendorBill bllSpec : vend.getPaidBills_direct() ) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//		      GnuCashVendorBill bllSpec = new GnuCashVendorBillImpl(invcGen); 
			GnuCashVendor vend2 = bllSpec.getVendor();
			if ( vend2.getID().equals(vend.getID()) ) {
				retval.add(bllSpec.getAmountWithoutTaxes());
			}
//            } // if bllSpec type
		} // for

		return retval;
    }

	public static FixedPointNumber getExpensesGenerated_viaAllJobs(final GnuCashVendor vend) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashJobInvoice bllSpec : vend.getPaidBills_viaAllJobs() ) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//		      GnuCashVendorBill bllSpec = new GnuCashVendorBillImpl(invcGen); 
			GnuCashVendor vend2 = bllSpec.getVendor();
			if ( vend2.getID().equals(vend.getID()) ) {
				retval.add(bllSpec.getAmountWithoutTaxes());
			}
//            } // if bllSpec type
		} // for

		return retval;
    }

    // -------------------------------------

	public static FixedPointNumber getOutstandingValue(final GnuCashVendor vend, GnuCashGenerInvoice.ReadVariant readVar) {
		if ( readVar == GnuCashGenerInvoice.ReadVariant.DIRECT ) {
			return getOutstandingValue_direct(vend);
		} else if ( readVar == GnuCashGenerInvoice.ReadVariant.VIA_JOB ) {
			return getOutstandingValue_viaAllJobs(vend);
		}

		return null; // Compiler happy
    }

	public static FixedPointNumber getOutstandingValue_direct(final GnuCashVendor vend) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashVendorBill bllSpec : vend.getUnpaidBills_direct() ) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//              GnuCashVendorBill bllSpec = new GnuCashVendorBillImpl(invcGen); 
			GnuCashVendor vend2 = bllSpec.getVendor();
			if ( vend2.getID().equals(vend.getID()) ) {
				retval.add(bllSpec.getAmountUnpaidWithTaxes());
			}
//            } // if bllSpec type
		} // for

		return retval;
    }

	public static FixedPointNumber getOutstandingValue_viaAllJobs(final GnuCashVendor vend) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashJobInvoice bllSpec : vend.getUnpaidBills_viaAllJobs() ) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//              GnuCashVendorBill bllSpec = new GnuCashVendorBillImpl(invcGen); 
			GnuCashVendor vend2 = bllSpec.getVendor();
			if ( vend2.getID().equals(vend.getID()) ) {
				retval.add(bllSpec.getAmountUnpaidWithTaxes());
			}
//            } // if bllSpec type
		} // for

		return retval;
    }
    
}
