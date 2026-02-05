package org.gnucash.api.read.impl.hlp;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.Const;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerInvcEntr_CustInvc_BF {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerInvcEntr_CustInvc_BF.class);

    // ---------------------------------------------------------------

    public static BigFraction getCustInvcApplicableTaxPercent(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 entr.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		if ( ! ((GnuCashGenerInvoiceEntryImpl) entr).isCustInvcTaxable_int() ) {
			return BigFraction.ZERO;
		}

		if ( entr.getJwsdpPeer().getEntryITaxtable() != null ) {
			if ( ! entr.getJwsdpPeer().getEntryITaxtable().getType().equals(Const.XML_DATA_TYPE_GUID) ) {
				LOGGER.error("getCustInvcApplicableTaxPercent: Customer invoice entry with id '" + entr.getID()
						+ "' has i-taxtable with type='" + entr.getJwsdpPeer().getEntryITaxtable().getType() + "' != 'guid'");
			}
		}

		GCshTaxTable taxTab = null;
		try {
			taxTab = ((GnuCashGenerInvoiceEntryImpl) entr).getCustInvcTaxTable_int();
		} catch (TaxTableNotFoundException exc) {
			LOGGER.error("getCustInvcApplicableTaxPercent: Customer invoice entry with id '" + entr.getID()
					+ "' is taxable but JWSDP peer has no i-taxtable-entry! " + "Assuming 0%");
			return BigFraction.ZERO;
		}

		// ::CHECK: Still necessary?
		if ( taxTab == null ) {
			LOGGER.error("getCustInvcApplicableTaxPercent: Customer invoice entry with id '" + entr.getID()
					+ "' is taxable but has an unknown i-taxtable! " + "Assuming 0%");
			return BigFraction.ZERO;
		}

		GCshTaxTableEntry taxTabEntr = taxTab.getEntries().get(0);
		if ( taxTabEntr.getType() == GCshTaxTableEntry.Type.VALUE ) {
			LOGGER.error("getCustInvcApplicableTaxPercent: Customer invoice entry with id '" + entr.getID()
					+ "' is taxable but has a i-taxtable of type '" + taxTabEntr.getType() + "' "
					+ "NOT IMPLEMENTED YET " + "Assuming 0%");
			return BigFraction.ZERO;
		}

		BigFraction val = taxTabEntr.getAmountRat();

		// the file contains, say, 19 for 19%, we need to convert it to 0,19.
		return val.divide(BigFraction.of(100));
    }

    // ---------------------------------------------------------------

    public static BigFraction getCustInvcPrice(GnuCashGenerInvoiceEntry entr) {
    	if ( entr.getType() != GCshOwner.Type.CUSTOMER && 
    		 entr.getType() != GCshOwner.Type.JOB )
    		throw new WrongInvoiceTypeException();

    	return BigFraction.parse(entr.getJwsdpPeer().getEntryIPrice());
    }

    // ---------------------------------------------------------------

    public static BigFraction getCustInvcSum(GnuCashGenerInvoiceEntry entr) {
    	return getCustInvcPrice(entr).multiply(entr.getQuantityRat());
    }

    public static BigFraction getCustInvcSumInclTaxes(GnuCashGenerInvoiceEntry entr) {
    	if ( entr.getJwsdpPeer().getEntryITaxincluded() == 1 ) {
    		return getCustInvcSum(entr);
    	}

    	return getCustInvcSum(entr).multiply(getCustInvcApplicableTaxPercent(entr).add(BigFraction.ONE));
    }

    public static BigFraction getCustInvcSumExclTaxes(GnuCashGenerInvoiceEntry entr) {
    	// System.err.println("debug: GnuCashInvoiceEntryImpl.getSumExclTaxes():"
    	// taxIncluded="+jwsdpPeer.getEntryITaxincluded()+" getSum()="+getSum()+"
    	// getApplicableTaxPercent()="+getApplicableTaxPercent());

    	if ( entr.getJwsdpPeer().getEntryITaxincluded() == 0 ) {
    		return getCustInvcSum(entr);
    	}

    	return getCustInvcSum(entr).divide(getCustInvcApplicableTaxPercent(entr).add(BigFraction.ONE));
    }

}
