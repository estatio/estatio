package org.estatio.module.budgetassignment.app;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.CalculationVMForLease;
import org.estatio.module.budget.dom.partioning.PartitioningRepository;
import org.estatio.module.budgetassignment.dom.BudgetAssignmentService;
import org.estatio.module.budgetassignment.dom.BudgetService;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.budgetassignment.imports.InvoiceItemValueForBudgetItem;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.budget.BudgetMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Service Charges",
        menuOrder = "60.1"
)
public class BudgetMenu {

    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.SAFE)
    @CollectionLayout(defaultView = "table")
    public List<Budget> allBudgets() {
        return budgetRepository.allBudgets();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Budget newBudget(
            final Property property,
            final int year) {
        Budget budget = budgetRepository.newBudget(property, new LocalDate(year, 1, 1), new LocalDate(year, 12, 31));
        budget.findOrCreatePartitioningForBudgeting();
        return budget;
    }

    public String validateNewBudget(
            final Property property,
            final int year) {
        return budgetRepository.validateNewBudget(property, year);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(defaultView = "table")
    public Budget findBudget(
            final Property property,
            final Budget budget) {
        return budget;
    }

    public List<Budget> choices1FindBudget(
            final Property property,
            final Budget budget) {
        return budgetRepository.findByProperty(property);
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @CollectionLayout(defaultView = "table")
    public List<BudgetCalculationResult> allBudgetCalculationResults(){
        return budgetCalculationResultRepository.allBudgetCalculationResults();
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob downloadAuditedCalculationsForLease(final Lease lease, final Budget budget){
        final List<CalculationVMForLease> calcVmsForLease = budgetService.calculationVmsForLease(lease, budget)
                .stream()
                .sorted(Comparator.comparing(CalculationVMForLease::getUnitReference))
                .collect(Collectors.toList());

        StringBuffer fileNameBuffer = new StringBuffer();
        fileNameBuffer.append("Audited Calculations for ");
        fileNameBuffer.append(lease.getReference());
        fileNameBuffer.append("-");
        fileNameBuffer.append(budget.getBudgetYear());
        fileNameBuffer.append(".xlsx");

        WorksheetSpec calcSpec = new WorksheetSpec(CalculationVMForLease.class, "calculations");
        WorksheetSpec invoiceItemValuesSpec = new WorksheetSpec(InvoiceItemValueForBudgetItem.class, "invoice item values");
        WorksheetContent calculationsContent = new WorksheetContent(calcVmsForLease, calcSpec);
        WorksheetContent invoiceItemValuesContent = new WorksheetContent(budgetService.invoiceItemValuesForBudgetAndLease(lease, budget), invoiceItemValuesSpec);
        return excelService.toExcel(
                Arrays.asList(calculationsContent, invoiceItemValuesContent), fileNameBuffer.toString());
    }

    public List<Lease> autoComplete0DownloadAuditedCalculationsForLease(@MinLength(5) final String search){
        return leaseRepository.autoComplete(search);
    }

    public List<Budget> choices1DownloadAuditedCalculationsForLease(){
        return partitioningRepository.allPartitionings().stream().filter(p->p.getType()== BudgetCalculationType.AUDITED).map(p->p.getBudget()).collect(
                Collectors.toList());
    }

    public InvoiceLinkManager linkUnlinkedIncomingInvoiceItemsToBudget(final Property property, final Budget budget){
        return new InvoiceLinkManager(budget);
    }

    public List<Budget> choices1LinkUnlinkedIncomingInvoiceItemsToBudget(final Property property){
        if (property==null) return Lists.emptyList();
        return budgetRepository.findByProperty(property).stream().filter(b->b.getStatus()!= Status.RECONCILED).collect(
                Collectors.toList());
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public List<BudgetCalculationResult> calculateAuditedBudgetCalculationResultsForLease(final Lease lease, final Budget budget){
        return budgetAssignmentService.calculateAuditedResultsForLease(budget, lease);
    }

    public List<Lease> autoComplete0CalculateAuditedBudgetCalculationResultsForLease(@MinLength(5) final String search){
        return leaseRepository.autoComplete(search);
    }

    public List<Budget> choices1CalculateAuditedBudgetCalculationResultsForLease(final Lease lease, final Budget budget){
        return budgetRepository.findByProperty(lease.getProperty()).stream().filter(b->b.getStatus()==Status.RECONCILED).collect(
                Collectors.toList());
    }

    @Inject BudgetService budgetService;

    @Inject ExcelService excelService;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    BudgetCalculationResultRepository budgetCalculationResultRepository;

    @Inject LeaseRepository leaseRepository;

    @Inject PartitioningRepository partitioningRepository;

    @Inject BudgetAssignmentService budgetAssignmentService;

}
