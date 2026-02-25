package org.gnucash.api.read.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.aux.GCshAcctLot;
import org.gnucash.api.read.aux.GCshAcctReconInfo;
import org.gnucash.api.read.impl.aux.GCshAcctLotImpl;
import org.gnucash.api.read.impl.aux.GCshAcctReconInfoImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.impl.hlp.acct.AccountBalanceHelper_FP;
import org.gnucash.api.read.impl.hlp.acct.SimpleAccount;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyTypeException;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.UnknownAccountTypeException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Implementation of GnuCashAccount that used a
 * jwsdp-generated backend.
 */
public class GnuCashAccountImpl extends SimpleAccount 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashAccountImpl.class);

    // ---------------------------------------------------------------

    // the JWSDP-object we are facading.
    protected final GncAccount jwsdpPeer;
    
    // ---------------------------------------------------------------

    // protected GnuCashObjectImpl helper;

    // ---------------------------------------------------------------

    /*
     * The splits of this transaction. May not be fully initialized during loading
     * of the GnuCash file.
     */
    private final List<GnuCashTransactionSplit> mySplits = new ArrayList<GnuCashTransactionSplit>();

    /*
     * If {@link #mySplits} needs to be sorted because it was modified. Sorting is
     * done in a lazy way.
     */
    private boolean mySplitsNeedSorting = false;
    
    // ----------------------------
    
    protected /* final */ List<GCshAcctLot> myLots = null; // sic, null, i.Ggs. zu oben

    // ---------------------------------------------------------------

    /**
     * @param peer    the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     */
    @SuppressWarnings("exports")
    public GnuCashAccountImpl(
    		final GncAccount peer,
    		final GnuCashFile gcshFile) {
    	super(gcshFile);

    	if ( peer == null ) {
    		throw new IllegalArgumentException("argument <peer> is null");
    	}

    	if ( gcshFile == null ) {
    		throw new IllegalArgumentException("argument <gcshFile> is null");
    	}

    	this.jwsdpPeer = peer;
    	
//		if ( peer.getActSlots() == null ) {
//			peer.setActSlots(new ObjectFactory().createSlotsType());
//		}
//
//		if ( peer.getActLots() == null ) {
//			peer.setActLots(new ObjectFactory().createGncAccountActLots());
//		}
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @Override
	@SuppressWarnings("exports")
    public GncAccount getJwsdpPeer() {
    	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
	public GCshAcctID getID() {
    	return new GCshAcctID(jwsdpPeer.getActId().getValue());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnuCashAccount#getParentAccountID()
     */
    @Override
	public GCshAcctID getParentAccountID() {
    	GncAccount.ActParent parent = jwsdpPeer.getActParent();
    	if (parent == null) {
    		return null;
    	}

    	return new GCshAcctID(parent.getValue());
    }

    /**
     * @see GnuCashAccount#getChildren()
     */
    @Override
	public List<GnuCashAccount> getChildren() {
    	return getGnuCashFile().getAccountsByParentID(getID());
    }

    @Override
    public List<GnuCashAccount> getChildrenRecursive() {
    	return getChildrenRecursiveCore(getChildren());
    }

    private static List<GnuCashAccount> getChildrenRecursiveCore(Collection<GnuCashAccount> accts) {
    	List<GnuCashAccount> result = new ArrayList<GnuCashAccount>();
    	
    	for ( GnuCashAccount acct : accts ) {
    		result.add(acct);
    		for ( GnuCashAccount childAcct : getChildrenRecursiveCore(acct.getChildren()) ) {
    			result.add(childAcct);
    		}
    	}
    	
    	return result;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
	public String getName() {
    	return jwsdpPeer.getActName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public String getDescription() {
    	return jwsdpPeer.getActDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public String getCode() {
    	return jwsdpPeer.getActCode();
    }

    private String getTypeStr() {
    	return jwsdpPeer.getActType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public Type getType() {
    	try {
    		Type result = Type.valueOf( getTypeStr() );
    		return result;
    	} catch ( Exception exc ) {
    		throw new UnknownAccountTypeException();
    	}
    }

    /**
	 * {@inheritDoc}
	 */
    @Override
	public GCshCmdtyID getCmdtyID() {
    	if ( jwsdpPeer.getActCommodity() == null &&
    		 jwsdpPeer.getActType().equals(Type.ROOT.toString()) ) {
    		return new GCshCurrID(); // default-currency because gnucash 2.2 has no currency on the root-account
    	}
	
    	GCshCmdtyID result = new GCshCmdtyID(jwsdpPeer.getActCommodity().getCmdtySpace(),
    												 jwsdpPeer.getActCommodity().getCmdtyId()); 
	
    	return result;
	}
    
    /**
	 * {@inheritDoc}
	 */
    @Override
    @Deprecated
	public GCshCmdtyID getCmdtyCurrID() {
    	return getCmdtyID();
	}
    
    // ----------------------------

	/**
     * @see GnuCashAccount#getTransactionSplits()
     */
    @Override
    public List<GnuCashTransactionSplit> getTransactionSplits() {

    	if (mySplitsNeedSorting) {
    		Collections.sort(mySplits);
    		mySplitsNeedSorting = false;
    	}

    	return mySplits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public void addTransactionSplit(final GnuCashTransactionSplit splt) {
		if ( splt == null ) {
			throw new IllegalArgumentException("argument <splt> is null");
		}

    	GnuCashTransactionSplit old = getTransactionSplitByID(splt.getID());
    	if ( old != null ) {
    		// There already is a split with that ID
    		if ( ! old.equals(splt) ) {
    			System.err.println(
    					"addTransactionSplit: New Transaction Split object with same ID, needs to be replaced: " +
    							splt.getID() + " [" + splt.getClass().getName() + "] and " +
    							old.getID() + " [" + old.getClass().getName() + "]\n" +
    							"new = " + splt.toString() + "\n" +
    							"old = " + old.toString());
    			LOGGER.error(
    					"addTransactionSplit: New Transaction Split object with same ID, needs to be replaced: " +
    							splt.getID() + " [" + splt.getClass().getName() + "] and " +
    							old.getID() + " [" + old.getClass().getName() + "]\n" +
    							"new=" + splt.toString() + "\n" +
    							"old=" + old.toString());
    			IllegalStateException exc = new IllegalStateException("DEBUG");
    			exc.printStackTrace();
    			replaceTransactionSplit(old, (GnuCashTransactionSplitImpl) splt);
    		}
    	} else {
    		// There is no split with that ID yet
    		mySplits.add(splt);
    		mySplitsNeedSorting = true;
    	}
    }

    /**
     * For internal use only.
     *
     * @param splt
     * @param impl 
     */
    public void replaceTransactionSplit(
    		final GnuCashTransactionSplit splt,
    		final GnuCashTransactionSplitImpl impl) {
    	if ( ! mySplits.remove(splt) ) {
    		throw new IllegalArgumentException("Could not remove split from local list");
    	}

    	mySplits.add(impl);
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GCshAcctLot> getLots() {
    	if (myLots == null) {
    	    initLots();
    	}
    	
    	return myLots;
    }

    private void initLots() {
    	if ( jwsdpPeer.getActLots() == null ) {
			return;
		}
    	
		List<GncAccount.ActLots.GncLot> jwsdpLots = jwsdpPeer.getActLots().getGncLot();

		myLots = new ArrayList<GCshAcctLot>();
		for ( GncAccount.ActLots.GncLot elt : jwsdpLots ) {
			myLots.add(createLot(elt));
		}
    }

    /**
     * Create a new split for a split found in the jaxb-data.
     *
     * @param jwsdpSplt the jaxb-data
     * @return the new split-instance
     */
    protected GCshAcctLotImpl createLot(
	    final GncAccount.ActLots.GncLot jwsdpLot) {
    	return new GCshAcctLotImpl(jwsdpLot, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public void addLot(final GCshAcctLot lot) {
    	GCshAcctLot old = getLotByID(lot.getID());
    	if ( old != null ) {
    		// There already is a lot with that ID
    		if ( ! old.equals(lot) ) {
    			System.err.println("addLot: New Account Lot object with same ID, needs to be replaced: " +
    					lot.getID() + " [" + lot.getClass().getName() + "] and " +
    					old.getID() + " [" + old.getClass().getName() + "]\n" +
    					"new = " + lot.toString() + "\n" +
    					"old = " + old.toString());
    			LOGGER.error("addLot: New Account Lot object with same ID, needs to be replaced: " +
    					lot.getID() + " [" + lot.getClass().getName() + "] and " +
    					old.getID() + " [" + old.getClass().getName() + "]\n" +
    					"new = " + lot.toString() + "\n" +
    					"old = " + old.toString());
    			IllegalStateException exc = new IllegalStateException("DEBUG");
    			exc.printStackTrace();
    			replaceLot(old, lot);
    		}
    	} else {
    		// There is no split with that ID yet
    		myLots.add(lot);
    	}
    }

    /**
     * For internal use only.
     *
     * @param lot
     * @param impl 
     */
    public void replaceLot(
    		final GCshAcctLot lot,
    		final GCshAcctLot impl) {
    	if ( ! myLots.remove(lot) ) {
    		throw new IllegalArgumentException("Could not remove lot from local list");
    	}

    	myLots.add(impl);
    }

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
    @Override
	public boolean isHidden() {
    	if ( jwsdpPeer.getActSlots() == null ) {
			return false;
		}
    	
    	String hiddenFlag = getUserDefinedAttribute(Const.SLOT_KEY_ACCT_HIDDEN); 
    	if ( hiddenFlag != null ) {
			if ( hiddenFlag.equals("true")) { // ::MAGIC
				return true;
			} else {
				return false;
			}
    	} else {
        	return false;
    	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public GCshAcctReconInfo getReconcileInfo() {
    	if ( jwsdpPeer.getActSlots() == null ) {
			return null;
		}
    	
    	for ( Slot slt : jwsdpPeer.getActSlots().getSlot() ) {
    		if ( slt.getSlotKey().equals(Const.SLOT_KEY_ACCT_RECONCILE_INFO) ) {
    			GCshAcctReconInfo rcnInf = new GCshAcctReconInfoImpl(slt, this);
    			return rcnInf;
    		}
    	}
    	
    	return null;
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

		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}

		if ( jwsdpPeer.getActSlots() == null ) {
			return null;
		}
		
		SlotsType slots = jwsdpPeer.getActSlots();
    	return HasUserDefinedAttributesImpl.getUserDefinedAttributeCore(slots, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getUserDefinedAttributeKeys() {
		if ( jwsdpPeer.getActSlots() == null ) {
			return null;
		}
		
		SlotsType slots = jwsdpPeer.getActSlots();
    	return HasUserDefinedAttributesImpl.getUserDefinedAttributeKeysCore(slots);
    }

    // -----------------------------------------------------------------

    @Override
	public String toString() {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("GnuCashAccountImpl [");
	
    	buffer.append("id=");
    	buffer.append(getID());
	
    	buffer.append(", code='");
    	buffer.append(getCode() + "'");
	
    	buffer.append(", type=");
    	try {
    		buffer.append(getType());
    	} catch (UnknownAccountTypeException e) {
    		buffer.append("ERROR");
    	}
	
    	buffer.append(", qualif-name='");
    	buffer.append(getQualifiedName() + "'");
	
    	buffer.append(", security/currency='");
    	try {
    		buffer.append(getCmdtyID() + "'");
    	} catch (InvalidCmdtyTypeException e) {
    		buffer.append("ERROR");
    	}
	
    	buffer.append("]");
	
    	return buffer.toString();
    }

    // https://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram-in-java
    @Override
    public void printTree(StringBuilder buffer, String prefix, String childrenPrefix) {
    	printTree(buffer, prefix, childrenPrefix, null);
    }
    
    public void printTree(StringBuilder buffer, String prefix, String childrenPrefix,
    					  GnuCashAccount.Type acctType) {
    	// 1) Top node
    	boolean hasChildrenMatchingRecurs = false;
    	if ( acctType != null ) {
    		hasChildrenMatchingRecurs = hasChildrenMatchingRecursive(this, acctType);
    	}
    	
    	if ( acctType == null ||
    		 this.getType() == acctType ||
    	     hasChildrenMatchingRecurs ) {
            buffer.append(prefix);
            buffer.append(this.toString());
            buffer.append('\n');
    	}

    	// 2) Children
        for ( Iterator<GnuCashAccount> it = getChildren().iterator(); it.hasNext(); ) {
        	GnuCashAccountImpl next = (GnuCashAccountImpl) it.next();
        	
        	hasChildrenMatchingRecurs = false;
        	if ( acctType != null ) {
        		hasChildrenMatchingRecurs = hasChildrenMatchingRecursive(next, acctType);
        	}
        	
        	if ( acctType == null ||
        		 next.getType() == acctType ||
        		 hasChildrenMatchingRecurs ) {
        		if ( it.hasNext() ) {
        			next.printTree(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ",
        					       acctType);
                   } else {
                	   next.printTree(buffer, childrenPrefix + "└── ", childrenPrefix + "    ",
                			   		  acctType);
               	}
           	}
        }
    }

    // ---------------------------------------------------------------
	// Helpers -- balance pre-computed
    // We have to provide this indirection for methods calling this
    // outside of this module, because the actual implementation is in
    // a non-exported package.

	public static String formatBalance(GnuCashAccountImpl acct, FixedPointNumber blnc) {
		return AccountBalanceHelper_FP.formatBalance( acct, blnc );
	}
	
	public static String formatBalance(GnuCashAccountImpl acct, FixedPointNumber blnc, Locale lcl) {
		return AccountBalanceHelper_FP.formatBalance( acct, blnc, lcl );
	}
	
    // ---------------------------------------------------------------

    public boolean hasChildrenMatching(GnuCashAccount acct, GnuCashAccount.Type acctType) {
    	for ( GnuCashAccount chld : acct.getChildren() ) {
    		if ( chld.getType() == acctType ) {
    			return true;
    		}
    	}
    	
    	return false;
	}

    public boolean hasChildrenMatchingRecursive(GnuCashAccount acct, GnuCashAccount.Type acctType) {
    	for ( GnuCashAccount chld : acct.getChildrenRecursive() ) {
    		if ( chld.getType() == acctType ) {
    			return true;
    		}
    	}
    	
    	return false;
	}

}
