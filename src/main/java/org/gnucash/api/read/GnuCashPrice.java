package org.gnucash.api.read;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.simple.GCshPrcID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * According to GnuCash's definition of a {@link GnuCashCommodity},
 * a price is an umbrella term comprising:
 * <ul>
 *   <li>A currency's exchange rate</li>
 *   <li>A security's quote</li>
 *   <li>A pseudo-security's price</li> 
 * </ul>
 */
public interface GnuCashPrice extends Comparable<GnuCashPrice>,
									  GnuCashObject 
{
	

    // For the following enums, cf.:
    // https://github.com/GnuCash/gnucash/blob/stable/libgnucash/engine/gnc-pricedb.h
    
    public enum Type {
	
    	LAST        ( "last" ),
    	TRANSACTION ( "transaction" ),
    	UNKNOWN     ( "unknown" );
	
    	// ---
	      
    	private String code = "UNSET";

    	// ---
	      
    	Type(String code) {
    		this.code = code;
    	}
	      
    	// ---
		
    	public String getCode() {
    		return code;
    	}
		
    	// no typo!
    	public static Type valueOff(String code) {
    		for ( Type type : values() ) {
    			if ( type.getCode().equals(code) ) {
    				return type;
    			}
    		}
	    
    		return null;
    	}
    }
	
    public enum Source {
    	
    	EDIT_DLG          ( "user:price-editor" ), 
    	FQ                ( "Finance::Quote" ),
    	USER_PRICE        ( "user:price" ),
    	XFER_DLG_VAL      ( "user:xfer-dialog" ),
    	SPLIT_REG         ( "user:split-register" ),
    	SPLIT_IMPORT      ( "user:split-import" ),
    	STOCK_SPLIT       ( "user:stock-split" ),
    	STOCK_TRANSACTION ( "user:stock-transaction" ),
    	INVOICE           ( "user:invoice-post" ),
    	TEMP              ( "temporary" ),
    	INVALID           ( "invalid" );
	
    	// ---
	      
    	private String code = "UNSET";

    	// ---
	      
    	Source(String code) {
    		this.code = code;
    	}
	      
    	// ---
		
    	public String getCode() {
    		return code;
    	}
		
    	// no typo!
    	public static Source valueOff(String code) {
    		for ( Source src : values() ) {
    			if ( src.getCode().equals(code) ) {
    				return src;
    			}
    		}
	    
    		return null;
    	}
    }
	
    // ---------------------------------------------------------------
	
    GCshPrcID getID();

    // ----------------------------

    GCshCmdtyCurrID getFromCmdtyCurrQualifID();

    GCshCmdtyID getFromCommodityQualifID();

    GCshCurrID getFromCurrencyQualifID();

    GnuCashCommodity getFromCommodity();

    String getFromCurrencyCode();

    GnuCashCommodity getFromCurrency();
    
    // ----------------------------

    GCshCurrID getToCurrencyQualifID();

    String getToCurrencyCode();

    GnuCashCommodity getToCurrency();

    // ----------------------------

    LocalDate getDate();
    
    LocalDateTime getDateTime();

    Source getSource();

    // String getSourceStr();

    Type getType();

    // String getTypeStr();

    FixedPointNumber getValue();
    
    String getValueFormatted();
    
}
