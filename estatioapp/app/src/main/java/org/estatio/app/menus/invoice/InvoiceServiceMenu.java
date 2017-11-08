package org.estatio.app.menus.invoice;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainService;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationParameters;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationSelection;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationService;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForInvoiceRun;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForInvoiceRunRepository;
import org.estatio.dom.togglz.EstatioTogglzFeature;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Invoices Out",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "50.2"
)
public class InvoiceServiceMenu extends UdoDomainService<InvoiceServiceMenu> {

    public InvoiceServiceMenu() {
        super(InvoiceServiceMenu.class);
    }

    // //////////////////////////////////////

    /**
     * Returns the
     * {@link InvoiceSummaryForInvoiceRunRepository
     * invoice summaries} that are newly calculated for all
     * of the {@link Lease}s matched by the provided <tt>property</tt> and the
     * other
     * parameters.
     */
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(named = "Calculate Invoices For Property")
    @MemberOrder(name = "Invoices", sequence = "1")
    public Object calculateInvoicesForPropertyLegacy(
            final Property property,
            final InvoiceRunType runType,
            final InvoiceCalculationSelection selection,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        final List<LeaseItemType> leaseItemTypes = selection.selectedTypes();
        return doCalculateInvoicesForProperty(property, runType, leaseItemTypes, invoiceDueDate, startDueDate, nextDueDate);
    }

    public InvoiceRunType default1CalculateInvoicesForPropertyLegacy() {
        return doDefault1CalculateInvoicesForProperty();
    }

    public InvoiceCalculationSelection default2CalculateInvoicesForPropertyLegacy() {
        return doDefault2CalculateInvoicesForProperty();
    }

    public LocalDate default3CalculateInvoicesForPropertyLegacy() {
        return doDefault3CalculateInvoicesForProperty();
    }

    public LocalDate default4CalculateInvoicesForPropertyLegacy() {
        return doDefault4CalculateInvoicesForProperty();
    }

    public LocalDate default5CalculateInvoicesForPropertyLegacy() { return doDefault5CalculateInvoicesForProperty();
    }

    public String validateCalculateInvoicesForPropertyLegacy(
            final Property property,
            final InvoiceRunType runType,
            final InvoiceCalculationSelection calculationSelection,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate) {
        return doValidateCalculateInvoicesForProperty(startDate, endDate);
    }

    public boolean hideCalculateInvoicesForPropertyLegacy() {
        return isMultiSelectActive();
    }

    // //////////////////

    /**
     * Returns the
     * {@link InvoiceSummaryForInvoiceRunRepository
     * invoice summaries} that are newly calculated for all
     * of the {@link Lease}s matched by the provided <tt>property</tt> and the
     * other
     * parameters.
     */
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(named = "Calculate Invoices For Property")
    @MemberOrder(sequence = "1")
    public Object calculateInvoicesForProperty(
            final Property property,
            final InvoiceRunType runType,
            final List<LeaseItemType> leaseItemTypes,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        return doCalculateInvoicesForProperty(property, runType, leaseItemTypes, invoiceDueDate, startDueDate, nextDueDate);
    }


    public List<LeaseItemType> choices2CalculateInvoicesForProperty() {
        return Arrays.asList(LeaseItemType.values());
    }

    public InvoiceRunType default1CalculateInvoicesForProperty() {
        return doDefault1CalculateInvoicesForProperty();
    }

    public List<LeaseItemType> default2CalculateInvoicesForProperty() {
        return doDefault2CalculateInvoicesForProperty().selectedTypes();
    }

    public LocalDate default3CalculateInvoicesForProperty() {
        return doDefault3CalculateInvoicesForProperty();
    }

    public LocalDate default4CalculateInvoicesForProperty() {
        return doDefault4CalculateInvoicesForProperty();
    }

    public LocalDate default5CalculateInvoicesForProperty() {
        return doDefault5CalculateInvoicesForProperty();
    }

    public String validateCalculateInvoicesForProperty(
            final Property property,
            final InvoiceRunType runType,
            final List<LeaseItemType> leaseItemTypes,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate) {
        return doValidateCalculateInvoicesForProperty(startDate, endDate);
    }

