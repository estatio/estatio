package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;

import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.LeaseItemType;

import lombok.Getter;

public enum LeaseAmendmentTemplate {

    COVID2_ITA_FREQ_CHANGE(
            LeaseAmendmentType.COVID_WAVE_2,
            new LocalDate(2020,12,1),
            null,
            null,
            null,
            null,
            null,
            null,
            Arrays.asList(
                    new Tuple<>(InvoicingFrequency.QUARTERLY_IN_ADVANCE, InvoicingFrequency.MONTHLY_IN_ADVANCE),
                    new Tuple<>(InvoicingFrequency.QUARTERLY_IN_ARREARS, InvoicingFrequency.MONTHLY_IN_ADVANCE),
                    new Tuple<>(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS1M, InvoicingFrequency.MONTHLY_IN_ADVANCE),
                    new Tuple<>(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS2M, InvoicingFrequency.MONTHLY_IN_ADVANCE)
            ),
            Arrays.asList(
                    LeaseItemType.RENT,
                    LeaseItemType.RENT_DISCOUNT,
                    LeaseItemType.RENT_DISCOUNT_FIXED,
                    LeaseItemType.SERVICE_CHARGE,
                    LeaseItemType.MARKETING
            ),
            new LocalDate(2021,1,1),
            new LocalDate(2021,3,31),
            "-FC2",
            new LocalDate(2020,10,1), // because of _PLUSM1 / _PLUSM2 calcs
            new LocalDate(2021,3,31),
            true),
    COVID_BEL(
            LeaseAmendmentType.COVID_WAVE_1,
            new LocalDate(2020,3,18), // min date because of lease selection
            new BigDecimal("50.00"),
            Arrays.asList(
                    LeaseItemType.RENT,
                    LeaseItemType.RENT_DISCOUNT
            ),
            new LocalDate(2020,3,18),
            new LocalDate(2020,5,10),
            Arrays.asList(
                    new Tuple<>(null, "BE2053") // DEFAULT
            ),
            new LocalDate(2020,7,1),
            null,
            null,
            null,
            null,
            "-A",
            new LocalDate(2020,1,1),
            new LocalDate(2020,12,31),
            false),
    COVID_FRA_50_PERC(
            LeaseAmendmentType.COVID_WAVE_1,
            new LocalDate(2020,3,16), // min date because of lease selection
            new BigDecimal("50.00"),
            Arrays.asList(
                    LeaseItemType.RENT,
                    LeaseItemType.RENT_DISCOUNT,
                    LeaseItemType.RENT_DISCOUNT_FIXED
            ),
            new LocalDate(2020,3,16),
            new LocalDate(2020,5,10),
            Arrays.asList(
                    new Tuple<>(null, "FR2054") // DEFAULT
            ),
            new LocalDate(2020,7,1),
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
            new LocalDate(2020,12,31),
            "-A50",
            new LocalDate(2020,1,1),
            new LocalDate(2020,12,31),
            false),
    COVID_FRA_100_PERC(
            LeaseAmendmentType.COVID_WAVE_1,
            new LocalDate(2020,4,1),
            new BigDecimal("100.00"),
            Arrays.asList(
                    LeaseItemType.RENT,
                    LeaseItemType.RENT_DISCOUNT,
                    LeaseItemType.RENT_DISCOUNT_FIXED
            ),
            new LocalDate(2020,4,1),
            new LocalDate(2020,6,30),
            Arrays.asList(
                    new Tuple<>(null, "FR2054") // DEFAULT
            ),
            new LocalDate(2020,7,1),
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
            new LocalDate(2020,12,31),
            "-A100",
            new LocalDate(2020,1,1),
            new LocalDate(2020,12,31),
            false),
    COVID_ITA_100_PERC_1M(
            LeaseAmendmentType.COVID_WAVE_1,
            new LocalDate(2020,7,1),
            new BigDecimal("100.00"),
            Arrays.asList(
                    LeaseItemType.RENT,
                    LeaseItemType.RENT_DISCOUNT
            ),
            new LocalDate(2020,7,1),
            new LocalDate(2020,7,31),
            Arrays.asList(
                    new Tuple<>("6001", "6014"),
                    new Tuple<>("6002", "6015"),
                    new Tuple<>("6031", "6014"), // current discount charge on any discount item
                    new Tuple<>("6032", "6015"), // current discount charge on any discount item
                    new Tuple<>(null, "6015") // TODO: check DEFAULT with users?
            ),
            new LocalDate(2020,7,1),
            null,
            null,
            null,
            null,
            "-A1M",
            new LocalDate(2020,4,1), // because of _PLUSM1 / _PLUSM2 calcs
            new LocalDate(2020,12,31),
            false),
    COVID_ITA_100_PERC_2M(
            LeaseAmendmentType.COVID_WAVE_1,
            new LocalDate(2020,7,1),
            new BigDecimal("100.00"),
            Arrays.asList(
                    LeaseItemType.RENT,
                    LeaseItemType.RENT_DISCOUNT
            ),
            new LocalDate(2020,7,1),
            new LocalDate(2020,8,31),
            Arrays.asList(
                    new Tuple<>("6001", "6014"),
                    new Tuple<>("6002", "6015"),
                    new Tuple<>("6031", "6014"), // current discount charge on any discount item
                    new Tuple<>("6032", "6015"), // current discount charge on any discount item
                    new Tuple<>(null, "6015") // TODO: check DEFAULT with users?
            ),
            new LocalDate(2020,7,1),
            null,
            null,
            null,
            null,
            "-A2M",
            new LocalDate(2020,4,1), // because of _PLUSM1 / _PLUSM2 calcs
            new LocalDate(2020,12,31),
            false),
    COVID_ITA_FREQ_CHANGE_ONLY(
            LeaseAmendmentType.COVID_WAVE_1,
            new LocalDate(2020,7,1),
            null,
            null,
            null,
            null,
            null,
            null,
            Arrays.asList(
                    new Tuple<>(InvoicingFrequency.QUARTERLY_IN_ADVANCE, InvoicingFrequency.MONTHLY_IN_ADVANCE),
                    new Tuple<>(InvoicingFrequency.QUARTERLY_IN_ARREARS, InvoicingFrequency.MONTHLY_IN_ADVANCE),
                    new Tuple<>(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS1M, InvoicingFrequency.MONTHLY_IN_ADVANCE),
                    new Tuple<>(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS2M, InvoicingFrequency.MONTHLY_IN_ADVANCE)
            ),
            Arrays.asList(
                    LeaseItemType.RENT,
                    LeaseItemType.RENT_DISCOUNT,
                    LeaseItemType.RENT_DISCOUNT_FIXED,
                    LeaseItemType.SERVICE_CHARGE,
                    LeaseItemType.MARKETING
            ),
            new LocalDate(2020,7,1),
            new LocalDate(2020,12,31),
            "-AF",
            new LocalDate(2020,4,1), // because of _PLUSM1 / _PLUSM2 calcs
            new LocalDate(2020,12,31),
            true),
    DEMO_TYPE(
            LeaseAmendmentType.COVID_WAVE_1,
            new LocalDate(2020,1,1),
            new BigDecimal("50"),
            Arrays.asList(LeaseItemType.RENT, LeaseItemType.RENT_DISCOUNT, LeaseItemType.RENT_DISCOUNT_FIXED),
            new LocalDate(2020,3,16),
            new LocalDate(2020,5,10),
            Arrays.asList(
                    new Tuple<>(null, "GBR_DISCOUNT")
            ),
            new LocalDate(2020,7,1),
            Arrays.asList(
                    new Tuple<>(InvoicingFrequency.QUARTERLY_IN_ADVANCE, InvoicingFrequency.MONTHLY_IN_ARREARS)
            ),
            Arrays.asList(LeaseItemType.RENT, LeaseItemType.SERVICE_CHARGE),
            new LocalDate(2020,7,1),
            new LocalDate(2020, 12, 31),
            "-DEM",
            new LocalDate(2020,1,1),
            new LocalDate(2020,12,31),
            false),
    DEMO_TYPE2(
            LeaseAmendmentType.COVID_WAVE_1,
            new LocalDate(2020,7,1),
            new BigDecimal("100"),
            Arrays.asList(LeaseItemType.RENT, LeaseItemType.RENT_DISCOUNT, LeaseItemType.RENT_DISCOUNT_FIXED),
            new LocalDate(2020,7,1),
            new LocalDate(2020,8,31),
            Arrays.asList(
                    new Tuple<>(null, "GBR_DISCOUNT")
            ),
            new LocalDate(2020,7,1),
            Arrays.asList(
                    new Tuple<>(InvoicingFrequency.QUARTERLY_IN_ADVANCE, InvoicingFrequency.MONTHLY_IN_ADVANCE)
            ),
            Arrays.asList(LeaseItemType.RENT, LeaseItemType.SERVICE_CHARGE),
            new LocalDate(2020,7,1),
            new LocalDate(2020, 12, 31),
            "-DEM2",
            new LocalDate(2020,7,1),
            new LocalDate(2020,12,31),
            false)
    ;

