package org.estatio.module.budgetassignment.contributions;

import java.util.ArrayList;
import java.util.Arrays;
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

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.CalculationVMForLease;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budgetassignment.dom.BudgetService;
import org.estatio.module.budgetassignment.dom.InvoiceItemValueForBudgetItemAndInterval;
import org.estatio.module.budgetassignment.imports.InvoiceItemValueForBudgetItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
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
            final Lease lease
    ) {
        final List<CalculationVMForLease> calcVmsForLease = new ArrayList<>();
        for (Occupancy occupancy : lease.getOccupancies()){
            budgetService.auditedCalculationsForBudgetAndUnitAndCalculationInterval(
                    budget,
                    occupancy.getUnit(),
                    occupancy.getEffectiveInterval()
            ).forEach(c->calcVmsForLease.add(budgetService.inMemCalculationToVMForLease(lease, c)));
        }

        final List<InvoiceItemValueForBudgetItemAndInterval> invoiceItemValuesForLease = new ArrayList<>();
        for (Occupancy occupancy : lease.getOccupancies()) {
            Lists.newArrayList(budget.getItems()).stream().sorted(Comparator.comparing(BudgetItem::getCharge))
                    .forEach(bi -> {
                        invoiceItemValuesForLease.addAll(budgetService
                                .invoiceItemValuesForBudgetItemAndInterval(bi, occupancy.getEffectiveInterval(), occupancy.getUnit().getReference()));
                    });
        }
        List<InvoiceItemValueForBudgetItem> iiVms = new ArrayList<>();
        invoiceItemValuesForLease
                .stream()
                .sorted(
                        Comparator.comparing(InvoiceItemValueForBudgetItemAndInterval::getReference)
                                .thenComparing(InvoiceItemValueForBudgetItemAndInterval::getBudgetItem))
                .forEach(iv->{
                    final IncomingInvoice invoice = (IncomingInvoice) iv.getInvoiceItem().getInvoice();
                    iiVms.add(
                    new InvoiceItemValueForBudgetItem(
                            iv.getReference(),
                            iv.getBudgetItem().getCharge().getReference(),
                            invoice.getInvoiceNumber(),
                            invoice.getBarcode(),
                            invoice.getSeller().getName(),
                            invoice.getInvoiceDate(),
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
        fileNameBuffer.append("Audited Calculations for ");
        fileNameBuffer.append(lease.getReference());
        fileNameBuffer.append("-");
        fileNameBuffer.append(budget.getStartDate().year());
        fileNameBuffer.append(".xlsx");

        WorksheetSpec calcSpec = new WorksheetSpec(CalculationVMForLease.class, "calculations");
        WorksheetSpec invoiceItemValuesSpec = new WorksheetSpec(InvoiceItemValueForBudgetItem.class, "invoice item values");
        WorksheetContent calculationsContent = new WorksheetContent(calcVmsForLease.stream().sorted(Comparator.comparing(CalculationVMForLease::getUnitReference)).collect(
                Collectors.toList()), calcSpec);
        WorksheetContent invoiceItemValuesContent = new WorksheetContent(iiVms, invoiceItemValuesSpec);
        return excelService.toExcel(
                Arrays.asList(calculationsContent, invoiceItemValuesContent), fileNameBuffer.toString());
    }

    public List<Lease> autoComplete0Act(@MinLength(3) final String reference){
        return leaseRepository.autoComplete(reference).stream().filter(l->l.getProperty()==budget.getProperty()).collect(
                Collectors.toList());
    }


    @Inject BudgetService budgetService;

    @Inject ExcelService excelService;

    @Inject LeaseRepository leaseRepository;

}
