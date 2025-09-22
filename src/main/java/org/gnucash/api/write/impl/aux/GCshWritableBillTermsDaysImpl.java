package org.gnucash.api.write.impl.aux;

import org.gnucash.api.generated.GncGncBillTerm;
import org.gnucash.api.read.impl.aux.GCshBillTermsDaysImpl;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.aux.GCshWritableBillTermsDays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Extension of GCshBillTermsDaysImpl to allow read-write access instead of
 * read-only access.
 */
public class GCshWritableBillTermsDaysImpl extends GCshBillTermsDaysImpl 
                                           implements GCshWritableBillTermsDays 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritableBillTermsDaysImpl.class);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GCshWritableBillTermsDaysImpl(
	    final GncGncBillTerm.BilltermDays jwsdpPeer, 
	    final GnuCashWritableFile gcshFile) {
    	super(jwsdpPeer, gcshFile);
    }

    public GCshWritableBillTermsDaysImpl(final GCshBillTermsDaysImpl bllTrm) {
    	super(bllTrm.getJwsdpPeer(), bllTrm.getGnuCashFile());
    }

    // ---------------------------------------------------------------

    @Override
    public void setDueDays(final Integer dueDays) {
		if ( dueDays == null ) {
			throw new IllegalArgumentException("argument <dueDays> is null");
		}

		if ( dueDays <= 0 ) {
			throw new IllegalArgumentException("argument <dueDays> is <= 0");
		}

		jwsdpPeer.setBtDaysDueDays(dueDays);
    }

    @Override
    public void setDiscountDays(final Integer dscntDays) {
		if ( dscntDays == null ) {
			throw new IllegalArgumentException("argument <dscntDays> is null");
		}

		if ( dscntDays <= 0 ) {
			throw new IllegalArgumentException("argument <dscntDays> is <= 0");
		}

		jwsdpPeer.setBtDaysDiscDays(dscntDays);
	}

	@Override
	public void setDiscount(final FixedPointNumber dscnt) {
		if ( dscnt == null ) {
			throw new IllegalArgumentException("argument <dscnt> is null");
		}

		if ( dscnt.getBigDecimal().doubleValue() <= 0 ) {
			throw new IllegalArgumentException("argument <dscnt> is <= 0");
		}

		jwsdpPeer.setBtDaysDiscount(dscnt.toGnuCashString());
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GCshWritableBillTermsDaysImpl [");

		buffer.append(" due-days: ");
		buffer.append(getDueDays());

		buffer.append(" discount-days: ");
		buffer.append(getDiscountDays());

		buffer.append(" discount: ");
		buffer.append(getDiscount());

		buffer.append("]");

		return buffer.toString();
    }
    
}
