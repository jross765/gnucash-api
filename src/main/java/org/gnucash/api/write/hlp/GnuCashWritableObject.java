package org.gnucash.api.write.hlp;

import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.write.GnuCashWritableFile;

/**
 * Interface that all interfaces for writable GnuCash entities shall implement
 */
public interface GnuCashWritableObject extends GnuCashObject {

    /**
     * @return the file we belong to.
     */
    GnuCashWritableFile getWritableGnuCashFile();

}
