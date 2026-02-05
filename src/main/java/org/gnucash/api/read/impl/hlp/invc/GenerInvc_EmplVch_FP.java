package org.gnucash.api.read.impl.hlp.invc;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenerInvc_EmplVch_FP {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashGenerInvoiceImpl.class);

	// -----------------------------------------------------------------

	public static FixedPointNumber getEmplVchAmountUnpaidWithTaxes(GnuCashGenerInvoice invc) {
		// System.err.println("debug: GnuCashInvoiceImpl.getAmountUnpaid(): "
		// + "getVoucherAmountUnpaid()="+getVoucherAmountWithoutTaxes()+"
		// getVoucherAmountPaidWithTaxes()="+getAmountPaidWithTaxes() );

		return getEmplVchAmountWithTaxes(invc).copy().subtract(getEmplVchAmountPaidWithTaxes(invc));	}

	public static FixedPointNumber getEmplVchAmountPaidWithTaxes(GnuCashGenerInvoice invc) {
		FixedPointNumber takenFromPayableAccount = new FixedPointNumber();
		for ( GnuCashTransaction trx : invc.getPayingTransactions() ) {
			for ( GnuCashTransactionSplit split : trx.getSplits() ) {
				if ( split.getAccount().getType() == GnuCashAccount.Type.PAYABLE ) {
					if ( split.getValue().isPositive() ) {
						takenFromPayableAccount.add(split.getValue());
					}
				}
			} // split
		} // trx

		// System.err.println("getVoucherAmountPaidWithTaxes="+takenFromPayableAccount.doubleValue());

		return takenFromPayableAccount;
	}

	public static FixedPointNumber getEmplVchAmountPaidWithoutTaxes(GnuCashGenerInvoice invc) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval.add(entry.getEmplVchSumExclTaxes());
			}
		}

		return retval;
	}

	public static FixedPointNumber getEmplVchAmountWithTaxes(GnuCashGenerInvoice invc) {
		FixedPointNumber retval = new FixedPointNumber();

		// Note: On the one hand, good practice and experience mandates,
		// in order to be calculating correctly, that the sums be computed 
		// without taxes first (grouped by tax%) and then the sums be 
		// multiplied by the resp. tax% values. 
		// On the other hand: We are calculating with BigDecimal, i.e.
		// with arbitrary precision, so it does not really matter.

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval.add(entry.getEmplVchSumInclTaxes());
			}
		}

		return retval;
	}

	public static FixedPointNumber getEmplVchAmountWithoutTaxes(GnuCashGenerInvoice invc) {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval.add(entry.getEmplVchSumExclTaxes());
			}
		}

		return retval;
	}

}
