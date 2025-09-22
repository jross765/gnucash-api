package org.gnucash.api.read.impl.aux;

import java.util.Objects;

import org.gnucash.api.generated.GncGncTaxTable;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GCshTaxTableEntryImpl implements GCshTaxTableEntry {

    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshTaxTableEntryImpl.class);

    // ---------------------------------------------------------------
    
    /**
     * the jwsdp-object we are wrapping.
     */
    protected GncGncTaxTable.TaxtableEntries.GncGncTaxTableEntry jwsdpPeer;

    /**
     * the file we belong to.
     */
    protected final GnuCashFile myFile;
    
    // ----------------------------
    
    protected GCshAcctID myAccountID;
    protected GnuCashAccount myAccount;

    // ---------------------------------------------------------------

    /**
     * @param element the jwsdp-object we are wrapping
     * @param gcshFile    the file we belong to
     */
    @SuppressWarnings("exports")
    public GCshTaxTableEntryImpl(
	    final GncGncTaxTable.TaxtableEntries.GncGncTaxTableEntry element,
	    final GnuCashFile gcshFile) {
		super();

		this.jwsdpPeer = element;
		this.myFile = gcshFile;
    }

    // ---------------------------------------------------------------

    /**
     * @return the jwsdp-object we are wrapping
     */
    @SuppressWarnings("exports")
    public GncGncTaxTable.TaxtableEntries.GncGncTaxTableEntry getJwsdpPeer() {
    	return jwsdpPeer;
    }

    public GnuCashFile getGnuCashFile() {
    	return myFile;
    }

    // ---------------------------------------------------------------

    /**
     * @see GCshTaxTableEntry#getType()
     */
    @Override
    public Type getType() {
    	return Type.valueOf( getJwsdpPeer().getTteType() );
    }

    /**
     * @return Returns the accountID.
     */
    @Override
    public GCshAcctID getAccountID() {
		if ( myAccountID == null ) {
			myAccountID = new GCshAcctID(getJwsdpPeer().getTteAcct().getValue());
		}

		return myAccountID;
    }

    /**
     * @return Returns the account.
     */
    @Override
    public GnuCashAccount getAccount() {
		if ( myAccount == null ) {
			myAccount = myFile.getAccountByID(getAccountID());
		}

		return myAccount;
    }

    /**
     * @return the amount the tax is
     */
    @Override
    public FixedPointNumber getAmount() {
    	return new FixedPointNumber(getJwsdpPeer().getTteAmount());
    }

    // ---------------------------------------------------------------
    
    @Override
    public int hashCode() {
    	return Objects.hash(jwsdpPeer, myFile);
    }

    @Override
    public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( !(obj instanceof GCshTaxTableEntryImpl) ) {
			return false;
		}
		GCshTaxTableEntryImpl other = (GCshTaxTableEntryImpl) obj;
		return Objects.equals(jwsdpPeer, other.jwsdpPeer) && Objects.equals(myFile, other.myFile);
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
		String result = "GCshTaxTableEntryImpl [";

		result += "type=" + getType();
		result += ", account-id=" + getAccountID();
		result += ", amount=" + getAmount();

		result += "]";

		return result;
    }

}
