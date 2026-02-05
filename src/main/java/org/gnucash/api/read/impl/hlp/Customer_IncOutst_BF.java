package org.gnucash.api.read.impl.hlp;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Customer_IncOutst_BF 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Customer_IncOutst_BF.class);

    // ---------------------------------------------------------------

	public static BigFraction getIncomeGenerated(final GnuCashCustomer cust, GnuCashGenerInvoice.ReadVariant readVar) {
		if ( readVar == GnuCashGenerInvoice.ReadVariant.DIRECT ) {
			return getIncomeGenerated_direct(cust);
		} else if ( readVar == GnuCashGenerInvoice.ReadVariant.VIA_JOB ) {
			return getIncomeGenerated_viaAllJobs(cust);
		}

		return null; // Compiler happy
    }

	public static BigFraction getIncomeGenerated_direct(final GnuCashCustomer cust) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashCustomerInvoice invcSpec : cust.getPaidInvoices_direct() ) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_CUSTOMER) ) {
//		      GnuCashCustomerInvoice invcSpec = new GnuCashCustomerInvoiceImpl(invcGen); 
			GnuCashCustomer cust2 = invcSpec.getCustomer();
			if ( cust2.getID().equals(cust.getID()) ) {
				retval = retval.add(invcSpec.getAmountWithoutTaxesRat());
			}
//            } // if invc type
		} // for

		return retval;
    }

	public static BigFraction getIncomeGenerated_viaAllJobs(final GnuCashCustomer cust) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashJobInvoice invcSpec : cust.getPaidInvoices_viaAllJobs() ) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_CUSTOMER) ) {
//		      GnuCashCustomerInvoice invcSpec = new GnuCashCustomerInvoiceImpl(invcGen); 
			GnuCashCustomer cust2 = invcSpec.getCustomer();
			if ( cust2.getID().equals(cust.getID()) ) {
				retval = retval.add(invcSpec.getAmountWithoutTaxesRat());
			}
//            } // if invc type
		} // for

		return retval;
    }

    // -------------------------------------

	public static BigFraction getOutstandingValue(final GnuCashCustomer cust, GnuCashGenerInvoice.ReadVariant readVar) {
		if ( readVar == GnuCashGenerInvoice.ReadVariant.DIRECT ) {
			return getOutstandingValue_direct(cust);
		} else if ( readVar == GnuCashGenerInvoice.ReadVariant.VIA_JOB ) {
			return getOutstandingValue_viaAllJobs(cust);
		}

		return null; // Compiler happy
    }

	public static BigFraction getOutstandingValue_direct(final GnuCashCustomer cust) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashCustomerInvoice invcSpec : cust.getUnpaidInvoices_direct() ) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_CUSTOMER) ) {
//              GnuCashCustomerInvoice invcSpec = new GnuCashCustomerInvoiceImpl(invcGen); 
			GnuCashCustomer cust2 = invcSpec.getCustomer();
			if ( cust2.getID().equals(cust.getID()) ) {
				retval = retval.add(invcSpec.getAmountUnpaidWithTaxesRat());
			}
//            } // if invc type
		} // for

		return retval;
    }

	public static BigFraction getOutstandingValue_viaAllJobs(final GnuCashCustomer cust) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashJobInvoice invcSpec : cust.getUnpaidInvoices_viaAllJobs() ) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_CUSTOMER) ) {
//              GnuCashCustomerInvoice invcSpec = new GnuCashCustomerInvoiceImpl(invcGen); 
			GnuCashCustomer cust2 = invcSpec.getCustomer();
			if ( cust2.getID().equals(cust.getID()) ) {
				retval = retval.add(invcSpec.getAmountUnpaidWithTaxesRat());
			}
//            } // if invc type
		} // for

		return retval;
    }

}
