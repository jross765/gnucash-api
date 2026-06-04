package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncBudget;
import org.gnucash.api.generated.GncIdType;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.RecurrenceStartType;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.aux.GCshBudgetAccount;
import org.gnucash.api.read.aux.GCshBudgetPeriod;
import org.gnucash.api.read.aux.GCshBudgetRecurrence;
import org.gnucash.api.read.impl.GnuCashBudgetImpl;
import org.gnucash.api.read.impl.aux.GCshBudgetAccountImpl;
import org.gnucash.api.read.impl.aux.GCshBudgetPeriodImpl;
import org.gnucash.api.read.impl.aux.GCshBudgetRecurrenceImpl;
import org.gnucash.api.write.GnuCashWritableBudget;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.ObjectCascadeException;
import org.gnucash.api.write.aux.GCshWritableBudgetAccount;
import org.gnucash.api.write.aux.GCshWritableBudgetPeriod;
import org.gnucash.api.write.aux.GCshWritableBudgetRecurrence;
import org.gnucash.api.write.impl.aux.GCshWritableBudgetAccountImpl;
import org.gnucash.api.write.impl.aux.GCshWritableBudgetPeriodImpl;
import org.gnucash.api.write.impl.aux.GCshWritableBudgetRecurrenceImpl;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshBdgtID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnuCashBudgetImpl to allow read-write access instead of
 * read-only access.
 */
public class GnuCashWritableBudgetImpl extends GnuCashBudgetImpl 
                                       implements GnuCashWritableBudget 
{

    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableBudgetImpl.class);

    // ---------------------------------------------------------------

    // Our helper to implement the GnuCashWritableObject-interface.
    private final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

    // ---------------------------------------------------------------

    /**
     * Please use {@link GnuCashWritableFile#createWritableBudget(String)}
     *
     * @param file      the file we belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
	public GnuCashWritableBudgetImpl(
			final GncBudget jwsdpPeer,
			final GnuCashWritableFileImpl file) {
    	super(jwsdpPeer, file);
    }

    /**
     * Please use ${@link GnuCashWritableFile#createWritableBudget(String)}.
     *
     * @param file the file we belong to
     * @param id   the ID we shall have
     */
    protected GnuCashWritableBudgetImpl(final GnuCashWritableFileImpl file) {
    	super(createBudget_int(file, new GCshBdgtID( GCshID.getNew() ) ), file);
    }

    public GnuCashWritableBudgetImpl(final GnuCashBudgetImpl bdgt) {
    	super(bdgt.getJwsdpPeer(), bdgt.getGnuCashFile());
    }

    // ---------------------------------------------------------------

	/**
	 * Create a new account slot for a split found in the jaxb-data.
	 *
	 * @param jwsdpBdgtAcct the jaxb-data
	 * @return the new account-slot-instance
	 */
	protected GCshWritableBudgetAccountImpl createBudgetAccount(
			final Slot jwsdpBdgtAcct
		    /* , final boolean addToAcct */ ) { // ::TODO
		GCshWritableBudgetAccountImpl bdgtAcct = 
				new GCshWritableBudgetAccountImpl(this, jwsdpBdgtAcct, getWritableGnuCashFile());
		
		if ( helper.getPropertyChangeSupport() != null ) {
			 helper.getPropertyChangeSupport().firePropertyChange("accounts", null, getWritableAccounts());
		}

		return bdgtAcct;
	}

	/**
	 * @see GnuCashWritableTransaction#createWritableSplit(GnuCashAccount)
	 */
	@Override
	public GCshWritableBudgetAccount createWritableAccount(final GCshAcctID acctID) {
		if ( acctID == null ) {
			throw new IllegalArgumentException("argument <acctID> is null");
		}

		if ( ! acctID.isSet() ) {
			throw new IllegalArgumentException("argument <acctID> is not set");
		}

		GCshWritableBudgetAccountImpl bdgtAcct = new GCshWritableBudgetAccountImpl(this, acctID);
		addAccount(bdgtAcct);
		
		if ( helper.getPropertyChangeSupport() != null ) {
			 helper.getPropertyChangeSupport().firePropertyChange("accounts", null, getWritableAccounts());
		}
		
		return bdgtAcct;
	}

	/**
	 * Create a new split for a split found in the jaxb-data.
	 *
	 * @return the new recurrence-instance
	 */
	public GCshWritableBudgetRecurrenceImpl createWritableBudgetRecurrence() {
		GCshWritableBudgetRecurrenceImpl bdgtRecurr = 
				new GCshWritableBudgetRecurrenceImpl((GCshBudgetRecurrenceImpl) getRecurrence());
		
		if ( helper.getPropertyChangeSupport() != null ) {
			 helper.getPropertyChangeSupport().firePropertyChange("recurrence", null, getWritableAccounts());
		}

		return bdgtRecurr;
	}

    /**
     * Creates a new budget and adds it to the given GnuCash file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param guid the ID we shall have
     * @return a new jwsdp-peer already entered into th jwsdp-peer of the file
     */
    protected static GncBudget createBudget_int(
    		final GnuCashWritableFileImpl file, 
    		final GCshBdgtID newID) {
		if ( file == null ) {
			throw new IllegalArgumentException("argument <file> is null");
		}

		if ( newID == null ) {
			throw new IllegalArgumentException("argument <newID> is null");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("argument <newID> is not set");
		}
    
        ObjectFactory factory = file.getObjectFactory();
    
        GncBudget jwsdpBdgt = factory.createGncBudget();
    
		{
			GncIdType id = factory.createGncIdType();
			id.setType(Const.XML_DATA_TYPE_GUID);
			id.setValue(newID.toString());
			jwsdpBdgt.setBgtId(id);
		}
        
        jwsdpBdgt.setBgtName("no name given");
    
		{
			// ::TODO: Umstellen auf GCshWritableBudgetRecurreceImpl.createBudgetRecurrence()
			GncBudget.BgtRecurrence jwsdpBdgtRecurr = factory.createGncBudgetBgtRecurrence();
			jwsdpBdgtRecurr.setRecurrenceMult(0);
			jwsdpBdgtRecurr.setRecurrencePeriodType(GCshBudgetRecurrence.PeriodType.MONTH.getCode());
			
			RecurrenceStartType jwsdpBdgtRecurrStartType = factory.createRecurrenceStartType();
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

	        jwsdpBdgt.setBgtRecurrence(jwsdpBdgtRecurr);
		}
        
        file.getRootElement().getGncBook().getBookElements().add(jwsdpBdgt);
        file.setModified(true);
    
        LOGGER.debug("createBudget_int: Created new budget (core): " + jwsdpBdgt.getBgtId());
        
        return jwsdpBdgt;
    }

    // ---------------------------------------------------------------

