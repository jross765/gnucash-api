package org.gnucash.api.read;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;

import org.gnucash.api.pricedb.ComplexPriceTable;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Acct;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Cmdty;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Cust;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Empl;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Invc;
import org.gnucash.api.read.hlp.fil.GnuCashFile_InvcEntr;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Job;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Prc;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Trx;
import org.gnucash.api.read.hlp.fil.GnuCashFile_TrxSplt;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Vend;
import org.gnucash.base.basetypes.simple.aux.GCshBllTrmID;
import org.gnucash.base.basetypes.simple.aux.GCshTaxTabID;

/**
 * Interface of a top-level class that gives access to a GnuCash file
 * with all its accounts, transactions, etc.
 */
public interface GnuCashFile extends GnuCashObject,
									 GnuCashFile_Acct,
									 GnuCashFile_Trx,
									 GnuCashFile_TrxSplt,
									 GnuCashFile_Invc,
									 GnuCashFile_InvcEntr,
									 GnuCashFile_Job,
									 GnuCashFile_Cust,
									 GnuCashFile_Vend,
									 GnuCashFile_Empl,
									 GnuCashFile_Cmdty,
									 GnuCashFile_Prc,
                                     HasUserDefinedAttributes 
{

    /**
     *
     * @return the file on disk we are managing
     */
    File getFile();

    // ---------------------------------------------------------------

    /**
     * The Currency-Table gets initialized with the latest prices found in the
     * GnuCash file.
     * 
     * @return Returns the currencyTable.
     */
    ComplexPriceTable getCurrencyTable();

    /**
     * Use a heuristic to determine the defaultcurrency-id. If we cannot find one,
     * we default to EUR.<br/>
     * Comodity-stace is fixed as "CURRENCY" .
     * 
     * @return the default-currencyID to use.
     */
    String getDefaultCurrencyID();

    // ---------------------------------------------------------------

    /**
     * @param taxTabID id of a tax table
     * @return the identified tax table or null
     */
    GCshTaxTable getTaxTableByID(GCshTaxTabID taxTabID);

    /**
     * @param name 
     * @param id name of a tax table
     * @return the identified tax table or null
     */
    GCshTaxTable getTaxTableByName(String name);

    /**
     * @return all tax tables defined in the book
     * @link GnuCashTaxTable
     */
    Collection<GCshTaxTable> getTaxTables();

    // ---------------------------------------------------------------

    /**
     * @param bllTrmID id of a tax table
     * @return the identified bill terms or null
     */
    GCshBillTerms getBillTermsByID(GCshBllTrmID bllTrmID);

    /**
     * @param name 
     * @param id name of a bill term
     * @return the identified bill term or null
     */
    GCshBillTerms getBillTermsByName(String name);

    /**
     * @return all bill terms defined in the book
     * @link GnuCashBillTerms
     */
    Collection<GCshBillTerms> getBillTerms();

    // ---------------------------------------------------------------
    
    void dump(PrintStream strm);

}
