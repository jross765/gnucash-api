package org.gnucash.api.read.impl;

import java.util.List;
import java.util.Locale;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.Const;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.hlp.AmountFormatter_FP;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Implementation of GnuCashTransactionSplit that uses JWSDSP.
 */
public class GnuCashTransactionSplitImpl extends GnuCashObjectImpl 
	                                     implements GnuCashTransactionSplit 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashTransactionSplitImpl.class);

	// ---------------------------------------------------------------

	// the JWSDP-object we are facading.
	protected final GncTransaction.TrnSplits.TrnSplit jwsdpPeer;

	// the transaction this split belongs to.
	protected final GnuCashTransaction myTrx;

	// ---------------------------------------------------------------

	/**
	 * @param peer the JWSDP-object we are facading.
	 * @param trx  the transaction this split belongs to
	 * @param addSpltToAcct 
	 * @param addSpltToInvc 
	 */
	@SuppressWarnings("exports")
	public GnuCashTransactionSplitImpl(
	    final GncTransaction.TrnSplits.TrnSplit peer,
	    final GnuCashTransaction trx,
	    final boolean addSpltToAcct,
	    final boolean addSpltToInvc) {
		super(trx.getGnuCashFile());

		this.jwsdpPeer = peer;
		this.myTrx = trx;

		if ( addSpltToAcct ) {
			GnuCashAccount acct = getAccount();
			if ( acct == null ) {
				LOGGER.error("No such Account id='" + getAccountID() + "' for Transactions-Split with id '" + getID()
					+ "' description '" + getDescription() + "' in transaction with id '" + getTransaction().getID()
					+ "' description '" + getTransaction().getDescription() + "'");
			} else {
				((GnuCashAccountImpl) acct).addTransactionSplit(this);
			}
		}

		if ( addSpltToInvc ) {
			GCshLotID spltLotID = getLotID();
			if ( spltLotID != null ) {
				for ( GnuCashGenerInvoice invc : getTransaction().getGnuCashFile().getGenerInvoices() ) {
					GCshLotID invcPostLotID = invc.getLotID();
					if ( invcPostLotID != null && invcPostLotID.equals(spltLotID) ) {
						// Check if it's a payment transaction.
						// If so, add it to the invoice's list of payment transactions.
						if ( getAction() == Action.PAYMENT ) {
							invc.addPayingTransactionSplit(this);
						}
					}
				} // for invc
			} // lot
		} // addSpltToInvc
	}

	// ---------------------------------------------------------------

	/**
	 * @return the JWSDP-object we are facading.
	 */
	@SuppressWarnings("exports")
	public GncTransaction.TrnSplits.TrnSplit getJwsdpPeer() {
		return jwsdpPeer;
	}

	// ---------------------------------------------------------------

	/**
	 * @see GnuCashTransactionSplit#getID()
	 */
	@Override
	public GCshSpltID getID() {
		return new GCshSpltID( jwsdpPeer.getSplitId().getValue() );
	}

	/**
	 * @return the lot-id that identifies this transaction to belong to an invoice
	 *         with that lot-id.
	 */
	public GCshLotID getLotID() {
		if ( jwsdpPeer.getSplitLot() == null ) {
			return null;
		}

		return new GCshLotID(jwsdpPeer.getSplitLot().getValue());
	}
	
	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Action getAction() {
		if ( getActionStr() == null )
			return null;
		
		if ( getActionStr().isBlank() )
			return null;

		return Action.valueOfff( getActionStr() );
	}

	/**
	 * {@inheritDoc}
	 */
	public String getActionStr() {
		return jwsdpPeer.getSplitAction();
	}

	// ----------------------------
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReconState getReconState() {
		if ( getReconStateStr() == null )
			return null;
		
		if ( getReconStateStr().isBlank() )
			return null;

		return ReconState.valueOff( getReconStateStr() );
	}

	/**
	 * The returned text is a one-character code.
	 * <br>
	 * <b>Using this method is discouraged.</b>
	 * Use {@link #getReconState()} whenever possible/applicable instead.
	 * 
	 * @return 'c','y', 'f', 'n', 'v'.
	 */
	public String getReconStateStr() {
		return jwsdpPeer.getSplitReconciledState();
	}

	/**
	 * @see GnuCashTransactionSplit#getAccountID()
	 */
	public GCshAcctID getAccountID() {
		if ( jwsdpPeer.getSplitAccount() == null ) {
			return null;
		}
		
		if ( ! jwsdpPeer.getSplitAccount().getType().equals(Const.XML_DATA_TYPE_GUID) ) {
			throw new IllegalStateException("JWSDP peer's attribute is of wrong type: " + jwsdpPeer.getSplitAccount().getType());
		}
		
		String acctID = jwsdpPeer.getSplitAccount().getValue();
		if ( acctID == null )
			return null;
		
		if ( acctID.isBlank() )
			return null;

		return new GCshAcctID(acctID);
	}

	/**
	 * @see GnuCashTransactionSplit#getAccount()
	 */
	public GnuCashAccount getAccount() {
		return myTrx.getGnuCashFile().getAccountByID(getAccountID());
	}

	/**
	 * @see GnuCashTransactionSplit#getAccountID()
	 */
	public GCshTrxID getTransactionID() {
		return myTrx.getID();
	}

	/**
	 * @see GnuCashTransactionSplit#getTransaction()
	 */
	public GnuCashTransaction getTransaction() {
		return myTrx;
	}

	// ---------------------------------------------------------------

	/**
	 * @see GnuCashTransactionSplit#getValue()
	 */
	@Override
	@Deprecated
	public FixedPointNumber getValue() {
		return new FixedPointNumber(jwsdpPeer.getSplitValue());
	}

	@Override
	public BigFraction getValueRat() {
		return BigFraction.parse(jwsdpPeer.getSplitValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValueFormatted() {
		return getValueFormatted(Locale.getDefault());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValueFormatted(final Locale lcl) {
		return AmountFormatter_FP.formatAmount( getGnuCashFile(),
												getValue(), getTransaction().getCmdtyID(), lcl );
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public FixedPointNumber getQuantity() {
		return new FixedPointNumber(jwsdpPeer.getSplitQuantity());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigFraction getQuantityRat() {
		return BigFraction.parse(jwsdpPeer.getSplitQuantity());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getQuantityFormatted() {
		return getQuantityFormatted(Locale.getDefault());
	}

	/**
	 * The value is in the currency of the account!
	 *
	 * @param lcl the locale to format to
	 * @return the formatted number
	 */
	public String getQuantityFormatted(final Locale lcl) {
		return AmountFormatter_FP.formatAmount( getGnuCashFile(),
												getQuantity(), getAccount().getCmdtyID(), lcl );
	}

	// ---------------------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		if ( jwsdpPeer.getSplitMemo() == null )
			return null;
		
		if ( jwsdpPeer.getSplitMemo().isBlank() )
			return null;
		
		return jwsdpPeer.getSplitMemo();
	}

	// ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
	@Override
	public String getUserDefinedAttribute(final String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}

		if ( name.isBlank() ) {
			throw new IllegalArgumentException("argument <name> is blank");
		}

		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeCore(jwsdpPeer.getSplitSlots(), name);
	}

    /**
     * For special case.
     * Intentionally not published in interface.
     */
    public String getUserDefinedAttributeType(final String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}

		if ( name.isBlank() ) {
			throw new IllegalArgumentException("argument <name> is blank");
		}

		if ( jwsdpPeer.getSplitSlots() == null) {
			return null;
		}
		
		return HasUserDefinedAttributesImpl
				.getUserDefinedAttributeTypeCore(jwsdpPeer.getSplitSlots(), name);
    }
    
    /**
     * {@inheritDoc}
     */
	@Override
	public List<String> getUserDefinedAttributeKeys() {
		if ( jwsdpPeer.getSplitSlots() == null) {
			return null;
		}
		
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeKeysCore(jwsdpPeer.getSplitSlots());
	}

	// ---------------------------------------------------------------

	public int compareTo(final GnuCashTransactionSplit otherSplt) {
		try {
			GnuCashTransaction otherTrans = otherSplt.getTransaction();
			int c = otherTrans.compareTo( getTransaction() );
			if ( c != 0 ) {
				return c;
			}

			if ( ! otherSplt.getID().equals( getID() ) ) {
				return otherSplt.getID().toString().compareTo( getID().toString() );
			}

			if ( otherSplt != this ) {
				LOGGER.error("compareTo: Duplicate transaction-split-id! " + otherSplt.getID() + "["
						+ otherSplt.getClass().getName() + "] and " + getID() + "[" + getClass().getName() + "]\n"
						+ "split0=" + otherSplt.toString() + "\n" + "split1=" + toString());
				IllegalStateException exc = new IllegalStateException("DEBUG");
				exc.printStackTrace();
			}

			return 0;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// ---------------------------------------------------------------

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashTransactionSplitImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", action=");
		try {
			buffer.append(getAction());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", recon-state=");
		try {
			buffer.append(getReconState());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", transaction-id=");
		buffer.append(getTransaction().getID());

		buffer.append(", account-id=");
		buffer.append(getAccountID());

		buffer.append(", value=");
		buffer.append(getValue());

		buffer.append(", quantity=");
		buffer.append(getQuantity());

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append("]");
		return buffer.toString();
	}

}
