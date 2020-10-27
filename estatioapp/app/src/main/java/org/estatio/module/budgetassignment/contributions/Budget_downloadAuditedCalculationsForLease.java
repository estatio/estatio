package org.estatio.module.budgetassignment.contributions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.CalculationVMForUnit2;
import org.estatio.module.budget.dom.budgetcalculation.InMemBudgetCalculation;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budgetassignment.dom.BudgetService;
import org.estatio.module.budgetassignment.dom.InvoiceItemValueForBudgetItemAndInterval;
import org.estatio.module.budgetassignment.imports.InvoiceItemValueForBudgetItem;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.occupancy.Occupancy;

@Mixin(method = "act")
public class Budget_downloadAuditedCalculationsForLease {

    private final Budget budget;
    public Budget_downloadAuditedCalculationsForLease(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Blob act(
            final Lease lease,
            final Unit unit
    ) {
        Occupancy occupancy = Lists.newArrayList(lease.getOccupancies()).stream()
                .filter(o->o.getUnit()==unit)
                .filter(o->o.getEffectiveInterval().overlaps(budget.getInterval()))
                .findFirst().orElse(null);
        if (occupancy==null) return null;
        final List<InMemBudgetCalculation> inMemBudgetCalculations = budgetService
                .auditedCalculationsForBudgetAndUnitAndCalculationInterval(budget, unit,
                        occupancy.getEffectiveInterval());
        final List<CalculationVMForUnit2> calcVms = new ArrayList<>();
        inMemBudgetCalculations.forEach(c->{
            calcVms.add(budgetService.inMemCalculationToVMForUnit2(c));
        });

        final List<InvoiceItemValueForBudgetItemAndInterval> invoiceItemValues = new ArrayList<>();
        Lists.newArrayList(budget.getItems()).stream().sorted(Comparator.comparing(BudgetItem::getCharge)).forEach(bi->{
            invoiceItemValues.addAll(budgetService.invoiceItemValuesForBudgetItemAndInterval(bi, occupancy.getEffectiveInterval()));
        });
        List<InvoiceItemValueForBudgetItem> iiVms = new ArrayList<>();
        invoiceItemValues.forEach(iv->{
            iiVms.add(
                    new InvoiceItemValueForBudgetItem(
                            iv.getBudgetItem().getCharge().getReference(),
                            iv.getInvoiceItem().getInvoice().getInvoiceNumber(),
                            iv.getInvoiceItem().getInvoice().getInvoiceDate(),
                            iv.getInvoiceItem().getNetAmount(),
                            iv.getCalculatedValue(),
                            iv.getInvoiceItem().getChargeStartDate(),
                            iv.getInvoiceItem().getChargeEndDate(),
                            iv.getCalculationInterval().startDate(),
                            iv.getCalculationInterval().endDate()
                    )
            );
        });

        StringBuffer fileNameBuffer = new StringBuffer();
        fileNameBuffer.append(lease.getReference());
        fileNameBuffer.append("-");
        fileNameBuffer.append(unit.getReference());
        fileNameBuffer.append(".xlsx");

        WorksheetSpec calcSpec = new WorksheetSpec(CalculationVMForUnit2.class, "calculations");
        WorksheetSpec invoiceItemValuesSpec = new WorksheetSpec(InvoiceItemValueForBudgetItem.class, "invoice item values");
        WorksheetContent calculationsContent = new WorksheetContent(calcVms, calcSpec);
        WorksheetContent invoiceItemValuesContent = new WorksheetContent(iiVms, invoiceItemValuesSpec);
        return excelService.toExcel(
                Arrays.asList(calculationsContent, invoiceItemValuesContent), fileNameBuffer.toString());
    }

    public List<Lease> autoComplete0Act(@MinLength(3) final String reference){
        return leaseRepository.autoComplete(reference).stream().filter(l->l.getProperty()==budget.getProperty()).collect(
                Collectors.toList());
    }

    public List<Unit> choices1Act(final Lease lease){
        if (lease==null) return Collections.EMPTY_LIST;
        return Lists.newArrayList(lease.getOccupancies()).stream()
                .filter(o->o.getUnit()!=null)
                .map(o->o.getUnit()).collect(Collectors.toList());
    }

    @Inject
    private BudgetService budgetService;

    @Inject ExcelService excelService;

    @Inject LeaseRepository leaseRepository;

}
