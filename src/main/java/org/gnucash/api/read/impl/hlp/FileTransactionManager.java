package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.GncTransaction.TrnSplits.TrnSplit.SplitLot;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.read.impl.GnuCashTransactionSplitImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.simple.GCshIDNotSetException;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransactionManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileTransactionManager.class);

	// ---------------------------------------------------------------

	protected GnuCashFileImpl gcshFile;

	protected Map<GCshTrxID, GnuCashTransaction>      trxMap;
	protected Map<GCshSpltID, GnuCashTransactionSplit> trxSpltMap;

	// ---------------------------------------------------------------

	public FileTransactionManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		init1(pRootElement);
		init2(pRootElement);
	}

	private void init1(final GncV2 pRootElement) {
		trxMap = new HashMap<GCshTrxID, GnuCashTransaction>();

		for ( GnuCashTransactionImpl trx : getTransactions_readAfresh() ) {
			trxMap.put(trx.getID(), trx);
		}

		LOGGER.debug("init1: No. of entries in transaction map: " + trxMap.size());
	}

	private void init2(final GncV2 pRootElement) {
		trxSpltMap = new HashMap<GCshSpltID, GnuCashTransactionSplit>();

		for ( GnuCashTransaction trx : trxMap.values() ) {
			try {
				List<GnuCashTransactionSplit> spltList = null;
				if ( gcshFile instanceof GnuCashWritableFileImpl ) {
					spltList = ((GnuCashTransactionImpl) trx).getSplits(false, true);
				} else {
					spltList = ((GnuCashTransactionImpl) trx).getSplits(true, true);
				}
				if ( spltList != null ) { // shouldn't happen, just in case...
					for ( GnuCashTransactionSplit splt : spltList ) {
						trxSpltMap.put(splt.getID(), splt);
					}
				}
			} catch (RuntimeException e) {
				LOGGER.error("init2: [RuntimeException] Problem in " + getClass().getName() + ".init2: "
						+ "ignoring illegal Transaction entry with id=" + trx.getID(), e);
//		System.err.println("init2: ignoring illegal Transaction entry with id: " + trx.getID());
//		System.err.println("  " + e.getMessage());
			}
		} // for trx

		LOGGER.debug("init2: No. of entries in transaction split map: " + trxSpltMap.size());
	}

	// ----------------------------

	protected GnuCashTransactionImpl createTransaction(final GncTransaction jwsdpTrx) {
		GnuCashTransactionImpl trx = new GnuCashTransactionImpl(jwsdpTrx, gcshFile.getGnuCashFile(), true);
		LOGGER.debug("createTransaction: Generated new transaction: " + trx.getID());
		return trx;
	}

	protected GnuCashTransactionSplitImpl createTransactionSplit(
			final GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt,
			final GnuCashTransaction trx, 
			final boolean addSpltToAcct, 
			final boolean addSpltToInvc) {
		GnuCashTransactionSplitImpl splt = new GnuCashTransactionSplitImpl(jwsdpTrxSplt, trx, 
				                                                           addSpltToAcct, addSpltToInvc);
		LOGGER.debug("createTransactionSplit: Generated new transaction split: " + splt.getID());
		return splt;
	}

	// ---------------------------------------------------------------

	public GnuCashTransaction getTransactionByID(final GCshTrxID trxID) {
		if ( trxID == null ) {
			throw new IllegalArgumentException("argument <trxID> is null");
		}
		
		if ( ! trxID.isSet() ) {
			throw new IllegalArgumentException("argument <trxID> is null");
		}
		
		if ( trxMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashTransaction retval = trxMap.get(trxID);
		if ( retval == null ) {
			LOGGER.warn("getTransactionByID: No Transaction with id '" + trxID + "'. We know " + trxMap.size() + " transactions.");
		}

		return retval;
	}

	// ---------------------------------------------------------------

	public GnuCashTransactionSplit getTransactionSplitByID(final GCshSpltID spltID) {
		if ( spltID == null ) {
			throw new IllegalArgumentException("argument <spltID> is null");
		}
		
		if ( ! spltID.isSet() ) {
			throw new IllegalArgumentException("argument <spltID> is not set");
		}
		
		if ( trxSpltMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashTransactionSplit retval = trxSpltMap.get(spltID);
		if ( retval == null ) {
			LOGGER.warn("getTransactionSplitByID: No Transaction-Split with id '" + spltID + "'. We know " + trxSpltMap.size() + " transaction splits.");
		}

		return retval;
	}

	public List<GnuCashTransactionSplit> getTransactionSplitsByAccountLotID(final GCshLotID acctLotID) {
		if ( acctLotID == null ) {
			throw new IllegalArgumentException("argument <acctLotID> is null");
		}
		
		if ( ! acctLotID.isSet() ) {
			throw new IllegalArgumentException("argument <acctLotID> is not set");
		}
		
		String acctLotIDStr = null;
		try {
			acctLotIDStr = acctLotID.get();
		} catch (GCshIDNotSetException e) {
			throw new IllegalArgumentException("Cannot get account-lot ID");
		}
		
		List<GnuCashTransactionSplit> result = new ArrayList<GnuCashTransactionSplit>();

		for ( GnuCashTransactionSplit splt : trxSpltMap.values() ) {
			SplitLot spltLot = ((GnuCashTransactionSplitImpl) splt).getJwsdpPeer().getSplitLot();
			if ( spltLot != null ) {
				if ( spltLot.getType().equals(Const.XML_DATA_TYPE_GUID) &&
					 spltLot.getValue().equals(acctLotIDStr) ) {
					GnuCashTransactionSplit newSplt = gcshFile.getTransactionSplitByID(splt.getID());
					result.add(newSplt);
				}
			}
		}

		return result;
	}

	public List<GnuCashTransactionSplit> getTransactionSplitsByCmdtyCurrID(final GCshCmdtyCurrID cmdtyCurrID) {
		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
		}
		
		List<GnuCashTransactionSplit> result = new ArrayList<GnuCashTransactionSplit>();

		for ( GnuCashTransactionSplit splt : trxSpltMap.values() ) {
			if ( splt.getAccount().getCmdtyCurrID().toString().equals(cmdtyCurrID.toString()) ) {
				GnuCashTransactionSplit newSplt = gcshFile.getTransactionSplitByID(splt.getID());
				result.add(newSplt);
			}
		}

		return result;
	}

	// ---------------------------------------------------------------

	public List<? extends GnuCashTransaction> getTransactions() {
		if ( trxMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		ArrayList<GnuCashTransaction> temp = new ArrayList<GnuCashTransaction>(trxMap.values());
		Collections.sort(temp);
		
		return Collections.unmodifiableList(temp);
	}

	public List<GnuCashTransactionImpl> getTransactions_readAfresh() {
		List<GnuCashTransactionImpl> result = new ArrayList<GnuCashTransactionImpl>();

		for ( GncTransaction jwsdpTrx : getTransactions_raw() ) {
			try {
				GnuCashTransactionImpl trx = createTransaction(jwsdpTrx);
				result.add(trx);
			} catch (RuntimeException e) {
				LOGGER.error("getTransactions_readAfresh: [RuntimeException] Problem in " + getClass().getName()
						+ ".getTransactions_readAfresh: " + "ignoring illegal Transaction entry with id="
						+ jwsdpTrx.getTrnId().getValue(), e);
//		System.err.println("getTransactions_readAfresh: ignoring illegal Transaction entry with id: " + jwsdpTrx.getTrnID().getValue());
//		System.err.println("  " + e.getMessage());
			}
		}

		return result;
	}

	private List<GncTransaction> getTransactions_raw() {
		GncV2 pRootElement = gcshFile.getRootElement();

		List<GncTransaction> result = new ArrayList<GncTransaction>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncTransaction) ) {
				continue;
			}

			GncTransaction jwsdpTrx = (GncTransaction) bookElement;
			result.add(jwsdpTrx);
		}

		return result;
	}

	// ----------------------------

	public List<GnuCashTransactionSplit> getTransactionSplits() {
		if ( trxSpltMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		
		ArrayList<GnuCashTransactionSplit> temp = new ArrayList<GnuCashTransactionSplit>(trxSpltMap.values());
		Collections.sort(temp);
		
		return Collections.unmodifiableList(temp);
	}

	public List<GnuCashTransactionSplitImpl> getTransactionSplits_readAfresh() {
		List<GnuCashTransactionSplitImpl> result = new ArrayList<GnuCashTransactionSplitImpl>();

		for ( GnuCashTransaction trx : getTransactions_readAfresh() ) {
			for ( GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt : getTransactionSplits_raw(trx.getID()) ) {
				try {
					GnuCashTransactionSplitImpl splt = createTransactionSplit(jwsdpTrxSplt, trx,
																false, false);
					result.add(splt);
				} catch (RuntimeException e) {
					LOGGER.error("getTransactionSplits_readAfresh(1): [RuntimeException] Problem in "
							+ "ignoring illegal Transaction Split entry with id="
							+ jwsdpTrxSplt.getSplitId().getValue(), e);
//			System.err.println("getTransactionSplits_readAfresh(1): ignoring illegal Transaction Split entry with id: " + jwsdpTrxSplt.getSplitID().getValue());
//			System.err.println("  " + e.getMessage());
				}
			} // for jwsdpTrxSplt
		} // for trx

		return result;
	}

	public List<GnuCashTransactionSplitImpl> getTransactionSplits_readAfresh(final GCshTrxID trxID) {
		List<GnuCashTransactionSplitImpl> result = new ArrayList<GnuCashTransactionSplitImpl>();

		for ( GnuCashTransaction trx : getTransactions_readAfresh() ) {
			if ( trx.getID().equals(trxID) ) {
				for ( GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt : getTransactionSplits_raw(trx.getID()) ) {
					try {
						GnuCashTransactionSplitImpl splt = createTransactionSplit(jwsdpTrxSplt, trx, 
																	true, true);
						result.add(splt);
					} catch (RuntimeException e) {
						LOGGER.error("getTransactionSplits_readAfresh(2): [RuntimeException] Problem in "
								+ "ignoring illegal Transaction Split entry with id="
								+ jwsdpTrxSplt.getSplitId().getValue(), e);
//			System.err.println("getTransactionSplits_readAfresh(2): ignoring illegal Transaction Split entry with id: " + jwsdpTrxSplt.getSplitID().getValue());
//			System.err.println("  " + e.getMessage());
					}
				} // for jwsdpTrxSplt
			} // if
		} // for trx

		return result;
	}

	@SuppressWarnings("unused")
	private List<GncTransaction.TrnSplits.TrnSplit> getTransactionSplits_raw(final GncTransaction jwsdpTrx) {
		List<GncTransaction.TrnSplits.TrnSplit> result = new ArrayList<GncTransaction.TrnSplits.TrnSplit>();

		for ( GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt : jwsdpTrx.getTrnSplits().getTrnSplit() ) {
			result.add(jwsdpTrxSplt);
		}

		return result;
	}

	private List<GncTransaction.TrnSplits.TrnSplit> getTransactionSplits_raw(final GCshTrxID trxID) {
		List<GncTransaction.TrnSplits.TrnSplit> result = new ArrayList<GncTransaction.TrnSplits.TrnSplit>();

		for ( GncTransaction jwsdpTrx : getTransactions_raw() ) {
			if ( jwsdpTrx.getTrnId().getValue().equals(trxID.toString()) ) {
				for ( GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt : jwsdpTrx.getTrnSplits().getTrnSplit() ) {
					result.add(jwsdpTrxSplt);
				}
			}
		}

		return result;
	}

	// ---------------------------------------------------------------
	
	protected GncTransaction getTransaction_raw(final GCshTrxID trxID) {
		for ( GncTransaction jwsdpTrx : getTransactions_raw() ) {
			if ( jwsdpTrx.getTrnId().getValue().equals(trxID.toString()) ) {
				return jwsdpTrx;
			}
		}
		
		return null;
	}

	// ---------------------------------------------------------------

	public int getNofEntriesTransactionMap() {
		return trxMap.size();
	}

	public int getNofEntriesTransactionSplitMap() {
		return trxSpltMap.size();
	}

}
