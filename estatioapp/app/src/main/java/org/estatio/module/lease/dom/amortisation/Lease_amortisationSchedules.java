package org.estatio.module.lease.dom.amortisation;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseStatus;

@Mixin
public class Lease_amortisationSchedules {

    private final Lease lease;

    public Lease_amortisationSchedules(Lease lease) {
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<AmortisationSchedule> $$() {
        return amortisationScheduleRepository.findByLease(lease);
    }

    public boolean hide$$(){
        if (lease.getStatus()== LeaseStatus.PREVIEW) return true;
        return false;
    }

    @Inject
    AmortisationScheduleRepository amortisationScheduleRepository;

}
