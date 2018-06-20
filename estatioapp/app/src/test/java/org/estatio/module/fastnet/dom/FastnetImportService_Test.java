package org.estatio.module.fastnet.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForFixed;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.dom.LeaseTermForTesting;

import static org.assertj.core.api.Assertions.assertThat;

public class FastnetImportService_Test {

    @Test
    public void mapPartionalKontraktNr() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();

        // when
        String number = "1234-4567-02";

        // then
        assertThat(service.mapPartialExternalReference(number)).isEqualTo("1234-4567");

    }

    @Test
    public void reference_comparison_test() throws Exception {

        final String externalReference1 = "1234-5678-12";
        final String externalReference2 = "1234-5678-13";
        final String externalReference3 = "1234-5678-01";
        final String externalReference4 = "1234-5678-02";

        assertThat(externalReference1.compareTo(externalReference2)).isLessThan(0);
        assertThat(externalReference3.compareTo(externalReference4)).isLessThan(0);
        assertThat(externalReference3.compareTo(externalReference1)).isLessThan(0);

    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Test
    public void find_or_create_works_when_item_not_found() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        LeaseItem leaseItem = new LeaseItem();
        LeaseItemType itemType = LeaseItemType.RENT_FIXED;
        Charge charge = new Charge();
        InvoicingFrequency frequency = InvoicingFrequency.QUARTERLY_IN_ADVANCE;
        LocalDate startdate = new LocalDate(2018, 01, 01);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLease).findItemsOfType(LeaseItemType.RENT_FIXED);
            will(Expectations.returnValue(Arrays.asList()));
            oneOf(mockLease).newItem(itemType, LeaseAgreementRoleTypeEnum.LANDLORD, charge, frequency, PaymentMethod.BANK_TRANSFER, startdate);
            will(Expectations.returnValue(leaseItem));
        }});

        // when
        service.findOrCreateLeaseItemForTypeAndCharge(mockLease, itemType, charge, frequency, startdate);

        // then
        assertThat(leaseItem.getEndDate()).isEqualTo(null);

    }

    @Test
    public void find_or_create_works_when_one_item_is_found() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        Charge charge = new Charge();
        LeaseItem leaseItem = new LeaseItem();
        leaseItem.setCharge(charge);
        LocalDate startdate = new LocalDate(2018, 01, 01);
        leaseItem.setStartDate(startdate);
        LeaseItemType itemType = LeaseItemType.RENT_FIXED;
        InvoicingFrequency frequency = InvoicingFrequency.QUARTERLY_IN_ADVANCE;

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLease).findItemsOfType(LeaseItemType.RENT_FIXED);
            will(Expectations.returnValue(Arrays.asList(leaseItem)));
        }});

        // when
        service.findOrCreateLeaseItemForTypeAndCharge(mockLease, itemType, charge, frequency, startdate);

    }

    @Test
    public void find_or_create_works_when_more_than_one_item_is_found() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        Charge charge = new Charge();
        charge.setReference("CH_REF");
        LocalDate startdate = new LocalDate(2018, 01, 01);
        LeaseItem leaseItem1 = new LeaseItem();
        LeaseItem leaseItem2 = new LeaseItem();
        leaseItem1.setCharge(charge);
        leaseItem2.setCharge(charge);
        leaseItem1.setStartDate(startdate);
        leaseItem2.setStartDate(startdate);
        LeaseItemType itemType = LeaseItemType.RENT_FIXED;
        InvoicingFrequency frequency = InvoicingFrequency.QUARTERLY_IN_ADVANCE;

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLease).findItemsOfType(LeaseItemType.RENT_FIXED);
            will(Expectations.returnValue(Arrays.asList(leaseItem1, leaseItem2)));
            oneOf(mockLease).getReference();
            will(Expectations.returnValue("LEASE_REF"));
        }});

        // then
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Multiple lease items of type RENT_FIXED and charge CH_REF found for lease LEASE_REF");

        // when
        service.findOrCreateLeaseItemForTypeAndCharge(mockLease, itemType, charge, frequency, startdate);
    }

    @Mock
    ChargeRepository mockChargeRepository;

    @Test
    public void same_dates_works() throws Exception {

        FastnetImportService service = new FastnetImportService();
        service.chargeRepository = mockChargeRepository;
        FastNetChargingOnLeaseDataLine cdl = new FastNetChargingOnLeaseDataLine();
        cdl.setLeaseTermStartDate(new LocalDate(2018, 1, 1));
        //when, then
        assertThat(service.sameDates(cdl)).isFalse();

        // and when
        cdl.setFromDat("2018-01-01");
        cdl.setTomDat("2019-01-01");

        // expect
        context.checking(new Expectations() {{
            oneOf(mockChargeRepository).findByReference(cdl.getKeyToChargeReference());
            will(returnValue(null));
        }});

        // then
        assertThat(service.sameDates(cdl)).isFalse();

        // and when
        cdl.setFromDat("2018-01-01");
        cdl.setTomDat(null);

        // then
        assertThat(service.sameDates(cdl)).isTrue();

        // and when
        cdl.setLeaseTermEndDate(new LocalDate(2019, 01, 01));
        cdl.setFromDat("2018-01-01");
        cdl.setTomDat("2019-01-01");

        // then
        assertThat(service.sameDates(cdl)).isTrue();
    }

    @Test
    public void samedate_exception_for_turnover_rent_fixed_works() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        service.chargeRepository = mockChargeRepository;
        Charge charge = new Charge();
        ChargeGroup group = new ChargeGroup();
        group.setReference("SE_TURNOVER_RENT_FIXED");
        charge.setGroup(group);

        FastNetChargingOnLeaseDataLine cdl = new FastNetChargingOnLeaseDataLine();
        cdl.setLeaseTermStartDate(new LocalDate(2018, 1, 1));
        cdl.setLeaseTermEndDate(new LocalDate(2018, 12, 31));

        // expect
        context.checking(new Expectations() {{
            oneOf(mockChargeRepository).findByReference(cdl.getKeyToChargeReference());
            will(returnValue(charge));
        }});

        // when
        cdl.setFromDat("2018-01-01");
        cdl.setTomDat(null);

        // then
        assertThat(service.sameDates(cdl)).isTrue();

    }

    @Test
    public void string_to_date_test() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();

        // when, then
        assertThat(service.stringToDate("2018-09-30")).isEqualTo(new LocalDate(2018, 9, 30));
        assertThat(service.stringToDate("2014-04-01")).isEqualTo(new LocalDate(2014, 4, 1));

    }

    @Test
    public void close_overlapping_existing_terms_works() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        LeaseItem leaseItem = new LeaseItem();
        LocalDate startDateNewTerm = new LocalDate(2018, 1, 1);
        LocalDate endDateNewTerm = new LocalDate(2018, 7, 1);

        LeaseTerm overlappingClosedTerm = new LeaseTermForIndexable();
        LeaseTerm overlappingOpenTerm = new LeaseTermForIndexable();
        LeaseTerm nonOverlappingOpenTerm = new LeaseTermForIndexable();

        overlappingClosedTerm.setStartDate(new LocalDate(2016, 1, 1));
        final LocalDate endDateOverlappingTerm = new LocalDate(2018, 4, 1);
        overlappingClosedTerm.setEndDate(endDateOverlappingTerm);

        overlappingOpenTerm.setStartDate(new LocalDate(2017, 1, 1));

        nonOverlappingOpenTerm.setStartDate(new LocalDate(2018, 7, 2));

        leaseItem.getTerms().addAll(Arrays.asList(overlappingClosedTerm, overlappingOpenTerm, nonOverlappingOpenTerm));

        // when
        service.closeOverlappingOpenEndedExistingTerms(leaseItem, startDateNewTerm, endDateNewTerm);

        // then
        assertThat(overlappingClosedTerm.getEndDate()).isEqualTo(endDateOverlappingTerm);
        assertThat(overlappingOpenTerm.getEndDate()).isEqualTo(startDateNewTerm.minusDays(1));
        assertThat(nonOverlappingOpenTerm.getEndDate()).isNull();

    }

    @Mock LeaseRepository mockLeaseRepository;

    @Test
    public void find_lease_by_external_reference_return_active_first_works() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        service.leaseRepository = mockLeaseRepository;
        Lease leaseActive = new Lease();
        leaseActive.setTenancyEndDate(service.EPOCH_DATE_FASTNET_IMPORT.plusDays(1));
        Lease leaseActive2 = new Lease();
        Lease leaseInactive = new Lease();
        leaseInactive.setTenancyEndDate(service.EPOCH_DATE_FASTNET_IMPORT);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseRepository).matchLeaseByExternalReference("1234-5678-01");
            will(Expectations.returnValue(Arrays.asList(leaseActive, leaseActive2, leaseInactive)));
        }});

        // when
        List<Lease> result = service.findLeaseByExternalReferenceReturnActiveFirst("1234-5678-01");

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(leaseActive);
        assertThat(result).contains(leaseActive2);
        assertThat(result).doesNotContain(leaseInactive);

    }

    @Test
    public void find_lease_by_external_reference_return_active_first_when_no_active_found_works() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        service.leaseRepository = mockLeaseRepository;
        Lease leaseInactive = new Lease();
        leaseInactive.setTenancyEndDate(service.EPOCH_DATE_FASTNET_IMPORT);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseRepository).matchLeaseByExternalReference("1234-5678-01");
            will(Expectations.returnValue(Arrays.asList(leaseInactive)));
        }});

        // when
        List<Lease> result = service.findLeaseByExternalReferenceReturnActiveFirst("1234-5678-01");

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).contains(leaseInactive);

    }

    @Mock ClockService mockClockService;

    @Test
    public void update_item_and_term_when_lease_not_found() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        service.leaseRepository = mockLeaseRepository;
        ChargingLine cLine = new ChargingLine();
        cLine.clockService = mockClockService;

        cLine.setFromDat("2018-01-01");
        cLine.setKeyToLeaseExternalReference("ABCD");

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseRepository).matchLeaseByExternalReference(cLine.getKeyToLeaseExternalReference());
            will(returnValue(Arrays.asList()));
            oneOf(mockClockService).nowAsLocalDateTime();
            will(returnValue(LocalDateTime.parse("2018-01-01")));
        }});

        // when
        service.updateOrCreateItemAndTerm(cLine);

        // then
        assertThat(cLine.getImportLog()).isEqualTo("2018-01-01 00:00:00 Lease with external reference ABCD not found.");

    }

    @Test
    public void update_item_and_term_when_charge_not_found() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        service.leaseRepository = mockLeaseRepository;
        service.chargeRepository = mockChargeRepository;
        ChargingLine cLine = new ChargingLine();
        cLine.clockService = mockClockService;
        cLine.setFromDat("2018-01-01");
        cLine.setKeyToLeaseExternalReference("ABCD");
        cLine.setKeyToChargeReference("SE123-4");

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseRepository).matchLeaseByExternalReference(cLine.getKeyToLeaseExternalReference());
            will(returnValue(Arrays.asList(new Lease())));
            oneOf(mockChargeRepository).findByReference(cLine.getKeyToChargeReference());
            will(returnValue(null));
            oneOf(mockClockService).nowAsLocalDateTime();
            will(returnValue(LocalDateTime.parse("2018-01-01")));
