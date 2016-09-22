package org.estatio.dom.lease.invoicing;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.viewmodel.InvoiceSummariesForInvoiceRunMenu;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForInvoiceRun;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;

@DomainService(nature = NatureOfService.VIEW)
@DomainServiceLayout(
        named = "Invoices",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "50.5")
public class InvoiceServiceMenuAndContributions extends UdoDomainService<InvoiceServiceMenuAndContributions> {

    public InvoiceServiceMenuAndContributions() {
        super(InvoiceServiceMenuAndContributions.class);
    }


    /**
     * Returns the
     * {@link InvoiceSummariesForInvoiceRunMenu
     * invoice summaries} that are newly calculated for all
     * of the {@link Lease}s matched by the provided <tt>property</tt> and the
     * other
     * parameters.
     */
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "Invoices", sequence = "1")
    public Object calculateInvoicesForProperty(
            final Property property,
            final InvoiceRunType runType,
            final InvoiceCalculationSelection selection,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        final String runId = invoiceCalculationService.calculateAndInvoice(
                new InvoiceCalculationParameters(
                        property,
                        selection.selectedTypes(),
                        runType,
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
        return getCalendarService().beginningOfNextQuarter();
    }

    public LocalDate default4CalculateInvoicesForProperty() {
        return getCalendarService().beginningOfNextQuarter();
    }

    public LocalDate default5CalculateInvoicesForProperty() {
        return getCalendarService().beginningOfNextQuarter().plusDays(1);
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

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @MemberOrder(name = "Invoices", sequence = "99")
    public Object calculateRetroInvoices(
            final ApplicationTenancy applicationTenancy,
            final InvoiceCalculationSelection selection,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        String runId = null;

        for (Lease lease : leaseRepository.allLeases()){
            if (lease.getApplicationTenancy().getPath().matches(applicationTenancy.getPath()+".*")){
                for (LocalDate dueDate : lease.dueDatesInRange(startDueDate, nextDueDate)) {
                    InvoiceCalculationParameters parameters =
                            new InvoiceCalculationParameters(
                                    lease,
                                    selection.selectedTypes(),
                                    InvoiceRunType.NORMAL_RUN,
                                    dueDate,
                                    startDueDate,
                                    dueDate.plusDays(1));
                    runId = invoiceCalculationService.calculateAndInvoice(parameters);
                    if (runId != null) {
                        final InvoiceSummaryForInvoiceRun summaryForInvoiceRun = invoiceSummaries.findByRunId(runId);
                        if (summaryForInvoiceRun != null) {
                            summaryForInvoiceRun.saveAllAsHistoric();
                        } else {
                            getContainer().warnUser(String.format("Run Id [%s] not found", runId));
                        }
                    }
                }
            }
        }
        return "Done";
    }

    // //////////////////////////////////////

    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    public Object calculate(
            final Lease lease,
            final InvoiceRunType runType,
            final InvoiceCalculationSelection calculationSelection,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
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
        return getCalendarService().beginningOfNextQuarter();
    }

    public LocalDate default4Calculate() {
        return getCalendarService().beginningOfNextQuarter();
    }

    public LocalDate default5Calculate() {
        return getCalendarService().beginningOfNextQuarter().plusDays(1);
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

    public String disableCalculate(final Lease lease,
            final InvoiceRunType runType,
            final InvoiceCalculationSelection selection,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate){

        if (lease == null) return null;
        return lease.getProperty() == null ? "Please set occupancy first" : null;

    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private LeaseRepository leaseRepository;

    @javax.inject.Inject
    private InvoiceCalculationService invoiceCalculationService;

    @javax.inject.Inject
    private InvoiceSummariesForInvoiceRunMenu invoiceSummaries;



}
