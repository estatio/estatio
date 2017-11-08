package org.estatio.module.capex.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.budgeting.dom.budgetitem.BudgetItem;

/**
 * This cannot be inlined (needs to be a mixin) because BudgetItem does not know about orders.
 */
@Mixin
public class BudgetItem_OrderItems {

    private final BudgetItem BudgetItem;
    public BudgetItem_OrderItems(BudgetItem BudgetItem){
        this.BudgetItem = BudgetItem;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<OrderItem> orderItems() {
        return orderItemRepository.findByBudgetItem(BudgetItem);
    }

    @Inject
    private OrderItemRepository orderItemRepository;
}
