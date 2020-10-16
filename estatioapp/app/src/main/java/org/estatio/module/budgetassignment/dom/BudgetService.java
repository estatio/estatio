package org.estatio.module.budgetassignment.dom;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.order.OrderItemRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetService {

    @Programmatic
    public String budgetItemCannotBeRemovedReason(final BudgetItem budgetItem){
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

    @Programmatic
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

    @Inject OrderItemRepository orderItemRepository;

    @Inject IncomingInvoiceItemRepository incomingInvoiceItemRepository;

}