    public boolean hideCalculateInvoicesForProperty() {
        return isMultiSelectInactive();
    }


    // //////////////////

    private Object doCalculateInvoicesForProperty(
            final Property property,
            final InvoiceRunType runType,
            final List<LeaseItemType> leaseItemTypes,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        final String runId = invoiceCalculationService.calculateAndInvoice(
                InvoiceCalculationParameters.builder()
                        .property(property)
                        .leaseItemTypes(leaseItemTypes)
                        .invoiceRunType(runType)
                        .invoiceDueDate(invoiceDueDate)
                        .startDueDate(startDueDate)
                        .nextDueDate(nextDueDate).build());
        return invoiceSummaries.findByRunId(runId);
    }

    private InvoiceRunType doDefault1CalculateInvoicesForProperty() {
        return InvoiceRunType.values()[0];
    }

    private InvoiceCalculationSelection doDefault2CalculateInvoicesForProperty() {
        return InvoiceCalculationSelection.values()[4];
    }

    private LocalDate doDefault3CalculateInvoicesForProperty() {
        return getCalendarService().beginningOfNextQuarter();
    }

    private LocalDate doDefault4CalculateInvoicesForProperty() {
        return getCalendarService().beginningOfNextQuarter();
    }

    private LocalDate doDefault5CalculateInvoicesForProperty() {
        return getCalendarService().beginningOfNextQuarter().plusDays(1);
    }

