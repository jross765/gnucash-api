package org.gnucash.api.read.impl.hlp.invc;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerInvc_CustInvc_BF {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GenerInvc_CustInvc_BF.class);

	// -----------------------------------------------------------------

	public static BigFraction getCustInvcAmountUnpaidWithTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		return getCustInvcAmountWithTaxes(invc).subtract(getCustInvcAmountPaidWithTaxes(invc));
	}

	public static BigFraction getCustInvcAmountPaidWithTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		BigFraction takenFromReceivableAccount = BigFraction.ZERO;
		for ( GnuCashTransaction trx : invc.getPayingTransactions() ) {
			for ( GnuCashTransactionSplit split : trx.getSplits() ) {
				if ( split.getAccount().getType() == GnuCashAccount.Type.RECEIVABLE ) {
					if ( !split.getValue().isPositive() ) {
						takenFromReceivableAccount = takenFromReceivableAccount.subtract(split.getValueRat());
					}
				}
			} // split
		} // trx

		return takenFromReceivableAccount;
	}

	public static BigFraction getCustInvcAmountPaidWithoutTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval = retval.add(entry.getCustInvcSumExclTaxesRat());
			}
		}

		return retval;
	}

	public static BigFraction getCustInvcAmountWithTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		BigFraction retval = BigFraction.ZERO;

		// Note: On the one hand, good practice and experience mandates,
		// in order to be calculating correctly, that the sums be computed 
		// without taxes first (grouped by tax%) and then the sums be 
		// multiplied by the resp. tax% values. 
		// On the other hand: We are calculating with BigFraction, i.e.
		// exactly, so it does not really matter.

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval = retval.add(entry.getCustInvcSumInclTaxesRat());
			}
		}

		return retval;
	}

	public static BigFraction getCustInvcAmountWithoutTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval = retval.add(entry.getCustInvcSumExclTaxesRat());
			}
		}

		return retval;
	}

}
