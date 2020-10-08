package org.estatio.module.lease.contributions.amortisation;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleAmendmentItemLinkRepository;

@Mixin
public class LeaseAmendmentItem_amortisationSchedules {

    private final LeaseAmendmentItemForDiscount leaseAmendmentItemForDiscount;

    public LeaseAmendmentItem_amortisationSchedules(LeaseAmendmentItemForDiscount leaseAmendmentItemForDiscount) {
        this.leaseAmendmentItemForDiscount = leaseAmendmentItemForDiscount;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<AmortisationSchedule> $$() {
        return amortisationScheduleAmendmentItemLinkRepository.findByAmendmentItem(leaseAmendmentItemForDiscount).stream()
                .map(l->l.getAmortisationSchedule())
                .collect(Collectors.toList());

    }

    @Inject
    AmortisationScheduleAmendmentItemLinkRepository amortisationScheduleAmendmentItemLinkRepository;

}
