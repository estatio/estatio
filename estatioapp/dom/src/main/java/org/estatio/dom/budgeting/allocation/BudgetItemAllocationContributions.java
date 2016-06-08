package org.estatio.dom.budgeting.allocation;

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

import org.estatio.dom.budgeting.keytable.KeyTable;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class BudgetItemAllocationContributions {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetItemAllocation> budgetItemAllocations(final KeyTable keyTable) {
        return budgetItemAllocationRepository.findByKeyTable(keyTable);
    }


    @Inject
    private BudgetItemAllocationRepository budgetItemAllocationRepository;

}
