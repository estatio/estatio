package org.estatio.dom.budgeting.scheduleitem.contributed;

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
import org.estatio.dom.budgeting.scheduleitem.ScheduleItem;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItems;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class OnKeyTableContributions {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<ScheduleItem> scheduleItems(final KeyTable keyTable) {
        return scheduleItemRepo.findByKeyTable(keyTable);
    }

    @Inject
    private ScheduleItems scheduleItemRepo;

}
