package org.gnucash.api.currency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class ComplexPriceTable implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ComplexPriceTable.class);

	private static final long serialVersionUID = -3303232787168479120L;

	// ---------------------------------------------------------------

	public interface ComplexPriceTableChangeListener {
		void conversionFactorChanged(final String currency, final FixedPointNumber factor);
		void conversionFactorChanged(final String currency, final BigFraction factor);
	}

	// -----------------------------------------------------------

	private transient volatile List<ComplexPriceTableChangeListener> listeners = null;

	// -----------------------------------------------------------

	private Map<String, SimplePriceTable> nameSpace2PrcTab = null;

	// -----------------------------------------------------------

	public ComplexPriceTable() {
		nameSpace2PrcTab = new HashMap<String, SimplePriceTable>();

		addForNameSpace(GCshCmdtyCurrNameSpace.CURRENCY, new SimpleCurrencyExchRateTable());
		// CAUTION: We do not / cannot add a default commodity name space as
		// in sister project JKMyMoneyLib, because we do not know in advance which
		// name spaces are going to be used.
	}

	// -----------------------------------------------------------

	public void addComplexPriceTableChangeListener(final ComplexPriceTableChangeListener listener) {
		if ( listeners == null ) {
			listeners = new ArrayList<>();
		}
		
		listeners.add(listener);
	}

	public void removeComplexPriceTableChangeListener(final ComplexPriceTableChangeListener listener) {
		if ( listeners == null ) {
			listeners = new ArrayList<>();
		}
		
		listeners.remove(listener);
	}

	protected void firePriceTableChanged(final String code, final FixedPointNumber factor) {
		if ( code == null ) {
			throw new IllegalArgumentException("argument <code> is null");
		}

		if ( code.trim().equals("") ) {
			throw new IllegalArgumentException("argument <code> is empty");
		}
		
		if ( factor == null ) {
			throw new IllegalArgumentException("argument <factor> is null");
		}
		
		// ::TODO ::CHECK
		// In the sister project, we had to remove this check (cf. comment there).
		// What about GnuCash?
		if ( factor.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <factor> must be > 0");
		}
		
		if ( listeners != null ) {
			for ( ComplexPriceTableChangeListener listener : listeners ) {
				listener.conversionFactorChanged(code, factor);
			}
		}
	}

	protected void firePriceTableChanged(final String code, final BigFraction factor) {
		if ( code == null ) {
			throw new IllegalArgumentException("argument <code> is null");
		}

		if ( code.trim().equals("") ) {
			throw new IllegalArgumentException("argument <code> is empty");
		}
		
		if ( factor == null ) {
			throw new IllegalArgumentException("argument <factor> is null");
		}
		
		// ::TODO ::CHECK
		// In the sister project, we had to remove this check (cf. comment there).
		// What about GnuCash?
		if ( factor.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <factor> must be > 0");
		}
		
		if ( listeners != null ) {
			for ( ComplexPriceTableChangeListener listener : listeners ) {
				listener.conversionFactorChanged(code, factor);
			}
		}
	}

	// ------------------------ support for propertyChangeListeners
	/// **
	// * support for firing PropertyChangeEvents
	// * (gets initialized only if we really have listeners)
	// */
	// protected volatile PropertyChangeSupport propertyChange = null;
	//
	/// **
	// * Add a PropertyChangeListener to the listener list.
	// * The listener is registered for all properties.
	// *
	// * @param listener The PropertyChangeListener to be added
	// */
	// public final void addPropertyChangeListener(
	// final PropertyChangeListener listener) {
	// if (propertyChange == null) {
	// propertyChange = new PropertyChangeSupport(this);
	// }
	// propertyChange.addPropertyChangeListener(listener);
	// }
	//
	/// **
	// * Add a PropertyChangeListener for a specific property. The listener
	// * will be invoked only when a call on firePropertyChange names that
	// * specific property.
	// *
	// * @param propertyName The name of the property to listen on.
	// * @param listener The PropertyChangeListener to be added
	// */
	// public final void addPropertyChangeListener(
	// final String propertyName,
	// final PropertyChangeListener listener) {
	// if (propertyChange == null) {
	// propertyChange = new PropertyChangeSupport(this);
	// }
	// propertyChange.addPropertyChangeListener(propertyName, listener);
	// }
	//
	/// **
	// * Remove a PropertyChangeListener for a specific property.
	// *
	// * @param propertyName The name of the property that was listened on.
	// * @param listener The PropertyChangeListener to be removed
	// */
	// public final void removePropertyChangeListener(
	// final String propertyName,
	// final PropertyChangeListener listener) {
	// if (propertyChange != null) {
	// propertyChange.removePropertyChangeListener(propertyName, listener);
	// }
	// }
	//
	/// **
	// * Remove a PropertyChangeListener from the listener list.
	// * This removes a PropertyChangeListener that was registered
	// * for all properties.
	// *
	// * @param listener The PropertyChangeListener to be removed
	// */
	// public synchronized void removePropertyChangeListener(
	// final PropertyChangeListener listener) {
	// if (propertyChange != null) {
	// propertyChange.removePropertyChangeListener(listener);
	// }
	// }
	//
	// -------------------------------------------------------

	/**
	 * Add a new name space with no conversion-factors.<br/>
	 * Will not overwrite an existing name space.
	 *
	 * @param nameSpace the new nameSpace to add.
	 */
	public void addForNameSpace(final String nameSpace) {
		if ( nameSpace == null ) {
			throw new IllegalArgumentException("argument <nameSpace> is null");
		}

		if ( nameSpace.trim().equals("") ) {
			throw new IllegalArgumentException("argument <nameSpace> is empty");
		}

		if ( nameSpace2PrcTab.keySet().contains(nameSpace) ) {
			return;
		}

		if ( nameSpace.equals(GCshCmdtyCurrNameSpace.CURRENCY) ) {
			SimpleCurrencyExchRateTable table = new SimpleCurrencyExchRateTable();
			table.clear();
			addForNameSpace(nameSpace, table);
		} else {
			SimpleCommodityQuoteTable table = new SimpleCommodityQuoteTable();
			table.clear();
			addForNameSpace(nameSpace, table);
		}
	}

	/**
	 * Add a new name space with an initial set of conversion-factors.
	 *
	 * @param nameSpace the new nameSpace to add.
	 * @param table     an initial set of conversion-factors.
	 */
	public void addForNameSpace(final String nameSpace, final SimplePriceTable table) {
		if ( nameSpace == null ) {
			throw new IllegalArgumentException("argument <nameSpace> is null");
		}

		if ( nameSpace.trim().equals("") ) {
			throw new IllegalArgumentException("argument <nameSpace> is empty");
		}

		if ( table == null ) {
			throw new IllegalArgumentException("argument <table> is null");
		}

		nameSpace2PrcTab.put(nameSpace, table);
		LOGGER.debug("addForNameSpace: Added new table for name space '" + nameSpace + "'");
	}

	// ---------------------------------------------------------------

	/**
	 * @param nameSpace 
	 * @param code 
	 * @return the factor to convert the price specified by the name-space-code-pair
	 * @see SimplePriceTable#setConversionFactor(java.lang.String, FixedPointNumber)
	 */
	public FixedPointNumber getConversionFactor(final String nameSpace, final String code) {
		if ( nameSpace == null ) {
			throw new IllegalArgumentException("argument <nameSpace> is null");
		}

		if ( nameSpace.trim().equals("") ) {
			throw new IllegalArgumentException("argument <nameSpace> is empty");
		}

		if ( code == null ) {
			throw new IllegalArgumentException("argument <code> is null");
		}

		if ( code.trim().equals("") ) {
			throw new IllegalArgumentException("argument <code> is empty");
		}

		SimplePriceTable table = getByNamespace(nameSpace);
		if ( table == null ) {
			return null;
		}

		return table.getConversionFactor(code);
	}

	public FixedPointNumber getConversionFactor(final GCshCmdtyCurrID cmdtyCurrID) {
		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}

		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
		}

		return getConversionFactor(cmdtyCurrID.getNameSpace(), cmdtyCurrID.getCode());
	}

	public FixedPointNumber getConversionFactor(final Currency curr) {
		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null");
		}

		return getConversionFactor(GCshCmdtyCurrNameSpace.CURRENCY, curr.getCurrencyCode());
	}

	// ----------------------------

	public BigFraction getConversionFactorRat(final String nameSpace, final String code) {
		if ( nameSpace == null ) {
			throw new IllegalArgumentException("argument <nameSpace> is null");
		}

		if ( nameSpace.trim().equals("") ) {
			throw new IllegalArgumentException("argument <nameSpace> is empty");
		}

		if ( code == null ) {
			throw new IllegalArgumentException("argument <code> is null");
		}

		if ( code.trim().equals("") ) {
			throw new IllegalArgumentException("argument <code> is empty");
		}

		SimplePriceTable table = getByNamespace(nameSpace);
		if ( table == null ) {
			return null;
		}

		return table.getConversionFactorRat(code);
	}

	public BigFraction getConversionFactorRat(final GCshCmdtyCurrID cmdtyCurrID) {
		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}

		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
		}

		return getConversionFactorRat(cmdtyCurrID.getNameSpace(), cmdtyCurrID.getCode());
	}

	public BigFraction getConversionFactorRat(final Currency curr) {
		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null");
		}

		return getConversionFactorRat(GCshCmdtyCurrNameSpace.CURRENCY, curr.getCurrencyCode());
	}

	// ----------------------------

	/**
	 * If the nameSpace does not exist yet, it is created.
	 * 
	 * @param nameSpace 
	 * @param code 
	 * @param factor 
	 *
	 * @see SimplePriceTable#setConversionFactor(java.lang.String, FixedPointNumber)
	 */
	public void setConversionFactor(final String nameSpace, final String code,
			final FixedPointNumber factor) {
		if ( nameSpace == null ) {
			throw new IllegalArgumentException("argument <nameSpace> is null");
		}

		if ( nameSpace.trim().equals("") ) {
			throw new IllegalArgumentException("argument <nameSpace> is empty");
		}

		if ( code == null ) {
			throw new IllegalArgumentException("argument <code> is null");
		}

		if ( code.trim().equals("") ) {
			throw new IllegalArgumentException("argument <code> is empty");
		}

		if ( factor == null ) {
		    throw new IllegalArgumentException("argument <factor> is null");
		}
		
		// ::TODO ::CHECK
		// In the sister project, we had to remove this check (cf. comment there).
		// What about GnuCash?
		if ( factor.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <factor> must be > 0");
		}

		SimplePriceTable table = getByNamespace(nameSpace);
		if ( table == null ) {
			addForNameSpace(nameSpace);
			table = getByNamespace(nameSpace);
		}

		table.setConversionFactor(code, factor);
		table.setConversionFactorRat(code, factor.toBigFraction());

		firePriceTableChanged(code, factor);
	}

	public void setConversionFactor(final GCshCmdtyCurrID cmdtyCurrID, final FixedPointNumber factor) {
		if ( cmdtyCurrID == null ) {
		    throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}
	
		if ( ! cmdtyCurrID.isSet() ) {
		    throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
		}
	
		if ( factor == null ) {
		    throw new IllegalArgumentException("argument <factor> is null");
		}
	
		setConversionFactor(cmdtyCurrID.getNameSpace(), cmdtyCurrID.getCode(),
			            factor);
	}
	
	public void setConversionFactor(final Currency curr, final FixedPointNumber factor) {
		if ( curr == null ) {
		    throw new IllegalArgumentException("argument <curr> is null");
		}
	
		if ( factor == null ) {
		    throw new IllegalArgumentException("argument <factor> is null");
		}
	
		GCshCmdtyCurrID cmdtyCurrID = new GCshCmdtyCurrID(curr);
		setConversionFactor(cmdtyCurrID, factor);
	}
	
	// ----------------------------

	public void setConversionFactorRat(final String nameSpace, final String code,
			final BigFraction factor) {
		if ( nameSpace == null ) {
			throw new IllegalArgumentException("argument <nameSpace> is null");
		}

		if ( nameSpace.trim().equals("") ) {
			throw new IllegalArgumentException("argument <nameSpace> is empty");
		}

		if ( code == null ) {
			throw new IllegalArgumentException("argument <code> is null");
		}

		if ( code.trim().equals("") ) {
			throw new IllegalArgumentException("argument <code> is empty");
		}

		if ( factor == null ) {
		    throw new IllegalArgumentException("argument <factor> is null");
		}
		
		// ::TODO ::CHECK
		// In the sister project, we had to remove this check (cf. comment there).
		// What about GnuCash?
		if ( factor.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <factor> must be > 0");
		}

		SimplePriceTable table = getByNamespace(nameSpace);
		if ( table == null ) {
			addForNameSpace(nameSpace);
			table = getByNamespace(nameSpace);
		}

		table.setConversionFactor(code, FixedPointNumber.of(factor));
		table.setConversionFactorRat(code, factor);

		firePriceTableChanged(code, factor);
	}

	public void setConversionFactorRat(final GCshCmdtyCurrID cmdtyCurrID, final BigFraction factor) {
		if ( cmdtyCurrID == null ) {
		    throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}
	
		if ( ! cmdtyCurrID.isSet() ) {
		    throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
		}
	
		if ( factor == null ) {
		    throw new IllegalArgumentException("argument <factor> is null");
		}
	
		setConversionFactorRat(cmdtyCurrID.getNameSpace(), cmdtyCurrID.getCode(),
			            factor);
	}
	
	public void setConversionFactorRat(final Currency curr, final BigFraction factor) {
		if ( curr == null ) {
		    throw new IllegalArgumentException("argument <curr> is null");
		}
	
		if ( factor == null ) {
		    throw new IllegalArgumentException("argument <factor> is null");
		}
	
		GCshCmdtyCurrID cmdtyCurrID = new GCshCmdtyCurrID(curr);
		setConversionFactorRat(cmdtyCurrID, factor);
	}
	
	// ---------------------------------------------------------------

	/**
	 * @param pValue 
	 * @param cmdtyCurrID 
	 * @return return the price of the given commodity/currency in base currencies
	 * @see SimplePriceTable#convertFromBaseCurrency(FixedPointNumber,
	 *      java.lang.String)
	 */
	public FixedPointNumber convertFromBaseCurrency(final FixedPointNumber pValue,  final GCshCmdtyCurrID cmdtyCurrID) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null"); 
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set"); 
		}
		
		SimplePriceTable table = getByNamespace(cmdtyCurrID.getNameSpace());
		if ( table == null ) {
        	LOGGER.error("convertFromBaseCurrency: Cannot get simple conversion table for value = " + pValue + " and code = '" + cmdtyCurrID + "'");
			return null;
		}

		return table.convertFromBaseCurrency(pValue, cmdtyCurrID.getCode());
	}

	public FixedPointNumber convertFromBaseCurrency(final FixedPointNumber pValue,  final Currency curr) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null"); 
		}
		
		GCshCmdtyCurrID cmdtyCurrID = new GCshCmdtyCurrID(curr);
		return convertFromBaseCurrency(pValue, cmdtyCurrID);
	}

	// ----------------------------

	public BigFraction convertFromBaseCurrencyRat(final BigFraction pValue, final GCshCmdtyCurrID cmdtyCurrID) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null"); 
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set"); 
		}
		
		SimplePriceTable table = getByNamespace(cmdtyCurrID.getNameSpace());
		if ( table == null ) {
        	LOGGER.error("convertFromBaseCurrencyRat: Cannot get simple conversion table for value = " + pValue + " and code = '" + cmdtyCurrID + "'");
			return null;
		}

		return table.convertFromBaseCurrencyRat(pValue, cmdtyCurrID.getCode());
	}
	
	public BigFraction convertFromBaseCurrencyRat(final BigFraction pValue, final Currency curr) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null"); 
		}
		
		GCshCmdtyCurrID cmdtyCurrID = new GCshCmdtyCurrID(curr);
		return convertFromBaseCurrencyRat(pValue, cmdtyCurrID);
	}

	// ----------------------------

	public FixedPointNumber convertToBaseCurrency(final FixedPointNumber pValue, final GCshCmdtyCurrID cmdtyCurrID) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null"); 
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set"); 
		}
		
		SimplePriceTable table = getByNamespace(cmdtyCurrID.getNameSpace());
		if ( table == null ) {
        	LOGGER.error("convertToBaseCurrency: Cannot get simple conversion table for value = " + pValue + " and code = '" + cmdtyCurrID + "'");
			return null;
		}

		return table.convertToBaseCurrency(pValue, cmdtyCurrID.getCode());
	}

	public FixedPointNumber convertToBaseCurrency(final FixedPointNumber pValue, final Currency curr) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null"); 
		}
		
		GCshCmdtyCurrID cmdtyCurrID = new GCshCmdtyCurrID(curr);
		return convertToBaseCurrency(pValue, cmdtyCurrID);
	}

	// ----------------------------

	public BigFraction convertToBaseCurrencyRat(final BigFraction pValue, final GCshCmdtyCurrID cmdtyCurrID) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null"); 
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set"); 
		}
		
		SimplePriceTable table = getByNamespace(cmdtyCurrID.getNameSpace());
		if ( table == null ) {
        	LOGGER.error("convertToBaseCurrencyRat: Cannot get simple conversion table for value = " + pValue + " and code = '" + cmdtyCurrID + "'");
			return null;
		}

		return table.convertToBaseCurrencyRat(pValue, cmdtyCurrID.getCode());
	}
	
	public BigFraction convertToBaseCurrencyRat(final BigFraction pValue, final Currency curr) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null"); 
		}
		
		GCshCmdtyCurrID cmdtyCurrID = new GCshCmdtyCurrID(curr);
		return convertToBaseCurrencyRat(pValue, cmdtyCurrID);
	}

	// ---------------------------------------------------------------

	public List<String> getNameSpaces() {
		if ( nameSpace2PrcTab == null ) {
			throw new IllegalStateException("table is not set"); 
		}
		
		ArrayList<String> result = new ArrayList<String>(nameSpace2PrcTab.keySet());
		Collections.sort(result);
		return result;
	}

	/**
	 * @param nameSpace
	 * @return
	 */
	protected SimplePriceTable getByNamespace(final String nameSpace) {
		if ( nameSpace == null ) {
			throw new IllegalArgumentException("argument <nameSpace> is null"); 
		}
		
		if ( nameSpace.trim().equals("") ) {
			throw new IllegalArgumentException("argument <nameSpace> is empty"); 
		}
		
		if ( nameSpace2PrcTab == null ) {
			throw new IllegalStateException("table is not set"); 
		}
		
		return nameSpace2PrcTab.get(nameSpace);
	}

	/**
	 * @param nameSpace
	 * @return list of currencies in the given name space
	 */
	public List<String> getCodes(final String nameSpace) {
		if ( nameSpace == null ) {
			throw new IllegalArgumentException("argument <nameSpace> is null"); 
		}
		
		if ( nameSpace.trim().equals("") ) {
			throw new IllegalArgumentException("argument <nameSpace> is empty"); 
		}
		
		SimplePriceTable table = getByNamespace(nameSpace);
		if ( table == null ) {
			return new ArrayList<String>();
		}
		
		return table.getCodes();
	}

	// ---------------------------------------------------------------

	/**
	 * @see SimplePriceTable#clear()
	 */
	public void clear() {
		if ( nameSpace2PrcTab == null ) {
			throw new IllegalStateException("table is not set"); 
		}
		
		for ( String nameSpace : nameSpace2PrcTab.keySet() ) {
			nameSpace2PrcTab.get(nameSpace).clear();
		}

		nameSpace2PrcTab.clear();
	}

	// ---------------------------------------------------------------

	@Override
	public String toString() {
		String result = "ComplexPriceTable [\n";

		for ( String nameSpace : getNameSpaces() ) {
			result += "=======================================\n";
			result += "Name space: " + nameSpace + "\n";
			result += "=======================================\n";
			result += getByNamespace(nameSpace).toString() + "\n";
		}

		result += "]";

		return result;
	}

}
