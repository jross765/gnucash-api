package org.gnucash.api.read.hlp.fil;

import org.gnucash.api.read.spec.hlp.fil.GnuCashFile_Invc_Cust;
import org.gnucash.api.read.spec.hlp.fil.GnuCashFile_Invc_Empl;
import org.gnucash.api.read.spec.hlp.fil.GnuCashFile_Invc_Job;
import org.gnucash.api.read.spec.hlp.fil.GnuCashFile_Invc_Vend;

public interface GnuCashFile_Invc extends GnuCashFile_Invc_Gener,
										  GnuCashFile_Invc_Cust,
										  GnuCashFile_Invc_Vend,
										  GnuCashFile_Invc_Empl,
										  GnuCashFile_Invc_Job
{

	// ::EMPTY

}
