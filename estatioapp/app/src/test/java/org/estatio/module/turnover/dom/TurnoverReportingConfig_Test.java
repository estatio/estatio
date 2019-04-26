package org.estatio.module.turnover.dom;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseForTesting;
import org.estatio.module.lease.dom.occupancy.Occupancy;

public class TurnoverReportingConfig_Test {

    @Test
    public void getEndDate() {

        // given
        TurnoverReportingConfig config = new TurnoverReportingConfig();
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
        Assertions.assertThat(config.getEndDate()).isEqualTo(leaseEndDate);

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

    }
}