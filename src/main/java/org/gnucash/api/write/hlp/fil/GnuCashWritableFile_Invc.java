package org.gnucash.api.write.hlp.fil;

import org.gnucash.api.write.spec.hlp.fil.GnuCashWritableFile_Invc_Cust;
import org.gnucash.api.write.spec.hlp.fil.GnuCashWritableFile_Invc_Empl;
import org.gnucash.api.write.spec.hlp.fil.GnuCashWritableFile_Invc_Job;
import org.gnucash.api.write.spec.hlp.fil.GnuCashWritableFile_Invc_Vend;

public interface GnuCashWritableFile_Invc extends  GnuCashWritableFile_Invc_Gener,
												   GnuCashWritableFile_Invc_Cust,
												   GnuCashWritableFile_Invc_Vend,
												   GnuCashWritableFile_Invc_Empl,
												   GnuCashWritableFile_Invc_Job
{

	// ::EMPTY

}
