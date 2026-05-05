package org.gnucash.api.read.hlp.invc;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

@Deprecated
public interface GnuCashGenerInvoice_Job_FP {
    
    FixedPointNumber getJobInvcAmountUnpaidWithTaxes();

    FixedPointNumber getJobInvcAmountPaidWithTaxes();

    FixedPointNumber getJobInvcAmountPaidWithoutTaxes();

    FixedPointNumber getJobInvcAmountWithTaxes();

    FixedPointNumber getJobInvcAmountWithoutTaxes();

}
