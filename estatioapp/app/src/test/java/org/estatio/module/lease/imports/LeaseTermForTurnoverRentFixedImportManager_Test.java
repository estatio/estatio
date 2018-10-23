package org.estatio.module.lease.imports;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForFixed;

public class LeaseTermForTurnoverRentFixedImportManager_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock LeaseRepository mockLeaseRepository;

    @Mock LeaseItemRepository mockLeaseItemRepository;

    @Mock LeaseItem mockLeaseItem1;

    @Mock LeaseItem mockLeaseItem2;

    LocalDate startDatePrevious;
    LocalDate endDatePrevious;

    @Test
    public void getTurnoverRentLines_when_endate_previous_on_end_of_year_or_empty_works() throws Exception {

        // given
        startDatePrevious = new LocalDate(2017, 1, 1);
        endDatePrevious = new LocalDate(2017, 12, 31);

        LeaseTermForTurnoverRentFixedImportManager manager = new LeaseTermForTurnoverRentFixedImportManager();
        Property property = new Property();
        int year = 2018;
        manager.setProperty(property);
        manager.setYear(year);
        manager.leaseItemRepository = mockLeaseItemRepository;
        manager.leaseRepository = mockLeaseRepository;
        Lease lease1 = new Lease();
        Lease lease2 = new Lease();
        LeaseTermForFixed term1 = new LeaseTermForFixed();
        term1.setStartDate(startDatePrevious);
        term1.setEndDate(endDatePrevious);
        final BigDecimal valuePreviousYear = new BigDecimal("1234.56");
        term1.setValue(valuePreviousYear);

        SortedSet termsItem1 = new TreeSet();
        termsItem1.add(term1);
        SortedSet termsItem2 = new TreeSet();

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseRepository).findLeasesByProperty(property);
            will(returnValue(Arrays.asList(lease1, lease2)));
            oneOf(mockLeaseItemRepository).findLeaseItemsByType(lease1, LeaseItemType.TURNOVER_RENT_FIXED);
            will(returnValue(Arrays.asList(mockLeaseItem1)));
            oneOf(mockLeaseItemRepository).findLeaseItemsByType(lease2, LeaseItemType.TURNOVER_RENT_FIXED);
            will(returnValue(Arrays.asList(mockLeaseItem2)));
            oneOf(mockLeaseItem1).getTerms();
            will(returnValue(termsItem1));
            oneOf(mockLeaseItem2).getTerms();
            will(returnValue(termsItem2));
        }});

        // when
        final List<LeaseTermForTurnOverRentFixedImport> turnoverRentLines = manager.getTurnoverRentLines();

        // then
        Assertions.assertThat(turnoverRentLines).hasSize(2);
        LeaseTermForTurnOverRentFixedImport line1 = turnoverRentLines.get(0);
        Assertions.assertThat(line1.getValuePreviousYear()).isEqualTo(valuePreviousYear);
        Assertions.assertThat(line1.getStartDatePreviousYear()).isEqualTo(startDatePrevious);
        Assertions.assertThat(line1.getEndDatePreviousYear()).isEqualTo(endDatePrevious);
        Assertions.assertThat(line1.getValue()).isNull();
        Assertions.assertThat(line1.getStartDate()).isEqualTo(new LocalDate(2018,1,1));
        Assertions.assertThat(line1.getEndDate()).isEqualTo(new LocalDate(2018,12,31));
        LeaseTermForTurnOverRentFixedImport line2 = turnoverRentLines.get(1);
        Assertions.assertThat(line2.getValuePreviousYear()).isNull();
        Assertions.assertThat(line2.getStartDate()).isEqualTo(new LocalDate(2018,1,1));
        Assertions.assertThat(line2.getEndDate()).isEqualTo(new LocalDate(2018,12,31));

    }

    @Test
    public void getTurnoverRentLines_when_endate_previous_before_end_of_year_works() throws Exception {

        // given
        startDatePrevious = new LocalDate(2017, 1, 1);
        endDatePrevious = new LocalDate(2017, 12, 30);

        LeaseTermForTurnoverRentFixedImportManager manager = new LeaseTermForTurnoverRentFixedImportManager();
        Property property = new Property();
        int year = 2018;
        manager.setProperty(property);
        manager.setYear(year);
        manager.leaseItemRepository = mockLeaseItemRepository;
        manager.leaseRepository = mockLeaseRepository;
        Lease lease1 = new Lease();
        LeaseTermForFixed previousTerm = new LeaseTermForFixed();
        previousTerm.setStartDate(startDatePrevious);
        previousTerm.setEndDate(endDatePrevious);
        final BigDecimal valuePreviousYear = new BigDecimal("1234.56");
        previousTerm.setValue(valuePreviousYear);

        SortedSet termsItem1 = new TreeSet();
        termsItem1.add(previousTerm);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseRepository).findLeasesByProperty(property);
            will(returnValue(Arrays.asList(lease1)));
            oneOf(mockLeaseItemRepository).findLeaseItemsByType(lease1, LeaseItemType.TURNOVER_RENT_FIXED);
            will(returnValue(Arrays.asList(mockLeaseItem1)));
            oneOf(mockLeaseItem1).getTerms();
            will(returnValue(termsItem1));
        }});

        // when
        final List<LeaseTermForTurnOverRentFixedImport> turnoverRentLines = manager.getTurnoverRentLines();

        // then
        Assertions.assertThat(turnoverRentLines).hasSize(1);
        LeaseTermForTurnOverRentFixedImport line = turnoverRentLines.get(0);
        Assertions.assertThat(line.getValuePreviousYear()).isEqualTo(valuePreviousYear);
        Assertions.assertThat(line.getStartDatePreviousYear()).isEqualTo(startDatePrevious);
        Assertions.assertThat(line.getEndDatePreviousYear()).isEqualTo(endDatePrevious);
        Assertions.assertThat(line.getValue()).isNull();
        Assertions.assertThat(line.getStartDate()).isEqualTo(new LocalDate(2018,1,1));
        Assertions.assertThat(line.getEndDate()).isEqualTo(new LocalDate(2018,12,31));
    }

    @Test
    public void getTurnoverRentLines_when_endate_previous_after_end_of_year_works() throws Exception {

        // given
        startDatePrevious = new LocalDate(2017, 1, 1);
        endDatePrevious = new LocalDate(2018, 1, 1);

        LeaseTermForTurnoverRentFixedImportManager manager = new LeaseTermForTurnoverRentFixedImportManager();
        Property property = new Property();
        int year = 2018;
        manager.setProperty(property);
        manager.setYear(year);
        manager.leaseItemRepository = mockLeaseItemRepository;
        manager.leaseRepository = mockLeaseRepository;
        Lease lease1 = new Lease();
        LeaseTermForFixed previousTerm = new LeaseTermForFixed();
        previousTerm.setStartDate(startDatePrevious);
        previousTerm.setEndDate(endDatePrevious);
        final BigDecimal valuePreviousYear = new BigDecimal("1234.56");
        previousTerm.setValue(valuePreviousYear);

        SortedSet termsItem1 = new TreeSet();
        termsItem1.add(previousTerm);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseRepository).findLeasesByProperty(property);
            will(returnValue(Arrays.asList(lease1)));
            oneOf(mockLeaseItemRepository).findLeaseItemsByType(lease1, LeaseItemType.TURNOVER_RENT_FIXED);
            will(returnValue(Arrays.asList(mockLeaseItem1)));
            oneOf(mockLeaseItem1).getTerms();
            will(returnValue(termsItem1));
        }});

        // when
        final List<LeaseTermForTurnOverRentFixedImport> turnoverRentLines = manager.getTurnoverRentLines();

        // then
        Assertions.assertThat(turnoverRentLines).hasSize(1);
        LeaseTermForTurnOverRentFixedImport line = turnoverRentLines.get(0);
        Assertions.assertThat(line.getValuePreviousYear()).isEqualTo(valuePreviousYear);
        Assertions.assertThat(line.getStartDatePreviousYear()).isEqualTo(startDatePrevious);
        Assertions.assertThat(line.getEndDatePreviousYear()).isEqualTo(endDatePrevious);
        Assertions.assertThat(line.getValue()).isNull();
        Assertions.assertThat(line.getStartDate()).isEqualTo(new LocalDate(2018,1,2));
        Assertions.assertThat(line.getEndDate()).isEqualTo(new LocalDate(2018,12,31));
    }

    @Test
    public void getTurnoverRentLines_makes_overlapping_terms_visible() throws Exception {

        // given
        startDatePrevious = new LocalDate(2017, 1, 1);
        endDatePrevious = new LocalDate(2018, 1, 1);
        final LocalDate startDateCurrentTerm = new LocalDate(2018, 1, 1);

        LeaseTermForTurnoverRentFixedImportManager manager = new LeaseTermForTurnoverRentFixedImportManager();
        Property property = new Property();
        int year = 2018;
        manager.setProperty(property);
        manager.setYear(year);
        manager.leaseItemRepository = mockLeaseItemRepository;
        manager.leaseRepository = mockLeaseRepository;
        Lease lease1 = new Lease();

        LeaseTermForFixed previousTerm = new LeaseTermForFixed();
        previousTerm.setStartDate(startDatePrevious);
        previousTerm.setEndDate(endDatePrevious);
        final BigDecimal valuePreviousYear = new BigDecimal("1234.56");
        previousTerm.setValue(valuePreviousYear);

        SortedSet termsItem1 = new TreeSet();
        termsItem1.add(previousTerm);

        LeaseTermForFixed currentTerm = new LeaseTermForFixed();
        currentTerm.setStartDate(startDateCurrentTerm);
        final LocalDate endDateCurrentTerm = new LocalDate(2018, 12, 31);
        currentTerm.setEndDate(endDateCurrentTerm);
        final BigDecimal valueCurrentYear = new BigDecimal("2345.67");
        currentTerm.setValue(valueCurrentYear);

        termsItem1.add(currentTerm);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseRepository).findLeasesByProperty(property);
            will(returnValue(Arrays.asList(lease1)));
            oneOf(mockLeaseItemRepository).findLeaseItemsByType(lease1, LeaseItemType.TURNOVER_RENT_FIXED);
            will(returnValue(Arrays.asList(mockLeaseItem1)));
            oneOf(mockLeaseItem1).getTerms();
            will(returnValue(termsItem1));
        }});

        // when
        final List<LeaseTermForTurnOverRentFixedImport> turnoverRentLines = manager.getTurnoverRentLines();

        // then
        Assertions.assertThat(turnoverRentLines).hasSize(1);
        LeaseTermForTurnOverRentFixedImport line = turnoverRentLines.get(0);
        Assertions.assertThat(line.getValuePreviousYear()).isEqualTo(valuePreviousYear);
        Assertions.assertThat(line.getStartDatePreviousYear()).isEqualTo(startDatePrevious);
        Assertions.assertThat(line.getEndDatePreviousYear()).isEqualTo(endDatePrevious);
        Assertions.assertThat(line.getValue()).isEqualTo(valueCurrentYear);
        Assertions.assertThat(line.getStartDate()).isEqualTo(startDateCurrentTerm);
        Assertions.assertThat(line.getEndDate()).isEqualTo(endDateCurrentTerm);
    }

}