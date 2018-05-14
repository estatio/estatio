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

    @Test
    public void getTurnoverRentLines_works() throws Exception {

        // given
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
        final LocalDate startDateCurrent = new LocalDate(2017, 1, 1);
        term1.setStartDate(startDateCurrent);
        final BigDecimal valueCurrent = new BigDecimal("1234.56");
        term1.setValue(valueCurrent);

        SortedSet itemsTerm1 = new TreeSet();
        itemsTerm1.add(term1);
        SortedSet itemsTerm2 = new TreeSet();

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLeaseRepository).findLeasesByProperty(property);
            will(returnValue(Arrays.asList(lease1, lease2)));
            oneOf(mockLeaseItemRepository).findLeaseItemsByType(lease1, LeaseItemType.TURNOVER_RENT_FIXED);
            will(returnValue(Arrays.asList(mockLeaseItem1)));
            oneOf(mockLeaseItemRepository).findLeaseItemsByType(lease2, LeaseItemType.TURNOVER_RENT_FIXED);
            will(returnValue(Arrays.asList(mockLeaseItem2)));
            oneOf(mockLeaseItem1).getTerms();
            will(returnValue(itemsTerm1));
            oneOf(mockLeaseItem2).getTerms();
            will(returnValue(itemsTerm2));
        }});

        // when
        final List<LeaseTermForTurnOverRentFixedImport> turnoverRentLines = manager.getTurnoverRentLines();

        // then
        Assertions.assertThat(turnoverRentLines).hasSize(1);
        LeaseTermForTurnOverRentFixedImport line = turnoverRentLines.get(0);
        Assertions.assertThat(line.getValueCurrent()).isEqualTo(valueCurrent);
        Assertions.assertThat(line.getStartDateCurrent()).isEqualTo(startDateCurrent);

    }

}