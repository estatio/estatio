package org.estatio.module.turnover.dom;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseForTesting;
import org.estatio.module.lease.dom.occupancy.Occupancy;

public class TurnoverReportingConfig_Test {

    @Test
    public void getEndDate() {

        // given
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        config.setStartDate(new LocalDate(2018,1,1));
        final Occupancy occupancy = new Occupancy();
        final Lease lease = new LeaseForTesting();
        occupancy.setLease(lease);
        config.setOccupancy(occupancy);

        // when, then
        Assertions.assertThat(config.getEndDate()).isNull();

        // and when
        final LocalDate leaseEndDate = new LocalDate(2019, 1, 31);
        lease.setEndDate(leaseEndDate);
        // then
        Assertions.assertThat(config.getEndDate()).isNull();

        // and when
        final LocalDate leaseTenancyEndDate = new LocalDate(2019, 2, 1);
        lease.setTenancyEndDate(leaseTenancyEndDate);
        // then
        Assertions.assertThat(config.getEndDate()).isEqualTo(leaseTenancyEndDate);

        // and when
        final LocalDate occupancyEndDate = new LocalDate(2019, 2, 2);
        occupancy.setEndDate(occupancyEndDate);
        // then
        Assertions.assertThat(config.getEndDate()).isEqualTo(occupancyEndDate);

        // and when
        config.setFrequency(Frequency.MONTHLY);
        // then
        Assertions.assertThat(config.getEndDate()).isEqualTo(occupancyEndDate.plusMonths(1));

        // and when
        config.setFrequency(Frequency.YEARLY);
        // then
        Assertions.assertThat(config.getEndDate()).isEqualTo(occupancyEndDate.plusYears(1));

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    TurnoverRepository turnoverRepository;

    @Test
    public void produceEmptyTurnovers_when_turnover_date_in_interval() throws Exception {

        final LocalDate turnoverDate = new LocalDate(2019,1,1);

        // given
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        final Currency currency = new Currency();
        Occupancy occupancy = new Occupancy();
        Lease lease = new LeaseForTesting();
        occupancy.setLease(lease);
        occupancy.setReportTurnover(Occupancy.OccupancyReportingType.YES);

        config.turnoverRepository = turnoverRepository;
        config.setStartDate(new LocalDate(2019, 01,01));
        config.setOccupancy(occupancy);
        config.setType(Type.PRELIMINARY);
        config.setFrequency(Frequency.MONTHLY);
        config.setCurrency(currency);

        // expect
        context.checking(new Expectations(){{
            oneOf(turnoverRepository).createNewEmpty(config, turnoverDate, Type.PRELIMINARY, Frequency.MONTHLY, currency);
        }});

        // when
        config.produceEmptyTurnover(turnoverDate);
        
    }

    @Test
    public void produceEmptyTurnoverswhen_turnover_date_NOT_in_interval() throws Exception {

        LocalDate turnoverDate;

        // given
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        Occupancy occupancy = new Occupancy();
        Lease lease = new LeaseForTesting();
        occupancy.setLease(lease);
        occupancy.setReportTurnover(Occupancy.OccupancyReportingType.YES);
        occupancy.setEndDate(new LocalDate(2019,01,31));

        config.setOccupancy(occupancy);
        config.setStartDate(new LocalDate(2019, 01,01));

        // when
        turnoverDate = new LocalDate(2018, 12,31);
        // then nothing
        config.produceEmptyTurnover(turnoverDate);

        // and when
        turnoverDate = new LocalDate(2019,2,1);
        // then nothing again
        config.produceEmptyTurnover(turnoverDate);

    }

    @Test
    public void produceEmptyTurnoverswhen_occupancy_set_to_NOT_report_turnovers() throws Exception {

        final LocalDate turnoverDate = new LocalDate(2019,1,1);

        // given
        TurnoverReportingConfig config = new TurnoverReportingConfig();
        final Currency currency = new Currency();
        Occupancy occupancy = new Occupancy();
        Lease lease = new LeaseForTesting();
        occupancy.setLease(lease);
        occupancy.setReportTurnover(Occupancy.OccupancyReportingType.NO);

        config.setStartDate(new LocalDate(2019, 01,01));
        config.setOccupancy(occupancy);

        // when
        config.produceEmptyTurnover(turnoverDate);

    }
}