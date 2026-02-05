package org.gnucash.api.read.impl.hlp;

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

public class GenerInvcEntr_VendBll_FP {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerInvcEntr_VendBll_FP.class);

    // ---------------------------------------------------------------

    public static FixedPointNumber getVendBllApplicableTaxPercent(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getType() != GnuCashGenerInvoice.TYPE_VENDOR && 
			 entr.getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		if ( ! ((GnuCashGenerInvoiceEntryImpl) entr).isVendBllTaxable_int() ) {
			return new FixedPointNumber();
		}

		if ( entr.getJwsdpPeer().getEntryBTaxtable() != null ) {
			if ( ! entr.getJwsdpPeer().getEntryBTaxtable().getType().equals(Const.XML_DATA_TYPE_GUID) ) {
				LOGGER.error("getVendBllApplicableTaxPercent: Vendor bill entry with id '" + entr.getID()
						+ "' has b-taxtable with type='" + entr.getJwsdpPeer().getEntryBTaxtable().getType() + "' != 'guid'");
			}
		}

		GCshTaxTable taxTab = null;
		try {
			taxTab = ((GnuCashGenerInvoiceEntryImpl) entr).getVendBllTaxTable_int();
		} catch (TaxTableNotFoundException exc) {
			LOGGER.error("getVendBllApplicableTaxPercent: Vendor bill entry with id '" + entr.getID()
					+ "' is taxable but JWSDP peer has no b-taxtable-entry! " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		// Cf. getInvcApplicableTaxPercent()
		if ( taxTab == null ) {
			LOGGER.error("getVendBllApplicableTaxPercent: Vendor bill entry with id '" + entr.getID()
					+ "' is taxable but has an unknown b-taxtable! " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		GCshTaxTableEntry taxTabEntr = taxTab.getEntries().get(0);
		if ( taxTabEntr.getType() == GCshTaxTableEntry.Type.VALUE ) {
			LOGGER.error("getVendBllApplicableTaxPercent: Vendor bill entry with id '" + entr.getID()
					+ "' is taxable but has a b-taxtable of type '" + taxTabEntr.getType() + "' "
					+ "NOT IMPLEMENTED YET " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		FixedPointNumber val = taxTabEntr.getAmount();

		// the file contains, say, 19 for 19%, we need to convert it to 0,19.
		return val.copy().divide(new FixedPointNumber("100"));
    }

    // ---------------------------------------------------------------

    public static FixedPointNumber getVendBllPrice(GnuCashGenerInvoiceEntry entr) {
    	if ( entr.getType() != GCshOwner.Type.VENDOR && 
    		 entr.getType() != GCshOwner.Type.JOB )
    		throw new WrongInvoiceTypeException();

    	return new FixedPointNumber(entr.getJwsdpPeer().getEntryBPrice());
    }

    // ---------------------------------------------------------------

    public static FixedPointNumber getVendBllSum(GnuCashGenerInvoiceEntry entr) {
    	return getVendBllSum_int(entr);
    }
    
    private static FixedPointNumber getVendBllSum_int(GnuCashGenerInvoiceEntry entr) {
    	return getVendBllPrice(entr).multiply(entr.getQuantity());
    }

    public static FixedPointNumber getVendBllSumInclTaxes(GnuCashGenerInvoiceEntry entr) {
    	return getVendBllSumInclTaxes_int(entr);
    }
    
    private static FixedPointNumber getVendBllSumInclTaxes_int(GnuCashGenerInvoiceEntry entr) {
    	if ( entr.getJwsdpPeer().getEntryBTaxincluded() == 1 ) {
    		return getVendBllSum_int(entr);
    	}

    	return getVendBllSum_int(entr).multiply(getVendBllApplicableTaxPercent(entr).add(BigDecimal.ONE));
    }

    public static FixedPointNumber getVendBllSumExclTaxes(GnuCashGenerInvoiceEntry entr) {
    	// System.err.println("debug: GnuCashInvoiceEntryImpl.getSumExclTaxes():"
    	// taxIncluded="+jwsdpPeer.getEntryITaxincluded()+" getSum()="+getSum()+"
    	// getApplicableTaxPercent()="+getApplicableTaxPercent());

    	if ( entr.getJwsdpPeer().getEntryBTaxincluded() == 0 ) {
    		return getVendBllSum_int(entr);
    	}

    	return getVendBllSum_int(entr).divide(getVendBllApplicableTaxPercent(entr).add(BigDecimal.ONE));
    }

}
