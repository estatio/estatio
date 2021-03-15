package org.estatio.module.lease.imports;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForFixed;
import org.estatio.module.lease.dom.LeaseTermForTurnoverRent;
import org.estatio.module.lease.dom.LeaseTermRepository;
import org.estatio.module.lease.dom.LeaseTermStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseTermForTurnOverRentSweImport_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock LeaseTermRepository mockLeaseTermRepository;

    @Test
    public void updateOrCreateTerm_works_for_new_term() throws Exception {

        // given
        LeaseTermForTurnOverRentSweImport importLine = new LeaseTermForTurnOverRentSweImport();
        importLine.leaseTermRepository = mockLeaseTermRepository;

        LeaseTermForTurnoverRent leaseTermForTurnoverRentSwe = new LeaseTermForTurnoverRent();
        LeaseItem leaseItem = new LeaseItem(){
            @Override
            public LeaseTerm newTerm(final LocalDate startDate, final LocalDate endDate){
                leaseTermForTurnoverRentSwe.setStartDate(startDate);
                leaseTermForTurnoverRentSwe.setEndDate(endDate);
                return leaseTermForTurnoverRentSwe;
            }
        };
        leaseItem.setType(LeaseItemType.TURNOVER_RENT);

        LocalDate startDate = new LocalDate(2018,01,01);
        LocalDate endDate = new LocalDate(2018,12,31);
        BigDecimal value = new BigDecimal("1234.56");
        BigDecimal percentage = new BigDecimal("3.4");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseTermRepository).findByLeaseItemAndStartDate(leaseItem, startDate);
            will(returnValue(null));
        }});

        // when
        importLine.updateOrCreateTerm(leaseItem, startDate, endDate, value, percentage);

        // then
        assertThat(leaseTermForTurnoverRentSwe.getManualTurnoverRent()).isEqualTo(value);
        assertThat(leaseTermForTurnoverRentSwe.getTurnoverRentRule()).isEqualTo("3.4");
        assertThat(leaseTermForTurnoverRentSwe.getTurnoverRentRule()).isEqualTo(percentage.toString());
        assertThat(leaseTermForTurnoverRentSwe.getStartDate()).isEqualTo(startDate);
        assertThat(leaseTermForTurnoverRentSwe.getEndDate()).isEqualTo(endDate);
        assertThat(leaseTermForTurnoverRentSwe.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);
    }

    @Test
    public void updateOrCreateTerm_works_for_existing_term() throws Exception {

        // given
        LeaseTermForTurnOverRentSweImport importLine = new LeaseTermForTurnOverRentSweImport();
        importLine.leaseTermRepository = mockLeaseTermRepository;

        LeaseTermForTurnoverRent leaseTermForTurnoverRent = new LeaseTermForTurnoverRent();
        LocalDate endDateTerm = new LocalDate(2018,11,20);
        leaseTermForTurnoverRent.setEndDate(endDateTerm);
        LeaseItem leaseItem = new LeaseItem();

        LocalDate startDate = new LocalDate(2018,01,01);
        LocalDate endDate = new LocalDate(2018,12,31);
        BigDecimal value = new BigDecimal("1234.56");
        BigDecimal percentage = new BigDecimal("3.4");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseTermRepository).findByLeaseItemAndStartDate(leaseItem, startDate);
            will(returnValue(leaseTermForTurnoverRent));
        }});

        // when
        importLine.updateOrCreateTerm(leaseItem, startDate, endDate, value, percentage);

        // then
        assertThat(leaseTermForTurnoverRent.getManualTurnoverRent()).isEqualTo(value);
        assertThat(leaseTermForTurnoverRent.getTurnoverRentRule()).isEqualTo("3.4");
        assertThat(leaseTermForTurnoverRent.getEndDate()).isEqualTo(endDate);
        assertThat(leaseTermForTurnoverRent.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);
    }

    @Test
    public void updateOrCreateTerm_works_with_zero_value() throws Exception {

        // given
        LeaseTermForTurnOverRentSweImport importLine = new LeaseTermForTurnOverRentSweImport();
        importLine.leaseTermRepository = mockLeaseTermRepository;

        LeaseTermForTurnoverRent leaseTermForTurnoverRent = new LeaseTermForTurnoverRent();
        LeaseItem leaseItem = new LeaseItem();

        LocalDate startDate = new LocalDate(2018,01,01);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseTermRepository).findByLeaseItemAndStartDate(leaseItem, startDate);
            will(returnValue(leaseTermForTurnoverRent));
        }});

        // when
        importLine.updateOrCreateTerm(leaseItem, startDate, null, new BigDecimal("0.00"), new BigDecimal("0"));

        // then
        assertThat(leaseTermForTurnoverRent.getManualTurnoverRent()).isEqualTo(new BigDecimal("0.00"));
        assertThat(leaseTermForTurnoverRent.getTurnoverRentRule()).isEqualTo("0");
        assertThat(leaseTermForTurnoverRent.getEndDate()).isNull();
        assertThat(leaseTermForTurnoverRent.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);
    }

    @Test
    public void updateOrCreateTerm_works_with_overlapping_previous_year() throws Exception {

        // given
        LeaseTermForTurnOverRentSweImport importLine = new LeaseTermForTurnOverRentSweImport();
        importLine.leaseTermRepository = mockLeaseTermRepository;

        LeaseTermForTurnoverRent leaseTermForTurnoverRent = new LeaseTermForTurnoverRent();
        LocalDate startDatePrevious = new LocalDate(2017,1,1);
        LocalDate endDatePrevious = new LocalDate(2018,1,1);
        leaseTermForTurnoverRent.setStartDate(startDatePrevious);
        leaseTermForTurnoverRent.setEndDate(endDatePrevious);
        leaseTermForTurnoverRent.setStatus(LeaseTermStatus.APPROVED);
        LeaseItem leaseItem = new LeaseItem(){
            @Override
            public LeaseTerm newTerm(
                    final LocalDate startDate,
                    final LocalDate endDate){
                return new LeaseTermForTurnoverRent();
            }
        };
        leaseItem.getTerms().add(leaseTermForTurnoverRent);

        LocalDate startDate = new LocalDate(2018,01,01);
        LocalDate endDate = new LocalDate(2018,12,31);
        BigDecimal value = new BigDecimal("1234.56");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseTermRepository).findByLeaseItemAndStartDate(leaseItem, startDate);
            will(returnValue(null));
        }});

        // when
        importLine.updateOrCreateTerm(leaseItem, startDate, endDate, value, BigDecimal.ZERO);

        // then
        assertThat(leaseTermForTurnoverRent.getEndDate()).isEqualTo(startDate.minusDays(1));
        assertThat(leaseTermForTurnoverRent.getEndDate()).isEqualTo(new LocalDate(2017,12,31));

    }

    @Test
    public void updateOrCreateTerm_works_with_overlapping_current_year() throws Exception {

        // given
        LeaseTermForTurnOverRentSweImport importLine = new LeaseTermForTurnOverRentSweImport();
        importLine.leaseTermRepository = mockLeaseTermRepository;

        LeaseTermForTurnoverRent leaseTermForTurnoverRent = new LeaseTermForTurnoverRent();
        LocalDate startDatePrevious = new LocalDate(2018,1,2);
        LocalDate endDatePrevious = new LocalDate(2018,2,1);
        leaseTermForTurnoverRent.setStartDate(startDatePrevious);
        leaseTermForTurnoverRent.setEndDate(endDatePrevious);
        leaseTermForTurnoverRent.setManualTurnoverRent(new BigDecimal("1111.11"));
        leaseTermForTurnoverRent.setStatus(LeaseTermStatus.APPROVED);
        LeaseItem leaseItem = new LeaseItem();
        leaseItem.getTerms().add(leaseTermForTurnoverRent);

        LocalDate startDate = new LocalDate(2018,01,01);
        LocalDate endDate = new LocalDate(2018,12,31);
        BigDecimal value = new BigDecimal("1234.56");
        BigDecimal percentage = new BigDecimal("3.4");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseTermRepository).findByLeaseItemAndStartDate(leaseItem, startDate);
            will(returnValue(null));
        }});

        // when
        importLine.updateOrCreateTerm(leaseItem, startDate, endDate, value, percentage);

        // then
        assertThat(leaseTermForTurnoverRent.getStartDate()).isEqualTo(startDate);
        assertThat(leaseTermForTurnoverRent.getEndDate()).isEqualTo(endDate);
        assertThat(leaseTermForTurnoverRent.getManualTurnoverRent()).isEqualTo(value);
        assertThat(leaseTermForTurnoverRent.getTurnoverRentRule()).isEqualTo("3.4");

    }

    @Test
    public void update_works_when_term_found() throws Exception {

        // given
        LeaseTermForTurnOverRentSweImport importLine = new LeaseTermForTurnOverRentSweImport();
        importLine.setLeaseReference("LEASE_REF");
        importLine.leaseTermRepository = mockLeaseTermRepository;

        LeaseTermForTurnoverRent leaseTermForTurnoverRent = new LeaseTermForTurnoverRent();
        LeaseItem leaseItem = new LeaseItem();

        final LocalDate startDate = new LocalDate(2017,01,01);
        final LocalDate endDate = new LocalDate(2017, 12, 31);

        leaseTermForTurnoverRent.setEndDate(endDate.minusDays(10));
        assertThat(leaseTermForTurnoverRent.getEndDate()).isEqualTo(endDate.minusDays(10));

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseTermRepository).findByLeaseItemAndStartDate(leaseItem, startDate);
            will(returnValue(leaseTermForTurnoverRent));
        }});

        // when
        importLine.updateTerm(leaseItem, startDate, endDate, new BigDecimal("1234.56"));

        // then
        assertThat(leaseTermForTurnoverRent.getManualTurnoverRent()).isEqualTo(new BigDecimal("1234.56"));
        assertThat(leaseTermForTurnoverRent.getEndDate()).isEqualTo(endDate);
        assertThat(leaseTermForTurnoverRent.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);
    }

    @Mock MessageService mockMessageService;

    @Test
    public void update_works_when_term_NOT_found() throws Exception {

        // given
        LeaseTermForTurnOverRentSweImport importLine = new LeaseTermForTurnOverRentSweImport();
        importLine.setLeaseReference("LEASE_REF");
        importLine.leaseTermRepository = mockLeaseTermRepository;
        importLine.messageService = mockMessageService;

        LeaseItem leaseItem = new LeaseItem();
        final LocalDate startDate = new LocalDate(2017,01,01);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseTermRepository).findByLeaseItemAndStartDate(leaseItem, startDate);
            will(returnValue(null));
            oneOf(mockMessageService).warnUser("Previous term not found for lease LEASE_REF");
        }});

        // when
        importLine.updateTerm(leaseItem, startDate, null, null);
    }

    @Mock LeaseRepository mockLeaseRepository;

    @Test
    public void multiple_items_found() throws Exception {

        // given
        LeaseTermForTurnOverRentSweImport importLine = new LeaseTermForTurnOverRentSweImport();
        importLine.leaseRepository = mockLeaseRepository;
        importLine.messageService = mockMessageService;
        importLine.setLeaseReference("Some ref");

        Lease lease = new Lease() {
            @Override
            public List<LeaseItem> findItemsOfType(final LeaseItemType type) {
                return Arrays.asList(new LeaseItem(), new LeaseItem());
            }
        };

        // expext
        context.checking(new Expectations(){{
            oneOf(mockLeaseRepository).findLeaseByReference("Some ref");
            will(returnValue(lease));
            oneOf(mockMessageService).warnUser("Multiple lease items of type TURNOVER_RENT found on lease with reference Some ref; could not update.");
        }});

        // when
        importLine.importData();

    }


    @Test
    public void reason_invalid_works() throws Exception {

        // given
        LeaseTermForTurnOverRentSweImport importLine = new LeaseTermForTurnOverRentSweImport();
        importLine.setYear(2018);
        importLine.setLeaseReference("LEASE_REF");
        // when, then
        assertThat(importLine.reasonLineInValid()).isNull();

        // and when
        importLine.setValuePreviousYear(BigDecimal.ONE);
        // then
        assertThat(importLine.reasonLineInValid()).isEqualTo("Missing date found for previous year for lease with reference LEASE_REF; please correct.");

        // and when
        importLine.setValue(BigDecimal.ONE);
        // then
        assertThat(importLine.reasonLineInValid()).isEqualTo("Missing date found for previous year for lease with reference LEASE_REF; please correct.Missing date found for lease with reference LEASE_REF; please correct.Overlapping interval found for lease with reference LEASE_REF; please correct.");

        // and when
        importLine.setStartDatePreviousYear(new LocalDate(2016,1,1));
        importLine.setEndDatePreviousYear(new LocalDate(2016,12,31));
        importLine.setStartDate(new LocalDate(2017,1,1));
        importLine.setEndDate(new LocalDate(2018,12,31));
        // then
        assertThat(importLine.reasonLineInValid()).isEqualTo("Start date previous year should be in 2017 for lease with reference LEASE_REF; please correct.Start date should be in 2018 for lease with reference LEASE_REF; please correct.");

        // and when
        importLine.setStartDatePreviousYear(new LocalDate(2017,1,1));
        importLine.setEndDatePreviousYear(new LocalDate(2018,1,1));
        importLine.setStartDate(new LocalDate(2018,1,1));
        importLine.setEndDate(new LocalDate(2018,12,31));
        // then
        assertThat(importLine.reasonLineInValid()).isEqualTo("Overlapping interval found for lease with reference LEASE_REF; please correct.");

        // and when
        importLine.setEndDatePreviousYear(new LocalDate(2017,12,1));
        // then finally again
        assertThat(importLine.reasonLineInValid()).isNull();
    }

    @Test
    public void percentageToTurnoverRentRuleString_works() throws Exception {
        assertThat(LeaseTermForTurnOverRentSweImport.percentageToTurnoverRentRuleString(new BigDecimal("8"))).isEqualTo("8");
        assertThat(LeaseTermForTurnOverRentSweImport.percentageToTurnoverRentRuleString(new BigDecimal("8.0"))).isEqualTo("8");
        assertThat(LeaseTermForTurnOverRentSweImport.percentageToTurnoverRentRuleString(new BigDecimal("8.00"))).isEqualTo("8");
        assertThat(LeaseTermForTurnOverRentSweImport.percentageToTurnoverRentRuleString(new BigDecimal("8.1"))).isEqualTo("8.1");
        assertThat(LeaseTermForTurnOverRentSweImport.percentageToTurnoverRentRuleString(new BigDecimal("8.01"))).isEqualTo("8.01");
    }

    @Test
    public void setPercentageOfTerm_works() throws Exception {

        // given
        LeaseTermForTurnOverRentSweImport importLine = new LeaseTermForTurnOverRentSweImport();
        importLine.messageService = mockMessageService;
        // when
        final LeaseTermForTurnoverRent term = new LeaseTermForTurnoverRent();
        final BigDecimal percentage = new BigDecimal("3.5");
        importLine.setPercentageOfTerm(term, percentage);
        // then
        assertThat(term.getTurnoverRentRule()).isEqualTo(percentage.toString());

        // expect
        context.checking(new Expectations(){{
            oneOf(mockMessageService).warnUser(with(any(String.class)));
        }});

        // and when no percentage present and no previous terms
        importLine.setPercentageOfTerm(term, null);

        // and when there is a previous term somewhere with turnover rent rule
        final LeaseTermForTurnoverRent previous = new LeaseTermForTurnoverRent();
        final LeaseTermForTurnoverRent previousOfPrevious = new LeaseTermForTurnoverRent();
        previousOfPrevious.setTurnoverRentRule(percentage.toString());
        previous.setPrevious(previousOfPrevious);
        term.setPrevious(previous);
        importLine.setPercentageOfTerm(term, null);
        // then
        assertThat(term.getTurnoverRentRule()).isEqualTo(percentage.toString());

    }

}