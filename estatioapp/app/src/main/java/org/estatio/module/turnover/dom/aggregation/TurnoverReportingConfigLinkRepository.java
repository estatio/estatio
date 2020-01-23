package org.estatio.module.turnover.dom.aggregation;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.turnover.dom.TurnoverReportingConfig;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = TurnoverReportingConfigLink.class)
public class TurnoverReportingConfigLinkRepository {

    public List<TurnoverReportingConfigLink> listAll() {
        return repositoryService.allInstances(TurnoverReportingConfigLink.class);
    }

    public TurnoverReportingConfigLink findUnique(
            final TurnoverReportingConfig config,
            final TurnoverReportingConfig  child
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        TurnoverReportingConfigLink.class,
                        "findUnique",
                        "turnoverReportingConfig", config,
                        "aggregationChild", child));
    }

    public List<TurnoverReportingConfigLink> findByTurnoverReportingConfig(final TurnoverReportingConfig config) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        TurnoverReportingConfigLink.class,
                        "findByTurnoverReportingConfig",
                        "turnoverReportingConfig", config)
        );
    }

    public TurnoverReportingConfigLink findOrCreate(
            final TurnoverReportingConfig config,
            final TurnoverReportingConfig child
    ) {
        TurnoverReportingConfigLink turnoverReportingConfigLink = findUnique(config, child);
        if (turnoverReportingConfigLink == null) {
            turnoverReportingConfigLink = create(config, child);
        }
        return turnoverReportingConfigLink;
    }

    public TurnoverReportingConfigLink create(final TurnoverReportingConfig config, final TurnoverReportingConfig child) {
        final TurnoverReportingConfigLink turnoverReportingConfigLink = new TurnoverReportingConfigLink(config, child);
        serviceRegistry.injectServicesInto(turnoverReportingConfigLink);
        repositoryService.persistAndFlush(turnoverReportingConfigLink);
        return turnoverReportingConfigLink;
    }

    public void remove(final TurnoverReportingConfigLink linkToRemove) {
        repositoryService.removeAndFlush(linkToRemove);
    }

    @Inject RepositoryService repositoryService;

    @Inject ServiceRegistry2 serviceRegistry;
}
