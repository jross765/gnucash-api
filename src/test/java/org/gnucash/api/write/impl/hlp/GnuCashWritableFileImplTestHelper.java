package org.gnucash.api.write.impl.hlp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.gnucash.api.write.impl.GnuCashWritableFileImpl;

public class GnuCashWritableFileImplTestHelper extends GnuCashWritableFileImpl
{
	// ---------------------------------------------------------------

	public GnuCashWritableFileImplTestHelper(final File pFile) throws IOException {
		super(pFile);
	}
	
	public GnuCashWritableFileImplTestHelper(final InputStream is) throws IOException {
		super(is);
	}

	// ---------------------------------------------------------------
	// For test purposes only

	public org.gnucash.api.write.impl.hlp.fil.FileCommodityManager getCommodityManager() {
		return (org.gnucash.api.write.impl.hlp.fil.FileCommodityManager) cmdtyMgr;
	}

	public org.gnucash.api.write.impl.hlp.fil.FilePriceManager getPriceManager() {
		return (org.gnucash.api.write.impl.hlp.fil.FilePriceManager) prcMgr;
	}

}
