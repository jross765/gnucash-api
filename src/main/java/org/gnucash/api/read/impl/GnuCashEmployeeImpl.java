package org.gnucash.api.read.impl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.generated.GncGncEmployee;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.impl.aux.GCshAddressImpl;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.base.basetypes.simple.GCshEmplID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GnuCashEmployeeImpl extends GnuCashObjectImpl 
                                 implements GnuCashEmployee 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashEmployeeImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncGncEmployee jwsdpPeer;

    /**
     * The currencyFormat to use for default-formating.<br/>
     * Please access only using {@link #getCurrencyFormat()}.
     *
     * @see #getCurrencyFormat()
     */
    private NumberFormat currencyFormat = null;

    // ---------------------------------------------------------------

    /**
     * @param peer    the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     */
    @SuppressWarnings("exports")
    public GnuCashEmployeeImpl(final GncGncEmployee peer, final GnuCashFile gcshFile) {
		super(gcshFile);

//		if (peer.getEmployeeSlots() == null) {
//	    	peer.setEmployeeSlots(getJwsdpPeer().getEmployeeSlots());
//		}

		jwsdpPeer = peer;
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncGncEmployee getJwsdpPeer() {
    	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
	public GCshEmplID getID() {
    	return new GCshEmplID(jwsdpPeer.getEmployeeGuid().getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public String getNumber() {
    	return jwsdpPeer.getEmployeeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public String getUserName() {
    	return jwsdpPeer.getEmployeeUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public GCshAddress getAddress() {
    	return new GCshAddressImpl(jwsdpPeer.getEmployeeAddr(), getGnuCashFile());
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public String getLanguage() {
    	return jwsdpPeer.getEmployeeLanguage();
    }

    // ---------------------------------------------------------------

    /**
     * @return the currency-format to use if no locale is given.
     */
    protected NumberFormat getCurrencyFormat() {
    	if (currencyFormat == null) {
    		currencyFormat = NumberFormat.getCurrencyInstance();
    	}

    	return currencyFormat;
    }

    // ---------------------------------------------------------------

//    /**
//     * {@inheritDoc}
//     */
//    public String getTaxTableID() {
//	GncGncEmployee.EmployeeTaxtable emplTaxtable = jwsdpPeer.getEmployeeTaxtable();
//	if (emplTaxtable == null) {
//	    return null;
//	}
//
//	return emplTaxtable.getValue();
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    public GCshTaxTable getTaxTable() {
//	String id = getTaxTableID();
//	if (id == null) {
//	    return null;
//	}
//	return getGnuCashFile().getTaxTableByID(id);
//    }

    // ---------------------------------------------------------------

    /**
     * date is not checked so invoiced that have entered payments in the future are
     * considered Paid.
     *
     * @return the current number of Unpaid invoices
     */
    @Override
    public int getNofOpenVouchers() {
    	return getGnuCashFile().getUnpaidVouchersForEmployee(this).size();
    }

    // -------------------------------------

    /**
     * @return the net sum of payments for invoices to this client
     */
    @Override
	public FixedPointNumber getExpensesGenerated() {
    	return getExpensesGenerated_direct();
    }

    /**
     * @return the net sum of payments for invoices to this client
     */
    @Override
	public FixedPointNumber getExpensesGenerated_direct() {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashEmployeeVoucher vchSpec : getPaidVouchers() ) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_EMPLOYEE) ) {
//		      GnuCashEmployeeVoucher vchSpec = new GnuCashEmployeeVoucherImpl(invcGen); 
			GnuCashEmployee empl = vchSpec.getEmployee();
			if ( empl.getID().equals(this.getID()) ) {
				retval.add(vchSpec.getAmountWithoutTaxes());
			}
//            } // if vch type
		} // for

		return retval;
    }

    /**
     * @return formatted according to the current locale's currency-format
     *  
     * @see #getExpensesGenerated()
     */
    @Override
	public String getExpensesGeneratedFormatted() {
    	return getCurrencyFormat().format(getExpensesGenerated());
    }

    /**
     * @param lcl the locale to format for
     * @return formatted according to the given locale's currency-format
     *  
     * @see #getExpensesGenerated()
     */
    @Override
	public String getExpensesGeneratedFormatted(final Locale lcl) {
    	return NumberFormat.getCurrencyInstance(lcl).format(getExpensesGenerated());
    }

    // -------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     * 
     * @see #getOutstandingValue_direct()
     */
    @Override
	public FixedPointNumber getOutstandingValue() {
    	return getOutstandingValue_direct();
    }

    /**
     * @return the sum of left to pay Unpaid invoiced
     */
    @Override
	public FixedPointNumber getOutstandingValue_direct() {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashEmployeeVoucher vchSpec : getUnpaidVouchers() ) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//              GnuCashEmployeeVoucher vchSpec = new GnuCashEmployeeVoucherImpl(invcGen); 
			GnuCashEmployee empl = vchSpec.getEmployee();
			if ( empl.getID().equals(this.getID()) ) {
				retval.add(vchSpec.getAmountUnpaidWithTaxes());
			}
//            } // if invc type
		} // for

		return retval;
    }

    /**
     * @return Formatted according to the current locale's currency-format
     *  
     * @see #getOutstandingValue()
     */
    @Override
	public String getOutstandingValueFormatted() {
    	return getCurrencyFormat().format(getOutstandingValue());
    }

    /**
     * @see #getOutstandingValue() Formatted according to the given locale's
     *      currency-format
     */
    @Override
	public String getOutstandingValueFormatted(final Locale lcl) {
    	return NumberFormat.getCurrencyInstance(lcl).format(getOutstandingValue());
    }

    // -----------------------------------------------------------------

    @Override
    public List<GnuCashGenerInvoice> getVouchers() {
    	List<GnuCashGenerInvoice> retval = new ArrayList<GnuCashGenerInvoice>();

    	for ( GnuCashEmployeeVoucher vch : getGnuCashFile().getVouchersForEmployee(this) ) {
    		retval.add(vch);
    	}
	
    	return retval;
    }

    @Override
    public List<GnuCashEmployeeVoucher> getPaidVouchers() {
    	return getGnuCashFile().getPaidVouchersForEmployee(this);
    }

    @Override
    public List<GnuCashEmployeeVoucher> getUnpaidVouchers() {
    	return getGnuCashFile().getUnpaidVouchersForEmployee(this);
    }

    // ------------------------------------------------------------

	@Override
	public String getUserDefinedAttribute(String name) {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeCore(jwsdpPeer.getEmployeeSlots(), name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeKeysCore(jwsdpPeer.getEmployeeSlots());
	}

    // ------------------------------------------------------------

    public static int getHighestNumber(GnuCashEmployee empl) {
    	return ((GnuCashFileImpl) empl.getGnuCashFile()).getHighestEmployeeNumber();
    }

    public static String getNewNumber(GnuCashEmployee empl) {
    	return ((GnuCashFileImpl) empl.getGnuCashFile()).getNewEmployeeNumber();
    }

    // -----------------------------------------------------------------

    @Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashEmployeeImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", number='");
		buffer.append(getNumber() + "'");

		buffer.append(", username='");
		buffer.append(getUserName() + "'");

		buffer.append("]");
		return buffer.toString();
    }

}
