package org.estatio.module.turnover.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Status;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.aggregation.TurnoverReportingConfigLinkRepository;

@Mixin
public class TurnoverReportingConfig_remove {

    private final TurnoverReportingConfig config;

    public TurnoverReportingConfig_remove(TurnoverReportingConfig config) {
        this.config = config;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Occupancy $$() {
        Occupancy occ = config.getOccupancy();
        config.getTurnovers().forEach(t->{
            if (t.getStatus()==Status.NEW){
                repositoryService.removeAndFlush(t);
            }
        });
        repositoryService.removeAndFlush(config);
        return occ;
    }

    public String disable$$(){
        final Turnover firstApprovedTurnoverForConfigIfAny = config.getTurnovers().stream().filter(t -> t.getStatus() == Status.APPROVED)
                .findFirst().orElse(null);
        if (firstApprovedTurnoverForConfigIfAny!=null) return "Approved turnover found";
        if (!reportingConfigLinkRepository.findByTurnoverReportingConfig(config).isEmpty()) return "Link to a child config found";
        return null;
    }

    @Inject RepositoryService repositoryService;

    @Inject TurnoverReportingConfigLinkRepository reportingConfigLinkRepository;

}
