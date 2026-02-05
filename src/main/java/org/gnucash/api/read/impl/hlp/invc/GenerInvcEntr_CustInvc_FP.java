package org.gnucash.api.read.impl.hlp.invc;

import java.math.BigDecimal;

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

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenerInvcEntr_CustInvc_FP {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerInvcEntr_CustInvc_FP.class);

    // ---------------------------------------------------------------

    public static FixedPointNumber getCustInvcApplicableTaxPercent(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 entr.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		if ( ! ((GnuCashGenerInvoiceEntryImpl) entr).isCustInvcTaxable_int() ) {
			return new FixedPointNumber();
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
			return new FixedPointNumber("0");
		}

		// ::CHECK: Still necessary?
		if ( taxTab == null ) {
			LOGGER.error("getCustInvcApplicableTaxPercent: Customer invoice entry with id '" + entr.getID()
					+ "' is taxable but has an unknown i-taxtable! " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		GCshTaxTableEntry taxTabEntr = taxTab.getEntries().get(0);
		if ( taxTabEntr.getType() == GCshTaxTableEntry.Type.VALUE ) {
			LOGGER.error("getCustInvcApplicableTaxPercent: Customer invoice entry with id '" + entr.getID()
					+ "' is taxable but has a i-taxtable of type '" + taxTabEntr.getType() + "' "
					+ "NOT IMPLEMENTED YET " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		FixedPointNumber val = taxTabEntr.getAmount();

		// the file contains, say, 19 for 19%, we need to convert it to 0,19.
		return val.copy().divide(new FixedPointNumber("100"));
    }

    // ---------------------------------------------------------------

    public static FixedPointNumber getCustInvcPrice(GnuCashGenerInvoiceEntry entr) {
    	if ( entr.getType() != GCshOwner.Type.CUSTOMER && 
    		 entr.getType() != GCshOwner.Type.JOB )
    		throw new WrongInvoiceTypeException();

    	return new FixedPointNumber(entr.getJwsdpPeer().getEntryIPrice());
    }

    // ---------------------------------------------------------------

    public static FixedPointNumber getCustInvcSum(GnuCashGenerInvoiceEntry entr) {
    	return getCustInvcPrice(entr).multiply(entr.getQuantity());
    }

    public static FixedPointNumber getCustInvcSumInclTaxes(GnuCashGenerInvoiceEntry entr) {
    	if ( entr.getJwsdpPeer().getEntryITaxincluded() == 1 ) {
    		return getCustInvcSum(entr);
    	}

    	return getCustInvcSum(entr).multiply(getCustInvcApplicableTaxPercent(entr).add(BigDecimal.ONE));
    }

    public static FixedPointNumber getCustInvcSumExclTaxes(GnuCashGenerInvoiceEntry entr) {
    	// System.err.println("debug: GnuCashInvoiceEntryImpl.getSumExclTaxes():"
    	// taxIncluded="+jwsdpPeer.getEntryITaxincluded()+" getSum()="+getSum()+"
    	// getApplicableTaxPercent()="+getApplicableTaxPercent());

    	if ( entr.getJwsdpPeer().getEntryITaxincluded() == 0 ) {
    		return getCustInvcSum(entr);
    	}

    	return getCustInvcSum(entr).divide(getCustInvcApplicableTaxPercent(entr).add(BigDecimal.ONE));
    }

}
