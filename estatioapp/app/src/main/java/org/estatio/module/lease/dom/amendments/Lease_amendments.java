package org.estatio.module.lease.dom.amendments;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.Lease;

@Mixin
public class Lease_amendments {

    private final Lease lease;

    public Lease_amendments(Lease lease) {
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<LeaseAmendment> $$() {
        return leaseAmendmentRepository.findByLease(lease);
    }

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;

}
