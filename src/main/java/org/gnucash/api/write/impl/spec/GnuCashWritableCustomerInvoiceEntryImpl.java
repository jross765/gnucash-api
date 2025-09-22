package org.gnucash.api.write.impl.spec;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashCustomerInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerInvoiceEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceEntryImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoiceEntry;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.spec.GCshCustInvcID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Customer invoice entry that can be modified.
 * 
 * @see GnuCashCustomerInvoiceEntry
 * 
 * @see GnuCashWritableEmployeeVoucherEntryImpl
 * @see GnuCashWritableVendorBillEntryImpl
 * @see GnuCashWritableJobInvoiceEntryImpl
 */
public class GnuCashWritableCustomerInvoiceEntryImpl extends GnuCashWritableGenerInvoiceEntryImpl 
                                                     implements GnuCashWritableCustomerInvoiceEntry
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableCustomerInvoiceEntryImpl.class);
	
	// ---------------------------------------------------------------

	/**
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnuCashGenerInvoiceEntryImpl
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableCustomerInvoiceEntryImpl(final GncGncEntry jwsdpPeer, final GnuCashWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * Create a taxable invoiceEntry. (It has the tax table of the customer with a
	 * fallback to the first tax table found assigned)
	 *
	 * @param invc     the invoice to add this split to
	 * @param account  the income-account the money comes from
	 * @param quantity see ${@link GnuCashGenerInvoiceEntry#getQuantity()}
	 * @param price    see ${@link GnuCashGenerInvoiceEntry#getCustInvcPrice()}}
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableCustomerInvoiceEntryImpl(
			final GnuCashWritableCustomerInvoiceImpl invc,
			final GnuCashAccount account, 
			final FixedPointNumber quantity, 
			final FixedPointNumber price)
			throws TaxTableNotFoundException {
		super(invc, createCustInvoiceEntry_int(invc, account, quantity, price));

		// Caution: Call addInvcEntry one level above now
		// (GnuCashWritableCustomerInvoiceImpl.createCustInvcEntry)
		// invc.addInvcEntry(this);
		this.myInvoice = invc;
	}

	public GnuCashWritableCustomerInvoiceEntryImpl(final GnuCashGenerInvoiceEntry entry) {
		super(entry.getJwsdpPeer(), (GnuCashWritableFileImpl) entry.getGenerInvoice().getGnuCashFile());
	}

	public GnuCashWritableCustomerInvoiceEntryImpl(final GnuCashCustomerInvoiceEntry entry) {
		super(entry.getJwsdpPeer(), (GnuCashWritableFileImpl) entry.getGenerInvoice().getGnuCashFile());
	}

	// ---------------------------------------------------------------

	@Override
	public void setTaxable(boolean val)
			throws TaxTableNotFoundException {
		setCustInvcTaxable(val);
	}

	@Override
	public void setTaxTable(GCshTaxTable taxTab)
			throws TaxTableNotFoundException {
		setCustInvcTaxTable(taxTab);
	}

	// ----------------------------

	/**
	 * Do not use
	 */
	@Override
    public void setEmplVchTaxable(final boolean val) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
	}
	
	/**
	 * Do not use
	 */
	@Override
    public void setVendBllTaxable(final boolean val) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
	}
	
	/**
	 * Do not use
	 */
	@Override
    public void setJobInvcTaxable(final boolean val) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
	}
	
	// ----------------------------

	/**
	 * Do not use
	 */
	@Override
    public void setEmplVchTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
	@Override
    public void setVendBllTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
	@Override
    public void setJobInvcTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

    // 	---------------------------------------------------------------
	
	@Override
	public void setPrice(String price)
			throws TaxTableNotFoundException {
		setCustInvcPrice(price);
	}

	@Override
	public void setPrice(FixedPointNumber price)
			throws TaxTableNotFoundException {
		setCustInvcPrice(price);
	}

	/**
	 * Do not use
	 */
    @Override
    public void setEmplVchPrice(final String n)
	    throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
    @Override
    public void setVendBllPrice(final String n)
	    throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
    @Override
    public void setJobInvcPrice(final String n)
	    throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	// ---------------------------------------------------------------

	@Override
	public GCshCustInvcID getInvoiceID() {
		return new GCshCustInvcID( getGenerInvoiceID() );
	}

	@Override
	public GnuCashCustomerInvoice getInvoice() {
		if ( myInvoice == null ) {
			myInvoice = getGenerInvoice();
			if ( myInvoice.getType() != GCshOwner.Type.CUSTOMER )
				throw new WrongInvoiceTypeException();

			if ( myInvoice == null ) {
				throw new IllegalStateException("No customer invoice with id '" + getInvoiceID()
						+ "' for invoice entry with id '" + getID() + "'");
			}
		}

		return new GnuCashCustomerInvoiceImpl(myInvoice);
	}

	@Override
	public GCshAcctID getAccountID() throws AccountNotFoundException {
		return super.getCustInvcAccountID();
	}

	@Override
	public GnuCashAccount getAccount() throws AccountNotFoundException {
		return getGnuCashFile().getAccountByID(getAccountID());
	}

	@Override
	public FixedPointNumber getPrice() {
		return super.getCustInvcPrice();
	}

	@Override
	public String getPriceFormatted() {
		return super.getCustInvcPriceFormatted();
	}

	// ---------------------------------------------------------------
	
	@Override
	public boolean isTaxable() {
		return super.isCustInvcTaxable();
	}

	@Override
	public GCshTaxTable getTaxTable() throws TaxTableNotFoundException {
		return super.getCustInvcTaxTable();
	}

	@Override
	public FixedPointNumber getApplicableTaxPercent() {
		return super.getCustInvcApplicableTaxPercent();
	}

	@Override
	public String getApplicableTaxPercentFormatted() {
		return super.getCustInvcApplicableTaxPercentFormatted();
	}

	@Override
	public FixedPointNumber getSum() {
		return super.getCustInvcSum();
	}

	@Override
	public FixedPointNumber getSumInclTaxes() {
		return super.getCustInvcSumInclTaxes();
	}

	@Override
	public FixedPointNumber getSumExclTaxes() {
		return super.getCustInvcSumExclTaxes();
	}

	@Override
	public String getSumFormatted() {
		return super.getCustInvcSumExclTaxesFormatted();
	}

	@Override
	public String getSumInclTaxesFormatted() {
		return super.getCustInvcSumInclTaxesFormatted();
	}

	@Override
	public String getSumExclTaxesFormatted() {
		return super.getCustInvcSumExclTaxesFormatted();
	}

	// ---------------------------------------------------------------

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashWritableCustomerInvoiceEntryImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", invoice-id=");
		buffer.append(getInvoiceID());

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append(", date=");
		try {
			buffer.append(getDate().toLocalDate().format(DATE_FORMAT_PRINT));
		} catch (Exception e) {
			buffer.append(getDate().toLocalDate().toString());
		}

		buffer.append(", action='");
		try {
			buffer.append(getAction() + "'");
		} catch (Exception e) {
			buffer.append("ERROR" + "'");
		}

		buffer.append(", account-id=");
		try {
			buffer.append(getAccountID());
		} catch (Exception e) {
		    buffer.append("ERROR");
		}
		
		buffer.append(", price=");
		try {
			buffer.append(getPrice());
		} catch (WrongInvoiceTypeException e) {
			buffer.append("ERROR");
		}

		buffer.append(", quantity=");
		buffer.append(getQuantity());

		buffer.append("]");
		return buffer.toString();
	}

}
