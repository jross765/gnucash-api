package org.gnucash.api.read.impl.hlp;

import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class Employee_ExpOutst_FP {
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Employee_ExpOutst_FP.class);

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

	public static FixedPointNumber getExpensesGenerated(final GnuCashEmployee empl) {
    	return getExpensesGenerated_direct(empl);
    }

	public static FixedPointNumber getExpensesGenerated_direct(final GnuCashEmployee empl) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashEmployeeVoucher vchSpec : empl.getPaidVouchers() ) {
//		    if ( vchSpec.getType().equals(GnuCashGenerInvoice.TYPE_EMPLOYEE) ) {
//		      GnuCashEmployeeVoucher vchSpec = new GnuCashEmployeeVoucherImpl(invcGen); 
			GnuCashEmployee empl2 = vchSpec.getEmployee();
			if ( empl2.getID().equals(empl.getID()) ) {
				retval.add(vchSpec.getAmountWithoutTaxes());
			}
//            } // if vchSpec type
		} // for

		return retval;
    }

    // -------------------------------------

	public static FixedPointNumber getOutstandingValue(GnuCashEmployee empl) {
    	return getOutstandingValue_direct(empl);
    }

	public static FixedPointNumber getOutstandingValue_direct(GnuCashEmployee empl) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashEmployeeVoucher vchSpec : empl.getUnpaidVouchers() ) {
//            if ( vchSpec.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//              GnuCashEmployeeVoucher vchSpec = new GnuCashEmployeeVoucherImpl(invcGen); 
			GnuCashEmployee empl2 = vchSpec.getEmployee();
			if ( empl2.getID().equals(empl.getID()) ) {
				retval.add(vchSpec.getAmountUnpaidWithTaxes());
			}
//            } // if vchSpec type
		} // for

		return retval;
    }

}
