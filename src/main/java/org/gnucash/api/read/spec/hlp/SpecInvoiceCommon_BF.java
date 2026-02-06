package org.gnucash.api.read.spec.hlp;

import org.apache.commons.numbers.fraction.BigFraction;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashCustomerInvoice
 * @see GnuCashEmployeeVoucher
 * @see GnuCashVendorBill
 * @see GnuCashJobInvoice
 */
public interface SpecInvoiceCommon_BF {

    BigFraction getAmountUnpaidWithTaxesRat();

    BigFraction getAmountPaidWithTaxesRat();

    BigFraction getAmountPaidWithoutTaxesRat();

    BigFraction getAmountWithTaxesRat();
    
    BigFraction getAmountWithoutTaxesRat();

}
