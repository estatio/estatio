package org.estatio.dom.lease.invoicing;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioInteractionCache;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.viewmodel.InvoiceSummariesForPropertyDueDateStatus;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;
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
    public List<InvoiceSummaryForPropertyDueDateStatus> calculateInvoicesForProperty(
            final @Named("Property") @DescribedAs("") Property property,
            final @Named("Run Type") InvoiceRunType runType,
            final @Named("Selection") InvoiceCalculationSelection calculationSelection,
            final @Named("Invoice due date") LocalDate invoiceDueDate,
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") LocalDate nextDueDate) {
        final List<Lease> results = leases.findLeasesByProperty(property);
        for (Lease lease : results) {
            if (lease.getStatus() != LeaseStatus.SUSPENDED) {
                lease.calculate(runType, calculationSelection, invoiceDueDate, startDueDate, nextDueDate);
            }
        }
        return invoiceSummaries.invoicesForPropertyDueDateStatus();
    }

    public LocalDate default3CalculateInvoicesForProperty() {
        return clockService.beginningOfNextQuarter();
    }

    public LocalDate default4CalculateInvoicesForProperty() {
        return clockService.beginningOfNextQuarter();
    }

    public LocalDate default5CalculateInvoicesForProperty() {
        return clockService.beginningOfNextQuarter();
    }

    public String validateCalculateInvoicesForProperty(
            final Property property,
            final InvoiceRunType runType,
            final InvoiceCalculationSelection calculationSelection,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (endDate.compareTo(startDate) < 0) {
            return "End date is before start date";
        }
        return null;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Invoices", sequence = "99")
    @Prototype
    public List<InvoiceSummaryForPropertyDueDateStatus> calculateInvoicesForLeases(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") String referenceOrName,
            final @Named("Run Type") InvoiceRunType runType,
            final @Named("Selection") InvoiceCalculationSelection selection,
            final @Named("Invoice due date") LocalDate invoiceDueDate,
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") LocalDate nextDueDate) {

        try {
            EstatioInteractionCache.startInteraction();

            final List<Lease> results = leases.findLeases(referenceOrName);
            for (Lease lease : results) {
                if (lease.getStatus() != LeaseStatus.SUSPENDED) {
                    lease.calculate(runType, selection, invoiceDueDate, startDueDate, nextDueDate);
                }
            }
            return invoiceSummaries.invoicesForPropertyDueDateStatus();
        } finally {
            EstatioInteractionCache.endInteraction();
        }
    }

    public String validateCalculateInvoicesForLeases(
            final String referenceOrName,
            final InvoiceRunType runType,
            final InvoiceCalculationSelection calculationSelection,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (endDate.compareTo(startDate) < 0) {
            return "End date is before start date";
        }
        return null;
    }
    
    public LocalDate default3CalculateInvoicesForLeases() {
        return clockService.beginningOfNextQuarter();
    }

    public LocalDate default4CalculateInvoicesForLeases() {
        return clockService.beginningOfNextQuarter();
    }

    public LocalDate default5CalculateInvoicesForLeases() {
        return clockService.beginningOfNextQuarter();
    }

    // //////////////////////////////////////

    private InvoiceSummariesForPropertyDueDateStatus invoiceSummaries;

    public void injectInvoiceSummaries(final InvoiceSummariesForPropertyDueDateStatus invoiceSummaries) {
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
