package org.estatio.module.budgetassignment.dom;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationService;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.CalculationVMForLease;
import org.estatio.module.budget.dom.budgetcalculation.InMemBudgetCalculation;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budgetassignment.imports.InvoiceItemValueForBudgetItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetService {

    @Programmatic
    public void removeExistingPartitionItemsAndBudgetItemsIfCanBeRemoved(final Budget budget){
        switch (budget.getStatus()){
        case RECONCILED:
            return;
        case NEW:
            for (BudgetItem item : budget.getItems()) {
                for (PartitionItem pItem : item.getPartitionItems()) {
                    pItem.remove();
                }
                if (budgetItemCannotBeRemovedReason(item) == null) {
                    repositoryService.removeAndFlush(item);
                }
            }
            break;
        case ASSIGNED:
        default:
            for (BudgetItem item : budget.getItems()) {
                for (PartitionItem pItem : item.getPartitionItems()) {
                    if (pItem.getType()==BudgetCalculationType.AUDITED) {
                        pItem.remove();
                    }
                }
            }
        }
    }

    @Programmatic
    public void removeExistingPartitionItemsIfCanBeRemoved(final Budget budget) {
        switch (budget.getStatus()){
        case RECONCILED:
            return;
        case NEW:
            for (BudgetItem item : budget.getItems()) {
                for (PartitionItem pItem : item.getPartitionItems()) {
                    pItem.remove();
                }
            }
            break;
        case ASSIGNED:
        default:
            for (BudgetItem item : budget.getItems()) {
                for (PartitionItem pItem : item.getPartitionItems()) {
                    if (pItem.getType()==BudgetCalculationType.AUDITED) {
                        pItem.remove();
                    }
                }
            }
        }
    }

    @Programmatic
    public String budgetItemCannotBeRemovedReason(final BudgetItem budgetItem){
        if (budgetItem.getBudget().getStatus()!=Status.NEW) return "The budget is not in a status of NEW";
        if (orderItemRepository.findByBudgetItem(budgetItem).size()>0) return "There are orderitems attached";
        if (incomingInvoiceItemRepository.findByBudgetItem(budgetItem).size()>0) return "There are invoice items attached";
        return null;
    }

    @Programmatic
    public String budgetCannotBeRemovedReason(final Budget budget){
        for (BudgetItem budgetItem : budget.getItems()){
            if (budgetItemCannotBeRemovedReason(budgetItem)!=null) return budgetItemCannotBeRemovedReason(budgetItem);
        }
        return null;
    }

    @Programmatic
    public void calculateAuditedBudgetItemValues(final Budget budget){
        if (budget.getStatus() == Status.RECONCILED) return;
        for (BudgetItem budgetItem : budget.getItems()){
            calculateAuditedValue(budgetItem);
        }
    }

    private BudgetItem calculateAuditedValue(final BudgetItem budgetItem){
        final List<IncomingInvoiceItem> invoiceItemsForBudgetItem = incomingInvoiceItemRepository.findByBudgetItem(budgetItem);
        budgetItem.upsertValue(sumInvoiceNetAmount(invoiceItemsForBudgetItem), budgetItem.getBudget().getStartDate(), BudgetCalculationType.AUDITED);
        return budgetItem;
    }

    BigDecimal sumInvoiceNetAmount(final List<IncomingInvoiceItem> invoiceItems){
        return invoiceItems.stream()
                .filter(ii->ii.getNetAmount()!=null)
                .map(ii -> ii.getNetAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Programmatic
    public BigDecimal auditedValueForBudgetItemAndCalculationInterval(final BudgetItem budgetItem, final LocalDate startDate, final LocalDate endDate){
        if (LocalDateInterval.including(startDate, endDate).isValid()){
            return incomingInvoiceItemRepository.findByBudgetItem(budgetItem).stream()
                    .map(ii-> netamountForInvoiceItemAndCalculationInterval(ii, LocalDateInterval.including(startDate, endDate)))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        }
        return null;
    }

    BigDecimal netamountForInvoiceItemAndCalculationInterval(final IncomingInvoiceItem invoiceItem, final LocalDateInterval calculationInterval){
        final LocalDateInterval chargeInterval = LocalDateInterval
                .including(invoiceItem.getChargeStartDate(), invoiceItem.getChargeEndDate());
        if (calculationInterval.overlaps(chargeInterval) && invoiceItem.getNetAmount()!=null){

            if (calculationInterval.contains(chargeInterval)) return invoiceItem.getNetAmount();
            if (chargeInterval.contains(calculationInterval)) return invoiceItem.getNetAmount();

            final int denominator = chargeInterval.days();
            final int numerator = chargeInterval.overlap(calculationInterval).days();
            return invoiceItem.getNetAmount().multiply(BigDecimal.valueOf(numerator)).divide(BigDecimal.valueOf(denominator), MathContext.DECIMAL64).setScale(
                    6, RoundingMode.HALF_UP);

        }
        return BigDecimal.ZERO;
    }

    @Programmatic
    public List<InMemBudgetCalculation> auditedCalculationsForBudgetAndUnitAndCalculationInterval(
            final Budget budget,
            final Unit unit,
            final LocalDateInterval calculationInterval
    ){
        List<InMemBudgetCalculation> result = new ArrayList<>();
        for (BudgetItem budgetItem : budget.getItems()){
            for (PartitionItem partitionItem : budgetItem.getPartitionItemsForType(BudgetCalculationType.AUDITED)){
                result.addAll(auditedCalculationsForPartitionItemAndUnitAndCalculationInterval(partitionItem, unit, calculationInterval));
            }
        }
        return result;
    }

    List<InMemBudgetCalculation> auditedCalculationsForPartitionItemAndUnitAndCalculationInterval(
            final PartitionItem partitionItem,
            final Unit unit,
            final LocalDateInterval calculationInterval
    ){
        // safe guard
        if (partitionItem.getPartitioning().getType()!=BudgetCalculationType.AUDITED) return Lists.emptyList();

        BigDecimal paritionItemValue;
        if (partitionItem.getFixedAuditedAmount()!=null) {
            // TODO: we do not know how to calculate at the moment
            paritionItemValue = BigDecimal.ZERO;
        } else {
            paritionItemValue = auditedValueForBudgetItemAndCalculationInterval(partitionItem.getBudgetItem(), calculationInterval.startDate(), calculationInterval.endDate())
                    .multiply(partitionItem.getPercentage())
                    .divide(new BigDecimal("100"), MathContext.DECIMAL64).setScale(6, RoundingMode.HALF_UP);
        }
        return budgetCalculationService
                .calculateInMemForUnitPartitionItemAndAuditedPartitionItemValue(
                        partitionItem,
                        paritionItemValue,
                        unit,
                        calculationInterval.startDate(),
                        calculationInterval.endDate());
    }

    public List<CalculationVMForLease> calculationVmsForLease(final Lease lease, final Budget budget){
        final List<CalculationVMForLease> calcVmsForLease = new ArrayList<>();
        for (Occupancy occupancy : lease.getOccupancies()){
            final LocalDateInterval calculationInterval = occupancy.getEffectiveInterval().overlap(budget.getInterval());
            if (calculationInterval!=null) {
                auditedCalculationsForBudgetAndUnitAndCalculationInterval(
                        budget,
                        occupancy.getUnit(),
                        calculationInterval
                ).forEach(c -> calcVmsForLease.add(inMemCalculationToVMForLease(lease, c)));
            }
        }
        return calcVmsForLease;
    }

    private CalculationVMForLease inMemCalculationToVMForLease(final Lease lease, final InMemBudgetCalculation calculation){
        final BigDecimal budgetItemAmountForCalculationPeriod =
                calculation.getCalculationType()==BudgetCalculationType.BUDGETED ?
                        calculation.getPartitionItem().getBudgetItem().getBudgetedValue() :
                        auditedValueForBudgetItemAndCalculationInterval(
                                calculation.getPartitionItem().getBudgetItem(),
                                calculation.getCalculationStartDate(),
                                calculation.getCalculationEndDate());
        return new CalculationVMForLease(
                lease.getReference(),
                calculation.getTableItem().getUnit().getReference(),
                BudgetCalculationService.incomingChargeReferenceAndPartitioning(calculation),
                budgetItemAmountForCalculationPeriod,
                calculation.getPartitionItem().getBudgetItem().getCalculationDescription(),
                BudgetCalculationService.tableNameAndSourceValue(calculation),
                calculation.getValue(),
                calculation.getAuditedCostForBudgetPeriod(),
                calculation.getCalculationStartDate(),
                calculation.getCalculationEndDate()
        );
    }

    public List<InvoiceItemValueForBudgetItem> invoiceItemValuesForBudgetAndLease(final Lease lease, final Budget budget){
        final List<InvoiceItemValueForBudgetItemAndInterval> invoiceItemValuesForLease = new ArrayList<>();
        for (Occupancy occupancy : lease.getOccupancies()) {
            com.google.common.collect.Lists.newArrayList(budget.getItems()).stream().sorted(Comparator.comparing(BudgetItem::getCharge))
                    .forEach(bi -> {
                        final LocalDateInterval calculationInterval = occupancy.getEffectiveInterval()
                                .overlap(budget.getEffectiveInterval());
                        if (calculationInterval!=null) {
                            invoiceItemValuesForLease.addAll(invoiceItemValuesForBudgetItemAndInterval(bi,
                                    calculationInterval, occupancy.getUnit().getReference()));
                        }
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
        return iiVms;
    }

    private List<InvoiceItemValueForBudgetItemAndInterval> invoiceItemValuesForBudgetItemAndInterval(final BudgetItem budgetItem, final LocalDateInterval calculationInterval, final String reference){
        List<InvoiceItemValueForBudgetItemAndInterval> result = new ArrayList<>();
        incomingInvoiceItemRepository.findByBudgetItem(budgetItem).forEach(ii->{
            result.add(
                    new InvoiceItemValueForBudgetItemAndInterval(
                            ii,
                            budgetItem,
                            netamountForInvoiceItemAndCalculationInterval(ii, calculationInterval),
                            calculationInterval,
                            reference)
            );
        });
        return result;
    }

    @Inject OrderItemRepository orderItemRepository;

    @Inject IncomingInvoiceItemRepository incomingInvoiceItemRepository;

    @Inject RepositoryService repositoryService;

    @Inject BudgetCalculationService budgetCalculationService;
}
