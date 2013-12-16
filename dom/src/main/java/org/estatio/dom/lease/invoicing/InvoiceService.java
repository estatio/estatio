package org.estatio.dom.lease.invoicing;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.viewmodel.InvoiceSummariesForPropertyDueDate;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDate;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseStatus;
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
            final @Named("Period end Date") LocalDate endDate) {
        final List<Lease> results = leases.findLeasesByProperty(property);
        for (Lease lease : results) {
            if (lease.getStatus() != LeaseStatus.SUSPENDED) {
                lease.calculate(runType, dueDate, startDate, endDate);
            }
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

    // public String validateCalculateInvoicesForProperty(
    // final Property property,
    // final InvoiceRunType runType,
    // final LocalDate dueDate,
    // final LocalDate startDate,
    // final LocalDate endDate) {
    // if(endDate.compareTo(startDate) < 0) {
    // return "End date is before start date";
    // }
    // return null;
    // }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Invoices", sequence = "99")
    @Prototype
    public List<InvoiceSummaryForPropertyDueDate> calculateInvoicesForLeases(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") String referenceOrName,
            final @Named("Run Type") InvoiceRunType runType,
            final @Named("Due date") LocalDate dueDate,
            final @Named("Period Start Date") LocalDate startDate,
            final @Named("Period End Date") LocalDate endDate) {
        final List<Lease> results = leases.findLeases(referenceOrName);
        for (Lease lease : results) {
            if (lease.getStatus() != LeaseStatus.SUSPENDED) {
                lease.calculate(runType, dueDate, startDate, endDate);
            }
        }
        return invoiceSummaries.invoicesForPropertyDueDate();
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
