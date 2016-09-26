package org.estatio.app.mixins.budgetoverview;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.dom.budgetassignment.BudgetAssignmentService;
import org.estatio.dom.budgetassignment.viewmodels.BudgetAssignmentResult;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationStatus;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, auditing = Auditing.DISABLED)
public class BudgetOverview  {

    public BudgetOverview(){}

    public BudgetOverview(final Budget budget) {

        this();
        this.budget = budget;
        this.budgetedValue = budget.getBudgetedValue();
        this.auditedValue = budget.getAuditedValue();
        this.budgetedValueForBudgetPeriod = budget.getBudgetedValueForBudgetInterval();
        this.auditedValueForBudgetPeriod = budget.getAuditedValueForBudgetInterval();
    }

    public TranslatableString title() {
        return TranslatableString.tr("{name}", "name", "Budget overview");
    }


    @Getter @Setter
    private Budget budget;

    @Getter @Setter
    private BigDecimal budgetedValue;

    @Getter @Setter
    private BigDecimal auditedValue;

    @Getter @Setter
    private BigDecimal budgetedValueForBudgetPeriod;

    @Getter @Setter
    private BigDecimal auditedValueForBudgetPeriod;

//    @Getter @Setter
//    private BigDecimal recoverableBudgetedValue;
//
//    @Getter @Setter
//    private BigDecimal recoverableAuditedValue;
//
//    @Getter @Setter
//    private BigDecimal shortfallBudgeted;
//
//    @Getter @Setter
//    private BigDecimal shortfallAudited;
//
//    @Getter @Setter
//    private BigDecimal assignedBudgetedValue;
//
//    @Getter @Setter
//    private BigDecimal assignedAuditedValue;

    @Programmatic
    public BudgetOverview init(){
//        this.shortfallBudgeted = budgetAssignmentService.getShortFallAmountBudgeted(budget);
//        this.shortfallAudited = budgetAssignmentService.getShortFallAmountAudited(budget);
//        this.recoverableBudgetedValue = budgetedValueForBudgetPeriod.subtract(shortfallBudgeted);
//        this.recoverableAuditedValue = auditedValueForBudgetPeriod.subtract(shortfallAudited);
//        this.assignedBudgetedValue = assignedValueForBudgetYear(BudgetCalculationType.BUDGETED);
//        this.assignedAuditedValue = assignedValueForBudgetYear(BudgetCalculationType.AUDITED);
        return this;
    }

    private BigDecimal assignedValueForBudgetYear(final BudgetCalculationType type){
        BigDecimal result = BigDecimal.ZERO;
        for (BudgetCalculation calculation : budgetCalculationRepository.findByBudgetAndStatusAndCalculationType(getBudget(), BudgetCalculationStatus.ASSIGNED, type)){
            result = result.add(calculation.getValueForBudgetPeriod());
        }
        return result;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob downloadCalculations() {
        final String fileName =  getBudget().title() + ".xlsx";
        WorksheetSpec spec = new WorksheetSpec(BudgetAssignmentResult.class, "calculations");
        WorksheetContent worksheetContent = new WorksheetContent(budgetAssignmentResults(), spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    private List<BudgetAssignmentResult> budgetAssignmentResults(){
        return budgetAssignmentService.getAssignmentResults(getBudget());
    }

    @Inject
    private BudgetAssignmentService budgetAssignmentService;

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private ExcelService excelService;

}
