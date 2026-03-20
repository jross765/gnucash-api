package org.gnucash.api.write.hlp.fil;

import org.gnucash.api.write.spec.hlp.fil.GnuCashWritableFile_InvcEntr_Cust;
import org.gnucash.api.write.spec.hlp.fil.GnuCashWritableFile_InvcEntr_Empl;
import org.gnucash.api.write.spec.hlp.fil.GnuCashWritableFile_InvcEntr_Job;
import org.gnucash.api.write.spec.hlp.fil.GnuCashWritableFile_InvcEntr_Vend;

public interface GnuCashWritableFile_InvcEntr extends GnuCashWritableFile_InvcEntr_Gener,
													  GnuCashWritableFile_InvcEntr_Cust,
													  GnuCashWritableFile_InvcEntr_Vend,
													  GnuCashWritableFile_InvcEntr_Empl,
													  GnuCashWritableFile_InvcEntr_Job
{

    // ::EMPTY

}
