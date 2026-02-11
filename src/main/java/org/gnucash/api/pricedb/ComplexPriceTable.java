package org.gnucash.api.pricedb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class ComplexPriceTable implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ComplexPriceTable.class);

	private static final long serialVersionUID = -3303232787168479120L;

	// ---------------------------------------------------------------

	public interface ComplexPriceTableChangeListener {
		void conversionFactorChanged(final String cmdtyIDStr, final FixedPointNumber factor);
		void conversionFactorChanged(final String cmdtyIDStr, final BigFraction factor);
	}

	// -----------------------------------------------------------

	private transient volatile List<ComplexPriceTableChangeListener> listeners = null;

	// -----------------------------------------------------------

	private Map<GCshCmdtyID.Type, SimplePriceTable> cmdtyCurrType2PrcTab = null;

	// -----------------------------------------------------------

	public ComplexPriceTable() {
		cmdtyCurrType2PrcTab = new HashMap<GCshCmdtyID.Type, SimplePriceTable>();

		addTabForType(GCshCmdtyID.Type.CURRENCY);
		addTabForType(GCshCmdtyID.Type.SECURITY);
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
			listeners = new ArrayList<ComplexPriceTableChangeListener>();
		}
		
		listeners.remove(listener);
	}

	protected void firePriceTableChanged(final String cmdtyIDStr, final FixedPointNumber factor) {
		if ( cmdtyIDStr == null ) {
			throw new IllegalArgumentException("argument <cmdtyIDStr> is null");
		}

		if ( cmdtyIDStr.trim().equals("") ) {
			throw new IllegalArgumentException("argument <cmdtyIDStr> is empty");
		}
		
		if ( factor == null ) {
			throw new IllegalArgumentException("argument <factor> is null");
		}
		
		// ::TODO ::CHECK
		// In the sister project, we had to remove this check (cf. comment there).
		// What about GnuCash?
		if ( factor.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <factor> is <= 0");
		}
		
		if ( listeners != null ) {
			for ( ComplexPriceTableChangeListener listener : listeners ) {
				listener.conversionFactorChanged(cmdtyIDStr, factor);
			}
		}
	}

	protected void firePriceTableChanged(final String cmdtyIDStr, final BigFraction factor) {
		if ( cmdtyIDStr == null ) {
			throw new IllegalArgumentException("argument <cmdtyIDStr> is null");
		}

		if ( cmdtyIDStr.trim().equals("") ) {
			throw new IllegalArgumentException("argument <cmdtyIDStr> is empty");
		}
		
		if ( factor == null ) {
			throw new IllegalArgumentException("argument <factor> is null");
		}
		
		// ::TODO ::CHECK
		// In the sister project, we had to remove this check (cf. comment there).
		// What about GnuCash?
		if ( factor.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <factor> is <= 0");
		}
		
		if ( listeners != null ) {
			for ( ComplexPriceTableChangeListener listener : listeners ) {
				listener.conversionFactorChanged(cmdtyIDStr, factor);
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

	private void addTabForType(final GCshCmdtyID.Type type) {
		if ( type == GCshCmdtyID.Type.CURRENCY ) {
			SimpleCurrencyExchRateTable table = new SimpleCurrencyExchRateTable();
			addTabForType(type, table, false);
		} else {
			SimpleSecurityQuoteTable table = new SimpleSecurityQuoteTable();
			addTabForType(type, table, false);
		}
	}

	private void addTabForType(final GCshCmdtyID.Type type, final SimplePriceTable table, boolean clear) {
		if ( table == null ) {
			throw new IllegalArgumentException("argument <table> is null");
		}

		if ( cmdtyCurrType2PrcTab == null ) {
			throw new IllegalStateException("Meta table is not set"); 
		}
		
		if ( cmdtyCurrType2PrcTab.keySet().contains(type) ) {
			throw new IllegalStateException("Simple table already exists for type " + type);
		}

		if ( clear ) {
			cmdtyCurrType2PrcTab.clear();
			LOGGER.debug("addTabForType: Cleared table for type " + type);
		}
		
		cmdtyCurrType2PrcTab.put(type, table);
		LOGGER.debug("addTabForType: Added new table for type " + type);
	}

	public List<GCshCmdtyID.Type> getTabTypes() {
		if ( cmdtyCurrType2PrcTab == null ) {
			throw new IllegalStateException("Meta table is not set"); 
		}
		
		ArrayList<GCshCmdtyID.Type> result = new ArrayList<GCshCmdtyID.Type>(cmdtyCurrType2PrcTab.keySet());
		Collections.sort(result);
		return result;
	}

	protected SimplePriceTable getTabByType(final GCshCmdtyID.Type type) {
		if ( cmdtyCurrType2PrcTab == null ) {
			throw new IllegalStateException("Meta table is not set"); 
		}
		
		return cmdtyCurrType2PrcTab.get(type);
	}

	// ---------------------------------------------------------------

	/**
	 * @param nameSpace 
	 * @param code 
	 * @return the factor to convert the price specified by the name-space-code-pair
	 * @see SimplePriceTable#setConversionFactor(java.lang.String, FixedPointNumber)
	 */
	@Deprecated
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

		if ( nameSpace.equals( GCshCmdtyNameSpace.CURRENCY ) ) {
			GCshCurrID currID = new GCshCurrID(code);
			return getConversionFactor(currID);
		} else {
			GCshSecID cmdtyID = new GCshSecID(nameSpace, code);
			return getConversionFactor(cmdtyID);
		}
	}

	public FixedPointNumber getConversionFactor(final GCshCmdtyID cmdtyID) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
		}

		SimplePriceTable table = getTabByType(cmdtyID.getType());
		if ( table == null ) {
        	LOGGER.error("getConversionFactor: Cannot get simple conversion table for security/currency ID " + cmdtyID);
			return null;
		}

		if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY ) {
			GCshCurrID currID = new GCshCurrID(cmdtyID.getCode());
			return ((SimpleCurrencyExchRateTable) table).getConversionFactor(currID);
		} else if ( cmdtyID.getType() == GCshCmdtyID.Type.SECURITY ) {
			GCshSecID secID = new GCshSecID(cmdtyID.getNameSpace(), cmdtyID.getCode());
			return ((SimpleSecurityQuoteTable) table).getConversionFactor(secID);
		}
		
		return null; // Compiler happy
	}

	public FixedPointNumber getConversionFactor(final Currency curr) {
		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null");
		}

		GCshCurrID currID = new GCshCurrID(curr);
		return getConversionFactor(currID);
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
		
		if ( nameSpace.equals( GCshCmdtyNameSpace.CURRENCY ) ) {
			GCshCurrID currID = new GCshCurrID(code);
			return getConversionFactorRat(currID);
		} else {
			GCshSecID cmdtyID = new GCshSecID(nameSpace, code);
			return getConversionFactorRat(cmdtyID);
		}
	}

	public BigFraction getConversionFactorRat(final GCshCmdtyID cmdtyID) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}

		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}

		SimplePriceTable table = getTabByType(cmdtyID.getType());
		if ( table == null ) {
        	LOGGER.error("getConversionFactorRat: Cannot get simple conversion table for security/currency ID " + cmdtyID);
			return null;
		}

		if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY ) {
			GCshCurrID currID = new GCshCurrID(cmdtyID.getCode());
			return ((SimpleCurrencyExchRateTable) table).getConversionFactorRat(currID);
		} else if ( cmdtyID.getType() == GCshCmdtyID.Type.SECURITY ) {
			GCshSecID secID = new GCshSecID(cmdtyID.getNameSpace(), cmdtyID.getCode());
			return ((SimpleSecurityQuoteTable) table).getConversionFactorRat(secID);
		}
		
		return null; // Compiler happy
	}

	public BigFraction getConversionFactorRat(final Currency curr) {
		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null");
		}

		GCshCurrID currID = new GCshCurrID(curr);
		return getConversionFactorRat(currID);
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
	@Deprecated
	public void setConversionFactor(final String nameSpace, final String code, final FixedPointNumber factor) {
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
		
		GCshCmdtyID cmdtyID = new GCshCmdtyID(nameSpace, code);
		setConversionFactor(cmdtyID, factor);
	}
	
	@Deprecated
	public void setConversionFactor(final String cmdtyCurrIDStr, final FixedPointNumber factor) {
		if ( cmdtyCurrIDStr == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrIDStr> is null");
		}

		if ( cmdtyCurrIDStr.trim().equals("") ) {
			throw new IllegalArgumentException("argument <cmdtyCurrIDStr> is empty");
		}

		GCshCmdtyID cmdtyID = GCshCmdtyID.parse(cmdtyCurrIDStr);
		setConversionFactor(cmdtyID, factor);
	}

	public void setConversionFactor(final GCshCmdtyID cmdtyID, final FixedPointNumber factor) {
		if ( cmdtyID == null ) {
		    throw new IllegalArgumentException("argument <cmdtyID> is null");
		}
	
		if ( ! cmdtyID.isSet() ) {
		    throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}
	
		if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY ) {
			GCshCurrID currID = new GCshCurrID(cmdtyID.getCode());
			setConversionFactor(currID, factor);
		} else if ( cmdtyID.getType() == GCshCmdtyID.Type.SECURITY ) {
			GCshSecID secID = new GCshSecID(cmdtyID.getNameSpace(), cmdtyID.getCode());
			setConversionFactor(secID, factor);
		}

		firePriceTableChanged(cmdtyID.toString(), factor);
	}
	
	public void setConversionFactor(final GCshSecID secID, final FixedPointNumber factor) {
		if ( secID == null ) {
		    throw new IllegalArgumentException("argument <secID> is null");
		}
	
		if ( ! secID.isSet() ) {
		    throw new IllegalArgumentException("argument <secID> is not set");
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

		SimplePriceTable table = getTabByType(GCshCmdtyID.Type.SECURITY);
		if ( table == null ) {
        	LOGGER.error("setConversionFactor: Cannot get simple conversion table for security/currency ID " + secID);
			return;
		}

		((SimpleSecurityQuoteTable) table).setConversionFactor(secID, factor);
		((SimpleSecurityQuoteTable) table).setConversionFactorRat(secID, factor.toBigFraction());

		firePriceTableChanged(secID.toString(), factor);
	}
	
	public void setConversionFactor(final GCshCurrID currID, final FixedPointNumber factor) {
		if ( currID == null ) {
		    throw new IllegalArgumentException("argument <currID> is null");
		}
	
		if ( ! currID.isSet() ) {
		    throw new IllegalArgumentException("argument <currID> is not set");
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

		SimplePriceTable table = getTabByType(GCshCmdtyID.Type.CURRENCY);
		if ( table == null ) {
        	LOGGER.error("setConversionFactor: Cannot get simple conversion table for currency ID " + currID);
			return;
		}

		((SimpleCurrencyExchRateTable) table).setConversionFactor(currID, factor);
		((SimpleCurrencyExchRateTable) table).setConversionFactorRat(currID, factor.toBigFraction());

		firePriceTableChanged(currID.toString(), factor);
	}
	
	public void setConversionFactor(final Currency curr, final FixedPointNumber factor) {
		if ( curr == null ) {
		    throw new IllegalArgumentException("argument <curr> is null");
		}
	
		if ( factor == null ) {
		    throw new IllegalArgumentException("argument <factor> is null");
		}
	
		GCshCurrID currID = new GCshCurrID(curr);
		setConversionFactor(currID, factor);
	}
	
	// ----------------------------

	@Deprecated
	public void setConversionFactorRat( final String nameSpace, final String code, final BigFraction factor) {
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
		
		GCshCmdtyID cmdtyCurrID = new GCshCmdtyID(nameSpace, code);
		setConversionFactorRat(cmdtyCurrID, factor);
	}

	@Deprecated
	public void setConversionFactorRat( final String cmdtyIDStr, final BigFraction factor) {
		if ( cmdtyIDStr == null ) {
			throw new IllegalArgumentException("argument <cmdtyIDStr> is null");
		}

		if ( cmdtyIDStr.trim().equals("") ) {
			throw new IllegalArgumentException("argument <cmdtyIDStr> is empty");
		}
		
		GCshCmdtyID cmdtyID = GCshCmdtyID.parse(cmdtyIDStr);
		setConversionFactorRat(cmdtyID, factor);
	}

	public void setConversionFactorRat(final GCshCmdtyID cmdtyID, final BigFraction factor) {
		if ( cmdtyID == null ) {
		    throw new IllegalArgumentException("argument <cmdtyID> is null");
		}
	
		if ( ! cmdtyID.isSet() ) {
		    throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}
	
		if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY ) {
			GCshCurrID currID = new GCshCurrID(cmdtyID.getCode());
			setConversionFactorRat(currID, factor);
		} else if ( cmdtyID.getType() == GCshCmdtyID.Type.SECURITY ) {
			GCshSecID secID = new GCshSecID(cmdtyID.getNameSpace(), cmdtyID.getCode());
			setConversionFactorRat(secID, factor);
		}

		firePriceTableChanged(cmdtyID.toString(), factor);
	}
	
	public void setConversionFactorRat(final GCshSecID secID, final BigFraction factor) {
		if ( secID == null ) {
		    throw new IllegalArgumentException("argument <secID> is null");
		}
	
		if ( ! secID.isSet() ) {
		    throw new IllegalArgumentException("argument <v> is not set");
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

		SimplePriceTable table = getTabByType(GCshCmdtyID.Type.SECURITY);
		if ( table == null ) {
        	LOGGER.error("setConversionFactorRat: Cannot get simple conversion table for commodity ID " + secID);
			return;
		}

		((SimpleSecurityQuoteTable) table).setConversionFactor(secID, FixedPointNumber.of(factor));
		((SimpleSecurityQuoteTable) table).setConversionFactorRat(secID, factor);

		firePriceTableChanged(secID.toString(), factor);
	}

	public void setConversionFactorRat(final GCshCurrID currID, final BigFraction factor) {
		if ( currID == null ) {
		    throw new IllegalArgumentException("argument <currID> is null");
		}
	
		if ( ! currID.isSet() ) {
		    throw new IllegalArgumentException("argument <currID> is not set");
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

		SimplePriceTable table = getTabByType(GCshCmdtyID.Type.CURRENCY);
		if ( table == null ) {
        	LOGGER.error("setConversionFactorRat: Cannot get simple conversion table for currency ID " + currID);
			return;
		}

		((SimpleCurrencyExchRateTable) table).setConversionFactor(currID, FixedPointNumber.of(factor));
		((SimpleCurrencyExchRateTable) table).setConversionFactorRat(currID, factor);

		firePriceTableChanged(currID.toString(), factor);
	}
	
	public void setConversionFactorRat(final Currency curr, final BigFraction factor) {
		if ( curr == null ) {
		    throw new IllegalArgumentException("argument <curr> is null");
		}
	
		if ( factor == null ) {
		    throw new IllegalArgumentException("argument <factor> is null");
		}
	
		GCshCurrID cmdtyCurrID = new GCshCurrID(curr);
		setConversionFactorRat(cmdtyCurrID, factor);
	}
	
	// ---------------------------------------------------------------

	/**
	 * @param pValue 
	 * @param cmdtyID 
	 * @return the price of the given security/currency in base currencies
	 * @see SimplePriceTable#convertFromBaseCurrency(FixedPointNumber,
	 *      java.lang.String)
	 */
	public FixedPointNumber convertFromBaseCurrency(final FixedPointNumber pValue,  final GCshCmdtyID cmdtyID) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null"); 
		}
		
		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set"); 
		}
		
		SimplePriceTable table = getTabByType(cmdtyID.getType());
		if ( table == null ) {
        	LOGGER.error("convertFromBaseCurrency: Cannot get simple conversion table for value = " + pValue + " and code = '" + cmdtyID + "'");
			return null;
		}

		if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY ) {
			GCshCurrID currID = new GCshCurrID(cmdtyID.getCode());
			return ((SimpleCurrencyExchRateTable) table).convertFromBaseCurrency(pValue, currID);
		} else if ( cmdtyID.getType() == GCshCmdtyID.Type.SECURITY ) {
			GCshSecID secID = new GCshSecID(cmdtyID.getNameSpace(), cmdtyID.getCode());
			return ((SimpleSecurityQuoteTable) table).convertFromBaseCurrency(pValue, secID);
		}
		
		return null; // Compiler happy
	}

	public FixedPointNumber convertFromBaseCurrency(final FixedPointNumber pValue,  final Currency curr) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null"); 
		}
		
		GCshCurrID cmdtyCurrID = new GCshCurrID(curr);
		return convertFromBaseCurrency(pValue, cmdtyCurrID);
	}

	// ----------------------------

	public BigFraction convertFromBaseCurrencyRat(final BigFraction pValue, final GCshCmdtyID cmdtyID) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null"); 
		}
		
		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set"); 
		}
		
		SimplePriceTable table = getTabByType(cmdtyID.getType());
		if ( table == null ) {
        	LOGGER.error("convertFromBaseCurrencyRat: Cannot get simple conversion table for value = " + pValue + " and code = '" + cmdtyID + "'");
			return null;
		}

		if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY ) {
			GCshCurrID currID = new GCshCurrID(cmdtyID.getCode());
			return ((SimpleCurrencyExchRateTable) table).convertFromBaseCurrencyRat(pValue, currID);
		} else if ( cmdtyID.getType() == GCshCmdtyID.Type.SECURITY ) {
			GCshSecID secID = new GCshSecID(cmdtyID.getNameSpace(), cmdtyID.getCode());
			return ((SimpleSecurityQuoteTable) table).convertFromBaseCurrencyRat(pValue, secID);
		}
		
		return null; // Compiler happy
	}
	
	public BigFraction convertFromBaseCurrencyRat(final BigFraction pValue, final Currency curr) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null"); 
		}
		
		GCshCurrID cmdtyCurrID = new GCshCurrID(curr);
		return convertFromBaseCurrencyRat(pValue, cmdtyCurrID);
	}

	// ----------------------------

	public FixedPointNumber convertToBaseCurrency(final FixedPointNumber pValue, final GCshCmdtyID cmdtyID) {
		if ( pValue == null ) {
			throw new IllegalArgumentException("argument <pValue> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null"); 
		}
		
		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set"); 
		}
		
		SimplePriceTable table = getTabByType(cmdtyID.getType());
		if ( table == null ) {
        	LOGGER.error("convertToBaseCurrency: Cannot get simple conversion table for value = " + pValue + " and code = '" + cmdtyID + "'");
			return null;
		}

		if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY ) {
			GCshCurrID currID = new GCshCurrID(cmdtyID.getCode());
			return ((SimpleCurrencyExchRateTable) table).convertToBaseCurrency(pValue, currID);
		} else if ( cmdtyID.getType() == GCshCmdtyID.Type.SECURITY ) {
			GCshSecID secID = new GCshSecID(cmdtyID.getNameSpace(), cmdtyID.getCode());
			return ((SimpleSecurityQuoteTable) table).convertToBaseCurrency(pValue, secID);
		}
		
		return null; // Compiler happy
	}

	public FixedPointNumber convertToBaseCurrency(final FixedPointNumber pValue, final Currency curr) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null"); 
		}
		
		GCshCurrID currID = new GCshCurrID(curr);
		return convertToBaseCurrency(pValue, currID);
	}

	// ----------------------------

	public BigFraction convertToBaseCurrencyRat(final BigFraction pValue, final GCshCmdtyID cmdtyID) {
		if ( pValue == null ) {
			throw new IllegalArgumentException("argument <pValue> is null");
		}

		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null"); 
		}
		
		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set"); 
		}
		
		SimplePriceTable table = getTabByType(cmdtyID.getType());
		if ( table == null ) {
        	LOGGER.error("convertToBaseCurrencyRat: Cannot get simple conversion table for value = " + pValue + " and code = '" + cmdtyID + "'");
			return null;
		}

		if ( cmdtyID.getType() == GCshCmdtyID.Type.CURRENCY ) {
			GCshCurrID currID = new GCshCurrID(cmdtyID.getCode());
			return ((SimpleCurrencyExchRateTable) table).convertToBaseCurrencyRat(pValue, currID);
		} else if ( cmdtyID.getType() == GCshCmdtyID.Type.SECURITY ) {
			GCshSecID secID = new GCshSecID(cmdtyID.getNameSpace(), cmdtyID.getCode());
			return ((SimpleSecurityQuoteTable) table).convertToBaseCurrencyRat(pValue, secID);
		}
		
		return null; // Compiler happy
	}
	
	public BigFraction convertToBaseCurrencyRat(final BigFraction pValue, final Currency curr) {
		if ( pValue == null )
			throw new IllegalArgumentException("argument <pValue> is null");

		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null"); 
		}
		
		GCshCurrID currID = new GCshCurrID(curr);
		return convertToBaseCurrencyRat(pValue, currID);
	}

	// ---------------------------------------------------------------

	/**
	 * @param type
	 * @return list of currencies in the given name space
	 */
	public List<String> getCodes(final GCshCmdtyID.Type type) {
		SimplePriceTable table = getTabByType(type);
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
		if ( cmdtyCurrType2PrcTab == null ) {
			throw new IllegalStateException("Meta table is not set"); 
		}
		
		for ( GCshCmdtyID.Type type : cmdtyCurrType2PrcTab.keySet() ) {
			cmdtyCurrType2PrcTab.get(type).clear();
		}

		cmdtyCurrType2PrcTab.clear();
	}

	// ---------------------------------------------------------------

	@Override
	public String toString() {
		String result = "ComplexPriceTable [\n";

		for ( GCshCmdtyID.Type type : getTabTypes() ) {
			if ( type != GCshCmdtyID.Type.UNSET ) {
				result += "=======================================\n";
				result += "Type: " + type + "\n";
				result += "=======================================\n";
				result += getTabByType(type).toString() + "\n";
			}
		}

		result += "]";

		return result;
	}

}
