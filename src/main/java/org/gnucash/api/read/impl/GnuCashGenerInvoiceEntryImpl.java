package org.gnucash.api.read.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.generated.GncGncEntry.EntryBAcct;
import org.gnucash.api.generated.GncGncEntry.EntryBTaxtable;
import org.gnucash.api.generated.GncGncEntry.EntryBill;
import org.gnucash.api.generated.GncGncEntry.EntryIAcct;
import org.gnucash.api.generated.GncGncEntry.EntryITaxtable;
import org.gnucash.api.generated.GncGncEntry.EntryInvoice;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.hlp.AmountFormatter_FP;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.impl.hlp.invc.GenerInvcEntr_CustInvc_BF;
import org.gnucash.api.read.impl.hlp.invc.GenerInvcEntr_CustInvc_FP;
import org.gnucash.api.read.impl.hlp.invc.GenerInvcEntr_EmplVch_BF;
import org.gnucash.api.read.impl.hlp.invc.GenerInvcEntr_EmplVch_FP;
import org.gnucash.api.read.impl.hlp.invc.GenerInvcEntr_JobInvc_BF;
import org.gnucash.api.read.impl.hlp.invc.GenerInvcEntr_JobInvc_FP;
import org.gnucash.api.read.impl.hlp.invc.GenerInvcEntr_VendBll_BF;
import org.gnucash.api.read.impl.hlp.invc.GenerInvcEntr_VendBll_FP;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.gnucash.base.basetypes.simple.aux.GCshTaxTabID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Implementation of GnuCashInvoiceEntry that uses JWSDP.
 */
