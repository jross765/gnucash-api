package org.gnucash.api.write.hlp.fil;

import java.util.Collection;
import java.util.List;

import org.gnucash.api.write.GnuCashWritableBudget;
import org.gnucash.api.write.ObjectCascadeException;
import org.gnucash.base.basetypes.simple.GCshBdgtID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashWritableFile_Bdgt {

	/**
	 * @param bdgtID 
	 * @param id the unique id of the budget to look for
	 * @return the budget or null if it's not found
	 * 
	 * @see #getBudgetByID(GCshBdgtID)
	 */
	GnuCashWritableBudget getWritableBudgetByID(GCshBdgtID bdgtID);

	Collection<GnuCashWritableBudget> getWritableBudgetsByName(String expr);

	/**
	 * @param expr search expression
	 * @param relaxed
	 * @return
	 */
	Collection<GnuCashWritableBudget> getWritableBudgetsByName(String expr, boolean relaxed);

	/**
	 * @param expr search expression
	 * @return
	 * @throws NoEntryFoundException
	 * @throws TooManyEntriesFoundException
	 */
	GnuCashWritableBudget getWritableBudgetByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

	/**
	 * @return a (read-only) collection of all budgets Do not modify the
	 *         returned collection!
	 */
	List<GnuCashWritableBudget> getWritableBudgets();

	// ----------------------------

	/**
	 * @param name 
	 * @return a new budget with no entries that is already added to this file
	 */
	GnuCashWritableBudget createWritableBudget(String name);

	/**
	 *
	 * @param bdgt the budget to remove.
	 */
	void removeBudget(GnuCashWritableBudget bdgt) throws ObjectCascadeException;

}
