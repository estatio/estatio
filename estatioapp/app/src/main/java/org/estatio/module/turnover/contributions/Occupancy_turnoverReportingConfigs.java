package org.estatio.module.turnover.contributions;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;

@Mixin
public class Occupancy_turnoverReportingConfigs {

    private final Occupancy occupancy;

    public Occupancy_turnoverReportingConfigs(final Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<TurnoverReportingConfig> turnoverReportingConfig() {
        return Arrays.asList(turnoverReportingConfigRepository.findUnique(occupancy));
    }

    @Inject
    TurnoverReportingConfigRepository turnoverReportingConfigRepository;


}
