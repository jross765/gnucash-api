package org.gnucash.api.read.impl.hlp;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenerInvc_CustInvc_FP {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GenerInvc_CustInvc_FP.class);

	// -----------------------------------------------------------------

	public static FixedPointNumber getCustInvcAmountUnpaidWithTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		return getCustInvcAmountWithTaxes(invc).copy().subtract(getCustInvcAmountPaidWithTaxes(invc));
	}

	public static FixedPointNumber getCustInvcAmountPaidWithTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		FixedPointNumber takenFromReceivableAccount = new FixedPointNumber();
		for ( GnuCashTransaction trx : invc.getPayingTransactions() ) {
			for ( GnuCashTransactionSplit split : trx.getSplits() ) {
				if ( split.getAccount().getType() == GnuCashAccount.Type.RECEIVABLE ) {
					if ( !split.getValue().isPositive() ) {
						takenFromReceivableAccount.subtract(split.getValue());
					}
				}
			} // split
		} // trx

		return takenFromReceivableAccount;
	}

	public static FixedPointNumber getCustInvcAmountPaidWithoutTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval.add(entry.getCustInvcSumExclTaxes());
			}
		}

		return retval;
	}

	public static FixedPointNumber getCustInvcAmountWithTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		FixedPointNumber retval = new FixedPointNumber();

		// Note: On the one hand, good practice and experience mandates,
		// in order to be calculating correctly, that the sums be computed 
		// without taxes first (grouped by tax%) and then the sums be 
		// multiplied by the resp. tax% values. 
		// On the other hand: We are calculating with BigDecimal, i.e.
		// with arbitrary precision, so it does not really matter.

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval.add(entry.getCustInvcSumInclTaxes());
			}
		}

		return retval;
	}

	public static FixedPointNumber getCustInvcAmountWithoutTaxes(GnuCashGenerInvoice invc) {
		if ( invc.getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 invc.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval.add(entry.getCustInvcSumExclTaxes());
			}
		}

		return retval;
	}

}
