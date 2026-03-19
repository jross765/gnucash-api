package org.gnucash.api.read.spec.hlp.fil;

import java.util.List;

import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;

public interface GnuCashFile_Invc_Empl {

    /**
     * @param empl the employee to look for (not null)
     * @return a (possibly read-only) collection of all vouchers that have fully been
     *         paid and are from the given employee Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getPaidVouchersForEmployee(GnuCashEmployee)
     * @see #getUnpaidVouchersForEmployee(GnuCashEmployee)
     */
    List<GnuCashEmployeeVoucher> getVouchersForEmployee(GnuCashEmployee empl);

    /**
     * @param empl the employee to look for (not null)
     * @return a (possibly read-only) collection of all vouchers that have fully been
     *         paid and are from the given employee Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getUnpaidVouchersForEmployee(GnuCashEmployee)
     */
    List<GnuCashEmployeeVoucher> getPaidVouchersForEmployee(GnuCashEmployee empl);

    /**
     * @param empl the employee to look for (not null)
     * @return a (possibly read-only) collection of all vouchers that have not fully
     *         been paid and are from the given employee Do not modify the returned
     *         collection!
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshGenerInvcID)
     * @see #getPaidVouchersForEmployee(GnuCashEmployee)
     */
    List<GnuCashEmployeeVoucher> getUnpaidVouchersForEmployee(GnuCashEmployee empl);

}
