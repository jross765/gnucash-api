package org.gnucash.api.read;

import java.util.List;

import org.gnucash.api.read.hlp.GnuCashEmployee_Invc_FP;
import org.gnucash.api.read.hlp.GnuCashEmployee_Invc_Str;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.read.hlp.HasAddress;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.base.basetypes.simple.GCshEmplID;

/**
 * An employee that can hand in expense vouchers and, obviously, receive
 * a salary
 * <br>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-emply.html">GnuCash manual</a>
 *
 * @see GnuCashEmployeeVoucher
 * 
 * @see GnuCashCustomer
 * @see GnuCashVendor
 */
public interface GnuCashEmployee extends GnuCashObject,
                                         GnuCashEmployee_Invc_FP,
                                         GnuCashEmployee_Invc_Str,
										 HasUserDefinedAttributes,
										 HasAddress
{
    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshEmplID getID();

    /**
     * @return Returns the user-assigned number of this employee (may contain non-digits)
     */
    String getNumber();

    /**
     * @return Returns the user name of the employee
     */
    String getUserName();

//    /**
//     * @return Returns the address of this employee including his/her name
//     *         (as opposed to the user name)
//     */
//    GCshAddress getAddress();

    /**
     * @return user-defined notes about the employee (may be null)
     */
    String getLanguage();

    // ------------------------------------------------------------

    /**
     * 
     * 
     * @return Returns the current number of Unpaid vouchers from this employee.
     *         The date is not checked so invoiced that have entered payments in the future are
     *         considered Paid.
     */
    int getNofOpenVouchers();

    // ------------------------------------------------------------

    /**
     * @return Returns all vouchers sent to this employee, 
     *         both paid and unpaid.
     */
    List<GnuCashGenerInvoice>    getVouchers();

    /**
     * @return Returns all paid vouchers sent from this employee.
     */
    List<GnuCashEmployeeVoucher> getPaidVouchers();

    /**
     * @return Returns all unpaid vouchers sent from this employee.
     */
    List<GnuCashEmployeeVoucher> getUnpaidVouchers();

}
