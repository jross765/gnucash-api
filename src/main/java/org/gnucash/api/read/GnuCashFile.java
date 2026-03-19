package org.gnucash.api.read;

import java.io.File;
import java.io.PrintStream;

import org.gnucash.api.pricedb.ComplexPriceTable;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Acct;
import org.gnucash.api.read.hlp.fil.GnuCashFile_BllTrm;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Cmdty;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Cust;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Empl;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Invc;
import org.gnucash.api.read.hlp.fil.GnuCashFile_InvcEntr;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Job;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Prc;
import org.gnucash.api.read.hlp.fil.GnuCashFile_TaxTab;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Trx;
import org.gnucash.api.read.hlp.fil.GnuCashFile_TrxSplt;
import org.gnucash.api.read.hlp.fil.GnuCashFile_Vend;

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
									 GnuCashFile_BllTrm,
									 GnuCashFile_TaxTab,
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
    
    void dump(PrintStream strm);

}
