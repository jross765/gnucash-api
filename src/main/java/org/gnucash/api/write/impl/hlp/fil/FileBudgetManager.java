package org.gnucash.api.write.impl.hlp.fil;

import org.gnucash.api.generated.GncBudget;
import org.gnucash.api.read.GnuCashBudget;
import org.gnucash.api.read.impl.GnuCashBudgetImpl;
import org.gnucash.api.write.impl.GnuCashWritableBudgetImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBudgetManager extends org.gnucash.api.read.impl.hlp.fil.FileBudgetManager 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileBudgetManager.class);
    
    // ---------------------------------------------------------------
    
    public FileBudgetManager(GnuCashWritableFileImpl gcshFile) {
    	super(gcshFile);
    }

    // ---------------------------------------------------------------
    
	/*
	 * Creates the writable version of the returned object.
	 */
    @Override
    protected GnuCashBudgetImpl createBudget(final GncBudget jwsdpBdgt) {
    	GnuCashWritableBudgetImpl bdgt = new GnuCashWritableBudgetImpl(jwsdpBdgt, (GnuCashWritableFileImpl) gcshFile);
    	LOGGER.debug("createBudget: Generated new writable budget: " + bdgt.getID());
    	return bdgt;
    }

	// ---------------------------------------------------------------

	public void addBudget(GnuCashBudget bdgt) {
		if ( bdgt == null ) {
			throw new IllegalArgumentException("argument <bdgt> is null");
		}
		
		bdgtMap.put(bdgt.getID(), bdgt);

		LOGGER.debug("Added budget to cache: " + bdgt.getID());
	}

	public void removeBudget(GnuCashBudget bdgt) {
		if ( bdgt == null ) {
			throw new IllegalArgumentException("argument <bdgt> is null");
		}
		
		bdgtMap.remove(bdgt.getID());
		
		LOGGER.debug("Removed budget from cache: " + bdgt.getID());
	}

}
