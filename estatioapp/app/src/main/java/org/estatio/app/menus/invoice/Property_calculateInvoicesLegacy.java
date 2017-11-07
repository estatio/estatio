package org.estatio.app.menus.invoice;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.InvoiceRunType;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForInvoiceRunRepository;

/**
 * This cannot be inlined (needs to be a mixin) because Property does not know about InvoiceServiceMenu
 */
@Mixin(method = "exec")
public class Property_calculateInvoicesLegacy {

    private final Property property;

    public Property_calculateInvoicesLegacy(final Property property) {
        this.property = property;
    }

    @Inject
    InvoiceServiceMenu invoiceServiceMenu;

    /**
     * Returns the
     * {@link InvoiceSummaryForInvoiceRunRepository
     * invoice summaries} that are newly calculated for all
     * of the {@link Lease}s matched by the provided <tt>property</tt> and the
     * other
     * parameters.
     */
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(named = "Calculate Invoices")
    @MemberOrder(name = "Invoices", sequence = "1")
    public Object exec(
            final InvoiceRunType runType,
            final InvoiceCalculationSelection selection,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        return invoiceServiceMenu.calculateInvoicesForPropertyLegacy(property, runType, selection, invoiceDueDate, startDueDate, nextDueDate);
    }

    public InvoiceRunType default0Exec() {
        return invoiceServiceMenu.default1CalculateInvoicesForPropertyLegacy();
    }

    public InvoiceCalculationSelection default1Exec() {
        return invoiceServiceMenu.default2CalculateInvoicesForPropertyLegacy();
    }

    public LocalDate default2Exec() {
        return invoiceServiceMenu.default3CalculateInvoicesForPropertyLegacy();
    }

    public LocalDate default3Exec() {
        return invoiceServiceMenu.default4CalculateInvoicesForPropertyLegacy();
    }

    public LocalDate default4Exec() {
        return invoiceServiceMenu.default5CalculateInvoicesForPropertyLegacy();
    }

    public String validateExec(
            final InvoiceRunType runType,
            final InvoiceCalculationSelection calculationSelection,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate) {
        return invoiceServiceMenu.validateCalculateInvoicesForPropertyLegacy(property, runType, calculationSelection, dueDate, startDate, endDate);
    }

    public boolean hideExec() {
        return invoiceServiceMenu.hideCalculateInvoicesForPropertyLegacy();
    }



}
