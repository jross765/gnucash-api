package org.gnucash.api.read.aux;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.hlp.GnuCashObject;

public interface GCshBudgetRecurrence extends GnuCashObject {
	
	// Cf. https://github.com/Gnucash/gnucash/blob/stable/libgnucash/engine/Recurrence.h
	public enum PeriodType {
		// ::MAGIC
		// ::CHECK: As opposed to other values in GnuCash, the following ones
		// are not locale-specific.
		// [ Note: yes and no -- they seem to always use the English key
		// value in the file, regardless of the locale. However, the key
		// in our test data file has not exactly the same value as in GnuCash's
		// PO-file: "month" vs. "month(s)", although the entry has been 
		// generated with the official GUI (under locale de_DE). ]
		// ::TODO: Entries are missing, cf. above-mentioned file.
		DAY   ("day"),
		WEEK  ("week"),
		MONTH ("month"),
		YEAR  ("year");

		// ---

		private String code = "UNSET";

		// ---

		PeriodType(String code) {
			if ( code == null )
				throw new IllegalArgumentException("argument <code> is null");
			
			if ( code.isBlank() )
				throw new IllegalArgumentException("argument <code> is blank");
			
			this.code = code.trim();
		}

		// ---

		public String getCode() {
			return code;
		}

		// No typo!
		public static PeriodType valueOff(String code) {
			if ( code == null )	{
				throw new IllegalStateException( "argument <code> is null" );
			}

			if ( code.isBlank() ) {
				throw new IllegalStateException( "argument <code> is blank" );
			}

			for ( PeriodType val : values() ) {
				if ( val.getCode().equals( code.trim() ) ) {
					return val;
				}
			}

			return null;
		}
	}
	
	// ---------------------------------------------------------------

	GnuCashBudget getParent();

	// ---------------------------------------------------------------

    int        getMult();
    
    PeriodType getPeriodType();
    
    String     getPeriodTypeStr();
    
    LocalDate  getStart();
    
    String     getStartStr();
    
}
