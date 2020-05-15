package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;

import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.LeaseItemType;

import lombok.Getter;

public enum AmendmentProposalType {

    COVID_FRA(
            new LocalDate(2020,6,1),
            new BigDecimal("50.00"),
            Arrays.asList(
                    LeaseItemType.RENT,
                    LeaseItemType.RENT_DISCOUNT,
                    LeaseItemType.RENT_DISCOUNT_FIXED
            ),
            new LocalDate(2020,3,16),
            new LocalDate(2020,5,10),
            Arrays.asList(
                    new Tuple<>(InvoicingFrequency.QUARTERLY_IN_ADVANCE, InvoicingFrequency.MONTHLY_IN_ADVANCE),
                    new Tuple<>(InvoicingFrequency.QUARTERLY_IN_ARREARS, InvoicingFrequency.MONTHLY_IN_ARREARS)
            ),
            Arrays.asList(
                    LeaseItemType.RENT,
                    LeaseItemType.RENT_DISCOUNT,
                    LeaseItemType.RENT_DISCOUNT_FIXED,
                    LeaseItemType.SERVICE_CHARGE,
                    LeaseItemType.MARKETING
            ),
            new LocalDate(2020,7,1),
            new LocalDate(2020,12,31)),
    DUMMY_TYPE(
            null, null, null, null, null, null, null, null, null)
    ;

    @Getter
    private final LocalDate amendmentStartDate;

    @Getter
    private final BigDecimal discountPercentage;

    @Getter
    private final List<LeaseItemType> discountAppliesTo;

    @Getter
    private final LocalDate discountStartDate;

    @Getter
    private final LocalDate discountEndDate;

    @Getter
    private final List<Tuple<InvoicingFrequency, InvoicingFrequency>> frequencyChanges;

    @Getter
    private final List<LeaseItemType> frequencyChangeAppliesTo;

    @Getter
    private final LocalDate frequencyChangeStartDate;

    @Getter
    private final LocalDate frequencyChangeEndDate;

    AmendmentProposalType(
            final LocalDate amendmentStartDate,
            final BigDecimal discountPercentage,
            final List<LeaseItemType> discountAppliesTo,
            final LocalDate discountStartDate,
            final LocalDate discountEndDate,
            final List<Tuple<InvoicingFrequency, InvoicingFrequency>> frequencyChanges,
            final List<LeaseItemType> frequencyChangeAppliesTo,
            final LocalDate frequencyChangeStartDate,
            final LocalDate frequencyChangeEndDate) {
        this.amendmentStartDate = amendmentStartDate;
        this.discountPercentage = discountPercentage;
        this.discountAppliesTo = discountAppliesTo;
        this.discountStartDate = discountStartDate;
        this.discountEndDate = discountEndDate;
        this.frequencyChanges = frequencyChanges;
        this.frequencyChangeAppliesTo = frequencyChangeAppliesTo;
        this.frequencyChangeStartDate = frequencyChangeStartDate;
        this.frequencyChangeEndDate = frequencyChangeEndDate;
    }

    public static class Tuple<X, Y> {
        public final X x;
        public final Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }
}
