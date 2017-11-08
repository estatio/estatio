package org.estatio.app.menus.invoice;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItemType;

/**
 * This cannot be inlined (needs to be a mixin) because Property does not know about InvoiceServiceMenu
 */
@Mixin(method = "exec")
public class Property_calculateInvoices {

    private final Property property;

    public Property_calculateInvoices(final Property property) {
        this.property = property;
    }

    @Inject
    InvoiceServiceMenu invoiceServiceMenu;

    /**
     * Returns the invoice summaries that are newly calculated for all
     * of the {@link Lease}s matched by the provided <tt>property</tt> and the
     * other
     * parameters.
     */
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(named = "Calculate Invoices For Property")
    @MemberOrder(name = "Invoices", sequence = "1")
    public Object exec(
            final InvoiceRunType runType,
            final List<LeaseItemType> leaseItemTypes,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        return invoiceServiceMenu.calculateInvoicesForProperty(property, runType, leaseItemTypes, invoiceDueDate, startDueDate, nextDueDate);
    }

    public List<LeaseItemType> choices1Exec() {
        return invoiceServiceMenu.choices2CalculateInvoicesForProperty();
    }

    public InvoiceRunType default0Exec() {
        return invoiceServiceMenu.default1CalculateInvoicesForProperty();
    }

    public List<LeaseItemType> default1Exec() {
        return invoiceServiceMenu.default2CalculateInvoicesForProperty();
    }

    public LocalDate default2Exec() {
        return invoiceServiceMenu.default3CalculateInvoicesForProperty();
    }

    public LocalDate default3Exec() {
        return invoiceServiceMenu.default4CalculateInvoicesForProperty();
    }

    public LocalDate default4Exec() {
        return invoiceServiceMenu.default5CalculateInvoicesForProperty();
    }

    public String validateExec(
            final InvoiceRunType runType,
            final List<LeaseItemType> leaseItemTypes,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate) {
        return invoiceServiceMenu.validateCalculateInvoicesForProperty(property, runType, leaseItemTypes, dueDate, startDate, endDate);
    }

    public boolean hideExec() {
        return invoiceServiceMenu.hideCalculateInvoicesForProperty();
    }


}
