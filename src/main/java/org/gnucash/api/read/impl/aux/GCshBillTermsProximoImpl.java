package org.gnucash.api.read.impl.aux;

import org.gnucash.api.generated.GncGncBillTerm;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshBillTermsProximo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GCshBillTermsProximoImpl implements GCshBillTermsProximo {

    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshBillTermsProximoImpl.class);

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncGncBillTerm.BilltermProximo jwsdpPeer;

    /**
     * the file we belong to.
     */
    protected final GnuCashFile myFile;
    
    // ---------------------------------------------------------------

    /**
     * @param peer the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     */
    @SuppressWarnings("exports")
    public GCshBillTermsProximoImpl(
	    final GncGncBillTerm.BilltermProximo peer, 
	    final GnuCashFile gcshFile) {
		super();

		this.jwsdpPeer = peer;
		this.myFile = gcshFile;
    }

    // ---------------------------------------------------------------

    /**
     *
     * @return The JWSDP-Object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncGncBillTerm.BilltermProximo getJwsdpPeer() {
    	return jwsdpPeer;
    }

    public GnuCashFile getGnuCashFile() {
    	return myFile;
    }

    // ---------------------------------------------------------------

    @Override
    public Integer getDueDay() {
    	return jwsdpPeer.getBtProxDueDay();
    }

    @Override
    public Integer getDiscountDay() {
    	return jwsdpPeer.getBtProxDiscDay();
    }

    @Override
    public FixedPointNumber getDiscount() {
		if ( jwsdpPeer.getBtProxDiscount() == null ) {
			return null;
		}

		return new FixedPointNumber(jwsdpPeer.getBtProxDiscount());
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GCshBillTermsProximoImpl [");

		buffer.append("due-day=");
		buffer.append(getDueDay());

		buffer.append(", discount-day=");
		buffer.append(getDiscountDay());

		buffer.append(", discount=");
		buffer.append(getDiscount());

		buffer.append("]");

		return buffer.toString();
    }
    
}
