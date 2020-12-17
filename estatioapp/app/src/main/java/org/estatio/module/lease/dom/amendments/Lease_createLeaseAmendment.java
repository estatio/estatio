package org.estatio.module.lease.dom.amendments;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin
public class Lease_createLeaseAmendment {

    private final Lease lease;

    public Lease_createLeaseAmendment(Lease lease) {
        this.lease = lease;
    }

    @Action()
    public Lease $$(
            final LeaseAmendmentTemplate leaseAmendmentTemplate
    ) {
        final LeaseAmendment amendment = leaseAmendmentRepository.create(lease, leaseAmendmentTemplate, leaseAmendmentTemplate.getLeaseAmendmentType(), LeaseAmendmentState.PROPOSED, leaseAmendmentTemplate
                .getAmendmentStartDate(), null);
        if (leaseAmendmentTemplate.getDiscountPercentage()!=null && leaseAmendmentTemplate.getDiscountAppliesTo()!=null && leaseAmendmentTemplate
                .getDiscountStartDate()!=null && leaseAmendmentTemplate.getDiscountEndDate()!=null) {
            leaseAmendmentItemRepository.create(amendment, leaseAmendmentTemplate.getDiscountPercentage(), null, leaseAmendmentTemplate
                    .getDiscountAppliesTo(), leaseAmendmentTemplate.getDiscountStartDate(), leaseAmendmentTemplate.getDiscountEndDate());
        }
        final LeaseAmendmentTemplate.Tuple<InvoicingFrequency, InvoicingFrequency> frequencyTuple = leaseAmendmentService
                .findInvoiceFrequencyTupleOnfirstFrequencyChangeCandidate(amendment);
        if (frequencyTuple!=null && leaseAmendmentTemplate.getFrequencyChanges()!=null && leaseAmendmentTemplate.getFrequencyChangeAppliesTo()!=null && leaseAmendmentTemplate
                .getFrequencyChangeStartDate()!=null && leaseAmendmentTemplate.getFrequencyChangeEndDate()!=null) {
            leaseAmendmentItemRepository.create(amendment, frequencyTuple.oldValue, frequencyTuple.newValue, leaseAmendmentTemplate
                            .getFrequencyChangeAppliesTo(),
                    leaseAmendmentTemplate.getFrequencyChangeStartDate(), leaseAmendmentTemplate.getFrequencyChangeEndDate());
        }
        return lease;
    }

    public List<LeaseAmendmentTemplate> choices0$$() {
        final List<LeaseAmendmentTemplate> templatesForAtPath = Arrays.asList(LeaseAmendmentTemplate.values())
                .stream()
                .filter(lat -> lease.getAtPath().startsWith(lat.getAtPath()))
                .collect(Collectors.toList());
        if (lease.getAtPath().startsWith("/ITA")) {
            return templatesForAtPath
                    .stream()
                    .filter(lat -> lat.getLeaseAmendmentType() == LeaseAmendmentType.COVID_WAVE_2)
                    .collect(Collectors.toList());
        }

        return templatesForAtPath;
    }

    public String validate$$(
            final LeaseAmendmentTemplate leaseAmendmentTemplate){
        return leaseAmendmentRepository.findUnique(lease, leaseAmendmentTemplate)==null ? null : String.format("There is already an amendment for type %s",
                leaseAmendmentTemplate);
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