//            oneOf(mockMessageService).warnUser("Charge with reference SE123-4 not found for lease ABCD.");
        }});

        // when
        service.updateOrCreateItemAndTerm(cLine);

        // then
        assertThat(cLine.getImportLog()).isEqualTo("2018-01-01 00:00:00 Charge with reference SE123-4 not found for lease ABCD.");

    }

    @Mock
    Lease mockLease;

    @Mock
    LeaseItem mockLeaseItem;

    @Mock
    ChargingLineRepository mockChargingLineRepository;

    @Test
    public void update_item_and_term_when_frequency_not_found() throws Exception {

        // given
        LeaseTerm leaseTerm = new LeaseTermForIndexable();
        FastnetImportService service = new FastnetImportService(){
            @Override
            LeaseTerm findOrCreateTermToUpdate(final LeaseItem itemToUpdate, final ChargingLine cLine){
                return leaseTerm;
            }
        };
        service.chargingLineRepository = mockChargingLineRepository;
        service.leaseRepository = mockLeaseRepository;
        service.chargeRepository = mockChargeRepository;
        ChargingLine cLine = new ChargingLine();
        cLine.clockService = mockClockService;
        cLine.setKeyToLeaseExternalReference("ABCD");
        cLine.setKeyToChargeReference("SE123-4");
        cLine.setFromDat("2018-01-01");
        cLine.setDebPer("some_thing_not_recognized");
        Charge charge = new Charge();
        charge.setReference(cLine.getKeyToChargeReference());
        ChargeGroup chargeGroup = new ChargeGroup();
        chargeGroup.setReference("SE_RENT");
        charge.setGroup(chargeGroup);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockChargingLineRepository).findByKeyToLeaseExternalReferenceAndKeyToChargeReferenceAndExportDate(cLine.getKeyToLeaseExternalReference(), cLine.getKeyToChargeReference(), cLine.getExportDate());
            will(returnValue(Collections.emptyList()));
            oneOf(mockLeaseRepository).matchLeaseByExternalReference(cLine.getKeyToLeaseExternalReference());
            will(returnValue(Arrays.asList(mockLease)));
            oneOf(mockChargeRepository).findByReference(cLine.getKeyToChargeReference());
            will(returnValue(charge));
            oneOf(mockLease).findFirstItemOfTypeAndCharge(service.mapToLeaseItemType(charge), charge);
            will(returnValue(mockLeaseItem));
            oneOf(mockClockService).nowAsLocalDateTime();
            will(returnValue(LocalDateTime.parse("2018-01-01")));
