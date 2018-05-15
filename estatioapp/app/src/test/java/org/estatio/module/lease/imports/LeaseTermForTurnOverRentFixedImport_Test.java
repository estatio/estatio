package org.estatio.module.lease.imports;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForFixed;
import org.estatio.module.lease.dom.LeaseTermRepository;
import org.estatio.module.lease.dom.LeaseTermStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseTermForTurnOverRentFixedImport_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock LeaseTermRepository mockLeaseTermRepository;

    @Test
    public void updateOrCreateTerm_works_for_new_term_with_end_date() throws Exception {

        // given
        LeaseTermForTurnOverRentFixedImport importLine = new LeaseTermForTurnOverRentFixedImport();
        importLine.leaseTermRepository = mockLeaseTermRepository;

        LeaseTermForFixed leaseTermForFixed = new LeaseTermForFixed();
        LeaseItem leaseItem = new LeaseItem(){
            @Override
            public LeaseTerm newTerm(final LocalDate startDate, final LocalDate endDate){
                leaseTermForFixed.setStartDate(startDate);
                leaseTermForFixed.setEndDate(endDate);
                return leaseTermForFixed;
            }
        };
        leaseItem.setType(LeaseItemType.TURNOVER_RENT_FIXED);

        LocalDate startDate = new LocalDate(2018,01,01);
        LocalDate endDate = new LocalDate(2018,12,31);
        BigDecimal value = new BigDecimal("1234.56");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseTermRepository).findByLeaseItemAndStartDate(leaseItem, startDate);
            will(returnValue(null));
        }});

        // when
        importLine.updateOrCreateTerm(leaseItem, startDate, endDate, value);

        // then
        assertThat(leaseTermForFixed.getValue()).isEqualTo(value);
        assertThat(leaseTermForFixed.getStartDate()).isEqualTo(startDate);
        assertThat(leaseTermForFixed.getEndDate()).isEqualTo(endDate);
        assertThat(leaseTermForFixed.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);
    }

    @Test
    public void updateOrCreateTerm_works_for_new_term_without_end_date() throws Exception {

        // given
        LeaseTermForTurnOverRentFixedImport importLine = new LeaseTermForTurnOverRentFixedImport();
        importLine.leaseTermRepository = mockLeaseTermRepository;

        LeaseTermForFixed leaseTermForFixed = new LeaseTermForFixed();
        LeaseItem leaseItem = new LeaseItem(){
            @Override
            public LeaseTerm newTerm(final LocalDate startDate, final LocalDate endDate){
                leaseTermForFixed.setStartDate(startDate);
                leaseTermForFixed.setEndDate(endDate);
                return leaseTermForFixed;
            }
        };
        leaseItem.setType(LeaseItemType.TURNOVER_RENT_FIXED);

        LocalDate startDate = new LocalDate(2018,01,01);
        BigDecimal value = new BigDecimal("1234.56");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseTermRepository).findByLeaseItemAndStartDate(leaseItem, startDate);
            will(returnValue(null));
        }});

        // when
        importLine.updateOrCreateTerm(leaseItem, startDate, null, value);

        // then
        assertThat(leaseTermForFixed.getValue()).isEqualTo(value);
        assertThat(leaseTermForFixed.getStartDate()).isEqualTo(startDate);
        final LocalDate expectedEndDate = new LocalDate(2018, 12, 31);
        assertThat(leaseTermForFixed.getEndDate()).isEqualTo(expectedEndDate);
        assertThat(leaseTermForFixed.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);
    }

    @Test
    public void updateOrCreateTerm_works_for_existing_term_with_end_date() throws Exception {

        // given
        LeaseTermForTurnOverRentFixedImport importLine = new LeaseTermForTurnOverRentFixedImport();
        importLine.leaseTermRepository = mockLeaseTermRepository;

        LeaseTermForFixed leaseTermForFixed = new LeaseTermForFixed();
        LocalDate endDateTerm = new LocalDate(2018,11,20);
        leaseTermForFixed.setEndDate(endDateTerm);
        LeaseItem leaseItem = new LeaseItem();

        LocalDate startDate = new LocalDate(2018,01,01);
        LocalDate endDate = new LocalDate(2018,12,31);
        BigDecimal value = new BigDecimal("1234.56");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseTermRepository).findByLeaseItemAndStartDate(leaseItem, startDate);
            will(returnValue(leaseTermForFixed));
        }});

        // when
        importLine.updateOrCreateTerm(leaseItem, startDate, endDate, value);

        // then
        assertThat(leaseTermForFixed.getValue()).isEqualTo(value);
        assertThat(leaseTermForFixed.getEndDate()).isEqualTo(endDate);
        assertThat(leaseTermForFixed.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);
    }

    @Test
    public void updateOrCreateTerm_works_when_no_end_date_for_existing_term_with_end_date() throws Exception {

        // given
        LeaseTermForTurnOverRentFixedImport importLine = new LeaseTermForTurnOverRentFixedImport();
        importLine.leaseTermRepository = mockLeaseTermRepository;

        LeaseTermForFixed leaseTermForFixed = new LeaseTermForFixed();
        LocalDate endDate = new LocalDate(2018,11,20);
        leaseTermForFixed.setEndDate(endDate);
        LeaseItem leaseItem = new LeaseItem();

        LocalDate startDate = new LocalDate(2018,01,01);
        BigDecimal value = new BigDecimal("1234.56");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseTermRepository).findByLeaseItemAndStartDate(leaseItem, startDate);
            will(returnValue(leaseTermForFixed));
        }});

        // when
        importLine.updateOrCreateTerm(leaseItem, startDate, null, value);

        // then
        assertThat(leaseTermForFixed.getValue()).isEqualTo(value);
        assertThat(leaseTermForFixed.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);
        assertThat(leaseTermForFixed.getEndDate()).isEqualTo(endDate);
    }

    @Test
    public void updateOrCreateTerm_works_when_no_end_date_for_existing_term_without_end_date() throws Exception {

        // given
        LeaseTermForTurnOverRentFixedImport importLine = new LeaseTermForTurnOverRentFixedImport();
        importLine.leaseTermRepository = mockLeaseTermRepository;

        LeaseTermForFixed leaseTermForFixed = new LeaseTermForFixed();
        LeaseItem leaseItem = new LeaseItem();

        LocalDate startDate = new LocalDate(2018,01,01);
        BigDecimal value = new BigDecimal("1234.56");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseTermRepository).findByLeaseItemAndStartDate(leaseItem, startDate);
            will(returnValue(leaseTermForFixed));
        }});

        // when
        importLine.updateOrCreateTerm(leaseItem, startDate, null, value);

        // then
        assertThat(leaseTermForFixed.getValue()).isEqualTo(value);
        assertThat(leaseTermForFixed.getEndDate()).isNull();
        assertThat(leaseTermForFixed.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);
    }

}