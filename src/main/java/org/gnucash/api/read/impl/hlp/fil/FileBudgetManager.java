package org.gnucash.api.read.impl.hlp.fil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncBudget;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.impl.GnuCashBudgetImpl;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.simple.GCshBdgtID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class FileBudgetManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileBudgetManager.class);

	// ---------------------------------------------------------------

	protected GnuCashFileImpl gcshFile;

	protected Map<GCshBdgtID, GnuCashBudget> bdgtMap;

	// ---------------------------------------------------------------

	public FileBudgetManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		bdgtMap = new HashMap<GCshBdgtID, GnuCashBudget>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncBudget) ) {
				continue;
			}
			GncBudget jwsdpBdgt = (GncBudget) bookElement;

			try {
				GnuCashBudgetImpl bdgt = createBudget(jwsdpBdgt);
				bdgtMap.put(bdgt.getID(), bdgt);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal Budget-Entry with id=" + jwsdpBdgt.getBgtId(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in budget map: " + bdgtMap.size());
	}

	protected GnuCashBudgetImpl createBudget(final GncBudget jwsdpBdgt) {
		GnuCashBudgetImpl bdgt = new GnuCashBudgetImpl(jwsdpBdgt, gcshFile);
		LOGGER.debug("Generated new budget: " + bdgt.getID());
		return bdgt;
	}

	// ---------------------------------------------------------------

	public GnuCashBudget getBudgetByID(final GCshBdgtID bdgtID) {
		if ( bdgtID == null ) {
			throw new IllegalArgumentException("argument <bdgtID> is null");
		}

		if ( ! bdgtID.isSet() ) {
			throw new IllegalArgumentException("argument <bdgtID> is not set");
		}

		if ( bdgtMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashBudget retval = bdgtMap.get(bdgtID);
		if ( retval == null ) {
			LOGGER.warn("getBudgetByID: No Budget with ID '" + bdgtID + "'. We know " + bdgtMap.size() + " budgets.");
		}

		return retval;
	}

	public List<GnuCashBudget> getBudgetsByName(final String expr) {
		if ( expr == null ) {
			throw new IllegalArgumentException("argument <expr> is null");
		}

		if ( expr.isBlank() ) {
			throw new IllegalArgumentException("argument <expr> is blank");
		}

		return getBudgetsByName(expr, true);
	}

	public List<GnuCashBudget> getBudgetsByName(final String expr, boolean relaxed) {
		if ( expr == null ) {
			throw new IllegalArgumentException("argument <expr> is null");
		}

		if ( expr.isBlank() ) {
			throw new IllegalArgumentException("argument <expr> is blank");
		}

		if ( bdgtMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashBudget> result = new ArrayList<GnuCashBudget>();

		for ( GnuCashBudget bdgt : getBudgets() ) {
			if ( relaxed ) {
				if ( bdgt.getName().toLowerCase().contains(expr.trim().toLowerCase()) ) {
					result.add(bdgt);
				}
			} else {
				if ( bdgt.getName().equals(expr) ) {
					result.add(bdgt);
				}
			}
		}

		return result;
	}

	public GnuCashBudget getBudgetByNameUniq(final String expr)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( expr == null ) {
			throw new IllegalArgumentException("argument <expr> is null");
		}

		if ( expr.isBlank() ) {
			throw new IllegalArgumentException("argument <expr> is blank");
		}

		List<GnuCashBudget> bdgtList = getBudgetsByName(expr, false);
		if ( bdgtList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( bdgtList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return bdgtList.get(0);
	}

	// ::CHECK
	// https://stackoverflow.com/questions/52620446/collectors-tounmodifiablelist-vs-collections-unmodifiablelist-in-java-10?rq=3
	public Collection<GnuCashBudget> getBudgets() {
		if ( bdgtMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(bdgtMap.values());
	}

	// ---------------------------------------------------------------

	public int getNofEntriesBudgetMap() {
		return bdgtMap.size();
	}

}
