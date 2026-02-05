package org.gnucash.api.read.impl.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerInvc_EmplVch_BF {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashGenerInvoiceImpl.class);

	// -----------------------------------------------------------------

	public static BigFraction getEmplVchAmountUnpaidWithTaxes(GnuCashGenerInvoice invc) {
		// System.err.println("debug: GnuCashInvoiceImpl.getAmountUnpaid(): "
		// + "getVoucherAmountUnpaid()="+getVoucherAmountWithoutTaxes()+"
		// getVoucherAmountPaidWithTaxes()="+getAmountPaidWithTaxes() );

		return getEmplVchAmountWithTaxes(invc).subtract(getEmplVchAmountPaidWithTaxes(invc));	}

	public static BigFraction getEmplVchAmountPaidWithTaxes(GnuCashGenerInvoice invc) {
		BigFraction takenFromPayableAccount = BigFraction.ZERO;
		for ( GnuCashTransaction trx : invc.getPayingTransactions() ) {
			for ( GnuCashTransactionSplit split : trx.getSplits() ) {
				if ( split.getAccount().getType() == GnuCashAccount.Type.PAYABLE ) {
					if ( split.getValue().isPositive() ) {
						takenFromPayableAccount = takenFromPayableAccount.add(split.getValueRat());
					}
				}
			} // split
		} // trx

		// System.err.println("getVoucherAmountPaidWithTaxes="+takenFromPayableAccount.doubleValue());

		return takenFromPayableAccount;
	}

	public static BigFraction getEmplVchAmountPaidWithoutTaxes(GnuCashGenerInvoice invc) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval = retval.add(entry.getEmplVchSumExclTaxesRat());
			}
		}

		return retval;
	}

	public static BigFraction getEmplVchAmountWithTaxes(GnuCashGenerInvoice invc) {
		BigFraction retval = BigFraction.ZERO;

		// Note: On the one hand, good practice and experience mandates,
		// in order to be calculating correctly, that the sums be computed 
		// without taxes first (grouped by tax%) and then the sums be 
		// multiplied by the resp. tax% values. 
		// On the other hand: We are calculating with BigFraction, i.e.
		// exactly, so it does not really matter.

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval = retval.add(entry.getEmplVchSumInclTaxesRat());
			}
		}

		return retval;
	}

	public static BigFraction getEmplVchAmountWithoutTaxes(GnuCashGenerInvoice invc) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval = retval.add(entry.getEmplVchSumExclTaxesRat());
			}
		}

		return retval;
	}

}
