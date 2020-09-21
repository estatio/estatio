package org.estatio.module.lease.dom.amortisation;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.LeaseItem;

@Mixin
public class AmortisationSchedule_leaseItems {

    private final AmortisationSchedule amortisationSchedule;

    public AmortisationSchedule_leaseItems(AmortisationSchedule amortisationSchedule) {
        this.amortisationSchedule = amortisationSchedule;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<LeaseItem> $$() {
        return amortisationScheduleLeaseItemLinkRepository.findBySchedule(amortisationSchedule)
        .stream()
        .map(l->l.getLeaseItem())
        .collect(Collectors.toList());
    }

    @Inject
    AmortisationScheduleLeaseItemLinkRepository amortisationScheduleLeaseItemLinkRepository;

}
