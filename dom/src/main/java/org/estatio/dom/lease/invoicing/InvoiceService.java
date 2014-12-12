package org.estatio.dom.lease.invoicing;

import java.util.List;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.estatio.dom.EstatioService;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.viewmodel.InvoiceSummariesForInvoiceRun;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;

@DomainService
@DomainServiceLayout(
        named="Invoices",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "50.5"
)
public class InvoiceService extends EstatioService<InvoiceService> {

    public InvoiceService() {
        super(InvoiceService.class);
    }

    /**
     * Returns the {@link org.estatio.dom.invoice.viewmodel.InvoiceSummariesForInvoiceRun invoice summaries} that are newly {@link Lease#calculate(InvoiceRunType, InvoiceCalculationSelection, org.joda.time.LocalDate, org.joda.time.LocalDate, org.joda.time.LocalDate) calculate}d for all of the
     * {@link Lease}s matched by the provided <tt>property</tt> and the other
     * parameters.
     */
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Invoices", sequence = "1")
    public Object calculateInvoicesForProperty(
            final @Named("Property") Property property,
            final @Named("Run Type") InvoiceRunType invoiceRunType,
            final @Named("Selection") InvoiceCalculationSelection calculationSelection,
            final @Named("Invoice due date") LocalDate invoiceDueDate,
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") LocalDate nextDueDate) {
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

    @ActionLayout(
            prototype = true
    )
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Invoices", sequence = "99")
    public Object calculateInvoicesForLeases(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") String referenceOrName,
            final @Named("Run Type") InvoiceRunType runType,
            final @Named("Selection") InvoiceCalculationSelection selection,
            final @Named("Invoice due date") LocalDate invoiceDueDate,
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") LocalDate nextDueDate) {
        String runId = null;
        final List<Lease> results = leases.findLeases(referenceOrName);
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

    @javax.inject.Inject
    private Leases leases;

    @javax.inject.Inject
    private InvoiceCalculationService invoiceCalculationService;


    @javax.inject.Inject
    private InvoiceSummariesForInvoiceRun invoiceSummaries;

}
