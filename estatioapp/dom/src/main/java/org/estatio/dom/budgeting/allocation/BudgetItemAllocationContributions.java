package org.estatio.dom.budgeting.allocation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.budgetitem.BudgetItemRepository;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class BudgetItemAllocationContributions {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetItemAllocation> budgetItemAllocations(final KeyTable keyTable) {
        return budgetItemAllocationRepository.findByKeyTable(keyTable);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public BudgetItemAllocation createBudgetItemAllocation(
            final Charge charge,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage
    ) {
        return budgetItemAllocationRepository.newBudgetItemAllocation(charge, keyTable, budgetItem, percentage);
    }

    public List<Charge> choices0CreateBudgetItemAllocation(
            final Charge charge,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage
    ){
        return chargeRepo.allCharges();
    }

    public List<KeyTable> choices1CreateBudgetItemAllocation(
            final Charge charge,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage) {
        return keyTableRepository.findByBudget(budgetItem.getBudget());
    }

    public List<BudgetItem> choices2CreateBudgetItemAllocation(
            final Charge charge,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage) {
        return budgetItemRepo.findByBudget(keyTable.getBudget());
    }

    public BigDecimal default3CreateBudgetItemAllocation(
            final Charge charge,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage) {
        return new BigDecimal(100);
    }

    public String validateCreateBudgetItemAllocation(
            final Charge charge,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage
    ){
        return budgetItemAllocationRepository.validateNewBudgetItemAllocation(charge,keyTable,budgetItem, percentage);
    }

    @Inject
    private BudgetItemAllocationRepository budgetItemAllocationRepository;

    @Inject
    private Charges chargeRepo;

    @Inject
    private BudgetItemRepository budgetItemRepo;

    @Inject
    private KeyTableRepository keyTableRepository;

}
