package org.gnucash.api.read.hlp.fil;

import java.util.Collection;

import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.base.basetypes.simple.GCshBdgtID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashFile_Bdgt {

	/**
	 * @param bdgtID the unique ID of the budget to look for
	 * @return the budget or null if it's not found
	 */
	GnuCashBudget getBudgetByID(GCshBdgtID bdgtID);

	/**
	 * @param expr search expression
	 * @return
	 */
	Collection<GnuCashBudget> getBudgetsByName(String expr);

	/**
	 * @param expr search expression
	 * @param relaxed
	 * @return
	 */
	Collection<GnuCashBudget> getBudgetsByName(String expr, boolean relaxed);

	/**
	 * @param expr search expression
	 * @return
	 * @throws NoEntryFoundException
	 * @throws TooManyEntriesFoundException
	 */
	GnuCashBudget getBudgetByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

	/**
	 * @return a (read-only) collection of all institutions Do not modify the
	 *         returned collection!
	 */
	Collection<GnuCashBudget> getBudgets();

}
