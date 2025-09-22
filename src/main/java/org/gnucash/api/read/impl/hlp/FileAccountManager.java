package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashAccount.Type;
import org.gnucash.api.read.aux.GCshAcctLot;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.aux.GCshAcctLotImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class FileAccountManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileAccountManager.class);

	// ---------------------------------------------------------------

	protected GnuCashFileImpl gcshFile;

	protected Map<GCshAcctID, GnuCashAccount> acctMap;
	protected Map<GCshLotID, GCshAcctLot>  acctLotMap;

	// ---------------------------------------------------------------

	public FileAccountManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		init1(pRootElement);
		init2(pRootElement);
	}

	private void init1(final GncV2 pRootElement) {
		acctMap = new HashMap<GCshAcctID, GnuCashAccount>();

		for ( GnuCashAccountImpl acct : getAccounts_readAfresh() ) {
			acctMap.put(acct.getID(), acct);
		}

		LOGGER.debug("init1: No. of entries in account map: " + acctMap.size());
	}

	private void init2(final GncV2 pRootElement) {
		acctLotMap = new HashMap<GCshLotID, GCshAcctLot>();

		for ( GnuCashAccount acct : acctMap.values() ) {
			try {
				List<GCshAcctLot> lotList = null;
				lotList = ((GnuCashAccountImpl) acct).getLots();
				if ( lotList != null ) { // yes, that happens
					for ( GCshAcctLot lot : lotList ) {
						acctLotMap.put(lot.getID(), lot);
					}
				}
			} catch (RuntimeException e) {
				LOGGER.error("init2: [RuntimeException] Problem in " + getClass().getName() + ".init2: "
						+ "ignoring illegal Account entry with id=" + acct.getID(), e);
//		System.err.println("init2: ignoring illegal Account entry with id: " + acct.getID());
//		System.err.println("  " + e.getMessage());
			}
		} // for acct

		LOGGER.debug("init2: No. of entries in account lot map: " + acctLotMap.size());
	}

	// ----------------------------

	protected GnuCashAccountImpl createAccount(final GncAccount jwsdpAcct) {
		GnuCashAccountImpl acct = new GnuCashAccountImpl(jwsdpAcct, gcshFile);
		LOGGER.debug("createAccount: Generated new account: " + acct.getID());
		return acct;
	}

	protected GCshAcctLotImpl createAccountLot(
			final GncAccount.ActLots.GncLot jwsdpAcctLot,
			final GnuCashAccountImpl acct) {
		GCshAcctLotImpl lot = new GCshAcctLotImpl(jwsdpAcctLot, acct);
		LOGGER.debug("createAccountLot: Generated new account lot: " + lot.getID());
		return lot;
	}

	// ---------------------------------------------------------------

	public GnuCashAccount getAccountByID(final GCshAcctID acctID) {
		if ( acctID == null ) {
			throw new IllegalArgumentException("argument <acctID> is null");
		}

		if ( ! acctID.isSet() ) {
			throw new IllegalArgumentException("argument <acctID> is not set");
		}

		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashAccount retval = acctMap.get(acctID);
		if ( retval == null ) {
			LOGGER.error("getAccountByID: No Account with ID '" + acctID + "'. We know " + acctMap.size() + " accounts.");
		}

		return retval;
	}

	public List<GnuCashAccount> getAccountsByParentID(final GCshAcctID acctID) {
		if ( acctID == null ) {
			throw new IllegalArgumentException("argument <acctID> is null");
		}
		
		if ( ! acctID.isSet() ) {
			throw new IllegalArgumentException("argument <acctID> is not set");
		}

		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashAccount> retval = new ArrayList<GnuCashAccount>();

		for ( GnuCashAccount acct : acctMap.values() ) {
			GCshAcctID prntID = acct.getParentAccountID();
			if ( prntID == null ) {
				if ( acctID == null ) {
					retval.add((GnuCashAccount) acct);
				} else if ( ! acctID.isSet() ) {
					retval.add((GnuCashAccount) acct);
				}
			} else {
				if ( prntID.equals(acctID) ) {
					retval.add((GnuCashAccount) acct);
				}
			}
		}

		retval.sort(Comparator.naturalOrder()); 

		return retval;
	}

	public List<GnuCashAccount> getAccountsByName(final String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}
		
		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}

		return getAccountsByName(name, true, true);
	}

	public List<GnuCashAccount> getAccountsByName(final String expr, boolean qualif, boolean relaxed) {
		if ( expr == null ) {
			throw new IllegalArgumentException("argument <expr> is null");
		}
		
		if ( expr.trim().equals("") ) {
			throw new IllegalArgumentException("argument <expr> is empty");
		}
		
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashAccount> result = new ArrayList<GnuCashAccount>();

		for ( GnuCashAccount acct : acctMap.values() ) {
			if ( relaxed ) {
				if ( qualif ) {
					if ( acct.getQualifiedName().toLowerCase().contains(expr.trim().toLowerCase()) ) {
						result.add(acct);
					}
				} else {
					if ( acct.getName().toLowerCase().contains(expr.trim().toLowerCase()) ) {
						result.add(acct);
					}
				}
			} else {
				if ( qualif ) {
					if ( acct.getQualifiedName().equals(expr) ) {
						result.add(acct);
					}
				} else {
					if ( acct.getName().equals(expr) ) {
						result.add(acct);
					}
				}
			}
		}

		result.sort(Comparator.naturalOrder()); 

		return result;
	}

	public GnuCashAccount getAccountByNameUniq(final String name, final boolean qualif)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}
		
		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}
		
		List<GnuCashAccount> acctList = getAccountsByName(name, qualif, false);
		if ( acctList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( acctList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return acctList.get(0);
	}

	/*
	 * warning: this function has to traverse all accounts. If it much faster to try
	 * getAccountByID first and only call this method if the returned account does
	 * not have the right name.
	 */
	public GnuCashAccount getAccountByNameEx(final String nameRegEx)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( nameRegEx == null ) {
			throw new IllegalArgumentException("argument <nameRegEx> is null");
		}
		
		if ( nameRegEx.trim().equals("") ) {
			throw new IllegalArgumentException("argument <nameRegEx> is empty");
		}

		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashAccount foundAccount = getAccountByNameUniq(nameRegEx, true);
		if ( foundAccount != null ) {
			return foundAccount;
		}
		Pattern pattern = Pattern.compile(nameRegEx);

		for ( GnuCashAccount acct : acctMap.values() ) {
			Matcher matcher = pattern.matcher(acct.getName());
			if ( matcher.matches() ) {
				return acct;
			}
		}

		return null;
	}

	/*
	 * First try to fetch the account by id, then fall back to traversing all
	 * accounts to get if by it's name.
	 */
	public GnuCashAccount getAccountByIDorName(final GCshAcctID acctID, final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( acctID == null ) {
			throw new IllegalArgumentException("argument <acctID> is null");
		}

		if ( ! acctID.isSet() ) {
			throw new IllegalArgumentException("argument <acctID> is not set");
		}

		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}

		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}
		
		GnuCashAccount retval = getAccountByID(acctID);
		if ( retval == null ) {
			retval = getAccountByNameUniq(name, true);
		}

		return retval;
	}

	/*
	 * First try to fetch the account by id, then fall back to traversing all
	 * accounts to get if by it's name.
	 */
	public GnuCashAccount getAccountByIDorNameEx(final GCshAcctID acctID, final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( acctID == null ) {
			throw new IllegalArgumentException("argument <acctID> is null");
		}

		if ( ! acctID.isSet() ) {
			throw new IllegalArgumentException("argument <acctID> is not set");
		}

		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}

		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}

		GnuCashAccount retval = getAccountByID(acctID);
		if ( retval == null ) {
			retval = getAccountByNameEx(name);
		}

		return retval;
	}

	public List<GnuCashAccount> getAccountsByType(Type type) {
		List<GnuCashAccount> result = new ArrayList<GnuCashAccount>();

		for ( GnuCashAccount acct : getAccounts() ) {
			if ( acct.getType() == type ) {
				result.add(acct);
			}
		}

		result.sort(Comparator.naturalOrder()); 

		return result;
	}

	public List<GnuCashAccount> getAccountsByTypeAndName(Type type, String expr, boolean qualif, boolean relaxed) {
		if ( expr == null ) {
			throw new IllegalArgumentException("argument <expr> is null");
		}

		if ( expr.trim().equals("") ) {
			throw new IllegalArgumentException("argument <expr> is empty");
		}

		List<GnuCashAccount> result = new ArrayList<GnuCashAccount>();

		for ( GnuCashAccount acct : getAccountsByName(expr, qualif, relaxed) ) {
			if ( acct.getType() == type ) {
				result.add(acct);
			}
		}

		result.sort(Comparator.naturalOrder()); 

		return result;
	}

	// ---------------------------------------------------------------

	public GCshAcctLot getAccountLotByID(final GCshLotID lotID) {
		if ( lotID == null ) {
			throw new IllegalArgumentException("argument <lotID> is null");
		}
		
		if ( ! lotID.isSet() ) {
			throw new IllegalArgumentException("argument <lotID> is not set");
		}
		
		if ( acctLotMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GCshAcctLot retval = acctLotMap.get(lotID);
		if ( retval == null ) {
			LOGGER.warn("getAccountLotByID: No Account-Lot with id '" + lotID + "'. We know " + acctLotMap.size() + " account lots.");
		}

		return retval;
	}

	// ---------------------------------------------------------------

	public List<GnuCashAccount> getAccounts() {
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		ArrayList<GnuCashAccount> temp = new ArrayList<GnuCashAccount>(acctMap.values());
		Collections.sort(temp);
		
		return Collections.unmodifiableList(temp);
	}

	public List<GnuCashAccountImpl> getAccounts_readAfresh() {
		List<GnuCashAccountImpl> result = new ArrayList<GnuCashAccountImpl>();

		for ( GncAccount jwsdpAcct : getAccounts_raw() ) {
			try {
				GnuCashAccountImpl acct = createAccount(jwsdpAcct);
				result.add(acct);
			} catch (RuntimeException e) {
				LOGGER.error("getAccounts_readAfresh: [RuntimeException] Problem in " + getClass().getName()
						+ ".getAccounts_readAfresh: " + "ignoring illegal Account entry with id="
						+ jwsdpAcct.getActId().getValue(), e);
//		System.err.println("getAccounts_readAfresh: ignoring illegal Account entry with id: " + jwsdpAcct.getActID().getValue());
//		System.err.println("  " + e.getMessage());
			}
		}

		return result;
	}

	private List<GncAccount> getAccounts_raw() {
		GncV2 pRootElement = gcshFile.getRootElement();

		List<GncAccount> result = new ArrayList<GncAccount>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncAccount) ) {
				continue;
			}

			GncAccount jwsdpAcct = (GncAccount) bookElement;
			result.add(jwsdpAcct);
		}

		return result;
	}

	// ----------------------------

	public List<GCshAcctLot> getAccountLots() {
		if ( acctLotMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		
		List<GCshAcctLot> result = new ArrayList<GCshAcctLot>();
		for ( GCshAcctLot elt : acctLotMap.values() ) {
			result.add(elt);
		}
		
		return Collections.unmodifiableList(result);
	}

	public List<GCshAcctLotImpl> getAccountLots_readAfresh() {
		List<GCshAcctLotImpl> result = new ArrayList<GCshAcctLotImpl>();

		for ( GnuCashAccountImpl acct : getAccounts_readAfresh() ) {
			for ( GncAccount.ActLots.GncLot jwsdpAcctLot : getAccountLots_raw(acct.getID()) ) {
				try {
					GCshAcctLotImpl lot = createAccountLot(jwsdpAcctLot, acct);
					result.add(lot);
				} catch (RuntimeException e) {
					LOGGER.error("getAccountLots_readAfresh(1): [RuntimeException] Problem in "
							+ "ignoring illegal Account Lot entry with id="
							+ jwsdpAcctLot.getLotId().getValue(), e);
//			System.err.println("getAccountLots_readAfresh(1): ignoring illegal Account Lot entry with id: " + jwsdpAcctLot.getLotID().getValue());
//			System.err.println("  " + e.getMessage());
				}
			} // for jwsdpAcctLot
		} // for acct

		return result;
	}

	public List<GCshAcctLotImpl> getAccountLots_readAfresh(final GCshAcctID acctID) {
		List<GCshAcctLotImpl> result = new ArrayList<GCshAcctLotImpl>();

		for ( GnuCashAccountImpl acct : getAccounts_readAfresh() ) {
			if ( acct.getID().equals(acctID) ) {
				for ( GncAccount.ActLots.GncLot jwsdpAcctLot : getAccountLots_raw(acct.getID()) ) {
					try {
						GCshAcctLotImpl lot = createAccountLot(jwsdpAcctLot, acct);
						result.add(lot);
					} catch (RuntimeException e) {
						LOGGER.error("getAccountLots_readAfresh(2): [RuntimeException] Problem in "
								+ "ignoring illegal Account Lot entry with id="
								+ jwsdpAcctLot.getLotId().getValue(), e);
//			System.err.println("getAccountLots_readAfresh(2): ignoring illegal Account Lot entry with id: " + jwsdpAcctLot.getLotID().getValue());
//			System.err.println("  " + e.getMessage());
					}
				} // for jwsdpAcctLot
			} // if
		} // for acct

		return result;
	}

	@SuppressWarnings("unused")
	private List<GncAccount.ActLots.GncLot> getAccountLots_raw(final GncAccount jwsdpAcct) {
		List<GncAccount.ActLots.GncLot> result = new ArrayList<GncAccount.ActLots.GncLot>();

		for ( GncAccount.ActLots.GncLot jwsdpAcctLot : jwsdpAcct.getActLots().getGncLot() ) {
			result.add(jwsdpAcctLot);
		}

		return result;
	}

	private List<GncAccount.ActLots.GncLot> getAccountLots_raw(final GCshAcctID acctID) {
		List<GncAccount.ActLots.GncLot> result = new ArrayList<GncAccount.ActLots.GncLot>();

		for ( GncAccount jwsdpAcct : getAccounts_raw() ) {
			if ( jwsdpAcct.getActId().getValue().equals(acctID.toString()) ) {
				for ( GncAccount.ActLots.GncLot jwsdpAcctLot : jwsdpAcct.getActLots().getGncLot() ) {
					result.add(jwsdpAcctLot);
				}
			}
		}

		return result;
	}

	// ---------------------------------------------------------------

	public GCshAcctID getRootAccountID()  {
		if ( getRootAccount() == null )
			return null;
		
		return getRootAccount().getID();
	}

	public GnuCashAccount getRootAccount()  {
		for ( GnuCashAccount acct : getAccounts() ) {
			if ( acct.getParentAccountID() == null && 
				 acct.getType() == GnuCashAccount.Type.ROOT ) {
				return acct;
			}
		}

		return null; // Compiler happy
	}

	public List<? extends GnuCashAccount> getParentlessAccounts() {
		try {
			List<GnuCashAccount> retval = new ArrayList<GnuCashAccount>();

			for ( GnuCashAccount acct : getAccounts() ) {
				if ( acct.getParentAccountID() == null ) {
					retval.add(acct);
				}

			}

			retval.sort(Comparator.naturalOrder()); 

			return retval;
		} catch (RuntimeException e) {
			LOGGER.error("getParentlessAccounts: Problem getting all root-account", e);
			throw e;
		} catch (Throwable e) {
			LOGGER.error("getParentlessAccounts: SERIOUS Problem getting all root-account", e);
			return new ArrayList<GnuCashAccount>();
		}
	}

	public List<GCshAcctID> getTopAccountIDs() {
		List<GCshAcctID> result = new ArrayList<GCshAcctID>();

		GCshAcctID rootAcctID = getRootAccount().getID();
		for ( GnuCashAccount acct : getAccounts() ) {
			if ( acct.getParentAccountID() != null ) {
				if ( acct.getParentAccountID().equals(rootAcctID) &&
					 ( acct.getType() == GnuCashAccount.Type.ASSET ||
					   acct.getType() == GnuCashAccount.Type.LIABILITY ||
					   acct.getType() == GnuCashAccount.Type.INCOME ||
					   acct.getType() == GnuCashAccount.Type.EXPENSE ||
					   acct.getType() == GnuCashAccount.Type.EQUITY) ) {
					result.add(acct.getID());
				}
			}
		}

		return result;
	}

	// ---------------------------

	public List<GnuCashAccount> getTopAccounts() {
		List<GnuCashAccount> result = new ArrayList<GnuCashAccount>();

		for ( GCshAcctID acctID : getTopAccountIDs() ) {
			GnuCashAccount acct = getAccountByID(acctID);
			result.add(acct);
		}

		result.sort(Comparator.naturalOrder()); 

		return result;
	}

	// ---------------------------------------------------------------

	public int getNofEntriesAccountMap() {
		return acctMap.size();
	}

	public int getNofEntriesAccountLotMap() {
		return acctLotMap.size();
	}

}
