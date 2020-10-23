package org.estatio.module.budgetassignment.dom;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.order.OrderItemRepository;

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
            calculateAuditedValues(budgetItem);
        }
    }

    private BudgetItem calculateAuditedValues(final BudgetItem budgetItem){
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
        // TODO: check with user the behaviour for charge start- and/or end date null
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

    @Inject OrderItemRepository orderItemRepository;

    @Inject IncomingInvoiceItemRepository incomingInvoiceItemRepository;

    @Inject RepositoryService repositoryService;
}