//            oneOf(mockMessageService).warnUser("Value debPer some_thing_not_recognized could not be mapped to invoicing frequency for charge SE123-4 on lease ABCD.");
        }});

        // when
        service.updateOrCreateItemAndTerm(cLine);
        // then
        assertThat(cLine.getImportLog()).isEqualTo("2018-01-01 00:00:00 Value debPer some_thing_not_recognized could not be mapped to invoicing frequency for charge SE123-4 on lease ABCD.");


    }

    @Test
    public void update_item_and_term_when_value_not_found() throws Exception {

        // given
        LeaseTermForFixed lastTerm = new LeaseTermForFixed();
        lastTerm.setEndDate(new LocalDate(2018, 12, 31));
        LeaseTermForFixed leaseTerm = new LeaseTermForFixed();
        FastnetImportService service = new FastnetImportService(){
            @Override
            LeaseTerm findOrCreateTermToUpdate(final LeaseItem itemToUpdate, final ChargingLine cLine){
                return leaseTerm;
            }
        };
        service.chargingLineRepository = mockChargingLineRepository;
        service.leaseRepository = mockLeaseRepository;
        service.chargeRepository = mockChargeRepository;
        ChargingLine cLine = new ChargingLine();
        cLine.setKeyToLeaseExternalReference("ABCD");
        cLine.setKeyToChargeReference("SE123-4");
        cLine.setFromDat("2018-01-01");
        cLine.setTomDat("2018-12-31");
        cLine.setDebPer("Månad");
        Charge charge = new Charge();
        charge.setReference(cLine.getKeyToChargeReference());
        ChargeGroup chargeGroup = new ChargeGroup();
        chargeGroup.setReference("SE_RENT_FIXED");
        charge.setGroup(chargeGroup);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockChargingLineRepository).findByKeyToLeaseExternalReferenceAndKeyToChargeReferenceAndExportDate(cLine.getKeyToLeaseExternalReference(), cLine.getKeyToChargeReference(), cLine.getExportDate());
            will(returnValue(Collections.emptyList()));
            oneOf(mockLeaseRepository).matchLeaseByExternalReference(cLine.getKeyToLeaseExternalReference());
            will(returnValue(Arrays.asList(mockLease)));
            oneOf(mockChargeRepository).findByReference(cLine.getKeyToChargeReference());
            will(returnValue(charge));
            oneOf(mockLease).findFirstItemOfTypeAndCharge(service.mapToLeaseItemType(charge), charge);
            will(returnValue(mockLeaseItem));
            oneOf(mockLeaseItem).setEndDate(new LocalDate(2018, 12, 31));
            oneOf(mockLeaseItem).setInvoicingFrequency(InvoicingFrequency.MONTHLY_IN_ADVANCE);
            oneOf(mockLeaseItem).getTerms();
            will(returnValue(new TreeSet<>(Arrays.asList(lastTerm))));
            oneOf(mockLeaseItem).getType();
            will(returnValue(LeaseItemType.RENT_FIXED));
        }});

        // when
        service.updateOrCreateItemAndTerm(cLine);

        // then
        assertThat(leaseTerm.getValue()).isEqualTo(BigDecimal.ZERO.setScale(2));
    }

    @Test
    public void update_item_and_term_when_all_is_fine() throws Exception {

        // given
        LeaseTermForFixed lastTerm = new LeaseTermForFixed();
        lastTerm.setEndDate(new LocalDate(2018, 12, 31));
        LeaseTermForFixed leaseTerm = new LeaseTermForFixed();
        FastnetImportService service = new FastnetImportService(){
            @Override
            LeaseTerm findOrCreateTermToUpdate(final LeaseItem itemToUpdate, final ChargingLine cLine){
                return leaseTerm;
            }
        };
        service.chargingLineRepository = mockChargingLineRepository;
        service.leaseRepository = mockLeaseRepository;
        service.chargeRepository = mockChargeRepository;
        ChargingLine cLine = new ChargingLine();
        cLine.setKeyToLeaseExternalReference("ABCD");
        cLine.setKeyToChargeReference("SE123-4");
        cLine.setFromDat("2018-01-01");
        cLine.setTomDat("2018-12-31");
        cLine.setDebPer("Månad");
        final BigDecimal arsBel = new BigDecimal("1234.56");
        cLine.setArsBel(arsBel);
        Charge charge = new Charge();
        charge.setReference(cLine.getKeyToChargeReference());
        ChargeGroup chargeGroup = new ChargeGroup();
        chargeGroup.setReference("SE_RENT_FIXED");
        charge.setGroup(chargeGroup);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockChargingLineRepository).findByKeyToLeaseExternalReferenceAndKeyToChargeReferenceAndExportDate(cLine.getKeyToLeaseExternalReference(), cLine.getKeyToChargeReference(), cLine.getExportDate());
            will(returnValue(Collections.emptyList()));
            oneOf(mockLeaseRepository).matchLeaseByExternalReference(cLine.getKeyToLeaseExternalReference());
            will(returnValue(Arrays.asList(mockLease)));
            oneOf(mockChargeRepository).findByReference(cLine.getKeyToChargeReference());
            will(returnValue(charge));
            oneOf(mockLease).findFirstItemOfTypeAndCharge(service.mapToLeaseItemType(charge), charge);
            will(returnValue(mockLeaseItem));
            oneOf(mockLeaseItem).getType();
            will(returnValue(LeaseItemType.RENT_FIXED));
            oneOf(mockLeaseItem).setEndDate(new LocalDate(2018, 12, 31));
            oneOf(mockLeaseItem).setInvoicingFrequency(InvoicingFrequency.MONTHLY_IN_ADVANCE);
            oneOf(mockLeaseItem).getTerms();
            will(returnValue(new TreeSet<>(Arrays.asList(lastTerm))));
        }});

        // when
        service.updateOrCreateItemAndTerm(cLine);

        // then
        assertThat(leaseTerm.getValue()).isEqualTo(arsBel);
    }

    @Test
    public void create_item_and_term_when_lease_not_found() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        service.leaseRepository = mockLeaseRepository;
        ChargingLine cLine = new ChargingLine();
        cLine.clockService = mockClockService;
        cLine.setFromDat("2018-01-01");
        cLine.setKeyToLeaseExternalReference("ABCD");

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseRepository).matchLeaseByExternalReference(cLine.getKeyToLeaseExternalReference());
            will(returnValue(Arrays.asList()));
            oneOf(mockClockService).nowAsLocalDateTime();
            will(returnValue(LocalDateTime.parse("2018-01-01")));

