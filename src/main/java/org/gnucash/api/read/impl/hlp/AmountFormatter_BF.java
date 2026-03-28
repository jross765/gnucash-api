package org.gnucash.api.read.impl.hlp;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmountFormatter_BF
{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AmountFormatter_BF.class);
	
	// ---------------------------------------------------------------

	public static String formatAmount(GnuCashFile gcshFile, 
									  BigFraction amt, GCshCmdtyID cmdtyID) {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}
		
		Locale lcl = Locale.getDefault();
		return formatAmount(gcshFile, 
							amt, cmdtyID, lcl);
	}
	
	public static String formatAmount(GnuCashFile gcshFile,
									  BigFraction amt, GCshCmdtyID cmdtyID, Locale lcl) {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <acct> is null");
		}
		
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}
		
		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}
		
		if ( lcl == null ) {
			throw new IllegalArgumentException("argument <lcl> is null");
		}
		
		NumberFormat nf = getCmdtyFormat(cmdtyID, lcl);
    	if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY ) {
			return nf.format(amt);
    	} else if ( cmdtyID.getType() == GCshCmdtyID.Type.SECURITY ) {
			GnuCashCommodity cmdty = gcshFile.getCommodityByID(cmdtyID);
			String secSymb = "(sec-symbol)";
			if ( cmdty.getSymbol() != null ) {
				secSymb = cmdty.getSymbol();
			} else if ( cmdty.getXCode() != null ) {
				secSymb = cmdty.getXCode();
			} else {
				secSymb = cmdty.toString();
			}
			nf = NumberFormat.getNumberInstance(lcl);
			return ( nf.format(amt.bigDecimalValue()) + " " + secSymb );
		}
    	
    	return "ERROR"; // Compiler happy
	}
	
	// ----------------------------

	public static NumberFormat getCurrencyFormat(GCshCmdtyID cmdtyID) {
		return getCmdtyFormat(cmdtyID, Locale.getDefault());
	}
	
	public static NumberFormat getCmdtyFormat(GCshCmdtyID cmdtyID, Locale lcl) {
		NumberFormat fmt = null;
		
		if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY ) {
			fmt = NumberFormat.getCurrencyInstance(lcl);
			Currency curr = Currency.getInstance( cmdtyID.getCode() );
			fmt.setCurrency(curr);
		} else {
			fmt = NumberFormat.getNumberInstance(lcl);
		}

		return fmt;
	}

}
