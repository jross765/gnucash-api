package org.gnucash.api.read.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Implementation of GnuCashTransaction that uses JWSDP.
 */
public class GnuCashTransactionImpl extends GnuCashObjectImpl 
                                    implements GnuCashTransaction 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashTransactionImpl.class);

    protected static final DateTimeFormatter DATE_ENTERED_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    protected static final DateTimeFormatter DATE_POSTED_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    
    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncTransaction jwsdpPeer;

    // ---------------------------------------------------------------
    
    /**
     * @see #getSplits()
     */
    protected List<GnuCashTransactionSplit> mySplits = null;

    // ---------------------------------------------------------------

    /**
     * @see GnuCashTransaction#getDateEntered()
     */
    protected ZonedDateTime dateEntered;

    /**
     * @see GnuCashTransaction#getDatePosted()
     */
    protected ZonedDateTime datePosted;

    // ---------------------------------------------------------------

    /**
     * The Currency-Format to use if no locale is given.
     */
    protected NumberFormat currencyFormat;

    // ---------------------------------------------------------------

    /**
     * Create a new Transaction, facading a JWSDP-transaction.
     *
     * @param peer    the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     * @param addTrxToInvc 
     */
    @SuppressWarnings("exports")
    public GnuCashTransactionImpl(
	    final GncTransaction peer, 
	    final GnuCashFile gcshFile,
	    final boolean addTrxToInvc) {
    	super(gcshFile);

//		if (peer.getTrnSlots() == null) {
//	   	 peer.setTrnSlots(jwsdpPeer.getTrnSlots());
//		}

    	if (peer == null) {
    		throw new IllegalArgumentException("argument <peer> is null");
    	}

    	if (gcshFile == null) {
    		throw new IllegalArgumentException("argument <gcshFile> is null");
    	}

    	jwsdpPeer = peer;

    	if ( addTrxToInvc ) {
    		for ( GnuCashGenerInvoice invc : getInvoices() ) {
    			invc.addTransaction(this);
    		}
    	}
    }

    // Copy-constructor
    public GnuCashTransactionImpl(final GnuCashTransaction trx) {
    	super(trx.getGnuCashFile());

//		if (trx.getJwsdpPeer().getTrnSlots() == null) {
//	   	 trx.getJwsdpPeer().setTrnSlots(jwsdpPeer.getTrnSlots());
//		}

    	if (trx.getJwsdpPeer() == null) {
    		throw new IllegalArgumentException("Transaction not correctly initialized: null jwsdpPeer given");
    	}

    	if (trx.getGnuCashFile() == null) {
    		throw new IllegalArgumentException("Transaction not correctly initialized: null file given");
    	}

    	jwsdpPeer = trx.getJwsdpPeer();

    	for ( GnuCashGenerInvoice invc : getInvoices() ) {
    		invc.addTransaction(this);
    	}
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GncTransaction getJwsdpPeer() {
    	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    /**
     *  
     * @see GnuCashTransaction#isBalanced()
     */
    public boolean isBalanced() {
    	return getBalance().equals(new FixedPointNumber());
    }

    /**
     */
    public GCshCmdtyCurrID getCmdtyCurrID() {
    	GCshCmdtyCurrID result = new GCshCmdtyCurrID(jwsdpPeer.getTrnCurrency().getCmdtySpace(), 
    												 jwsdpPeer.getTrnCurrency().getCmdtyId());
    	return result;
    }

    /**
     * The result is in the currency of the transaction.
     *
     * @return the balance of the sum of all splits
     *  
     * @see GnuCashTransaction#getBalance()
     */
    public FixedPointNumber getBalance() {
		FixedPointNumber fp = new FixedPointNumber();

		for ( GnuCashTransactionSplit split : getSplits() ) {
			fp.add(split.getValue());
		}

		return fp;
    }

    /**
     * The result is in the currency of the transaction.
     *
     * @see GnuCashTransaction#getBalanceFormatted()
     */
    public String getBalanceFormatted() {
    	return getCurrencyFormat().format(getBalance());
    }

    /**
     * The result is in the currency of the transaction.
     *
     * @see GnuCashTransaction#getBalanceFormatted(java.util.Locale)
     */
    public String getBalanceFormatted(final Locale lcl) {
		NumberFormat cf = NumberFormat.getInstance(lcl);
		if ( getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
			cf.setCurrency(new GCshCurrID(getCmdtyCurrID()).getCurrency());
		} else {
			cf.setCurrency(null);
		}

		return cf.format(getBalance());
    }

    /**
     * The result is in the currency of the transaction.
     *  
     *
     * @see GnuCashTransaction#getNegatedBalance()
     */
    public FixedPointNumber getNegatedBalance() {
    	return getBalance().multiply(new FixedPointNumber("-100/100"));
    }

    /**
     * The result is in the currency of the transaction.
     *
     * @see GnuCashTransaction#getNegatedBalanceFormatted()
     */
    public String getNegatedBalanceFormatted() {
    	return getCurrencyFormat().format(getNegatedBalance());
    }

    /**
     * The result is in the currency of the transaction.
     *
     * @see GnuCashTransaction#getNegatedBalanceFormatted(java.util.Locale)
     */
    public String getNegatedBalanceFormatted(final Locale lcl) {
		NumberFormat nf = NumberFormat.getInstance(lcl);
		if ( getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
			nf.setCurrency(new GCshCurrID(getCmdtyCurrID()).getCurrency());
		} else {
			nf.setCurrency(null);
		}

		return nf.format(getNegatedBalance());
    }

    /**
     * @see GnuCashTransaction#getID()
     */
    public GCshTrxID getID() {
    	return new GCshTrxID( jwsdpPeer.getTrnId().getValue() );
    }

    /**
     * @return the invoices this transaction belongs to (not payments but the
     *         transaction belonging to handing out the invoice)
     */
    public List<GnuCashGenerInvoice> getInvoices() {
    	List<GCshGenerInvcID> invoiceIDs = getInvoiceIDs();
		List<GnuCashGenerInvoice> retval = new ArrayList<GnuCashGenerInvoice>();

		for ( GCshGenerInvcID invoiceID : invoiceIDs ) {

			GnuCashGenerInvoice invoice = getGnuCashFile().getGenerInvoiceByID(invoiceID);
			if ( invoice == null ) {
				LOGGER.error("No invoice with id='" + invoiceID + "' for transaction '" + getID() + "' description '"
						+ getDescription() + "'");
			} else {
				retval.add(invoice);
			}

		}

		return retval;
    }

    /**
     * @return the invoices this transaction belongs to (not payments but the
     *         transaction belonging to handing out the invoice)
     */
    public List<GCshGenerInvcID> getInvoiceIDs() {
		List<GCshGenerInvcID> retval = new ArrayList<GCshGenerInvcID>();

		SlotsType slots = jwsdpPeer.getTrnSlots();
		if ( slots == null ) {
			return retval;
		}

		for ( Slot slot : (List<Slot>) slots.getSlot() ) {
			if ( !slot.getSlotKey().equals(Const.SLOT_KEY_INVC_TYPE) ) {
				continue;
			}

			SlotValue slotVal = slot.getSlotValue();

			ObjectFactory objectFactory = new ObjectFactory();
			Slot subSlot = objectFactory.createSlot();
			subSlot.setSlotKey(slot.getSlotKey());
			SlotValue subSlotVal = objectFactory.createSlotValue();
			subSlotVal.setType(Const.XML_DATA_TYPE_STRING);
			subSlotVal.getContent().add(slotVal.getContent().get(0));
			subSlot.setSlotValue(subSlotVal);
			if ( !subSlot.getSlotKey().equals("invoice-guid") ) {
				continue;
			}

			if ( !subSlot.getSlotValue().getType().equals(Const.XML_DATA_TYPE_GUID) ) {
				continue;
			}

			retval.add(new GCshGenerInvcID((String) subSlot.getSlotValue().getContent().get(0)));

		}

		return retval;
    }

    /**
     * @see GnuCashTransaction#getDescription()
     */
    public String getDescription() {
    	return jwsdpPeer.getTrnDescription();
    }

    // ----------------------------

    /**
     * @param impl the split to add to mySplits
     */
    protected void addSplit(final GnuCashTransactionSplitImpl impl) {
		if ( !jwsdpPeer.getTrnSplits().getTrnSplit().contains(impl.getJwsdpPeer()) ) {
			jwsdpPeer.getTrnSplits().getTrnSplit().add(impl.getJwsdpPeer());
		}

		List<GnuCashTransactionSplit> splits = getSplits();
		if ( !splits.contains(impl) ) {
			splits.add(impl);
		}
    }

    /**
     *  
     * @see GnuCashTransaction#getSplitsCount()
     */
    public int getSplitsCount() {
    	return getSplits().size();
    }

    public GnuCashTransactionSplit getSplitByID(final GCshSpltID spltID) {
		for ( GnuCashTransactionSplit split : getSplits() ) {
			if ( split.getID().equals(spltID) ) {
				return split;
			}

		}
		
		return null;
    }

    /**
     *  
     * @see GnuCashTransaction#getFirstSplit()
     */
    public GnuCashTransactionSplit getFirstSplit() throws TransactionSplitNotFoundException {
    	if ( getSplits().size() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return getSplits().get(0);
    }

    /**
     *  
     * @see GnuCashTransaction#getSecondSplit()
     */
    public GnuCashTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException {
		if ( getSplits().size() <= 1 )
			throw new TransactionSplitNotFoundException();

		return getSplits().get(1);
    }

    /**
     *  
     * @see GnuCashTransaction#getSplits()
     */
    @Override
    public List<GnuCashTransactionSplit> getSplits() {
    	return getSplits(false, false);
    }

    public List<GnuCashTransactionSplit> getSplits(final boolean addToAcct, final boolean addToInvc) {
		if ( mySplits == null ) {
			initSplits(addToAcct, addToInvc);
		}
		return mySplits;
    }

    protected void initSplits(final boolean addToAcct, final boolean addToInvc) {
		if ( jwsdpPeer.getTrnSplits() == null )
			return;

		List<GncTransaction.TrnSplits.TrnSplit> jwsdpSplits = jwsdpPeer.getTrnSplits().getTrnSplit();

		mySplits = new ArrayList<GnuCashTransactionSplit>();
		for ( GncTransaction.TrnSplits.TrnSplit elt : jwsdpSplits ) {
			mySplits.add(createSplit(elt, addToAcct, addToInvc));
		}
    }

    /**
     * Create a new split for a split found in the jaxb-data.
     *
     * @param jwsdpSplt the jaxb-data
     * @return the new split-instance
     */
    protected GnuCashTransactionSplitImpl createSplit(
	    final GncTransaction.TrnSplits.TrnSplit jwsdpSplt,
	    final boolean addToAcct, 
	    final boolean addToInvc) {
    	return new GnuCashTransactionSplitImpl(jwsdpSplt, this, 
    										   addToAcct, addToInvc);
    }

    /**
     * @see GnuCashTransaction#getDateEntered()
     */
    public ZonedDateTime getDateEntered() {
		if ( dateEntered == null ) {
			String s = jwsdpPeer.getTrnDateEntered().getTsDate();
			try {
				// "2001-09-18 00:00:00 +0200"
				dateEntered = ZonedDateTime.parse(s, DATE_ENTERED_FORMAT);
			} catch (Exception e) {
				IllegalStateException ex = new IllegalStateException("unparsable date '" + s + "' in transaction");
				ex.initCause(e);
				throw ex;
			}
		}

		return dateEntered;
    }

    /**
     * The Currency-Format to use if no locale is given.
     *
     * @return default currency-format with the transaction's currency set
     */
    protected NumberFormat getCurrencyFormat() {
		if ( currencyFormat == null ) {
			currencyFormat = NumberFormat.getCurrencyInstance();
		}

		// the currency may have changed
		if ( getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
			Currency currency = new GCshCurrID(getCmdtyCurrID()).getCurrency();
			currencyFormat.setCurrency(currency);
		} else {
			currencyFormat = NumberFormat.getNumberInstance();
		}

		return currencyFormat;
    }

    /**
     * @see GnuCashTransaction#getDatePostedFormatted()
     */
    public String getDatePostedFormatted() {
    	return DateFormat.getDateInstance().format(getDatePosted());
    }

    /**
     * @see GnuCashTransaction#getDatePosted()
     */
    public ZonedDateTime getDatePosted() {
		if ( datePosted == null ) {
			String s = jwsdpPeer.getTrnDatePosted().getTsDate();
			try {
				// "2001-09-18 00:00:00 +0200"
				datePosted = ZonedDateTime.parse(s, DATE_POSTED_FORMAT);
			} catch (Exception e) {
				IllegalStateException ex = new IllegalStateException(
						"unparsable date '" + s + "' in transaction with id='" + getID() + "'");
				ex.initCause(e);
				throw ex;
			}
		}

		return datePosted;
    }

	// -----------------------------------------------------------
    
    @Override
    public String getURL() {
    	return getUserDefinedAttribute(Const.SLOT_KEY_ASSOC_URI);
    }

	// -----------------------------------------------------------
    
	@Override
	public String getUserDefinedAttribute(String name) {
		return HasUserDefinedAttributesImpl
				.getUserDefinedAttributeCore(jwsdpPeer.getTrnSlots(), name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
				.getUserDefinedAttributeKeysCore(jwsdpPeer.getTrnSlots());
	}

	// -----------------------------------------------------------
    
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashTransactionImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", balance=");
		buffer.append(getBalanceFormatted());

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append(", #splits=");
		try {
			buffer.append(getSplitsCount());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", date-posted=");
		try {
			buffer.append(getDatePosted().format(DATE_POSTED_FORMAT));
		} catch (Exception e) {
			buffer.append(getDatePosted().toString());
		}

		buffer.append(", date-entered=");
		try {
			buffer.append(getDateEntered().format(DATE_ENTERED_FORMAT));
		} catch (Exception e) {
			buffer.append(getDateEntered().toString());
		}

		buffer.append("]");

		return buffer.toString();
    }

    /**
     * sorts primarily on the date the transaction happened and secondarily on the
     * date it was entered.
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final GnuCashTransaction otherTrx) {
		try {
			int compare = otherTrx.getDatePosted().compareTo(getDatePosted());
			if ( compare != 0 ) {
				return compare;
			}

			return otherTrx.getDateEntered().compareTo(getDateEntered());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
    }

    public String getNumber() {
    	return getJwsdpPeer().getTrnNum();
    }

}
