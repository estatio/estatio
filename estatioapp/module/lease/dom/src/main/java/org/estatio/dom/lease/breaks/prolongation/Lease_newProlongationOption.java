package org.estatio.dom.lease.breaks.prolongation;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;

import org.estatio.dom.lease.Lease;

@Mixin
public class Lease_newProlongationOption {

    private final Lease lease;

    public Lease_newProlongationOption(Lease lease) {
        this.lease = lease;
    }

    @Action()
    @MemberOrder(name = "breakOptions", sequence = "1")
    public Lease $$(
            final String prolongationPeriod,
            final String notificationPeriod,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String description) {
        prolongationOptionRepository.newProlongationOption(lease,prolongationPeriod,notificationPeriod, description);
        return lease;
    }

    public String disable$$(){
        return prolongationOptionRepository.findByLease(lease) == null ? null : "A prolongation already exists";
    }

    @Inject
    private ProlongationOptionRepository prolongationOptionRepository;



}
