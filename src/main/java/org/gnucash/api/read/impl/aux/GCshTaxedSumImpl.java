package org.gnucash.api.read.impl.aux;

import org.apache.commons.numbers.fraction.BigFraction;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * This Class represents a sum of the taxes of
 * multiple invoice-lines for one of the different
 * tax-percentages that occurred.<br/>
 * e.g. you may have 2 sales-tax-rates of 7% and 16%
 * and both occur, so you will get 2 instances
 * of this class. One sum of the 7%-items and one for
 * the 16%-items.
 */
public class GCshTaxedSumImpl {

    /**
     * How much tax it is. 16%=0.16
     */
    private BigFraction      myTaxpercent;

    /**
     * The sum of Paid taxes.
     */
    private BigFraction      taxSum;

    // -----------------------------------------------------------

    /**
     * @param pTaxpercent how much tax it is
     * @param pTaxsum     the sum of Paid taxes
     */
    public GCshTaxedSumImpl(final FixedPointNumber pTaxpercent, final FixedPointNumber pTaxsum) {
    	super();
    	myTaxpercent = pTaxpercent.toBigFraction();
    	taxSum = pTaxpercent.toBigFraction();
    }

    public GCshTaxedSumImpl(final BigFraction pTaxpercent, final BigFraction pTaxsum) {
    	super();
    	myTaxpercent = pTaxpercent;
    	taxSum = pTaxsum;
    }

    /**
     * @param taxpercent How much tax it is.
     */
    public GCshTaxedSumImpl(final FixedPointNumber taxpercent) {
    	super();
    	myTaxpercent = taxpercent.toBigFraction();
    }

    public GCshTaxedSumImpl(final BigFraction taxpercent) {
    	super();
    	myTaxpercent = taxpercent;
    }

    // -----------------------------------------------------------

    /**
     *
     * @return How much tax it is.
     */
    public FixedPointNumber getTaxpercent() {
    	return FixedPointNumber.of(myTaxpercent);
    }

    public BigFraction getTaxpercentRat() {
    	return myTaxpercent;
    }

    /**
     *
     * @param taxpercent How much tax it is.
     */
    public void setTaxpercent(final FixedPointNumber taxpercent) {
		if ( taxpercent.doubleValue() < 0.0 ) {
			throw new IllegalArgumentException("negative value '" + taxpercent + "' not allowed for field this.taxpercent");
		}

		myTaxpercent = taxpercent.toBigFraction();
    }

    public void setTaxpercent(final BigFraction taxpercent) {
		if ( taxpercent.doubleValue() < 0.0 ) {
			throw new IllegalArgumentException("negative value '" + taxpercent + "' not allowed for field this.taxpercent");
		}

		myTaxpercent = taxpercent;
    }

    /**
     *
     * @return The sum of Paid taxes.
     */
    public FixedPointNumber getTaxsum() {
    	return FixedPointNumber.of(taxSum);
    }

    public BigFraction getTaxsumRat() {
    	return taxSum;
    }

    /**
     *
     * @param pTaxsum The sum of Paid taxes.
     */
    public void setTaxsum(final FixedPointNumber pTaxsum) {
    	taxSum = pTaxsum.toBigFraction();
    }

    public void setTaxsum(final BigFraction pTaxsum) {
    	taxSum = pTaxsum;
    }
}
