package org.estatio.module.budgetassignment.dom;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
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

    @Inject OrderItemRepository orderItemRepository;

    @Inject IncomingInvoiceItemRepository incomingInvoiceItemRepository;

}
