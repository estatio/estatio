package org.estatio.module.lease.contributions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.integtests.VT;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class Property_vacantUnits_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock OccupancyRepository mockOccupancyRepository;
    @Mock UnitRepository mockUnitRepository;

    @Mock ClockService mockClockService;

    Property_vacantUnits mixin;

    Property property;
    Unit unit1;
    Unit unit2;

    Occupancy occupancy1;
    Occupancy occupancy2;

    LocalDate now;

    @Before
    public void setUp() throws Exception {

        property = new Property();
        unit1 = new Unit();
        unit2 = new Unit();

        context.checking(new Expectations(){{
            allowing(mockUnitRepository).findByProperty(property);
            will(returnValue(Arrays.asList(unit1, unit2)));
        }});

        occupancy1 = new Occupancy();
        occupancy1.setUnit(unit1);
        occupancy2 = new Occupancy();
        occupancy1.setUnit(unit2);


        mixin = new Property_vacantUnits(property);
        mixin.occupancyRepository = mockOccupancyRepository;
        mixin.unitRepository = mockUnitRepository;
        mixin.clockService = mockClockService;


        now = VT.ld(2017, 1, 1);
        context.checking(new Expectations(){{
            allowing(mockClockService).now();
            will(returnValue(now));
        }});
    }

    @Test
    public void occupiedUnits() throws Exception {

        // given
        context.checking(new Expectations(){{
            allowing(mockOccupancyRepository).findByProperty(property);
            will(returnValue(Arrays.asList(occupancy1, occupancy2)));
        }});

        // when
        final List<Unit> units = mixin.occupiedUnits();

        // then
        assertThat(units.size()).isEqualTo(2);

        // given
        occupancy1.setEndDate(now);

        // when
        occupancy2.setEndDate(now.plusDays(1));

        // then
        assertThat(mixin.occupiedUnits().size()).isEqualTo(1);

    }

    @Test
    public void vacant_Units_works() throws Exception {

        // given
        context.checking(new Expectations(){{
            allowing(mockOccupancyRepository).findByProperty(property);
            will(returnValue(Collections.emptyList()));
        }});

        // when
        List<Unit> expectedVacantUnits = mixin.coll();

        // then
        assertThat(expectedVacantUnits.size()).isEqualTo(2);



        // given
        unit1.setEndDate(now.plusDays(1));

        // when
        expectedVacantUnits = mixin.coll();

        // then
        assertThat(expectedVacantUnits.size()).isEqualTo(2);



        // and given
        unit1.setEndDate(now);

        // when
        expectedVacantUnits = mixin.coll();

        // then
        assertThat(expectedVacantUnits.size()).isEqualTo(1);
        assertThat(expectedVacantUnits).doesNotContain(unit1);

    }

}