    @Getter
    private final LeaseAmendmentType leaseAmendmentType;

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
    private final List<Tuple<String, String>> chargeReferenceForDiscountItem;

    @Getter
    private final LocalDate minimalAmortisationReferenceDate;

    @Getter
    private final List<Tuple<InvoicingFrequency, InvoicingFrequency>> frequencyChanges;

    @Getter
    private final List<LeaseItemType> frequencyChangeAppliesTo;

    @Getter
    private final LocalDate frequencyChangeStartDate;

    @Getter
    private final LocalDate frequencyChangeEndDate;

    @Getter
    private String ref_suffix;

    @Getter
    private final LocalDate previewInvoicingStartDate;

    @Getter
    private final LocalDate previewInvoicingEndDate;

    @Getter
    private Boolean allowsBulkApply;

    LeaseAmendmentTemplate(
            final LeaseAmendmentType leaseAmendmentType,
            final LocalDate amendmentStartDate,
            final BigDecimal discountPercentage,
            final List<LeaseItemType> discountAppliesTo,
            final LocalDate discountStartDate,
            final LocalDate discountEndDate,
            final List<Tuple<String, String>> chargeReferenceForDiscountItem,
            final LocalDate minimalAmortisationReferenceDate,
            final List<Tuple<InvoicingFrequency, InvoicingFrequency>> frequencyChanges,
            final List<LeaseItemType> frequencyChangeAppliesTo,
            final LocalDate frequencyChangeStartDate,
            final LocalDate frequencyChangeEndDate,
            final String ref_suffix,
            final LocalDate previewInvoicingStartDate,
            final LocalDate previewInvoicingEndDate,
            final boolean allowsBulkApply) {
        this.leaseAmendmentType = leaseAmendmentType;
        this.amendmentStartDate = amendmentStartDate;
        this.discountPercentage = discountPercentage;
        this.discountAppliesTo = discountAppliesTo;
        this.discountStartDate = discountStartDate;
        this.discountEndDate = discountEndDate;
        this.chargeReferenceForDiscountItem = chargeReferenceForDiscountItem;
        this.minimalAmortisationReferenceDate = minimalAmortisationReferenceDate;
        this.frequencyChanges = frequencyChanges;
        this.frequencyChangeAppliesTo = frequencyChangeAppliesTo;
        this.frequencyChangeStartDate = frequencyChangeStartDate;
        this.frequencyChangeEndDate = frequencyChangeEndDate;
        this.ref_suffix = ref_suffix;
        this.previewInvoicingStartDate = previewInvoicingStartDate;
        this.previewInvoicingEndDate = previewInvoicingEndDate;
        this.allowsBulkApply = allowsBulkApply;
    }

    public static class Tuple<X, Y> {
        public final X oldValue;
        public final Y newValue;
        public Tuple(X oldValue, Y newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }
}