public class GnuCashGenerInvoiceEntryImpl extends GnuCashObjectImpl 
                                          implements GnuCashGenerInvoiceEntry 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashGenerInvoiceEntryImpl.class);

    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    protected static final DateTimeFormatter DATE_FORMAT_BOOK = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    protected static final DateTimeFormatter DATE_FORMAT_PRINT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Format of the JWSDP-Field for the entry-date.
     */
    protected static final DateFormat ENTRY_DATE_FORMAT = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncGncEntry jwsdpPeer;

    // ------------------------------

    /**
     * @see GnuCashGenerInvoice#getDateOpened()
     */
    protected ZonedDateTime date;

    private GCshAcctID myInvcAcctID;
    private GCshAcctID myBllAcctID;

    /**
     * The tax table in the GnuCash XML file. It defines what sales-tax-rates are
     * known.
     */
    private GCshTaxTable myInvcTaxTable;
    private GCshTaxTable myBillTaxTable;

    // ----------------------------

    private DateFormat dateFormat = null;

    /**
     * The numberFormat to use for non-currency-numbers for default-formating.<br/>
     * Please access only using {@link #getNumberFormat()}.
     *
     * @see #getNumberFormat()
     */
    private NumberFormat numberFormat = null;

    /**
     * The numberFormat to use for percentFormat-numbers for default-formating.<br/>
     * Please access only using {@link #getPercentFormat()}.
     *
     * @see #getPercentFormat()
     */
    private NumberFormat percentFormat = null;

    // ---------------------------------------------------------------

    /**
     * This constructor is used when an invoice is created by java-code.
     *
     * @param invc The invoice we belong to.
     * @param peer    the JWSDP-Object we are wrapping.
     * @param addEntrToInvc 
     */
    @SuppressWarnings("exports")
    public GnuCashGenerInvoiceEntryImpl(
	    final GnuCashGenerInvoice invc, 
	    final GncGncEntry peer,
	    final boolean addEntrToInvc) {
    	super(invc.getGnuCashFile());

//		if (peer.getEntrySlots() == null) {
//	    	peer.setEntrySlots(getJwsdpPeer().getEntrySlots());
//		}

    	this.myInvoice = invc;
    	this.jwsdpPeer = peer;

		if ( addEntrToInvc ) {
			if (invc != null) {
				invc.addGenerEntry(this);
			}
		}
    }

    /**
     * This code is used when an invoice is loaded from a file.
     *
     * @param gcshFile the file we belong to
     * @param peer    the JWSDP-object we are facading.
     * @param addEntrToInvc 
     */
    @SuppressWarnings("exports")
    public GnuCashGenerInvoiceEntryImpl(
	    final GncGncEntry peer, 
	    final GnuCashFileImpl gcshFile,
	    final boolean addEntrToInvc) {
    	super(gcshFile);

//		if (peer.getEntrySlots() == null) {
//	    	peer.setEntrySlots(getJwsdpPeer().getEntrySlots());
//		}

    	this.jwsdpPeer = peer;

    	if ( addEntrToInvc ) {
    		// an exception is thrown here if we have an invoice-ID but the invoice does not
    		// exist
    		GnuCashGenerInvoice invc = getGenerInvoice();
    		if (invc != null) {
    			// ...so we only need to handle the case of having no invoice-id at all
    			invc.addGenerEntry(this);
    		}
    	}
    }

    // Copy-constructor
    public GnuCashGenerInvoiceEntryImpl(final GnuCashGenerInvoiceEntry entry) {
    	super(entry.getGenerInvoice().getGnuCashFile());

//		if (entry.getJwsdpPeer().getEntrySlots() == null) {
//			HasUserDefinedAttributesImpl
//				.setSlotsInit(getJwsdpPeer().getEntrySlots(), 
//							  new ObjectFactory().createSlotsType());
//		} else {
//			HasUserDefinedAttributesImpl
//				.setSlotsInit(getJwsdpPeer().getEntrySlots(), 
//							  entry.getJwsdpPeer().getEntrySlots());
//		}

    	this.myInvoice = entry.getGenerInvoice();
    	this.jwsdpPeer = entry.getJwsdpPeer();
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GCshGenerInvcEntrID getID() {
    	return new GCshGenerInvcEntrID( jwsdpPeer.getEntryGuid().getValue() );
    }

    /**
     * {@inheritDoc}
     */
    public GCshOwner.Type getType() {
    	return getGenerInvoice().getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT);
    }

    /**
     * MAY RETURN NULL. {@inheritDoc}
     */
    public GCshGenerInvcID getGenerInvoiceID() {
		EntryInvoice entrInvc = null;
		EntryBill entrBill = null;

		try {
			entrInvc = jwsdpPeer.getEntryInvoice();
		} catch (Exception exc) {
			// ::EMPTY
		}

		try {
			entrBill = jwsdpPeer.getEntryBill();
		} catch (Exception exc) {
			// ::EMPTY
		}

		if ( entrInvc == null && entrBill == null ) {
			LOGGER.error("file contains an invoice-entry with GUID=" + getID()
					+ " without an invoice-element (customer) AND " + "without a bill-element (vendor)");
			return null;
		} else if ( entrInvc != null && entrBill == null ) {
			return new GCshGenerInvcID(entrInvc.getValue());
		} else if ( entrInvc == null && entrBill != null ) {
			return new GCshGenerInvcID(entrBill.getValue());
		} else if ( entrInvc != null && entrBill != null ) {
			LOGGER.error("file contains an invoice-entry with GUID=" + getID()
					+ " with BOTH an invoice-element (customer) and " + "a bill-element (vendor)");
			return null;
		}

		return null;
    }

    /**
     * The invoice this entry is from.
     */
    protected GnuCashGenerInvoice myInvoice;

    /**
     * {@inheritDoc}
     */
    public GnuCashGenerInvoice getGenerInvoice() {
		if ( myInvoice == null ) {
			GCshGenerInvcID invcId = getGenerInvoiceID();
			if ( invcId != null ) {
				myInvoice = getGnuCashFile().getGenerInvoiceByID(invcId);
				if ( myInvoice == null ) {
					throw new IllegalStateException("No generic invoice with ID '" + getGenerInvoiceID()
							+ "' for invoice entry with ID '" + getID() + "'");
				}
			}
		}
		return myInvoice;
    }

    // ---------------------------------------------------------------

    /**
     * @param aTaxtable the tax table to set
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException 
     */
    protected void setCustInvcTaxTable(final GCshTaxTable aTaxtable)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException {
    	if ( getType() != GCshOwner.Type.CUSTOMER && 
      		 getType() != GCshOwner.Type.JOB )
       		    throw new WrongInvoiceTypeException();

    	myInvcTaxTable = aTaxtable;
    }

    /**
     * @param aTaxtable the tax table to set
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException 
     */
    protected void setVendBllTaxTable(final GCshTaxTable aTaxtable)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException {
    	if ( getType() != GCshOwner.Type.VENDOR && 
   		     getType() != GCshOwner.Type.JOB )
    		    throw new WrongInvoiceTypeException();

    	myBillTaxTable = aTaxtable;
    }

    /**
     * @param aTaxtable the tax table to set
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException 
     */
    protected void setEmplVchTaxTable(final GCshTaxTable aTaxtable)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException {
    	if ( getType() != GCshOwner.Type.EMPLOYEE )
    		    throw new WrongInvoiceTypeException();

    	myBillTaxTable = aTaxtable;
    }

    protected void setJobInvcTaxTable(final GCshTaxTable aTaxtable)
	    throws TaxTableNotFoundException, UnknownInvoiceTypeException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			setCustInvcTaxTable(aTaxtable);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			setVendBllTaxTable(aTaxtable);
    	}

    /**
     * @return The tax table in the GnuCash XML file. It defines what sales-tax-rates
     *         are known.
     * @throws TaxTableNotFoundException
     */
    @Override
    public GCshTaxTable getCustInvcTaxTable() throws TaxTableNotFoundException {
    	return getCustInvcTaxTable_int();
    }
    
    public GCshTaxTable getCustInvcTaxTable_int() throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.CUSTOMER && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( myInvcTaxTable == null ) {
			EntryITaxtable taxTableEntry = jwsdpPeer.getEntryITaxtable();
			if ( taxTableEntry == null ) {
				throw new TaxTableNotFoundException();
			}

			String taxTableIdStr = taxTableEntry.getValue();
			if ( taxTableIdStr == null ) {
				LOGGER.error("getInvcTaxTable: Customer invoice with id '" + getID()
						+ "' is i-taxable but has empty id for the i-taxtable");
				return null;
			}
			GCshTaxTabID taxTableId = new GCshTaxTabID(taxTableIdStr);
			myInvcTaxTable = getGnuCashFile().getTaxTableByID(taxTableId);

			if ( myInvcTaxTable == null ) {
				LOGGER.error("getInvcTaxTable: Customer invoice with id '" + getID()
						+ "' is i-taxable but has an unknown " + "i-taxtable-id '" + taxTableId + "'");
			}
		} // myInvcTaxtable == null

		return myInvcTaxTable;
    }

    /**
     * @return The tax table in the GnuCash XML file. It defines what sales-tax-rates
     *         are known.
     * @throws TaxTableNotFoundException
     */
    @Override
    public GCshTaxTable getVendBllTaxTable() throws TaxTableNotFoundException {
    	return getVendBllTaxTable_int();
    }
    
    public GCshTaxTable getVendBllTaxTable_int() throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.VENDOR && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( myBillTaxTable == null ) {
			EntryBTaxtable taxTableEntry = jwsdpPeer.getEntryBTaxtable();
			if ( taxTableEntry == null ) {
				throw new TaxTableNotFoundException();
			}

			String taxTableIdStr = taxTableEntry.getValue();
			if ( taxTableIdStr == null ) {
				LOGGER.error("getBillTaxTable: Vendor bill with id '" + getID()
						+ "' is b-taxable but has empty id for the b-taxtable");
				return null;
			}
			GCshTaxTabID taxTableId = new GCshTaxTabID(taxTableIdStr);
			myBillTaxTable = getGnuCashFile().getTaxTableByID(taxTableId);

			if ( myBillTaxTable == null ) {
				LOGGER.error("getBillTaxTable: Vendor bill with id '" + getID() + "' is b-taxable but has an unknown "
						+ "b-taxtable-id '" + taxTableId + "'");
			}
		} // myBillTaxtable == null

		return myBillTaxTable;
    }

    /**
     * @return The taxt able in the GnuCash XML file. It defines what sales-tax-rates
     *         are known.
     * @throws TaxTableNotFoundException
     */
    @Override
    public GCshTaxTable getEmplVchTaxTable() throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.EMPLOYEE )
			throw new WrongInvoiceTypeException();

		if ( myBillTaxTable == null ) {
			EntryBTaxtable taxTableEntry = jwsdpPeer.getEntryBTaxtable();
			if ( taxTableEntry == null ) {
				throw new TaxTableNotFoundException();
			}

			String taxTableIdStr = taxTableEntry.getValue();
			if ( taxTableIdStr == null ) {
				LOGGER.error("getVoucherTaxTable: Employee voucher with id '" + getID()
						+ "' is b-taxable but has empty id for the b-taxtable");
				return null;
			}
			GCshTaxTabID taxTableId = new GCshTaxTabID(taxTableIdStr);
			myBillTaxTable = getGnuCashFile().getTaxTableByID(taxTableId);

			if ( myBillTaxTable == null ) {
				LOGGER.error("getVoucherTaxTable: Employee voucher with id '" + getID()
						+ "' is b-taxable but has an unknown " + "b-taxtable-id '" + taxTableId + "'");
			}
		} // myBillTaxtable == null

		return myBillTaxTable;
    }

    @Override
    public GCshTaxTable getJobInvcTaxTable() throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return getCustInvcTaxTable();
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return getVendBllTaxTable();

		return null; // Compiler happy
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public FixedPointNumber getCustInvcApplicableTaxPercent() {
    	return getCustInvcApplicableTaxPercent_int();
    }
    
    // Important separate it from public variant for
    // getJobInvcApplicableTaxPercent().
    // Else, we would have funny effects.
    private FixedPointNumber getCustInvcApplicableTaxPercent_int() {
		return GenerInvcEntr_CustInvc_FP.getCustInvcApplicableTaxPercent(this);
    }
    
    // ----------------------------


    /**
     * {@inheritDoc}
     */
    @Override
    public BigFraction getCustInvcApplicableTaxPercentRat() {
    	return getCustInvcApplicableTaxPercentRat_int();
    }
    
    // Important separate it from public variant for
    // getJobInvcApplicableTaxPercent().
    // Else, we would have funny effects.
    private BigFraction getCustInvcApplicableTaxPercentRat_int() {
		return GenerInvcEntr_CustInvc_BF.getCustInvcApplicableTaxPercent(this);
    }
    
    // ----------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public FixedPointNumber getVendBllApplicableTaxPercent() {
    	return getVendBllApplicableTaxPercent_int();
    }
    
    // Important separate it from public variant for
    // getJobInvcApplicableTaxPercent().
    // Else, we would have funny effects.
    private FixedPointNumber getVendBllApplicableTaxPercent_int() {
		return GenerInvcEntr_VendBll_FP.getVendBllApplicableTaxPercent(this);
    }

    // ----------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public BigFraction getVendBllApplicableTaxPercentRat() {
    	return getVendBllApplicableTaxPercentRat_int();
    }
    
    // Important separate it from public variant for
    // getJobInvcApplicableTaxPercent().
    // Else, we would have funny effects.
    private BigFraction getVendBllApplicableTaxPercentRat_int() {
		return GenerInvcEntr_VendBll_BF.getVendBllApplicableTaxPercent(this);
    }

    // ----------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public FixedPointNumber getEmplVchApplicableTaxPercent() {
		return GenerInvcEntr_EmplVch_FP.getEmplVchApplicableTaxPercent(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigFraction getEmplVchApplicableTaxPercentRat() {
		return GenerInvcEntr_EmplVch_BF.getEmplVchApplicableTaxPercent(this);
    }

    // ----------------------------

    /**
     * {@inheritDoc}
     *  
     */
    @Override
    public FixedPointNumber getJobInvcApplicableTaxPercent() {
		return GenerInvcEntr_JobInvc_FP.getJobInvcApplicableTaxPercent(this);
    }

    /**
     * {@inheritDoc}
     *  
     */
    @Override
    public BigFraction getJobInvcApplicableTaxPercentRat() {
		return GenerInvcEntr_JobInvc_BF.getJobInvcApplicableTaxPercent(this);
    }

    // ----------------------------

    /**
     * @return never null, "0%" if no taxtable is there
     */
    @Override
    public String getCustInvcApplicableTaxPercentFormatted() {
    	return getCustInvcApplicableTaxPercentFormatted_int();
    }
    
    private String getCustInvcApplicableTaxPercentFormatted_int() {
		FixedPointNumber applTaxPerc = getCustInvcApplicableTaxPercent_int();
		if ( applTaxPerc == null ) {
			return this.getPercentFormat().format(0);
		}
		return this.getPercentFormat().format(applTaxPerc);
    }

    /**
     * @return never null, "0%" if no taxtable is there
     */
    @Override
    public String getVendBllApplicableTaxPercentFormatted() {
    	return getVendBllApplicableTaxPercentFormatted_int();
    }
    
    private String getVendBllApplicableTaxPercentFormatted_int() {
		FixedPointNumber applTaxPerc = getVendBllApplicableTaxPercent_int();
		if ( applTaxPerc == null ) {
			return this.getPercentFormat().format(0);
		}
		return this.getPercentFormat().format(applTaxPerc);
    }

    /**
     * @return never null, "0%" if no taxtable is there
     */
    @Override
    public String getEmplVchApplicableTaxPercentFormatted() {
		FixedPointNumber applTaxPerc = getEmplVchApplicableTaxPercent();
		if ( applTaxPerc == null ) {
			return this.getPercentFormat().format(0);
		}
		return this.getPercentFormat().format(applTaxPerc);
    }

    /**
     * @return never null, "0%" if no taxtable is there
     */
    @Override
    public String getJobInvcApplicableTaxPercentFormatted() {
		FixedPointNumber applTaxPerc = getJobInvcApplicableTaxPercent();
		if ( applTaxPerc == null ) {
			return this.getPercentFormat().format(0);
		}
		return this.getPercentFormat().format(applTaxPerc);
    }

    // ---------------------------------------------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcPrice()
     */
    @Override
    public FixedPointNumber getCustInvcPrice() {
    	return getCustInvcPrice_int();
    }
    
    private FixedPointNumber getCustInvcPrice_int() {
    	return GenerInvcEntr_CustInvc_FP.getCustInvcPrice(this);
    }
    
    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcPrice()
     */
    @Override
    public BigFraction getCustInvcPriceRat() {
    	return getCustInvcPriceRat_int();
    }
    
    private BigFraction getCustInvcPriceRat_int() {
    	return GenerInvcEntr_CustInvc_BF.getCustInvcPrice(this);
    }
    
    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcPrice()
     */
    @Override
    public FixedPointNumber getVendBllPrice() {
    	return getVendBllPrice_int();
    }
    
    private FixedPointNumber getVendBllPrice_int() {
    	return GenerInvcEntr_VendBll_FP.getVendBllPrice(this);
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcPrice()
     */
    @Override
    public BigFraction getVendBllPriceRat() {
    	return getVendBllPriceRat_int();
    }
    
    private BigFraction getVendBllPriceRat_int() {
    	return GenerInvcEntr_VendBll_BF.getVendBllPrice(this);
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcPrice()
     */
    @Override
    public FixedPointNumber getEmplVchPrice() {
    	return GenerInvcEntr_EmplVch_FP.getEmplVchPrice(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcPrice()
     */
    @Override
    public BigFraction getEmplVchPriceRat() {
    	return GenerInvcEntr_EmplVch_BF.getEmplVchPrice(this);
    }

    // ----------------------------

    /**
     *  
     * @see GnuCashGenerInvoiceEntry#getCustInvcPrice()
     */
    @Override
    public FixedPointNumber getJobInvcPrice() {
    	return GenerInvcEntr_JobInvc_FP.getJobInvcPrice(this);
    }

    /**
     *  
     * @see GnuCashGenerInvoiceEntry#getCustInvcPrice()
     */
    @Override
    public BigFraction getJobInvcPriceRat() {
    	return GenerInvcEntr_JobInvc_BF.getJobInvcPrice(this);
    }

    // ----------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustInvcPriceFormatted() {
    	return getCustInvcPriceFormatted_int();
    }
    
    private String getCustInvcPriceFormatted_int() {
    	return getCustInvcPriceFormatted_int(Locale.getDefault());
    }

    private String getCustInvcPriceFormatted_int(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getCustInvcPrice_int(), getGenerInvoice().getCurrID(), lcl );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVendBllPriceFormatted() {
    	return getVendBllPriceFormatted_int();
    }
    
    private String getVendBllPriceFormatted_int() {
    	return getVendBllPriceFormatted_int(Locale.getDefault());
    }

    private String getVendBllPriceFormatted_int(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getVendBllPrice_int(), getGenerInvoice().getCurrID(), lcl );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmplVchPriceFormatted() {
    	return getEmplVchPriceFormatted(Locale.getDefault());
    }

    public String getEmplVchPriceFormatted(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getEmplVchPrice(), getGenerInvoice().getCurrID(), lcl );
    }

    /**
     * {@inheritDoc}
     *  
     */
    @Override
    public String getJobInvcPriceFormatted() {
    	return getJobInvcPriceFormatted(Locale.getDefault());

    }

    public String getJobInvcPriceFormatted(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getJobInvcPrice(), getGenerInvoice().getCurrID(), lcl );

    }

    // ---------------------------------------------------------------
    
	@Override
	public GCshAcctID getCustInvcAccountID() throws AccountNotFoundException {
		if ( getType() != GCshOwner.Type.CUSTOMER && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();
			
		return getCustInvcAccountID_int();
	}
	
	private GCshAcctID getCustInvcAccountID_int() throws AccountNotFoundException {
		if ( getType() != GCshOwner.Type.CUSTOMER && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( myInvcAcctID == null ) {
			EntryIAcct jwsdpAcctID = jwsdpPeer.getEntryIAcct();
			if ( jwsdpAcctID == null ) {
				throw new AccountNotFoundException();
			}

			String acctIdStr = jwsdpAcctID.getValue();
			if ( acctIdStr == null ) {
				LOGGER.error("getCustInvcAccountID: Customer invoice entry with id '" + getID()
						+ "' has not i-account-id");
				return null;
			}
			
			myInvcAcctID = new GCshAcctID(acctIdStr);
		}

		return myInvcAcctID;
	}

	@Override
	public GCshAcctID getVendBllAccountID() throws AccountNotFoundException {
		if ( getType() != GCshOwner.Type.VENDOR && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();
		
		return getVendBllAccountID_int();
	}
	
	private GCshAcctID getVendBllAccountID_int() throws AccountNotFoundException {
		if ( getType() != GCshOwner.Type.VENDOR && 
			 getType() != GCshOwner.Type.EMPLOYEE && // sic
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( myBllAcctID == null ) {
			EntryBAcct jwsdpAcctID = jwsdpPeer.getEntryBAcct();
			if ( jwsdpAcctID == null ) {
				throw new AccountNotFoundException();
			}

			String acctIdStr = jwsdpAcctID.getValue();
			if ( acctIdStr == null ) {
				LOGGER.error("getVendBllAccountID: Vendor bill entry with id '" + getID() + "' has no b-account-id");
				return null;
			}

			myBllAcctID = new GCshAcctID(acctIdStr);
		}

		return myBllAcctID;
	}

	@Override
	public GCshAcctID getEmplVchAccountID() throws AccountNotFoundException {
		if ( getType() != GCshOwner.Type.EMPLOYEE )
		    throw new WrongInvoiceTypeException();

		return getVendBllAccountID_int(); // sic
	}

	@Override
	public GCshAcctID getJobInvcAccountID() throws AccountNotFoundException {
		if ( getType() != GCshOwner.Type.JOB )
		    throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
		    return getCustInvcAccountID_int();
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
		    return getVendBllAccountID_int();

		return null; // Compiler happy
	}

    // ---------------------------------------------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public FixedPointNumber getCustInvcSum() {
    	return getCustInvcSum_int();
    }
    
    private FixedPointNumber getCustInvcSum_int() {
    	return GenerInvcEntr_CustInvc_FP.getCustInvcSum(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumInclTaxes()
     */
    @Override
    public FixedPointNumber getCustInvcSumInclTaxes() {
    	return getCustInvcSumInclTaxes_int();
    }
    
    private FixedPointNumber getCustInvcSumInclTaxes_int() {
    	return GenerInvcEntr_CustInvc_FP.getCustInvcSumInclTaxes(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumExclTaxes()
     */
    @Override
    public FixedPointNumber getCustInvcSumExclTaxes() {
    	return getCustInvcSumExclTaxes_int();
    }
    
    private FixedPointNumber getCustInvcSumExclTaxes_int() {
    	return GenerInvcEntr_CustInvc_FP.getCustInvcSumExclTaxes(this);
    }

    // ---------------------------------------------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public BigFraction getCustInvcSumRat() {
    	return getCustInvcSumRat_int();
    }
    
    private BigFraction getCustInvcSumRat_int() {
    	return GenerInvcEntr_CustInvc_BF.getCustInvcSum(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumInclTaxes()
     */
    @Override
    public BigFraction getCustInvcSumInclTaxesRat() {
    	return getCustInvcSumInclTaxesRat_int();
    }
    
    private BigFraction getCustInvcSumInclTaxesRat_int() {
    	return GenerInvcEntr_CustInvc_BF.getCustInvcSumInclTaxes(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumExclTaxes()
     */
    @Override
    public BigFraction getCustInvcSumExclTaxesRat() {
    	return getCustInvcSumExclTaxesRat_int();
    }
    
    private BigFraction getCustInvcSumExclTaxesRat_int() {
    	return GenerInvcEntr_CustInvc_BF.getCustInvcSumExclTaxes(this);
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public String getCustInvcSumFormatted() {
    	return getCustInvcSumFormatted_int();
    }
    
    private String getCustInvcSumFormatted_int() {
    	return getCustInvcSumFormatted_int(Locale.getDefault());
    }

    private String getCustInvcSumFormatted_int(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getCustInvcSum_int(), getGenerInvoice().getCurrID(), lcl );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustInvcSumInclTaxesFormatted() {
    	return getCustInvcSumInclTaxesFormatted_int();
    }
    
    private String getCustInvcSumInclTaxesFormatted_int() {
    	return getCustInvcSumInclTaxesFormatted_int(Locale.getDefault());
    }

    private String getCustInvcSumInclTaxesFormatted_int(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getCustInvcSumInclTaxes_int(), getGenerInvoice().getCurrID(), lcl );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustInvcSumExclTaxesFormatted() {
    	return getCustInvcSumExclTaxesFormatted_int();
    }
    
    public String getCustInvcSumExclTaxesFormatted_int() {
    	return getCustInvcSumExclTaxesFormatted_int(Locale.getDefault());
    }

    public String getCustInvcSumExclTaxesFormatted_int(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getCustInvcSumExclTaxes_int(), getGenerInvoice().getCurrID(), lcl );
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public FixedPointNumber getVendBllSum() {
    	return getVendBllSum_int();
    }
    
    private FixedPointNumber getVendBllSum_int() {
    	return GenerInvcEntr_VendBll_FP.getVendBllSum(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumInclTaxes()
     */
    @Override
    public FixedPointNumber getVendBllSumInclTaxes() {
    	return getVendBllSumInclTaxes_int();
    }
    
    private FixedPointNumber getVendBllSumInclTaxes_int() {
    	return GenerInvcEntr_VendBll_FP.getVendBllSumInclTaxes(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumExclTaxes()
     */
    @Override
    public FixedPointNumber getVendBllSumExclTaxes() {
    	return getVendBllSumExclTaxes_int();
    }
    
    private FixedPointNumber getVendBllSumExclTaxes_int() {
    	return GenerInvcEntr_VendBll_FP.getVendBllSumExclTaxes(this);
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public BigFraction getVendBllSumRat() {
    	return getVendBllSumRat_int();
    }
    
    private BigFraction getVendBllSumRat_int() {
    	return GenerInvcEntr_VendBll_BF.getVendBllSum(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumInclTaxes()
     */
    @Override
    public BigFraction getVendBllSumInclTaxesRat() {
    	return getVendBllSumInclTaxesRat_int();
    }
    
    private BigFraction getVendBllSumInclTaxesRat_int() {
    	return GenerInvcEntr_VendBll_BF.getVendBllSumInclTaxes(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumExclTaxes()
     */
    @Override
    public BigFraction getVendBllSumExclTaxesRat() {
    	return getVendBllSumExclTaxesRat_int();
    }
    
    private BigFraction getVendBllSumExclTaxesRat_int() {
    	return GenerInvcEntr_VendBll_BF.getVendBllSumExclTaxes(this);
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public String getVendBllSumFormatted() {
    	return getVendBllSumFormatted_int();
    }
    
    private String getVendBllSumFormatted_int() {
    	return getVendBllSumFormatted_int(Locale.getDefault());
    }

    private String getVendBllSumFormatted_int(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getVendBllSum_int(), getGenerInvoice().getCurrID(), lcl );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVendBllSumInclTaxesFormatted() {
    	return getVendBllSumInclTaxesFormatted_int();
    }
    
    private String getVendBllSumInclTaxesFormatted_int() {
    	return getVendBllSumInclTaxesFormatted_int(Locale.getDefault());
    }

    private String getVendBllSumInclTaxesFormatted_int(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getVendBllSumInclTaxes_int(), getGenerInvoice().getCurrID(), lcl );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVendBllSumExclTaxesFormatted() {
    	return getVendBllSumExclTaxesFormatted_int();
    }
    
    private String getVendBllSumExclTaxesFormatted_int() {
    	return getVendBllSumExclTaxesFormatted_int(Locale.getDefault());
    }

    private String getVendBllSumExclTaxesFormatted_int(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getVendBllSumExclTaxes_int(), getGenerInvoice().getCurrID(), lcl );
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public FixedPointNumber getEmplVchSum() {
    	return GenerInvcEntr_EmplVch_FP.getEmplVchSum(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumInclTaxes()
     */
    @Override
    public FixedPointNumber getEmplVchSumInclTaxes() {
    	return GenerInvcEntr_EmplVch_FP.getEmplVchSumInclTaxes(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumExclTaxes()
     */
    @Override
    public FixedPointNumber getEmplVchSumExclTaxes() {
    	return GenerInvcEntr_EmplVch_FP.getEmplVchSumExclTaxes(this);
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public BigFraction getEmplVchSumRat() {
    	return GenerInvcEntr_EmplVch_BF.getEmplVchSum(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumInclTaxes()
     */
    @Override
    public BigFraction getEmplVchSumInclTaxesRat() {
    	return GenerInvcEntr_EmplVch_BF.getEmplVchSumInclTaxes(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumExclTaxes()
     */
    @Override
    public BigFraction getEmplVchSumExclTaxesRat() {
    	return GenerInvcEntr_EmplVch_BF.getEmplVchSumExclTaxes(this);
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public String getEmplVchSumFormatted() {
    	return getEmplVchSumFormatted(Locale.getDefault());
    }

    public String getEmplVchSumFormatted(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getEmplVchSum(), getGenerInvoice().getCurrID(), lcl );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmplVchSumInclTaxesFormatted() {
    	return getEmplVchSumInclTaxesFormatted(Locale.getDefault());
    }

    public String getEmplVchSumInclTaxesFormatted(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getEmplVchSumInclTaxes(), getGenerInvoice().getCurrID(), lcl );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmplVchSumExclTaxesFormatted() {
    	return getEmplVchSumExclTaxesFormatted(Locale.getDefault());
    }

    public String getEmplVchSumExclTaxesFormatted(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getEmplVchSumExclTaxes(), getGenerInvoice().getCurrID(), lcl );
    }

    // ----------------------------

    /**
     *  
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public FixedPointNumber getJobInvcSum() {
		return GenerInvcEntr_JobInvc_FP.getJobInvcSum(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumInclTaxes()
     */
    @Override
    public FixedPointNumber getJobInvcSumInclTaxes() {
		return GenerInvcEntr_JobInvc_FP.getJobInvcSumInclTaxes(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumExclTaxes()
     */
    @Override
    public FixedPointNumber getJobInvcSumExclTaxes() {
		return GenerInvcEntr_JobInvc_FP.getJobInvcSumExclTaxes(this);
    }

    // ----------------------------

    /**
     *  
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public BigFraction getJobInvcSumRat() {
		return GenerInvcEntr_JobInvc_BF.getJobInvcSum(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumInclTaxes()
     */
    @Override
    public BigFraction getJobInvcSumInclTaxesRat() {
		return GenerInvcEntr_JobInvc_BF.getJobInvcSumInclTaxes(this);
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumExclTaxes()
     */
    @Override
    public BigFraction getJobInvcSumExclTaxesRat() {
		return GenerInvcEntr_JobInvc_BF.getJobInvcSumExclTaxes(this);
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public String getJobInvcSumFormatted() {
    	return getJobInvcSumFormatted(Locale.getDefault());
    }

    public String getJobInvcSumFormatted(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getJobInvcSum(), getGenerInvoice().getCurrID(), lcl );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJobInvcSumInclTaxesFormatted() {
    	return getJobInvcSumInclTaxesFormatted(Locale.getDefault());
    }

    public String getJobInvcSumInclTaxesFormatted(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getJobInvcSumInclTaxes(), getGenerInvoice().getCurrID(), lcl );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJobInvcSumExclTaxesFormatted() {
    	return getJobInvcSumExclTaxesFormatted(Locale.getDefault());
    }

    public String getJobInvcSumExclTaxesFormatted(final Locale lcl) {
    	return AmountFormatter_FP.formatAmount( getGnuCashFile(),
    											getJobInvcSumExclTaxes(), getGenerInvoice().getCurrID(), lcl );
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCustInvcTaxable() {
    	return isCustInvcTaxable_int();
    }
    
    public boolean isCustInvcTaxable_int() {
    	if ( getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
    		 getType() != GnuCashGenerInvoice.TYPE_JOB )
    		throw new WrongInvoiceTypeException();

    	return (jwsdpPeer.getEntryITaxable() == 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVendBllTaxable() {
    	return isVendBllTaxable_int();
    }
    
    public boolean isVendBllTaxable_int() {
    	if ( getType() != GnuCashGenerInvoice.TYPE_VENDOR && 
    		 getType() != GnuCashGenerInvoice.TYPE_JOB )
    		throw new WrongInvoiceTypeException();

    	return (jwsdpPeer.getEntryBTaxable() == 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmplVchTaxable() {
		if ( getType() != GnuCashGenerInvoice.TYPE_EMPLOYEE )
			throw new WrongInvoiceTypeException();

		return (jwsdpPeer.getEntryBTaxable() == 1);
    }

    /**
     * {@inheritDoc}
     *  
     */
    @Override
    public boolean isJobInvcTaxable() {
		if ( getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return isCustInvcTaxable_int();
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return isVendBllTaxable_int();

		return false; // Compiler happy
    }

    /**
     *  
     * @see GnuCashGenerInvoiceEntry#getAction()
     */
    public Action getAction() {
    	if ( getActionStr() == null )
    		return null;
    	
    	if ( getActionStr().isBlank() )
    		return null;

    	return Action.valueOfff( getActionStr() );
    }

    /**
     *  
     * @see GnuCashGenerInvoiceEntry#getActionStr()
     */
    public String getActionStr() {
    	return jwsdpPeer.getEntryAction();
    }

    /**
     * {@inheritDoc}
     */
    public FixedPointNumber getQuantity() {
    	String val = getJwsdpPeer().getEntryQty();
    	return new FixedPointNumber(val);
    }

    /**
     * {@inheritDoc}
     */
    public BigFraction getQuantityRat() {
    	String val = getJwsdpPeer().getEntryQty();
    	return BigFraction.parse(val);
    }

    /**
     * {@inheritDoc}
     */
    public String getQuantityFormatted() {
    	return getNumberFormat().format(getQuantity());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ZonedDateTime getDate() {
		if ( date == null ) {
			String dateStr = getJwsdpPeer().getEntryDate().getTsDate();
			try {
				// "2001-09-18 00:00:00 +0200"
				date = ZonedDateTime.parse(dateStr, DATE_FORMAT);
			} catch (Exception e) {
				IllegalStateException ex = new IllegalStateException("unparsable date '" + dateStr + "' in invoice");
				ex.initCause(e);
				throw ex;
			}

		}
		return date;
    }

	/**
	 * @see #getDateOpenedFormatted()
	 * @see #getDatePostedFormatted()
	 * @return the Dateformat to use.
	 */
	protected DateFormat getDateFormat() {
		if ( dateFormat == null ) {
		    if ( ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getDateFormat() != null ) {
		    	dateFormat = ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getDateFormat();
		    } else {
		    	dateFormat = DateFormat.getDateInstance();
		    }
		}

		return dateFormat;
	}

    /**
     * {@inheritDoc}
     */
    public String getDateFormatted() {
    	return getDateFormat().format(getDate());
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
    	if (getJwsdpPeer().getEntryDescription() == null) {
    		return "";
    	}

    	return getJwsdpPeer().getEntryDescription();
    }

    /**
     * @return the number-format to use for non-currency-numbers if no locale is
     *         given.
     */
    protected NumberFormat getNumberFormat() {
    	if (numberFormat == null) {
    		numberFormat = NumberFormat.getInstance();
    	}

    	return numberFormat;
    }

    /**
     * @return the number-format to use for percentage-numbers if no locale is
     *         given.
     */
    protected NumberFormat getPercentFormat() {
    	if (percentFormat == null) {
    		percentFormat = NumberFormat.getPercentInstance();
    	}

    	return percentFormat;
    }

    // ---------------------------------------------------------------

    /**
     * @return The JWSDP-Object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncGncEntry getJwsdpPeer() {
    	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public int compareTo(final GnuCashGenerInvoiceEntry otherEntr) {
		try {
			GnuCashGenerInvoice otherInvc = otherEntr.getGenerInvoice();
			if ( otherInvc != null && getGenerInvoice() != null ) {
				int c = otherInvc.compareTo(getGenerInvoice());
				if ( c != 0 ) {
					return c;
				}
			}

			int c = otherEntr.getID().toString().compareTo(getID().toString());
			if ( c != 0 ) {
				return c;
			}

			if ( otherEntr != this ) {
				LOGGER.error("Duplicate invoice-entry-id!! " + otherEntr.getID() + " and " + getID());
			}

			return 0;

		} catch (Exception e) {
			LOGGER.error("error comparing", e);
			return 0;
		}
    }
    
    // ---------------------------------------------------------------
    
	@Override
	public String getUserDefinedAttribute(String name) {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeCore(jwsdpPeer.getEntrySlots(), name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeKeysCore(jwsdpPeer.getEntrySlots());
	}

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashGenerInvoiceEntryImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", type=");
		try {
			buffer.append(getType());
		} catch (WrongInvoiceTypeException e) {
			buffer.append("ERROR");
		}

		buffer.append(", invoice-id=");
		buffer.append(getGenerInvoiceID());

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append(", date=");
		try {
			buffer.append(getDate().toLocalDate().format(DATE_FORMAT_PRINT));
		} catch (Exception e) {
			buffer.append(getDate().toLocalDate().toString());
		}

		buffer.append(", action='");
		try {
			buffer.append(getAction() + "'");
		} catch (Exception e) {
			buffer.append("ERROR" + "'");
		}

		buffer.append(", account-id=");
		try {
			if ( getType() == GCshOwner.Type.CUSTOMER ) {
				buffer.append(getCustInvcAccountID());
			} else if ( getType() == GCshOwner.Type.VENDOR ) {
				buffer.append(getVendBllAccountID());
			} else if ( getType() == GCshOwner.Type.EMPLOYEE ) {
				buffer.append(getEmplVchAccountID());
			} else if ( getType() == GCshOwner.Type.JOB ) {
				buffer.append(getJobInvcAccountID());
			} else {
				buffer.append("ERROR");
			}
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", price=");
		try {
			if ( getType() == GCshOwner.Type.CUSTOMER ) {
				buffer.append(getCustInvcPrice());
			} else if ( getType() == GCshOwner.Type.VENDOR ) {
				buffer.append(getVendBllPrice());
			} else if ( getType() == GCshOwner.Type.EMPLOYEE ) {
				buffer.append(getEmplVchPrice());
			} else if ( getType() == GCshOwner.Type.JOB ) {
				buffer.append(getJobInvcPrice());
			} else {
				buffer.append("ERROR");
			}
		} catch (WrongInvoiceTypeException e) {
			buffer.append("ERROR");
		}

		buffer.append(", quantity=");
		buffer.append(getQuantity());

		buffer.append("]");
		return buffer.toString();
    }

}
