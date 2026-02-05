package org.gnucash.api.read.impl.hlp.invc;

import java.math.BigDecimal;

import org.gnucash.api.Const;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenerInvcEntr_EmplVch_FP {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerInvcEntr_EmplVch_FP.class);

    // ---------------------------------------------------------------

    public static FixedPointNumber getEmplVchApplicableTaxPercent(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getType() != GnuCashGenerInvoice.TYPE_EMPLOYEE )
			throw new WrongInvoiceTypeException();

		if ( ! entr.isEmplVchTaxable() ) {
			return new FixedPointNumber();
		}

		if ( entr.getJwsdpPeer().getEntryBTaxtable() != null ) {
			if ( ! entr.getJwsdpPeer().getEntryBTaxtable().getType().equals(Const.XML_DATA_TYPE_GUID) ) {
				LOGGER.error("getEmplVchApplicableTaxPercent: Employee voucher entry with id '" + entr.getID()
						+ "' has b-taxtable with type='" + entr.getJwsdpPeer().getEntryBTaxtable().getType() + "' != 'guid'");
			}
		}

		GCshTaxTable taxTab = null;
		try {
			taxTab = entr.getEmplVchTaxTable();
		} catch (TaxTableNotFoundException exc) {
			LOGGER.error("getEmplVchApplicableTaxPercent: Employee voucher entry with id '" + entr.getID()
					+ "' is taxable but JWSDP peer has no b-taxtable-entry! " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		// Cf. getInvcApplicableTaxPercent()
		if ( taxTab == null ) {
			LOGGER.error("getEmplVchApplicableTaxPercent: Employee voucher entry with id '" + entr.getID()
					+ "' is taxable but has an unknown b-taxtable! " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		GCshTaxTableEntry taxTabEntr = taxTab.getEntries().get(0);
		if ( taxTabEntr.getType() == GCshTaxTableEntry.Type.VALUE ) {
			LOGGER.error("getEmplVchApplicableTaxPercent: Employee voucher entry with id '" + entr.getID()
					+ "' is taxable but has a b-taxtable of type '" + taxTabEntr.getType() + "' "
					+ "NOT IMPLEMENTED YET " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		FixedPointNumber val = taxTabEntr.getAmount();

		// the file contains, say, 19 for 19%, we need to convert it to 0,19.
		return val.copy().divide(new FixedPointNumber("100"));
    }

    // ---------------------------------------------------------------

    public static FixedPointNumber getEmplVchPrice(GnuCashGenerInvoiceEntry entr) {
    	if ( entr.getType() != GCshOwner.Type.EMPLOYEE )
    		throw new WrongInvoiceTypeException();

    	return new FixedPointNumber(entr.getJwsdpPeer().getEntryBPrice());
    }

    // ---------------------------------------------------------------

    public static FixedPointNumber getEmplVchSum(GnuCashGenerInvoiceEntry entr) {
    	return getEmplVchPrice(entr).multiply(entr.getQuantity());
    }

    public static FixedPointNumber getEmplVchSumInclTaxes(GnuCashGenerInvoiceEntry entr) {
		if ( entr.getJwsdpPeer().getEntryBTaxincluded() == 1 ) {
			return GenerInvcEntr_VendBll_FP.getVendBllSum(entr); // ::TODO ::CHECK
		}

		return getEmplVchSum(entr).multiply(getEmplVchApplicableTaxPercent(entr).add(BigDecimal.ONE));
    }

    public static FixedPointNumber getEmplVchSumExclTaxes(GnuCashGenerInvoiceEntry entr) {

    	// System.err.println("debug: GnuCashInvoiceEntryImpl.getSumExclTaxes():"
    	// taxIncluded="+jwsdpPeer.getEntryITaxincluded()+" getSum()="+getSum()+"
    	// getApplicableTaxPercent()="+getApplicableTaxPercent());

		if ( entr.getJwsdpPeer().getEntryBTaxincluded() == 0 ) {
			return getEmplVchSum(entr);
		}

		return getEmplVchSum(entr).divide(getEmplVchApplicableTaxPercent(entr).add(BigDecimal.ONE));
    }

}
