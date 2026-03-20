package org.gnucash.api.write;

import java.io.File;
import java.io.IOException;

import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_Acct;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_BllTrm;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_Cmdty;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_Cust;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_Empl;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_Invc;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_InvcEntr;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_Job;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_Prc;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_TaxTab;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_Trx;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_TrxSplt;
import org.gnucash.api.write.hlp.fil.GnuCashWritableFile_Vend;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Extension of GnuCashFile that allows writing.
 * 
 * @see {@link GnuCashFile}
 */
public interface GnuCashWritableFile extends GnuCashFile, 
                                             GnuCashWritableObject,
                                             GnuCashWritableFile_Acct,
                                             GnuCashWritableFile_Trx,
                                             GnuCashWritableFile_TrxSplt,
                                             GnuCashWritableFile_Invc,
                                             GnuCashWritableFile_InvcEntr,
                                             GnuCashWritableFile_Cust,
                                             GnuCashWritableFile_Vend,
                                             GnuCashWritableFile_Empl,
                                             GnuCashWritableFile_Job,
                                             GnuCashWritableFile_Cmdty,
                                             GnuCashWritableFile_Prc,
                                             GnuCashWritableFile_BllTrm,
                                             GnuCashWritableFile_TaxTab,
                                             HasWritableUserDefinedAttributes
{
	public enum CompressMode {
		COMPRESS,
		DO_NOT_COMPRESS,
		GUESS_FROM_FILENAME
	}
	
	// ---------------------------------------------------------------

    /**
     * @return true if this file has been modified.
     */
    boolean isModified();

	/**
     * @param pB true if this file has been modified.
     * @see {@link #isModified()}
     */
    void setModified(boolean pB);

    /**
     * Write the data to the given file. That file becomes the new file returned by
     * {@link GnuCashFile#getGnuCashFile()}
     * 
     * @param file the file to write to
     * @throws IOException kn io-poblems
     */
    void writeFile(File file) throws IOException;

    void writeFile(File file, CompressMode compMode) throws IOException;

    /**
     * The value is guaranteed not to be bigger then the maximum of the current
     * system-time and the modification-time in the file at the time of the last
     * (full) read or successfull write.
	 * <br> 
	 * It is thus suitable to detect if the file has been modified outside of this library.
     * 
     * @return the time in ms (compatible with File.lastModified) of the last
     *         write-operation
     */
    long getLastWriteTime();

    // ---------------------------------------------------------------

    /**
     * @return the underlying JAXB-element
     */
    @SuppressWarnings("exports")
    GncV2 getRootElement();

    // ---------------------------------------------------------------

    /**
     * Add a new currency.<br/>
     * If the currency already exists, add a new price-quote for it.
     * 
     * @param pCmdtySpace        the name space (e.g. "GOODS" or "CURRENCY")
     * @param pCmdtyId           the currency-name
     * @param conversionFactor   the conversion-factor from the base-currency (EUR).
     * @param pCmdtyNameFraction number of decimal-places after the comma
     * @param pCmdtyName         common name of the new currency
     */
    void addCurrency(String pCmdtySpace, String pCmdtyId, FixedPointNumber conversionFactor,
	    int pCmdtyNameFraction, String pCmdtyName);

}
