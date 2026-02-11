package org.gnucash.api.pricedb;

import java.util.List;

public interface SimplePriceTable {

    List<String> getCodes();

    void clear();

}
