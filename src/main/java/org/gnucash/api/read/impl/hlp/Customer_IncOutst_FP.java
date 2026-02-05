package org.gnucash.api.read.impl.hlp;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class Customer_IncOutst_FP 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Customer_IncOutst_FP.class);

    // ---------------------------------------------------------------

	public static FixedPointNumber getIncomeGenerated(final GnuCashCustomer cust, GnuCashGenerInvoice.ReadVariant readVar) {
		if ( readVar == GnuCashGenerInvoice.ReadVariant.DIRECT ) {
			return getIncomeGenerated_direct(cust);
		} else if ( readVar == GnuCashGenerInvoice.ReadVariant.VIA_JOB ) {
			return getIncomeGenerated_viaAllJobs(cust);
		}

		return null; // Compiler happy
    }

	public static FixedPointNumber getIncomeGenerated_direct(final GnuCashCustomer cust) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashCustomerInvoice invcSpec : cust.getPaidInvoices_direct() ) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_CUSTOMER) ) {
//		      GnuCashCustomerInvoice invcSpec = new GnuCashCustomerInvoiceImpl(invcGen); 
			GnuCashCustomer cust2 = invcSpec.getCustomer();
			if ( cust2.getID().equals(cust.getID()) ) {
				retval.add(invcSpec.getAmountWithoutTaxes());
			}
//            } // if invc type
		} // for

		return retval;
    }

	public static FixedPointNumber getIncomeGenerated_viaAllJobs(final GnuCashCustomer cust) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashJobInvoice invcSpec : cust.getPaidInvoices_viaAllJobs() ) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_CUSTOMER) ) {
//		      GnuCashCustomerInvoice invcSpec = new GnuCashCustomerInvoiceImpl(invcGen); 
			GnuCashCustomer cust2 = invcSpec.getCustomer();
			if ( cust2.getID().equals(cust.getID()) ) {
				retval.add(invcSpec.getAmountWithoutTaxes());
			}
//            } // if invc type
		} // for

		return retval;
    }

    // -------------------------------------

	public static FixedPointNumber getOutstandingValue(final GnuCashCustomer cust, GnuCashGenerInvoice.ReadVariant readVar) {
		if ( readVar == GnuCashGenerInvoice.ReadVariant.DIRECT ) {
			return getOutstandingValue_direct(cust);
		} else if ( readVar == GnuCashGenerInvoice.ReadVariant.VIA_JOB ) {
			return getOutstandingValue_viaAllJobs(cust);
		}

		return null; // Compiler happy
    }

	public static FixedPointNumber getOutstandingValue_direct(final GnuCashCustomer cust) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashCustomerInvoice invcSpec : cust.getUnpaidInvoices_direct() ) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_CUSTOMER) ) {
//              GnuCashCustomerInvoice invcSpec = new GnuCashCustomerInvoiceImpl(invcGen); 
			GnuCashCustomer cust2 = invcSpec.getCustomer();
			if ( cust2.getID().equals(cust.getID()) ) {
				retval.add(invcSpec.getAmountUnpaidWithTaxes());
			}
//            } // if invc type
		} // for

		return retval;
    }

	public static FixedPointNumber getOutstandingValue_viaAllJobs(final GnuCashCustomer cust) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashJobInvoice invcSpec : cust.getUnpaidInvoices_viaAllJobs() ) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_CUSTOMER) ) {
//              GnuCashCustomerInvoice invcSpec = new GnuCashCustomerInvoiceImpl(invcGen); 
			GnuCashCustomer cust2 = invcSpec.getCustomer();
			if ( cust2.getID().equals(cust.getID()) ) {
				retval.add(invcSpec.getAmountUnpaidWithTaxes());
			}
//            } // if invc type
		} // for

		return retval;
    }

}
