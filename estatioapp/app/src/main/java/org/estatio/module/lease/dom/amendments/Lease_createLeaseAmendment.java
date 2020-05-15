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
        // TODO: for the moment we can have just 1 immutable amendment per lease
        if (leaseAmendmentRepository.findByLease(lease).isEmpty()) {
            final LeaseAmendment leaseAmendment = leaseAmendmentRepository
                    .create(lease, LeaseAmendmentType.DUMMY_TYPE, LeaseAmendmentState.PROPOSED, startDate, endDate);
            leaseAmendmentItemForDiscountRepository
                    .create(leaseAmendment, discountPercentage, discountAppliesTo, discountStartDate, discountEndDate);
            leaseAmendmentItemForFrequencyChangeRepository
                    .create(leaseAmendment, invoicingFrequencyOnLease, newInvoicingFrequency, frequencyChangeAppliesTo,
                            invoicingFrequencyStartDate, invoicingFrequencyEndDate);
        }
        return lease;
    }

    public String disable$$(){
        return leaseAmendmentRepository.findByLease(lease).isEmpty() ? null : "At the moment we allow 1 amendment per lease";
    }

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;

    @Inject
    LeaseAmendmentItemForDiscountRepository leaseAmendmentItemForDiscountRepository;

    @Inject
    LeaseAmendmentItemForFrequencyChangeRepository leaseAmendmentItemForFrequencyChangeRepository;

}
