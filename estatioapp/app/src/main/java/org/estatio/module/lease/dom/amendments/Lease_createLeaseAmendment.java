package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItemType;

@Mixin
public class Lease_createLeaseAmendment {

    private final Lease lease;

    public Lease_createLeaseAmendment(Lease lease) {
        this.lease = lease;
    }

    @Action()
    public Lease $$(
            final LeaseAmendmentType leaseAmendmentType,
            @Nullable
            final LocalDate startDate,
            @Nullable
            final LocalDate endDate,
            @Nullable
            final BigDecimal discountPercentage,
            @Nullable
            final List<LeaseItemType> discountAppliesTo,
            @Nullable
            final LocalDate discountStartDate,
            @Nullable
            final LocalDate discountEndDate,
            @Nullable
            final InvoicingFrequency invoicingFrequencyOnLease,
            @Nullable
            final InvoicingFrequency newInvoicingFrequency,
            @Nullable
            final List<LeaseItemType> frequencyChangeAppliesTo,
            @Nullable
            final LocalDate invoicingFrequencyStartDate,
            @Nullable
            final LocalDate invoicingFrequencyEndDate
    ) {
        if (leaseAmendmentRepository.findUnique(lease, leaseAmendmentType)==null) {
            final LeaseAmendment leaseAmendment = leaseAmendmentRepository
                    .upsert(lease, leaseAmendmentType, LeaseAmendmentState.PROPOSED, startDate, endDate);
            leaseAmendmentItemRepository
                    .create(leaseAmendment, discountPercentage, discountAppliesTo, discountStartDate, discountEndDate);
            leaseAmendmentItemRepository
                    .create(leaseAmendment, invoicingFrequencyOnLease, newInvoicingFrequency, frequencyChangeAppliesTo,
                            invoicingFrequencyStartDate, invoicingFrequencyEndDate);
        }
        return lease;
    }

    public String validate$$(
            final LeaseAmendmentType leaseAmendmentType,
            @Nullable
            final LocalDate startDate,
            @Nullable
            final LocalDate endDate,
            @Nullable
            final BigDecimal discountPercentage,
            @Nullable
            final List<LeaseItemType> discountAppliesTo,
            @Nullable
            final LocalDate discountStartDate,
            @Nullable
            final LocalDate discountEndDate,
            @Nullable
            final InvoicingFrequency invoicingFrequencyOnLease,
            @Nullable
            final InvoicingFrequency newInvoicingFrequency,
            @Nullable
            final List<LeaseItemType> frequencyChangeAppliesTo,
            @Nullable
            final LocalDate invoicingFrequencyStartDate,
            @Nullable
            final LocalDate invoicingFrequencyEndDate){
        return leaseAmendmentRepository.findUnique(lease, leaseAmendmentType)==null ? null : String.format("There is already an amendment for type %s", leaseAmendmentType);
    }

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;

    @Inject
    LeaseAmendmentItemRepository leaseAmendmentItemRepository;

}
