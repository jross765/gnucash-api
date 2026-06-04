package org.gnucash.api.write.impl.aux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.gnucash.api.generated.GncBudget;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.RecurrenceStartType;
import org.gnucash.api.read.aux.GCshBudgetRecurrence;
import org.gnucash.api.read.impl.aux.GCshBudgetRecurrenceImpl;
import org.gnucash.api.write.GnuCashWritableBudget;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.aux.GCshWritableBudgetRecurrence;
import org.gnucash.api.write.impl.DateMappingException;
import org.gnucash.api.write.impl.GnuCashWritableBudgetImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GCshAddressImpl to allow read-write access instead of
 * read-only access.
 */
public class GCshWritableBudgetRecurrenceImpl extends GCshBudgetRecurrenceImpl 
                                              implements GCshWritableBudgetRecurrence 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritableBudgetRecurrenceImpl.class);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GCshWritableBudgetRecurrenceImpl(
    		final GnuCashWritableBudget parent,
    		final GncBudget.BgtRecurrence jwsdpPeer,
    		final GnuCashWritableFile gcshFile) {
    	super(parent, jwsdpPeer, gcshFile);
    }

    public GCshWritableBudgetRecurrenceImpl(final GCshBudgetRecurrenceImpl bdgtRecurr) {
    	super(bdgtRecurr.getParent(), bdgtRecurr.getJwsdpPeer(), bdgtRecurr.getGnuCashFile());
    }

	public GCshWritableBudgetRecurrenceImpl(final GnuCashWritableBudgetImpl bdgt) {
		super(bdgt,
				createBudgetRecurrence_int(bdgt.getWritableGnuCashFile(), bdgt), 
				bdgt.getGnuCashFile());

		bdgt.setRecurrence(this);
	}

	// ---------------------------------------------------------------

    /**
     * The GnuCash file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnuCashWritableFileImpl getWritableGnuCashFile() {
    	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

//    /**
//     * The GnuCash file is the top-level class to contain everything.
//     *
//     * @return the file we are associated with
//     */
//    @Override
//    public GnuCashWritableFileImpl getGnuCashFile() {
//    	return (GnuCashWritableFileImpl) super.getGnuCashFile();
//    }

	// ---------------------------------------------------------------
	
	/**
	 * Creates a new budget recurrence and adds it to the given GnuCash file.
	 */
	public static GncBudget.BgtRecurrence createBudgetRecurrence_int(
			final GnuCashWritableFileImpl file, 
			final GnuCashWritableBudgetImpl bdgt) {
		if ( file == null ) {
			throw new IllegalArgumentException("argument <file> is null");
		}

		if ( bdgt == null ) {
			throw new IllegalArgumentException("argument <bdgt> is null");
		}

		// This is needed because transaction.addAccount() later
		// must have an already built account.
		// Otherwise, it will create it from the JAXB-Data
		// thus 2 instances of this GnuCashWritableAddressImpl
		// will exist. One created in getRecurrence() from this JAXB-Data
		// the other is this object.
		bdgt.getRecurrence();

		ObjectFactory fact = file.getObjectFactory();

		GncBudget.BgtRecurrence jwsdpBdgtRecurr = fact.createGncBudgetBgtRecurrence();
		jwsdpBdgtRecurr.setRecurrenceMult(0);
		jwsdpBdgtRecurr.setRecurrencePeriodType(GCshBudgetRecurrence.PeriodType.MONTH.getCode());
			
		{
			RecurrenceStartType jwsdpBdgtRecurrStartType = fact.createRecurrenceStartType();
			try {
				// https://stackoverflow.com/questions/835889/java-util-date-to-xmlgregoriancalendar
				// https://stackoverflow.com/questions/49667772/localdate-to-gregoriancalendar-conversion
				// CAUTION: The following two lines with new Date() do not work so well.
//	            GregorianCalendar cal = new GregorianCalendar();
//	            cal.setTime(new Date());
				GregorianCalendar cal = GregorianCalendar
						.from( LocalDateTime.now().atZone(ZoneId.systemDefault()) );
				XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
				jwsdpBdgtRecurrStartType.setGdate(xmlCal);
			} catch ( DatatypeConfigurationException exc ) {
				throw new DateMappingException();
			}
		}

		bdgt.getJwsdpPeer().setBgtRecurrence(jwsdpBdgtRecurr);
		
		// NO, not here but in the calling method:
		// trx.addAccount(new GnuCashWritableBudgetAccountImpl(jwsdpBdgtAcct, bdgt.getGnuCashFile(), bdgt));
		file.setModified(true);
    
        LOGGER.debug("createBudgetAccount_int: Created new budget recurrence (core): " + jwsdpBdgtRecurr);
		
        return jwsdpBdgtRecurr;
	}

    // ---------------------------------------------------------------

	@Override
	public void setMult(final int mult) {
		if ( mult <= 0 ) {
			throw new IllegalArgumentException("argument <mult> is <= 0");
		}
		
		jwsdpPeer.setRecurrenceMult(mult);
	}

	@Override
	public void setPeriodType(final PeriodType type) {
		if ( type == null ) {
			throw new IllegalArgumentException("argument <type> is null");
		}
		
		jwsdpPeer.setRecurrencePeriodType( type.getCode() );
	}

	@Override
	public void setStart(final LocalDate date) {
		if ( date == null ) {
			throw new IllegalArgumentException("argument <date> is null");
		}
		
        try {
            // https://stackoverflow.com/questions/835889/java-util-date-to-xmlgregoriancalendar
			// https://stackoverflow.com/questions/49667772/localdate-to-gregoriancalendar-conversion
			// CAUTION: The following two lines with new Date() do not work so well.
//            GregorianCalendar cal = new GregorianCalendar();
//            cal.setTime(new Date());
			GregorianCalendar cal = GregorianCalendar
					.from( date.atStartOfDay().atZone(ZoneId.systemDefault()) );
            XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            
            if ( jwsdpPeer.getRecurrenceStart() != null ) {
            	jwsdpPeer.getRecurrenceStart().setGdate(xmlCal);
            } else {
        		ObjectFactory fact = getWritableGnuCashFile().getObjectFactory();
    			RecurrenceStartType jwsdpBdgtRecurrStartType = fact.createRecurrenceStartType();
    			jwsdpBdgtRecurrStartType.setGdate(xmlCal);
            }
        } catch ( DatatypeConfigurationException exc ) {
        	throw new DateMappingException();
        }
	}

    // ---------------------------------------------------------------

    @Override
    public String toString() {
		String result = "GCshWritableBudgetRecurrenceImpl [";

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
