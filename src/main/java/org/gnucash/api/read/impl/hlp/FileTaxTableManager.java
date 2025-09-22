package org.gnucash.api.read.impl.hlp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncGncTaxTable;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.aux.GCshTaxTableImpl;
import org.gnucash.base.basetypes.simple.aux.GCshTaxTabID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTaxTableManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileTaxTableManager.class);

	// ---------------------------------------------------------------

	protected GnuCashFileImpl gcshFile;

	protected Map<GCshTaxTabID, GCshTaxTable> taxTabMap;

	// ---------------------------------------------------------------

	public FileTaxTableManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		taxTabMap = new HashMap<GCshTaxTabID, GCshTaxTable>();

		List<Object> bookElements = pRootElement.getGncBook().getBookElements();
		for ( Object bookElement : bookElements ) {
			if ( !(bookElement instanceof GncGncTaxTable) ) {
				continue;
			}

			GncGncTaxTable jwsdpPeer = (GncGncTaxTable) bookElement;
			GCshTaxTableImpl taxTab = createTaxTable(jwsdpPeer);
			taxTabMap.put(taxTab.getID(), taxTab);
		}

		LOGGER.debug("init: No. of entries in tax table map: " + taxTabMap.size());
	}

	protected GCshTaxTableImpl createTaxTable(final GncGncTaxTable jwsdpTaxTab) {
		GCshTaxTableImpl taxTab = new GCshTaxTableImpl(jwsdpTaxTab, gcshFile);
		LOGGER.debug("Generated new tax table: " + taxTab.getID());
		return taxTab;
	}

	// ---------------------------------------------------------------

	public GCshTaxTable getTaxTableByID(final GCshTaxTabID taxTabID) {
		if ( taxTabID == null ) {
			throw new IllegalArgumentException("argument <taxTabID> is null");
		}
		
		if ( ! taxTabID.isSet() ) {
			throw new IllegalArgumentException("argument <taxTabID> is not set");
		}
		
		if ( taxTabMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GCshTaxTable retval = taxTabMap.get(taxTabID);
		if ( retval == null ) {
			LOGGER.error("getBillTermsByID: No tax table with ID '" + taxTabID + "'. " + "We know " + taxTabMap.size() + " tax tables.");
		}
		
		return retval;
	}

	public GCshTaxTable getTaxTableByName(final String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}
		
		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}
		
		if ( taxTabMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		for ( GCshTaxTable taxTab : taxTabMap.values() ) {
			if ( taxTab.getName().equals(name) ) {
				return taxTab;
			}
		}

		return null;
	}

	public Collection<GCshTaxTable> getTaxTables() {
		if ( taxTabMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(taxTabMap.values());
	}

	// ---------------------------------------------------------------

	public int getNofEntriesTaxTableMap() {
		return taxTabMap.size();
	}

}
