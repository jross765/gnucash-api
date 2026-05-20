package org.gnucash.api.read.impl.aux;

import java.time.LocalDate;

import javax.xml.datatype.XMLGregorianCalendar;

import org.gnucash.api.generated.GncBudget.BgtRecurrence;
import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshBudgetRecurrence;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshBudgetRecurrenceImpl extends GnuCashObjectImpl 
							          implements GCshBudgetRecurrence 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshBudgetRecurrenceImpl.class);

    // -----------------------------------------------------------

    protected final BgtRecurrence jwsdpPeer;

    // -----------------------------------------------------------
    
    protected final GnuCashBudget parent;

    // -----------------------------------------------------------

    /**
     * @param parent 
     * @param newPeer the JWSDP-object we are wrapping.
     * @param gcshFile 
     */
    @SuppressWarnings("exports")
    public GCshBudgetRecurrenceImpl(
    		final GnuCashBudget parent, 
    		final BgtRecurrence newPeer, 
    		final GnuCashFile gcshFile) {
    	super(gcshFile);
		
    	this.parent    = parent;
    	this.jwsdpPeer = newPeer;
    }

	// ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public BgtRecurrence getJwsdpPeer() {
    	return jwsdpPeer;
    }

    // -----------------------------------------------------------------

	@Override
	public GnuCashBudget getParent() {
		return parent;
	}

    // -----------------------------------------------------------------

	@Override
	public int getMult() {
		return jwsdpPeer.getRecurrenceMult();
	}

	// ::TODO move that mapping into the enum type,
	// analogously to the other enums.
	@Override
	public PeriodType getPeriodType() {
		String typeStr = getPeriodTypeStr();
		if ( typeStr.equals("day") ) {
			return PeriodType.DAY;
		} else if ( typeStr.equals("week") ) {
			return PeriodType.WEEK;			
		} else if ( typeStr.equals("month") ) {
			return PeriodType.MONTH;			
		} else if ( typeStr.equals("year") ) {
			return PeriodType.YEAR;			
		}
		
		return null; // Compiler happy
	}

	@Override
	public String getPeriodTypeStr() {
		return jwsdpPeer.getRecurrencePeriodType();
	}

	@Override
	public LocalDate getStart() {
		if ( jwsdpPeer.getRecurrenceStart() == null )
			return null;

		XMLGregorianCalendar cal = jwsdpPeer.getRecurrenceStart().getGdate();
		try {
			return LocalDate.of(cal.getYear(), cal.getMonth(), cal.getDay());
		} catch (Exception e) {
			IllegalStateException ex = new IllegalStateException("unparsable date '" + cal + "' in price!");
			ex.initCause(e);
			throw ex;
		}
	}

	@Override
	public String getStartStr() {
		if ( jwsdpPeer.getRecurrenceStart() == null )
			return null;

		if ( jwsdpPeer.getRecurrenceStart().getGdate() == null )
			return null;

		return jwsdpPeer.getRecurrenceStart().getGdate().toString();
	}

    // -----------------------------------------------------------------

    @Override
    public String toString() {
		String result = "GCshBudgetRecurrenceImpl [";

		try {
			result += "mult=" + getMult();
		} catch (Exception e) {
			result += "mult=" + "ERROR";
		}

		try {
			result += ", period-type=" + getPeriodType();
		} catch (Exception e) {
			result += ", period-type=" + "ERROR";
		}

		try {
			result += ", start=" + getStart();
		} catch (Exception e) {
			result += ", start=" + "ERROR";
		}

		result += "]";

		return result;
    }

}
