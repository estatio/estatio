package org.estatio.module.lease.dom.amortisation;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;

@Mixin
public class AmortisationSchedule_leaseAmendmentItems {

    private final AmortisationSchedule amortisationSchedule;

    public AmortisationSchedule_leaseAmendmentItems(AmortisationSchedule amortisationSchedule) {
        this.amortisationSchedule = amortisationSchedule;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<LeaseAmendmentItemForDiscount> $$() {
        return amortisationScheduleAmendmentItemLinkRepository.findBySchedule(amortisationSchedule)
        .stream()
        .map(l->l.getLeaseAmendmentItemForDiscount())
        .collect(Collectors.toList());
    }

    @Inject
    AmortisationScheduleAmendmentItemLinkRepository amortisationScheduleAmendmentItemLinkRepository;

}
