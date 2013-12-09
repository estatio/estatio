package org.estatio.dom.lease.invoicing;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.viewmodel.InvoiceSummariesForPropertyDueDate;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDate;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.services.clock.ClockService;

public class InvoiceService {

    /**
     * Returns the {@link InvoiceSummary}s that are newly
     * {@link Lease#calculate(LocalDate, LocalDate) calculate}d for all of the
     * {@link Lease}s matched by the provided <tt>property</tt> and the other
     * parameters.
     */
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Invoices", sequence = "6")
    public List<InvoiceSummaryForPropertyDueDate> calculateInvoicesForProperty(
            final @Named("Property") @DescribedAs("") Property property,
            final @Named("Run Type") InvoiceRunType runType,
            final @Named("Due date") LocalDate dueDate,
            final @Named("Period start Date") LocalDate startDate,
            final @Named("Period end Date") @Optional LocalDate endDate) {
        final List<Lease> results = leases.findLeasesByProperty(property);
        for (Lease lease : results) {
            lease.calculate(startDate, endDate, dueDate, runType);
        }
        return invoiceSummaries.invoicesForPropertyDueDate();
    }

    public LocalDate default2CalculateInvoicesForProperty() {
        return clockService.beginningOfNextQuarter();
    }

    public LocalDate default3CalculateInvoicesForProperty() {
        return clockService.beginningOfNextQuarter();
    }

    public LocalDate default4CalculateInvoicesForProperty() {
        return clockService.beginningOfNextQuarter();
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Invoices", sequence = "99")
    @Prototype
    public List<InvoiceItemForLease> calculateInvoicesForLeases(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") String referenceOrName,
            final @Named("Run Type") InvoiceRunType runType,
            final @Named("Due date") LocalDate dueDate,
            final @Named("Period Start Date") LocalDate startDate,
            final @Named("Period End Date") @Optional LocalDate endDate) {
        final List<Lease> results = leases.findLeases(referenceOrName);
        for (Lease lease : results) {
            lease.verifyUntil(endDate);
            lease.calculate(startDate, endDate, dueDate, runType);
        }
        // As a convenience, we now go find them and display them.
        // We've done it this way so that the user can always just go to the
        // menu and make this query.
        return invoiceItemsForLease.findInvoiceItemsByLease(referenceOrName,
                startDate, dueDate);
    }

    public LocalDate default2CalculateInvoicesForLeases() {
        return clockService.beginningOfNextQuarter();
    }

    public LocalDate default3CalculateInvoicesForLeases() {
        return clockService.beginningOfNextQuarter();
    }

    public LocalDate default4CalculateInvoicesForLeases() {
        return clockService.beginningOfNextQuarter();
    }

    // //////////////////////////////////////

    private InvoiceItemsForLease invoiceItemsForLease;

    public final void injectInvoiceItemsForLease(final InvoiceItemsForLease invoiceItemsForLease) {
        this.invoiceItemsForLease = invoiceItemsForLease;
    }

    private InvoiceSummariesForPropertyDueDate invoiceSummaries;

    public void injectInvoiceSummaries(final InvoiceSummariesForPropertyDueDate invoiceSummaries) {
        this.invoiceSummaries = invoiceSummaries;
    }

    private Leases leases;

    public void setLeases(final Leases leases) {
        this.leases = leases;
    }

    private ClockService clockService;

    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }
}
