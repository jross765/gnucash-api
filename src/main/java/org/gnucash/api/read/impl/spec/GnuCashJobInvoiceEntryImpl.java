package org.gnucash.api.read.impl.spec;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashJobInvoiceEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.spec.GCshJobInvcID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * 
 * @see GnuCashCustomerInvoiceEntryImpl
 * @see GnuCashEmployeeVoucherEntryImpl
 * @see GnuCashVendorBillEntryImpl
 * @see GnuCashGenerInvoiceEntryImpl
 */
public class GnuCashJobInvoiceEntryImpl extends GnuCashGenerInvoiceEntryImpl
                                        implements GnuCashJobInvoiceEntry 
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashJobInvoiceEntryImpl.class);

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public GnuCashJobInvoiceEntryImpl(final GnuCashJobInvoice invoice, final GncGncEntry peer) {
		super(invoice, peer, true);
	}

	@SuppressWarnings("exports")
	public GnuCashJobInvoiceEntryImpl(final GnuCashGenerInvoice invoice, final GncGncEntry peer) {
		super(invoice, peer, true);

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invoice.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();
	}

	@SuppressWarnings("exports")
	public GnuCashJobInvoiceEntryImpl(final GncGncEntry peer, final GnuCashFileImpl gcshFile) {
		super(peer, gcshFile, true);
	}

	public GnuCashJobInvoiceEntryImpl(final GnuCashGenerInvoiceEntry entry) {
		super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( entry.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();
	}

	public GnuCashJobInvoiceEntryImpl(final GnuCashJobInvoiceEntry entry) {
		super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);
	}

	// ---------------------------------------------------------------

	public GCshJobInvcID getInvoiceID() {
		return new GCshJobInvcID( getGenerInvoiceID() );
	}

	@Override
	public GnuCashJobInvoice getInvoice() {
		if ( myInvoice == null ) {
			myInvoice = getGenerInvoice();
			if ( myInvoice.getType() != GCshOwner.Type.JOB )
				throw new WrongInvoiceTypeException();

			if ( myInvoice == null ) {
				throw new IllegalStateException(
						"No job invoice with id '" + getInvoiceID() + "' for invoice entry with id '" + getID() + "'");
			}
		}

		return new GnuCashJobInvoiceImpl(myInvoice);
	}

	// ---------------------------------------------------------------

	@Override
	public GCshAcctID getAccountID() throws AccountNotFoundException {
		return getJobInvcAccountID();
	}

	@Override
	public GnuCashAccount getAccount() throws AccountNotFoundException {
		return getGnuCashFile().getAccountByID(getAccountID());
	}

	// ---------------------------------------------------------------

	@Override
	public FixedPointNumber getPrice() {
		return getJobInvcPrice();
	}

	@Override
	public String getPriceFormatted() {
		return getJobInvcPriceFormatted();
	}

	// ---------------------------------------------------------------

	@Override
	public FixedPointNumber getCustInvcPrice() {
		throw new WrongInvoiceTypeException();
	}

	@Override
	public String getCustInvcPriceFormatted() {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllPrice() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllPriceFormatted() {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getEmplVchPrice() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getEmplVchPriceFormatted() {
		throw new WrongInvoiceTypeException();
	}

	// ---------------------------------------------------------------
	
	@Override
	public boolean isTaxable() {
		return isJobInvcTaxable();
	}

	@Override
	public GCshTaxTable getTaxTable() throws TaxTableNotFoundException {
		return getJobInvcTaxTable();
	}

	// ----------------------------
	
	@Override
	public boolean isCustInvcTaxable() {
		throw new WrongInvoiceTypeException();
	}

	@Override
	public GCshTaxTable getCustInvcTaxTable() throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
	}

	// ----------------------------
	
	@Override
	public boolean isVendBllTaxable() {
		throw new WrongInvoiceTypeException();
	}

	@Override
	public GCshTaxTable getVendBllTaxTable() throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
	}

	// ----------------------------
	
	@Override
	public boolean isEmplVchTaxable() {
		throw new WrongInvoiceTypeException();
	}

	@Override
	public GCshTaxTable getEmplVchTaxTable() throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
	}

	// ----------------------------
	
	@Override
	public FixedPointNumber getApplicableTaxPercent() {
		return getJobInvcApplicableTaxPercent();
	}

	@Override
	public String getApplicableTaxPercentFormatted() {
		return getJobInvcApplicableTaxPercentFormatted();
	}

	// ---------------------------------------------------------------
	
	@Override
	public FixedPointNumber getSum() {
		return getJobInvcSum();
	}

	@Override
	public FixedPointNumber getSumInclTaxes() {
		return getJobInvcSumInclTaxes();
	}

	@Override
	public FixedPointNumber getSumExclTaxes() {
		return getJobInvcSumExclTaxes();
	}

	// ----------------------------
	
	@Override
	public FixedPointNumber getCustInvcSum() {
		throw new WrongInvoiceTypeException();
	}

	@Override
	public FixedPointNumber getVendBllSumInclTaxes() {
		throw new WrongInvoiceTypeException();
	}

	@Override
	public FixedPointNumber getEmplVchSumExclTaxes() {
		throw new WrongInvoiceTypeException();
	}

	// ----------------------------
	
	@Override
	public String getSumFormatted() {
		return getJobInvcSumFormatted();
	}

	@Override
	public String getSumInclTaxesFormatted() {
		return getJobInvcSumInclTaxesFormatted();
	}

	@Override
	public String getSumExclTaxesFormatted() {
		return getJobInvcSumExclTaxesFormatted();
	}

	// ----------------------------
	
	@Override
	public String getCustInvcSumFormatted() {
		throw new WrongInvoiceTypeException();
	}

	@Override
	public String getVendBllSumInclTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	@Override
	public String getEmplVchSumExclTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	// ---------------------------------------------------------------

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashJobInvoiceEntryImpl [");

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
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", quantity=");
		buffer.append(getQuantity());

		buffer.append("]");
		return buffer.toString();
	}

}
