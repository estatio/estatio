package org.estatio.dom.budgeting.budgetkeyitem.contributions;

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

import org.estatio.dom.budgeting.budgetkeyitem.BudgetKeyItem;
import org.estatio.dom.lease.Occupancies;
import org.estatio.dom.lease.Occupancy;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class OccupanciesOnBudgetKeyItemContributions {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<Occupancy> occupancies(final BudgetKeyItem budgetKeyItem) {

       return occupancies.occupanciesByUnitAndInterval(budgetKeyItem.getUnit(), budgetKeyItem.getBudgetKeyTable().getInterval());

    }

    @Inject
    private Occupancies occupancies;

}