    private String doValidateCalculateInvoicesForProperty(final LocalDate startDate, final LocalDate endDate) {
        if (endDate.compareTo(startDate) < 0) {
            return "End date is before start date";
        }
        return null;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(named = "Calculate Retro Invoices")
    @MemberOrder(name = "Invoices", sequence = "99")
    public Object calculateRetroInvoicesLegacy(
            final ApplicationTenancy applicationTenancy,
            final InvoiceCalculationSelection selection,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {

        final List<LeaseItemType> leaseItemTypes = selection.selectedTypes();
        return doCalculateRetroInvoices(applicationTenancy, leaseItemTypes, startDueDate, nextDueDate);
    }

    public boolean hideCalculateRetroInvoicesLegacy() {
        return isMultiSelectActive();
    }


    // //////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @MemberOrder(sequence = "99")
    public Object calculateRetroInvoices(
            final ApplicationTenancy applicationTenancy,
            final List<LeaseItemType> leaseItemTypes,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        return doCalculateRetroInvoices(applicationTenancy, leaseItemTypes, startDueDate, nextDueDate);
    }

    public List<LeaseItemType> choices1CalculateRetroInvoices() {
        return Arrays.asList(LeaseItemType.values());
    }

    public boolean hideCalculateRetroInvoices() {
        return isMultiSelectInactive();
    }

    // //////////////////

    private Object doCalculateRetroInvoices(
            final ApplicationTenancy applicationTenancy,
            final List<LeaseItemType> leaseItemTypes, final LocalDate startDueDate, final LocalDate nextDueDate) {
        for (Lease lease : leaseRepository.allLeases()){
            if (lease.getApplicationTenancy().getPath().matches(applicationTenancy.getPath()+".*")){
                for (LocalDate dueDate : lease.dueDatesInRange(startDueDate, nextDueDate)) {
                    InvoiceCalculationParameters parameters =
                            InvoiceCalculationParameters.builder()
                                    .lease(lease)
                                    .leaseItemTypes(leaseItemTypes)
                                    .invoiceRunType(InvoiceRunType.NORMAL_RUN)
                                    .invoiceDueDate(dueDate)
                                    .startDueDate(startDueDate)
                                    .nextDueDate(dueDate.plusDays(1)).build();
                    String runId = invoiceCalculationService.calculateAndInvoice(parameters);
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
    @ActionLayout(named = "Calculate")
    public Object calculateLegacy(
            final Lease lease,
            final InvoiceRunType runType,
            final InvoiceCalculationSelection calculationSelection,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        final List<LeaseItemType> leaseItemTypes = calculationSelection.selectedTypes();
        return doCalculate(lease, runType, invoiceDueDate, startDueDate, nextDueDate, leaseItemTypes);
    }

    public InvoiceRunType default1CalculateLegacy() {
        return doDefault1Calculate();
    }

    public InvoiceCalculationSelection default2CalculateLegacy() {
        return doDefault2Calculate();
    }

    public LocalDate default3CalculateLegacy() {
        return doDefault3Calculate();
    }

    public LocalDate default4CalculateLegacy() {
        return doDefault4Calculate();
    }

    public LocalDate default5CalculateLegacy() {
        return doDefault5Calculate();
    }

    public String validateCalculateLegacy(
            final Lease lease,
            final InvoiceRunType runType,
            final InvoiceCalculationSelection selection,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate) {
        return doValidateCalculate(startDate, endDate);
    }

    public boolean hideCalculateLegacy() {
        return isMultiSelectActive();
    }

    // //////////////////


    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    @ActionLayout(named = "Calculate")
    public Object calculate(
            final Lease lease,
            final InvoiceRunType runType,
            final List<LeaseItemType> leaseItemTypes,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        return doCalculate(lease, runType, invoiceDueDate, startDueDate, nextDueDate, leaseItemTypes);
    }

    public List<LeaseItemType> choices2Calculate() {
        return Arrays.asList(LeaseItemType.values());
    }

    public InvoiceRunType default1Calculate() {
        return doDefault1Calculate();
    }

    public List<LeaseItemType> default2Calculate() {
        return doDefault2Calculate().selectedTypes();
    }

    public LocalDate default3Calculate() {
        return doDefault3Calculate();
    }

    public LocalDate default4Calculate() {
        return doDefault4Calculate();
    }

    public LocalDate default5Calculate() {
        return doDefault5Calculate();
    }

    public String validateCalculate(
            final Lease lease,
            final InvoiceRunType runType,
            final List<LeaseItemType> leaseItemTypes,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate) {
        return doValidateCalculate(startDate, endDate);
    }

    public boolean hideCalculate() {
        return isMultiSelectInactive();
    }

    // //////////////////

    private Object doCalculate(
            final Lease lease,
            final InvoiceRunType runType,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate, final LocalDate nextDueDate, final List<LeaseItemType> leaseItemTypes) {
        String runId = invoiceCalculationService.calculateAndInvoice(
                InvoiceCalculationParameters.builder()
                        .lease(lease)
                        .leaseItemTypes(leaseItemTypes)
                        .invoiceRunType(runType)
                        .invoiceDueDate(invoiceDueDate)
                        .startDueDate(startDueDate)
                        .nextDueDate(nextDueDate)
                        .build());
        if (runId != null) {
            return invoiceSummaries.findByRunId(runId);
        }
        getContainer().informUser("No invoices created");
        return lease;
    }

    private InvoiceRunType doDefault1Calculate() {
        return InvoiceRunType.values()[0];
    }

    private InvoiceCalculationSelection doDefault2Calculate() {
        return InvoiceCalculationSelection.values()[4];
    }

    private LocalDate doDefault3Calculate() {
        return getCalendarService().beginningOfNextQuarter();
    }

    private LocalDate doDefault4Calculate() {
        return getCalendarService().beginningOfNextQuarter();
    }

    private LocalDate doDefault5Calculate() {
        return getCalendarService().beginningOfNextQuarter().plusDays(1);
    }

    private String doValidateCalculate(final LocalDate startDate, final LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            return "End date cannot be before start date";
        }
        return null;
    }

    @Programmatic
    public String doDisableCalculate(final Lease lease) {
        if (lease == null) return null;
        return lease.getProperty() == null ? "Please set occupancy first" : null;
    }


    // //////////////////////////////////////

    private boolean isMultiSelectActive() {
        return EstatioTogglzFeature.invoiceCalculationMultiSelect.isActive();
    }

    private boolean isMultiSelectInactive() {
        return !isMultiSelectActive();
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private LeaseRepository leaseRepository;

    @javax.inject.Inject
    private InvoiceCalculationService invoiceCalculationService;

    @javax.inject.Inject
    private InvoiceSummaryForInvoiceRunRepository invoiceSummaries;



}
