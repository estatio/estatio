package org.estatio.dom.lease.invoicing;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioService;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.viewmodel.InvoiceSummariesForInvoiceRun;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;

@DomainService
@DomainServiceLayout(
        named = "Invoices",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "50.5")
public class InvoiceService extends EstatioService<InvoiceService> {

    public InvoiceService() {
        super(InvoiceService.class);
    }

    /**
     * Returns the
     * {@link org.estatio.dom.invoice.viewmodel.InvoiceSummariesForInvoiceRun
     * invoice summaries} that are newly {@link #MISSING() calculate}d for all
     * of the {@link Lease}s matched by the provided <tt>property</tt> and the
     * other
     * parameters.
     */
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Invoices", sequence = "1")
    public Object calculateInvoicesForProperty(
            final @ParameterLayout(named = "Property") Property property,
            final @ParameterLayout(named = "Run Type") InvoiceRunType invoiceRunType,
            final @ParameterLayout(named = "Selection") InvoiceCalculationSelection calculationSelection,
            final @ParameterLayout(named = "Invoice due date") LocalDate invoiceDueDate,
            final @ParameterLayout(named = "Start due date") LocalDate startDueDate,
            final @ParameterLayout(named = "Next due date") LocalDate nextDueDate) {
        final String runId = invoiceCalculationService.calculateAndInvoice(
                new InvoiceCalculationParameters(
                        property,
                        calculationSelection.selectedTypes(),
                        invoiceRunType,
                        invoiceDueDate,
                        startDueDate,
                        nextDueDate));
        return invoiceSummaries.findByRunId(runId);
    }

    public InvoiceRunType default1CalculateInvoicesForProperty() {
        return InvoiceRunType.values()[0];
    }

    public InvoiceCalculationSelection default2CalculateInvoicesForProperty() {
        return InvoiceCalculationSelection.values()[0];
    }

    public LocalDate default3CalculateInvoicesForProperty() {
        return getClockService().beginningOfNextQuarter();
    }

    public LocalDate default4CalculateInvoicesForProperty() {
        return getClockService().beginningOfNextQuarter();
    }

    public LocalDate default5CalculateInvoicesForProperty() {
        return getClockService().beginningOfNextQuarter().plusDays(1);
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

    @Prototype
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Invoices", sequence = "99")
    public Object calculateInvoicesForLeases(
            final @ParameterLayout(named = "Reference or Name", describedAs = "May include wildcards '*' and '?'") String referenceOrName,
            final @ParameterLayout(named = "Run Type") InvoiceRunType runType,
            final @ParameterLayout(named = "Selection") InvoiceCalculationSelection selection,
            final @ParameterLayout(named = "Invoice due date") LocalDate invoiceDueDate,
            final @ParameterLayout(named = "Start due date") LocalDate startDueDate,
            final @ParameterLayout(named = "Next due date") LocalDate nextDueDate) {
        String runId = null;
        final List<Lease> results = leases.findLeases(referenceOrName, false);
        if (results != null && results.size() > 0) {
            runId = invoiceCalculationService.calculateAndInvoice(
                    new InvoiceCalculationParameters(
                            results,
                            selection.selectedTypes(),
                            runType,
                            invoiceDueDate,
                            startDueDate,
                            nextDueDate));
        }
        return invoiceSummaries.findByRunId(runId);
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
        return getClockService().beginningOfNextQuarter();
    }

    public LocalDate default4CalculateInvoicesForLeases() {
        return getClockService().beginningOfNextQuarter();
    }

    public LocalDate default5CalculateInvoicesForLeases() {
        return getClockService().beginningOfNextQuarter();
    }

    // //////////////////////////////////////

    @Bulk
    public Object calculate(
            final Lease lease,
            final InvoiceRunType runType,
            final InvoiceCalculationSelection calculationSelection,
            final @ParameterLayout(named = "Invoice due date") LocalDate invoiceDueDate,
            final @ParameterLayout(named = "Start due date") LocalDate startDueDate,
            final @ParameterLayout(named = "Next due date") LocalDate nextDueDate) {
        String runId = invoiceCalculationService.calculateAndInvoice(
                new InvoiceCalculationParameters(
                        lease,
                        calculationSelection.selectedTypes(),
                        runType,
                        invoiceDueDate,
                        startDueDate,
                        nextDueDate));
        if (runId != null) {
            return invoiceSummaries.findByRunId(runId);
        }
        getContainer().informUser("No invoices created");
        return lease;
    }

    public InvoiceRunType default1Calculate() {
        return InvoiceRunType.values()[0];
    }

    public InvoiceCalculationSelection default2Calculate() {
        return InvoiceCalculationSelection.values()[0];
    }

    public LocalDate default3Calculate() {
        return getClockService().beginningOfNextQuarter();
    }

    public LocalDate default4Calculate() {
        return getClockService().beginningOfNextQuarter();
    }

    public LocalDate default5Calculate() {
        return getClockService().beginningOfNextQuarter().plusDays(1);
    }

    public String validateCalculate(
            final Lease lease,
            final InvoiceRunType runType,
            final InvoiceCalculationSelection selection,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            return "End date cannot be before start date";
        }
        return null;
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private Leases leases;

    @javax.inject.Inject
    private InvoiceCalculationService invoiceCalculationService;

    @javax.inject.Inject
    private InvoiceSummariesForInvoiceRun invoiceSummaries;

}
