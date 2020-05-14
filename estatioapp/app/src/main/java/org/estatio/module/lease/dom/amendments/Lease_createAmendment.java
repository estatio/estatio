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
public class Lease_createAmendment {

    private final Lease lease;

    public Lease_createAmendment(Lease lease) {
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
            final LocalDate discountStartDate,
            @Nullable
            final LocalDate discountEndDate,
            @Nullable
            final List<LeaseItemType> discountAppliesTo,
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
        final Amendment amendment = amendmentRepository.create(lease, AmendmentState.PROPOSED, startDate, endDate);
        amendmentItemForDiscountRepository.create(amendment, discountPercentage, discountAppliesTo, discountStartDate, discountEndDate);
        amendmentItemForFrequencyChangeRepository.create(amendment, invoicingFrequencyOnLease, newInvoicingFrequency, frequencyChangeAppliesTo, invoicingFrequencyStartDate, invoicingFrequencyEndDate);
        return lease;
    }

    @Inject
    AmendmentRepository amendmentRepository;

    @Inject
    AmendmentItemForDiscountRepository amendmentItemForDiscountRepository;

    @Inject
    AmendmentItemForFrequencyChangeRepository amendmentItemForFrequencyChangeRepository;

}
