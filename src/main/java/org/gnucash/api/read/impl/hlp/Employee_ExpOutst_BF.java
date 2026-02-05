package org.gnucash.api.read.impl.hlp;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Employee_ExpOutst_BF {
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Employee_ExpOutst_BF.class);

    // ---------------------------------------------------------------

//    public String getTaxTableID() {
//	GncGncEmployee.EmployeeTaxtable emplTaxtable = jwsdpPeer.getEmployeeTaxtable();
//	if (emplTaxtable == null) {
//	    return null;
//	}
//
//	return emplTaxtable.getValue();
//    }
//
//    public GCshTaxTable getTaxTable() {
//	String id = getTaxTableID();
//	if (id == null) {
//	    return null;
//	}
//	return getGnuCashFile().getTaxTableByID(id);
//    }

    // ---------------------------------------------------------------

	public static BigFraction getExpensesGenerated(final GnuCashEmployee empl) {
    	return getExpensesGenerated_direct(empl);
    }

	public static BigFraction getExpensesGenerated_direct(final GnuCashEmployee empl) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashEmployeeVoucher vchSpec : empl.getPaidVouchers() ) {
//		    if ( vchSpec.getType().equals(GnuCashGenerInvoice.TYPE_EMPLOYEE) ) {
//		      GnuCashEmployeeVoucher vchSpec = new GnuCashEmployeeVoucherImpl(invcGen); 
			GnuCashEmployee empl2 = vchSpec.getEmployee();
			if ( empl2.getID().equals(empl.getID()) ) {
				retval = retval.add(vchSpec.getAmountWithoutTaxesRat());
			}
//            } // if vchSpec type
		} // for

		return retval;
    }

    // -------------------------------------

	public static BigFraction getOutstandingValue(GnuCashEmployee empl) {
    	return getOutstandingValue_direct(empl);
    }

	public static BigFraction getOutstandingValue_direct(GnuCashEmployee empl) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashEmployeeVoucher vchSpec : empl.getUnpaidVouchers() ) {
//            if ( vchSpec.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//              GnuCashEmployeeVoucher vchSpec = new GnuCashEmployeeVoucherImpl(invcGen); 
			GnuCashEmployee empl2 = vchSpec.getEmployee();
			if ( empl2.getID().equals(empl.getID()) ) {
				retval = retval.add(vchSpec.getAmountUnpaidWithTaxesRat());
			}
//            } // if vchSpec type
		} // for

		return retval;
    }

}
