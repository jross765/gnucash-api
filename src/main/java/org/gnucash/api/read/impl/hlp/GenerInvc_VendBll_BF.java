package org.gnucash.api.read.impl.hlp;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerInvc_VendBll_BF {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GenerInvc_VendBll_BF.class);

	// -----------------------------------------------------------------

	public static BigFraction getVendBllAmountUnpaidWithTaxes(GnuCashGenerInvoice invc) {
		// System.err.println("debug: GnuCashInvoiceImpl.getAmountUnpaid(): "
		// + "getBillAmountUnpaid()="+getBillAmountWithoutTaxes()+"
		// getBillAmountPaidWithTaxes()="+getAmountPaidWithTaxes() );

		return getVendBllAmountWithTaxes(invc).subtract(getVendBllAmountPaidWithTaxes(invc));
	}

	public static BigFraction getVendBllAmountPaidWithTaxes(GnuCashGenerInvoice invc) {
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

		// System.err.println("getBillAmountPaidWithTaxes="+takenFromPayableAccount.doubleValue());

		return takenFromPayableAccount;
	}

	public static BigFraction getVendBllAmountPaidWithoutTaxes(GnuCashGenerInvoice invc) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval = retval.add(entry.getVendBllSumExclTaxesRat());
			}
		}

		return retval;
	}

	public static BigFraction getVendBllAmountWithTaxes(GnuCashGenerInvoice invc) {
		BigFraction retval = BigFraction.ZERO;

		// Note: On the one hand, good practice and experience mandates,
		// in order to be calculating correctly, that the sums be computed 
		// without taxes first (grouped by tax%) and then the sums be 
		// multiplied by the resp. tax% values. 
		// On the other hand: We are calculating with BigDecimal, i.e.
		// with arbitrary precision, so it does not really matter.

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval = retval.add(entry.getVendBllSumInclTaxesRat());
			}
		}

		return retval;
	}

	public static BigFraction getVendBllAmountWithoutTaxes(GnuCashGenerInvoice invc) {
		BigFraction retval = BigFraction.ZERO;

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			if ( entry.getType() == invc.getType() ) {
				retval = retval.add(entry.getVendBllSumExclTaxesRat());
			}
		}

		return retval;
	}

}
