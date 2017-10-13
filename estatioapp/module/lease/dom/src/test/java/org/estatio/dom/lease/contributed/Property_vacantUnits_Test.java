package org.estatio.dom.lease.contributed;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.OccupancyRepository;

public class Property_vacantUnits_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock OccupancyRepository mockOccupancyRepository;

    @Mock ClockService mockClockService;

    @Test
    public void occupiedUnits() throws Exception {

        // given
        Property property = new Property();
        Unit unit1 = new Unit();
        Unit unit2 = new Unit();
        Property_vacantUnits mixin = new Property_vacantUnits(property);
        mixin.occupancyRepository = mockOccupancyRepository;
        mixin.clockService = mockClockService;

        // when
        Occupancy occupancy1 = new Occupancy();
        occupancy1.setUnit(unit1);
        Occupancy occupancy2 = new Occupancy();
        occupancy1.setUnit(unit2);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOccupancyRepository).findByProperty(property);
            will(returnValue(Arrays.asList(occupancy1, occupancy2)));
        }});

        // then
        Assertions.assertThat(mixin.occupiedUnits().size()).isEqualTo(2);

        // and when
        LocalDate now = new LocalDate(2017,01,01);
        occupancy1.setEndDate(now);
        occupancy2.setEndDate(now.plusDays(1));

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOccupancyRepository).findByProperty(property);
            will(returnValue(Arrays.asList(occupancy1, occupancy2)));
            allowing(mockClockService).now();
            will(returnValue(now));
        }});

        // then
        Assertions.assertThat(mixin.occupiedUnits().size()).isEqualTo(1);

    }

    @Mock UnitRepository mockUnitRepository;

    @Test
    public void vacant_Units_works() throws Exception {

        // given
        Property property = new Property();
        Unit unit1 = new Unit();
        Unit unit2 = new Unit();
        Property_vacantUnits mixin = new Property_vacantUnits(property);
        mixin.unitRepository = mockUnitRepository;
        mixin.occupancyRepository = mockOccupancyRepository;
        mixin.clockService = mockClockService;
        LocalDate now = new LocalDate(2017,01,01);

        // expect
        context.checking(new Expectations(){{
            allowing(mockOccupancyRepository).findByProperty(property);
            oneOf(mockUnitRepository).findByProperty(property);
            will(returnValue(Arrays.asList(unit1, unit2)));
        }});
        // when
        List<Unit> expectedVacantUnits = mixin.$$();
        // then
        Assertions.assertThat(expectedVacantUnits.size()).isEqualTo(2);


        // and expect
        context.checking(new Expectations(){{
            allowing(mockOccupancyRepository).findByProperty(property);
            oneOf(mockUnitRepository).findByProperty(property);
            will(returnValue(Arrays.asList(unit1, unit2)));
            oneOf(mockClockService).now();
            will(returnValue(now));
        }});
        // when
        unit1.setEndDate(now.plusDays(1));
        expectedVacantUnits = mixin.$$();
        // then
        Assertions.assertThat(expectedVacantUnits.size()).isEqualTo(2);


        // and expect
        context.checking(new Expectations(){{
            allowing(mockOccupancyRepository).findByProperty(property);
            oneOf(mockUnitRepository).findByProperty(property);
            will(returnValue(Arrays.asList(unit1, unit2)));
            oneOf(mockClockService).now();
            will(returnValue(now));
        }});
        // when
        unit1.setEndDate(now);
        expectedVacantUnits = mixin.$$();
        // then
        Assertions.assertThat(expectedVacantUnits.size()).isEqualTo(1);
        Assertions.assertThat(expectedVacantUnits).doesNotContain(unit1);


    }

}