package org.estatio.module.lease.dom.amendments;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseStatus;

@Mixin
public class Lease_createLeaseAmendment {

    private final Lease lease;

    public Lease_createLeaseAmendment(Lease lease) {
        this.lease = lease;
    }

    @Action()
    public Lease $$(
            final LeaseAmendmentType leaseAmendmentType
    ) {
        final LeaseAmendment amendment = leaseAmendmentRepository.create(lease, leaseAmendmentType, LeaseAmendmentState.PROPOSED, leaseAmendmentType.getAmendmentStartDate(), null);
        if (leaseAmendmentType.getDiscountPercentage()!=null && leaseAmendmentType.getDiscountAppliesTo()!=null && leaseAmendmentType.getDiscountStartDate()!=null && leaseAmendmentType.getDiscountEndDate()!=null) {
            leaseAmendmentItemRepository.create(amendment, leaseAmendmentType.getDiscountPercentage(), leaseAmendmentType.getDiscountAppliesTo(), leaseAmendmentType.getDiscountStartDate(), leaseAmendmentType.getDiscountEndDate());
        }
        final LeaseAmendmentType.Tuple<InvoicingFrequency, InvoicingFrequency> frequencyTuple = leaseAmendmentService
                .findInvoiceFrequencyTupleOnfirstFrequencyChangeCandidate(amendment);
        if (frequencyTuple!=null && leaseAmendmentType.getFrequencyChanges()!=null && leaseAmendmentType.getFrequencyChangeAppliesTo()!=null && leaseAmendmentType.getFrequencyChangeStartDate()!=null && leaseAmendmentType.getFrequencyChangeEndDate()!=null) {
            leaseAmendmentItemRepository.create(amendment, frequencyTuple.oldFrequency, frequencyTuple.newFrequency, leaseAmendmentType.getFrequencyChangeAppliesTo(),
                    leaseAmendmentType.getFrequencyChangeStartDate(), leaseAmendmentType.getFrequencyChangeEndDate());
        }
        return lease;
    }

    public String validate$$(
            final LeaseAmendmentType leaseAmendmentType){
        return leaseAmendmentRepository.findUnique(lease, leaseAmendmentType)==null ? null : String.format("There is already an amendment for type %s", leaseAmendmentType);
    }

    public boolean hide$$(){
        return lease.getStatus()== LeaseStatus.PREVIEW;
    }

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;

    @Inject
    LeaseAmendmentItemRepository leaseAmendmentItemRepository;

    @Inject
    LeaseAmendmentService leaseAmendmentService;

}