//            oneOf(mockMessageService).warnUser("Lease with external reference ABCD not found.");
        }});

        // when
        service.updateOrCreateItemAndTerm(cLine);
        // then
        assertThat(cLine.getImportLog()).isEqualTo("2018-01-01 00:00:00 Lease with external reference ABCD not found.");

    }

    @Test
    public void create_item_and_term_when_charge_not_found() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        service.leaseRepository = mockLeaseRepository;
        service.chargeRepository = mockChargeRepository;
        ChargingLine cLine = new ChargingLine();
        cLine.clockService = mockClockService;
        cLine.setFromDat("2018-01-01");
        cLine.setKeyToLeaseExternalReference("ABCD");
        cLine.setKeyToChargeReference("SE123-4");

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseRepository).matchLeaseByExternalReference(cLine.getKeyToLeaseExternalReference());
            will(returnValue(Arrays.asList(new Lease())));
            oneOf(mockChargeRepository).findByReference(cLine.getKeyToChargeReference());
            will(returnValue(null));
            oneOf(mockClockService).nowAsLocalDateTime();
            will(returnValue(LocalDateTime.parse("2018-01-01")));
//            oneOf(mockMessageService).warnUser("Charge with reference SE123-4 not found for lease ABCD.");
        }});

        // when
        service.updateOrCreateItemAndTerm(cLine);
        // then
        assertThat(cLine.getImportLog()).isEqualTo("2018-01-01 00:00:00 Charge with reference SE123-4 not found for lease ABCD.");

    }

    @Test
    public void update_lease_term_value_works_for_service_charge() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        LeaseItemType type = LeaseItemType.SERVICE_CHARGE;
        LeaseTermForServiceCharge term = new LeaseTermForServiceCharge();
        BigDecimal amount = new BigDecimal("123.45");

        // when
        service.updateLeaseTermValue(type, amount, term);

        // then
        assertThat(term.getBudgetedValue()).isEqualTo(amount);

    }

    @Test
    public void update_lease_term_value_works_for_fixed() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        LeaseItemType type = LeaseItemType.RENT_FIXED;
        LeaseTermForFixed term = new LeaseTermForFixed();
        BigDecimal amount = new BigDecimal("123.45");

        // when
        service.updateLeaseTermValue(type, amount, term);

        // then
        assertThat(term.getValue()).isEqualTo(amount);

    }

    @Test
    public void close_all_items_of_type_active_on_epoch_date_works() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        final LocalDate epochDateFastnetImport = service.EPOCH_DATE_FASTNET_IMPORT;

        LeaseItemType leaseItemType = LeaseItemType.RENT_FIXED;
        Lease lease = new Lease();

        LeaseItem itemToBeClosed = new LeaseItem();
        itemToBeClosed.setSequence(BigInteger.valueOf(1));
        itemToBeClosed.setType(leaseItemType);
        itemToBeClosed.setEndDate(epochDateFastnetImport);
        lease.getItems().add(itemToBeClosed);

        LeaseItem itemNotToBeClosed = new LeaseItem();
        itemNotToBeClosed.setSequence(BigInteger.valueOf(2));
        itemNotToBeClosed.setType(leaseItemType);
        itemNotToBeClosed.setStartDate(epochDateFastnetImport);
        lease.getItems().add(itemNotToBeClosed);

        LeaseItem itemClosedInPast = new LeaseItem();
        itemClosedInPast.setSequence(BigInteger.valueOf(3));
        itemClosedInPast.setType(leaseItemType);
        itemClosedInPast.setEndDate(epochDateFastnetImport.minusDays(2));
        lease.getItems().add(itemClosedInPast);

        // when
        service.closeAllItemsOfTypeActiveOnEpochDate(lease, leaseItemType);

        // then
        assertThat(itemToBeClosed.getEndDate()).isEqualTo(epochDateFastnetImport.minusDays(1));
        assertThat(itemNotToBeClosed.getEndDate()).isNull();
        assertThat(itemClosedInPast.getEndDate()).isEqualTo(epochDateFastnetImport.minusDays(2));

    }

    @Test
    public void handle_ChargingLines_With_Same_Charge_works_when_no_overlap() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService() {

            int order = 1; // used to check order

            @Override
            ImportStatus createItemAndTerm(final ChargingLine cLine, final Lease lease, final Charge charge) {
                cLine.setImportStatus(ImportStatus.LEASE_ITEM_CREATED);
                cLine.setEnhetAndr(order); // used to check order
                order++;
                return ImportStatus.LEASE_ITEM_CREATED;
            }
        };
        Lease lease = mockLease;
        Charge charge = new Charge();
        ChargeGroup group = new ChargeGroup();
        group.setReference("SE_RENT");
        charge.setGroup(group);

        ChargingLine line1 = new ChargingLine();
        line1.setFromDat("2017-1-1");
        line1.setTomDat("2017-12-31");
        ChargingLine line2 = new ChargingLine();
        line2.setFromDat("2018-1-1");
        line2.setTomDat("2018-06-20");
        ChargingLine line3 = new ChargingLine();
        line3.setFromDat("2018-7-1");

        List<ChargingLine> linesWithSameCharge = Arrays.asList(line2, line3, line1); // order is shifted

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLease).findFirstItemOfTypeAndCharge(LeaseItemType.RENT_FIXED, charge);
            will(returnValue(null));
        }});

        // when
        ImportStatus result = service.handleChargingLinesWithSameCharge(linesWithSameCharge, lease, charge);

        // then
        assertThat(result).isEqualTo(ImportStatus.LEASE_ITEM_CREATED);
        // set by mock method overriding to check order
        assertThat(line1.getImportStatus()).isEqualTo(ImportStatus.LEASE_ITEM_CREATED);
        assertThat(line1.getEnhetAndr()).isEqualTo(1);
        assertThat(line2.getImportStatus()).isEqualTo(ImportStatus.LEASE_ITEM_CREATED);
        assertThat(line2.getEnhetAndr()).isEqualTo(2);
        assertThat(line3.getImportStatus()).isEqualTo(ImportStatus.LEASE_ITEM_CREATED);
        assertThat(line3.getEnhetAndr()).isEqualTo(3);

    }

    @Test
    public void handle_ChargingLines_With_Same_Charge_works_when_no_start_date() throws Exception {

        FastnetImportService service = new FastnetImportService();
        Lease lease = new Lease();
        Charge charge = new Charge();
        ChargeGroup group = new ChargeGroup();
        group.setReference("SE_RENT");
        charge.setGroup(group);

        ChargingLine line1 = new ChargingLine();
        line1.clockService = mockClockService;
        line1.setTomDat("2017-12-31");
        line1.setKeyToLeaseExternalReference("ABCD");
        line1.setKeyToChargeReference("SE123-4");
        ChargingLine line2 = new ChargingLine();
        line2.clockService = mockClockService;
        line2.setFromDat("2017-01-01");

        List<ChargingLine> linesWithSameCharge = Arrays.asList(line1, line2);

        // expect
        context.checking(new Expectations() {{
            allowing(mockClockService).nowAsLocalDateTime();
            will(returnValue(LocalDateTime.parse("2018-01-01")));
//            oneOf(mockMessageService).warnUser("Charging line for lease ABCD with charge SE123-4 has no start date (fromdat) while also multiple lines with this charge found. Please handle manually.");
        }});

        // when
        service.handleChargingLinesWithSameCharge(linesWithSameCharge, lease, charge);
        // then
        assertThat(line1.getImportLog()).isEqualTo("2018-01-01 00:00:00 Charging line for lease ABCD with charge SE123-4 has no start date (fromdat) while also multiple lines with this charge found. Please handle manually.");
        assertThat(line2.getImportLog()).isNull();

    }

    @Test
    public void handle_ChargingLines_With_Same_Charge_works_when_overlap_cannot_be_handled() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        Lease lease = new Lease();
        Charge charge = new Charge();
        ChargeGroup group = new ChargeGroup();
        group.setReference("SE_RENT");
        charge.setGroup(group);

        ChargingLine line1 = new ChargingLine();
        line1.clockService = mockClockService;
        line1.setFromDat("2017-1-1");
        line1.setTomDat("2017-12-31");
        line1.setKeyToLeaseExternalReference("ABCD");
        line1.setKeyToChargeReference("SE123-4");
        ChargingLine line2 = new ChargingLine();
        line2.clockService = mockClockService;
        line2.setFromDat("2017-12-31");
        line2.setTomDat("2018-06-20");

        List<ChargingLine> linesWithSameCharge = Arrays.asList(line2, line1);

        // expect
        context.checking(new Expectations() {{
            allowing(mockClockService).nowAsLocalDateTime();
            will(returnValue(LocalDateTime.parse("2018-01-01")));
//            oneOf(mockMessageService).warnUser("Multiple lines for lease ABCD with charge SE123-4 found that could not be aggregated. Please handle manually.");
        }});

        // when
        service.handleChargingLinesWithSameCharge(linesWithSameCharge, lease, charge);
        // then
        assertThat(line1.getImportLog()).isEqualTo("2018-01-01 00:00:00 Multiple lines for lease ABCD with charge SE123-4 found that could not be aggregated. Please handle manually.");
        assertThat(line2.getImportLog()).isEqualTo("2018-01-01 00:00:00 Multiple lines for lease ABCD with charge SE123-4 found that could not be aggregated. Please handle manually.");

    }

    @Test
    public void handle_ChargingLines_With_Same_Charge_works_when_same_fromdat_and_no_tomdat() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        service.chargingLineRepository = mockChargingLineRepository;
        Lease lease = new Lease();
        Charge charge = new Charge();
        ChargeGroup group = new ChargeGroup();
        group.setReference("SE_RENT");
        charge.setGroup(group);

        ChargingLine line1 = new ChargingLine();
        line1.setFromDat("2016-1-1");
        line1.setArsBel(new BigDecimal("100.00"));
        line1.setKeyToLeaseExternalReference("ABCD");
        line1.setKeyToChargeReference("SE123-4");
        ChargingLine line2 = new ChargingLine();
        line2.setFromDat("2017-1-1");
        line2.setArsBel(new BigDecimal("23.45"));

        List<ChargingLine> linesWithSameCharge = Arrays.asList(line2, line1);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockChargingLineRepository).persist(with(any(ChargingLine.class)));
        }});

        // when
        service.handleChargingLinesWithSameCharge(linesWithSameCharge, lease, charge);

        assertThat(line1.getImportStatus()).isEqualTo(ImportStatus.AGGREGATED);
        assertThat(line2.getImportStatus()).isEqualTo(ImportStatus.AGGREGATED);

    }

    @Test
    public void handle_ChargingLines_With_Same_Charge_works_when_same_tomdat() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        service.chargingLineRepository = mockChargingLineRepository;
        Lease lease = new Lease();
        Charge charge = new Charge();
        ChargeGroup group = new ChargeGroup();
        group.setReference("SE_RENT");
        charge.setGroup(group);

        ChargingLine line1 = new ChargingLine();
        line1.setFromDat("2016-1-1");
        line1.setTomDat("2017-7-1");
        line1.setArsBel(new BigDecimal("100.00"));
        line1.setKeyToLeaseExternalReference("ABCD");
        line1.setKeyToChargeReference("SE123-4");
        ChargingLine line2 = new ChargingLine();
        line2.setFromDat("2017-1-1");
        line2.setTomDat("2017-7-1");
        line2.setArsBel(new BigDecimal("23.45"));

        List<ChargingLine> linesWithSameCharge = Arrays.asList(line2, line1);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockChargingLineRepository).persist(with(any(ChargingLine.class)));
        }});

        // when
        service.handleChargingLinesWithSameCharge(linesWithSameCharge, lease, charge);

        assertThat(line1.getImportStatus()).isEqualTo(ImportStatus.AGGREGATED);
        assertThat(line2.getImportStatus()).isEqualTo(ImportStatus.AGGREGATED);

    }

    @Test
    public void summedArsBel_works() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        ChargingLine line1 = new ChargingLine();
        line1.setArsBel(new BigDecimal("100.00"));
        ChargingLine line2 = new ChargingLine();
        line2.setArsBel(new BigDecimal("23.45"));

        List<ChargingLine> lines = Arrays.asList(line2, line1);

        // when, then
        assertThat(service.summedArsBel(lines)).isEqualTo(new BigDecimal("123.45"));

    }

    @Test
    public void getMinFromDatAsString_works() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        ChargingLine line1 = new ChargingLine();
        line1.setFromDat("2017-01-01");
        ChargingLine line2 = new ChargingLine();
        line2.setFromDat("2017-01-02");

        List<ChargingLine> lines = Arrays.asList(line2, line1);

        // when, then
        assertThat(service.getMinFromDatAsString(lines)).isEqualTo("2017-01-01");

    }

    @Test
    public void getMaxTomDatAsString_works() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        ChargingLine line1 = new ChargingLine();
        line1.setTomDat("2017-01-01");
        ChargingLine line2 = new ChargingLine();
        line2.setTomDat("2017-01-02");

        List<ChargingLine> lines = Arrays.asList(line2, line1);

        // when, then
        assertThat(service.getMaxTomDatAsString(lines)).isEqualTo("2017-01-02");

    }

    @Test
    public void chargingLineMustHaveFromDat() throws Exception {
        // given
        FastnetImportService service = new FastnetImportService();
        service.leaseRepository = mockLeaseRepository;
        ChargingLine cLine = new ChargingLine();
        cLine.clockService = mockClockService;
        cLine.setKeyToChargeReference("SE123-1");
        cLine.setKeyToLeaseExternalReference("ABCD");

        // expect
        context.checking(new Expectations() {{
            oneOf(mockClockService).nowAsLocalDateTime();
            will(returnValue(LocalDateTime.parse("2018-01-01")));
//            oneOf(mockMessageService).warnUser("Charging line for lease ABCD with charge SE123-1 has no start date (fromdat).");
        }});

        // when
        service.updateOrCreateItemAndTerm(cLine);
        // then
        assertThat(cLine.getImportLog()).isEqualTo("2018-01-01 00:00:00 Charging line for lease ABCD with charge SE123-1 has no start date (fromdat).");

    }

    @Test
    public void findOrCreateTermToUpdate_works_when_no_terms_on_item() throws Exception {

        // given
        FastnetImportService service = new FastnetImportService();
        LeaseItem item = mockLeaseItem;
        final LocalDate expectedStartDate = new LocalDate(2017, 1, 1);
        final LocalDate expectedEndDate = new LocalDate(2017, 6, 30);
        ChargingLine cLine = new ChargingLine();
        cLine.setFromDat("2017-01-01");
        cLine.setTomDat("2017-06-30");

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseItem).getTerms();
            will(returnValue(Collections.emptySortedSet()));
            oneOf(mockLeaseItem).newTerm(expectedStartDate, expectedEndDate);
        }});

        // when
        service.findOrCreateTermToUpdate(item, cLine);

    }

    @Test
    public void deriveTermToUpdateFromLastTerm_works_when_term_interval_open_ended() throws Exception {

        // given
        LeaseItem item = mockLeaseItem;
        LeaseTerm lastTerm = new LeaseTermForTesting();
        lastTerm.setLeaseItem(item);

        ChargingLine cLine = new ChargingLine();

        LocalDate expectedLastTermEndDate;

        // no new term scenario's ////////////////////////////////////////////////////////////////
        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), null);
        initDatesChargingLine(cLine, new LocalDate(2017, 1, 1), null);
        testWhenNoNewTermIsCreated(lastTerm, cLine, "2017-01-01/----------");

        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), null);
        initDatesChargingLine(cLine, new LocalDate(2016, 12, 31), null);
        testWhenNoNewTermIsCreated(lastTerm, cLine, "2017-01-01/----------");

        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), null);
        initDatesChargingLine(cLine, new LocalDate(2017, 1, 1), new LocalDate(2017, 6, 30));
        testWhenNoNewTermIsCreated(lastTerm, cLine, "2017-01-01/2017-07-01");

        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), null);
        initDatesChargingLine(cLine, new LocalDate(2016, 12, 31), new LocalDate(2017, 6, 30));
        testWhenNoNewTermIsCreated(lastTerm, cLine, "2017-01-01/2017-07-01");

        // new term scenario's ////////////////////////////////////////////////////////////////
        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), null);
        initDatesChargingLine(cLine, new LocalDate(2017,2,1), null);
        expectedLastTermEndDate = new LocalDate(2017, 1, 31);
        testWhenNewTermIsCreated(lastTerm, cLine, expectedLastTermEndDate);

        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), null);
        initDatesChargingLine(cLine, new LocalDate(2017,2,1), new LocalDate(2017, 6, 30));
        expectedLastTermEndDate = new LocalDate(2017, 1, 31);
        testWhenNewTermIsCreated(lastTerm, cLine, expectedLastTermEndDate);


        // no overlap, cLine interval before term ////////////////////////////////////////////////////////////////
        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), null);
        initDatesChargingLine(cLine, new LocalDate(2016,1,1), new LocalDate(2016, 12, 31));
        testWhenNoOverlapAndClineBeforeLastTerm(lastTerm, cLine);

    }

    @Test
    public void deriveTermToUpdateFromLastTerm_works_when_term_interval_closed() throws Exception {

        // given
        LeaseItem item = mockLeaseItem;
        LeaseTerm lastTerm = new LeaseTermForTesting();
        lastTerm.setLeaseItem(item);

        ChargingLine cLine = new ChargingLine();

        LocalDate expectedLastTermEndDate;

        // no new term scenario's ////////////////////////////////////////////////////////////////
        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), new LocalDate(2017,4,30));
        initDatesChargingLine(cLine, new LocalDate(2017, 1, 1), null);
        testWhenNoNewTermIsCreated(lastTerm, cLine, "2017-01-01/----------");

        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), new LocalDate(2017,4,30));
        initDatesChargingLine(cLine, new LocalDate(2016, 12, 31), null);
        testWhenNoNewTermIsCreated(lastTerm, cLine, "2017-01-01/----------");

        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), new LocalDate(2017,4,30));
        initDatesChargingLine(cLine, new LocalDate(2017, 1, 1), new LocalDate(2017, 6, 30));
        testWhenNoNewTermIsCreated(lastTerm, cLine, "2017-01-01/2017-07-01");

        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), new LocalDate(2017,4,30));
        initDatesChargingLine(cLine, new LocalDate(2016, 12, 31), new LocalDate(2017, 6, 30));
        testWhenNoNewTermIsCreated(lastTerm, cLine, "2017-01-01/2017-07-01");

        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), new LocalDate(2017,4,30));
        initDatesChargingLine(cLine, new LocalDate(2017, 1, 1), new LocalDate(2017, 4, 1));
        testWhenNoNewTermIsCreated(lastTerm, cLine, "2017-01-01/2017-05-01");

        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), new LocalDate(2017,4,30));
        initDatesChargingLine(cLine, new LocalDate(2016, 12, 31), new LocalDate(2017, 4, 1));
        testWhenNoNewTermIsCreated(lastTerm, cLine, "2017-01-01/2017-05-01");

        // new term scenario's ////////////////////////////////////////////////////////////////
        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), new LocalDate(2017,4,30));
        initDatesChargingLine(cLine, new LocalDate(2017,2,1), null);
        expectedLastTermEndDate = new LocalDate(2017, 1, 31);
        testWhenNewTermIsCreated(lastTerm, cLine, expectedLastTermEndDate);

        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), new LocalDate(2017,4,30));
        initDatesChargingLine(cLine, new LocalDate(2017,2,1), new LocalDate(2017, 6, 30));
        expectedLastTermEndDate = new LocalDate(2017, 1, 31);
        testWhenNewTermIsCreated(lastTerm, cLine, expectedLastTermEndDate);

        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), new LocalDate(2017,4,30));
        initDatesChargingLine(cLine, new LocalDate(2017,2,1), new LocalDate(2017, 4, 1));
        expectedLastTermEndDate = new LocalDate(2017, 1, 31);
        LocalDate expectedResultEndDate = lastTerm.getEndDate();
        testWhenNewTermIsCreatedAndClineEndDateBeforeTermEndDate(lastTerm, cLine, expectedLastTermEndDate, expectedResultEndDate);

        // no overlap, cLine interval before term ////////////////////////////////////////////////////////////////
        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), new LocalDate(2017,4,30));
        initDatesChargingLine(cLine, new LocalDate(2016,1,1), new LocalDate(2016, 12, 31));
        testWhenNoOverlapAndClineBeforeLastTerm(lastTerm, cLine);

        // no overlap, cLine interval after term ////////////////////////////////////////////////////////////////
        initDatesLastTerm(lastTerm, new LocalDate(2017,1,1), new LocalDate(2017,4,30));
        initDatesChargingLine(cLine, new LocalDate(2017,5,15), new LocalDate(2017, 12, 31));
        expectedLastTermEndDate = lastTerm.getEndDate();
        testWhenNoOverlapAndClineAfterLastTerm(lastTerm, cLine, expectedLastTermEndDate);

    }

    LeaseTerm initDatesLastTerm(LeaseTerm lastTerm, final LocalDate termStartDate, final LocalDate termEndDate){
        lastTerm.setStartDate(termStartDate);
        lastTerm.setEndDate(termEndDate);
        return lastTerm;
    }

    ChargingLine initDatesChargingLine(ChargingLine cLine, final LocalDate clineFromDat, final LocalDate clineTomdat){
        cLine.setFromDat(clineFromDat.toString("yyyy-MM-dd"));
        cLine.setTomDat(clineTomdat == null ? null : clineTomdat.toString("yyyy-MM-dd"));
        return cLine;
    }


    void testWhenNoNewTermIsCreated(LeaseTerm lastTerm, ChargingLine cLine, String expectedIntervalOnLastTerm) {

        // given
        FastnetImportService service = new FastnetImportService();
        // when
        LeaseTerm result = service.deriveTermToUpdateFromLastTerm(lastTerm, cLine);
        // then
        assertThat(result.getInterval().toString()).isEqualTo(expectedIntervalOnLastTerm);
        assertThat(lastTerm.getInterval().toString()).isEqualTo(expectedIntervalOnLastTerm);

    }

    void testWhenNewTermIsCreated(LeaseTerm lastTerm, ChargingLine cLine, LocalDate expectedLastTermEndDate) {

        // given
        FastnetImportService service = new FastnetImportService();
        LeaseTerm newTerm = new LeaseTermForTesting();

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseItem).newTerm(service.stringToDate(cLine.getFromDat()), service.stringToDate(cLine.getTomDat()));
            will(returnValue(newTerm));
        }});

        // when
        service.deriveTermToUpdateFromLastTerm(lastTerm, cLine);

        // then
        assertThat(lastTerm.getEndDate()).isEqualTo(expectedLastTermEndDate);

    }

    void testWhenNewTermIsCreatedAndClineEndDateBeforeTermEndDate(LeaseTerm lastTerm, ChargingLine cLine, LocalDate expectedLastTermEndDate, LocalDate expectedResultEndDate) {

        // given
        FastnetImportService service = new FastnetImportService();
        LeaseTerm newTerm = new LeaseTermForTesting();

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseItem).newTerm(service.stringToDate(cLine.getFromDat()), expectedResultEndDate);
            will(returnValue(newTerm));
        }});

        // when
        service.deriveTermToUpdateFromLastTerm(lastTerm, cLine);

        // then
        assertThat(lastTerm.getEndDate()).isEqualTo(expectedLastTermEndDate);

    }

    void testWhenNoOverlapAndClineBeforeLastTerm(LeaseTerm lastTerm, ChargingLine cLine) {

        // given
        FastnetImportService service = new FastnetImportService();
        cLine.clockService = mockClockService;

        // expect
        context.checking(new Expectations(){{
            oneOf(mockClockService).nowAsLocalDateTime();
            will(returnValue(LocalDateTime.parse("2018-01-01")));
//            oneOf(mockMessageService).warnUser("Item with charge null for lease null cannot be updated. FromDat 2016-01-01 is before last term start date 2017-01-01");
        }});

        // when
        service.deriveTermToUpdateFromLastTerm(lastTerm, cLine);
        // then
        assertThat(cLine.getImportLog()).isEqualTo("2018-01-01 00:00:00 Item with charge null for lease null cannot be updated. FromDat 2016-01-01 is before last term start date 2017-01-01");

    }

    void testWhenNoOverlapAndClineAfterLastTerm(LeaseTerm lastTerm, ChargingLine cLine, LocalDate expectedLastTermEndDate) {

        // given
        FastnetImportService service = new FastnetImportService();

        LeaseTerm newTerm = new LeaseTermForTesting();

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseItem).newTerm(service.stringToDate(cLine.getFromDat()), service.stringToDate(cLine.getTomDat()));
            will(returnValue(newTerm));
        }});

        // when
        service.deriveTermToUpdateFromLastTerm(lastTerm, cLine);

        // then
        assertThat(lastTerm.getEndDate()).isEqualTo(expectedLastTermEndDate);

    }

}