//	protected void setAddress(final GCshWritableAddressImpl addr) {
//		super.setAddress(addr);
//	}

    /**
     * Delete this Budget and remove it from the file.
     * @throws ObjectCascadeException 
     *
     * @see GnuCashWritableBudget#remove()
     */
    @Override
    public void remove() throws ObjectCascadeException {
    	if ( hasAccounts() ) {
    		throw new ObjectCascadeException();
    	}
    	
//    	for ( GCshBudgetAccount bdgtAcct : getAccounts() ) {
//    		if ( bdgtAcct.hasPeriods() ) {
//        		throw new ObjectCascadeException();
//    		}
//    	}

    	GncBudget peer = jwsdpPeer;
    	getWritableGnuCashFile().getRootElement().getGncBook().getBookElements().remove(peer);
    	getWritableGnuCashFile().removeBudget(this);
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
     * @see GnuCashWritableBudget#setName(java.lang.String)
     */
    @Override
    public void setName(final String name) {
    	if ( name == null ) {
    		throw new IllegalArgumentException("argument <name> is null");
    	}

    	if ( name.isBlank() ) {
    		throw new IllegalArgumentException("argument <name> is blank");
    	}

    	String oldName = getName();
    	jwsdpPeer.setBgtName(name);
    	getWritableGnuCashFile().setModified(true);

    	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
    	if ( propertyChangeSupport != null) {
    		propertyChangeSupport.firePropertyChange("name", oldName, name);
    	}
    }

	@Override
	public void setDescription(String descr) {
    	if ( descr == null ) {
    		throw new IllegalArgumentException("argument <descr> is null");
    	}

    	// Sic: May be blank
//    	if ( descr.isBlank() ) {
//    		throw new IllegalArgumentException("argument <descr> is blank");
//    	}

    	String oldDescr = getDescription();
    	jwsdpPeer.setBgtDescription(descr);
    	getWritableGnuCashFile().setModified(true);

    	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
    	if ( propertyChangeSupport != null) {
    		propertyChangeSupport.firePropertyChange("description", oldDescr, descr);
    	}
	}

	@Override
	public GCshWritableBudgetRecurrence getWritableRecurrence() {
		return new GCshWritableBudgetRecurrenceImpl( (GCshBudgetRecurrenceImpl) getRecurrence() );
	}

	@Override
	public void setRecurrence(GCshWritableBudgetRecurrence bdgtRecurr) {
		if ( bdgtRecurr == null ) {
			throw new IllegalArgumentException("argument <bdgtRecurr> is null");
		}

    	GCshBudgetRecurrence oldRecurr = getRecurrence();
    	
    	jwsdpPeer.setBgtRecurrence(((GCshWritableBudgetRecurrenceImpl) bdgtRecurr).getJwsdpPeer());

		getWritableGnuCashFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if ( propertyChangeSupport != null) {
			propertyChangeSupport.firePropertyChange("recurrence", oldRecurr, bdgtRecurr);
		}
	}

    // ---------------------------------------------------------------

	@Override
	public void clearAccounts() {
		if ( getAccounts() == null )
			return;
		
		if ( getAccounts().size() == 0 )
			return;
		
		// ---
		
		List<GCshBudgetAccount> oldAccts = getAccounts();
		
		for ( int i = jwsdpPeer.getBgtSlots().getSlot().size(); i >= 0; i-- ) {
			jwsdpPeer.getBgtSlots().getSlot().remove(i);
		}

		getWritableGnuCashFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if ( propertyChangeSupport != null) {
			propertyChangeSupport.firePropertyChange("accounts", oldAccts, null);
		}
	}

	@Override
	public void removeAccount(final GCshBudgetAccount bdgtAcct) {
		if ( bdgtAcct == null ) {
			throw new IllegalArgumentException("argument <bdgtAcct> is null");
		}
		
		if ( getAccounts() == null )
			return;
		
		if ( getAccounts().size() == 0 )
			return;
		
		// ---
		
		List<GCshBudgetAccount> oldAccts = getAccounts();
		
		jwsdpPeer.getBgtSlots().getSlot()
			.remove(((GCshBudgetAccountImpl) bdgtAcct).getJwsdpPeer());
		
		getWritableGnuCashFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if ( propertyChangeSupport != null) {
			propertyChangeSupport.firePropertyChange("accounts", oldAccts, getAccounts());
		}
	}

	public void addAccount(final GCshWritableBudgetAccountImpl bdgtAcct) {
		// Important:
    	if ( jwsdpPeer.getBgtSlots() == null ) {
    		ObjectFactory fact = getWritableGnuCashFile().getObjectFactory();
    		SlotsType slots = fact.createSlotsType();
    		jwsdpPeer.setBgtSlots(slots);
    	}

		super.addAccount(bdgtAcct);		
	}

	@Override
	public List<GCshWritableBudgetAccount> getWritableAccounts() {
		List<GCshWritableBudgetAccount> result = new ArrayList<GCshWritableBudgetAccount>();
		
		for ( GCshBudgetAccount bdgtAcct : super.getAccounts() ) {
			GCshWritableBudgetAccount newBdgtAcct = new GCshWritableBudgetAccountImpl((GCshBudgetAccountImpl) bdgtAcct);
		    result.add(newBdgtAcct);
		}

		return result;
	}

	@Override
	public GCshWritableBudgetAccount getWritableAccount(GCshAcctID acctID) {
		if ( acctID == null ) {
			throw new IllegalArgumentException("argument <acctID> is null");
		}

		if ( ! acctID.isSet() ) {
			throw new IllegalArgumentException("argument <acctID> is not set");
		}

		return new GCshWritableBudgetAccountImpl( (GCshBudgetAccountImpl) getAccount(acctID) );
	}

    // ----------------------------

	@Override
	public void clearPeriods(GCshAcctID acctID) {
		List<GCshBudgetPeriod> oldPrds = getPeriods(acctID);
		
		for ( GCshWritableBudgetAccount bdgtAcct : getWritableAccounts() ) {
			bdgtAcct.clearPeriods();
		}
		
		getWritableGnuCashFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if ( propertyChangeSupport != null) {
			propertyChangeSupport.firePropertyChange("periods", oldPrds, getAccounts());
		}
	}

	@Override
	public List<GCshWritableBudgetPeriod> getWritablePeriods(final GCshAcctID acctID) {
		List<GCshWritableBudgetPeriod> result = new ArrayList<GCshWritableBudgetPeriod>();

		for ( GCshBudgetPeriod bdgtPrd : super.getPeriods(acctID) ) {
			GCshWritableBudgetPeriod newBdgtPrd = new GCshWritableBudgetPeriodImpl((GCshBudgetPeriodImpl) bdgtPrd);
		    result.add(newBdgtPrd);
		}

		return result;
	}

    // ---------------------------------------------------------------

    @Override
    public String toString() {
		String result = "GCshWritableBudgetImpl [";

		result += "id=" + getID() + ", ";
		result += "name='" + getName() + "', ";
		result += "descr='" + getDescription() + "', ";
		result += "recurr=" + getRecurrence() + ", ";
		
		result += "nof-periods=" + getNofPeriods() + ", ";
		result += "nof-accounts=" + getAccounts().size();
		
		result += "]";

		return result;
    }

}
