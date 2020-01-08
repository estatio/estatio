package org.estatio.module.turnoveraggregate.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;

@Mixin
public class Lease_aggregateTurnovers {

    private final Lease lease;

    public Lease_aggregateTurnovers(Lease lease) {
        this.lease = lease;
    }

    @Action()
    public Lease $$() {
        for (Occupancy o : lease.getOccupancies()) {
            final Occupancy_aggregateTurnovers mixin = factoryService.mixin(Occupancy_aggregateTurnovers.class, o);
            wrapperFactory.wrap(mixin).$$();
        }
        return lease;
    }

    @Inject WrapperFactory wrapperFactory;

    @Inject FactoryService factoryService;


}
