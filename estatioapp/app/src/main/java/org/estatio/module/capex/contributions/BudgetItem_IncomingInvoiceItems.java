package org.estatio.module.capex.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;

/**
 * This cannot be inlined (must be a mixin) because BudgetItem does not know about incoming invoices.
 */
@Mixin
public class BudgetItem_IncomingInvoiceItems {

    private final BudgetItem BudgetItem;
    public BudgetItem_IncomingInvoiceItems(BudgetItem BudgetItem){
        this.BudgetItem = BudgetItem;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<IncomingInvoiceItem> invoiceItems() {
        return incomingInvoiceItemRepository.findByBudgetItem(BudgetItem).stream()
                .filter(i->!i.isDiscarded())
                .collect(Collectors.toList());
    }

    @Inject
    private IncomingInvoiceItemRepository incomingInvoiceItemRepository;
}
