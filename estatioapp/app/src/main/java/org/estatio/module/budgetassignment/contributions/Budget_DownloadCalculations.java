package org.estatio.module.budgetassignment.contributions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budgetassignment.dom.service.CalculationResultViewModel;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;

/**
 * This cannot be inlined (needs to be a mixin) because Budget doesn't know about CalculationResultViewModel
 */
@Mixin
public class Budget_DownloadCalculations {

    private final Budget budget;
    public Budget_DownloadCalculations(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob downloadCalculations() {
        final String fileName =  budget.title() + ".xlsx";
        WorksheetSpec spec = new WorksheetSpec(CalculationResultViewModel.class, "values");
        WorksheetContent worksheetContent = new WorksheetContent(getCalculationResultsForBudget(), spec);
        return excelService.toExcelPivot(worksheetContent, fileName);
    }

    @Programmatic
    public List<CalculationResultViewModel> getCalculationResultsForBudget(){
        List<CalculationResultViewModel> result = new ArrayList<>();
        List<BudgetCalculation> calculations = budgetCalculationRepository.findByBudget(budget);
        List<Charge> distinctInvoiceCharges = distinctInvoiceCharges(calculations);
        for (Charge charge : distinctInvoiceCharges){
            List<Unit> distinctUnitsForCharge = distinctUnitsForCharge(calculations, charge);
            for (Unit unit : distinctUnitsForCharge) {
                CalculationResultViewModel vm = new CalculationResultViewModel();
                vm.setInvoiceCharge(charge.getReference());
                vm.setUnit(unit.getReference());
                if (firstActiveLeaseOnBudgetStartDate(unit) !=null) vm.setLeaseReference(firstActiveLeaseOnBudgetStartDate(unit).getReference());
                List<BudgetCalculation> calcsForUnitAndCharge = calculations.stream().filter(c -> c.getUnit().equals(unit) && c.getInvoiceCharge().equals(charge)).collect(Collectors.toList());
                for (BudgetCalculation calculation : calcsForUnitAndCharge){
                    if (calculation.getCalculationType() == BudgetCalculationType.BUDGETED){
                        BigDecimal newVal = vm.getBudgetedValue() == null ? calculation.getValue() : vm.getBudgetedValue().add(calculation.getValue());
                        vm.setBudgetedValue(newVal);
                        if (vm.getLeaseReference()==null) {
                            BigDecimal newShortFall = vm.getShortfallBudgeted() == null ? calculation.getValue() : vm.getShortfallBudgeted().add(calculation.getValue());
                            vm.setShortfallBudgeted(newShortFall);
                            BigDecimal newEffVal = vm.getBudgetedValue().subtract(vm.getShortfallBudgeted());
                            vm.setEffectiveBudgetedValue(newEffVal);
                        } else {
                            vm.setEffectiveBudgetedValue(newVal);
                        }
                    } else {
                        BigDecimal newVal = vm.getActualValue() == null ? calculation.getValue() : vm.getActualValue().add(calculation.getValue());
                        vm.setActualValue(newVal);
                        if (vm.getLeaseReference()==null) {
                            BigDecimal newShortFall = vm.getShortfallActual() == null ? calculation.getValue() : vm.getShortfallActual().add(calculation.getValue());
                            vm.setShortfallActual(newShortFall);
                            BigDecimal newEffVal = vm.getActualValue().subtract(vm.getShortfallBudgeted());
                            vm.setEffectiveActualValue(newEffVal);
                        } else {
                            vm.setEffectiveActualValue(newVal);
                        }
                    }
                }
                if (vm.getLeaseReference()==null) vm.setLeaseReference("No occupancy at budget start for " + unit.getReference());
                result.add(vm);
            }
        }
        return result;
    }

    private List<Unit> distinctUnitsForCharge(final List<BudgetCalculation> calculations, final Charge charge) {
        return calculations.stream().filter(c->c.getInvoiceCharge().equals(charge)).map(c->c.getUnit()).distinct().collect(Collectors.toList());
    }

    private List<Charge> distinctInvoiceCharges(final List<BudgetCalculation> calculations) {
        return calculations.stream().map(c->c.getInvoiceCharge()).distinct().collect(Collectors.toList());
    }

    private Lease firstActiveLeaseOnBudgetStartDate(final Unit unit){
        List<Occupancy> occupanciesOnBudgetStartDate = occupancyRepository.occupanciesByUnitAndInterval(unit, LocalDateInterval.including(budget.getStartDate(), budget.getStartDate()));
        if (occupanciesOnBudgetStartDate.isEmpty()) return null;
        return occupanciesOnBudgetStartDate.get(0).getLease();
    }

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private OccupancyRepository occupancyRepository;

    @Inject
    private ExcelService excelService;

}
