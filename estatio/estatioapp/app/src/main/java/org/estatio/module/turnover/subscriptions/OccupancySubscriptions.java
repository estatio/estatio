package org.estatio.module.turnover.subscriptions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.currency.dom.CurrencyRepository;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.turnover.contributions.Occupancy_createTurnoverReportingConfig;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;

@DomainService(nature = NatureOfService.DOMAIN)
public class OccupancySubscriptions extends org.apache.isis.applib.AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final OccupancyRepository.OccupancyCreatedEvent ev) {
            switch (ev.getEventPhase()) {
            case EXECUTED:
                final Occupancy newOcc = ev.getSource();
                if (newOcc.getEffectiveStartDate()!=null &&
                        newOcc.getReportTurnover()!= Occupancy.OccupancyReportingType.NO &&
                        turnoverReportingConfigRepository.findByOccupancy(newOcc).isEmpty()){
                    createDefaultTurnoverReportingConfig(newOcc);
                }
                break;
            default:
                break;
            }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final Occupancy.OccupancyChangeReportingOptionsEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            final Occupancy changedOcc = ev.getSource();
            if (changedOcc.getEffectiveStartDate()!=null &&
                    changedOcc.getReportTurnover()!= Occupancy.OccupancyReportingType.NO &&
                    turnoverReportingConfigRepository.findByOccupancy(changedOcc).isEmpty()){
                createDefaultTurnoverReportingConfig(changedOcc);
            }
            break;
        default:
            break;
        }
    }

    private void createDefaultTurnoverReportingConfig(final Occupancy newOcc) {
        factoryService.mixin(Occupancy_createTurnoverReportingConfig.class, newOcc).createTurnoverReportingConfig(
                Type.PRELIMINARY,
                newOcc.getEffectiveStartDate(),
                Frequency.MONTHLY,
                newOcc.getAtPath().startsWith("/SWE") ? currencyRepository.findCurrency(
                        Currency_enum.SEK.getReference()) : currencyRepository.findCurrency(Currency_enum.EUR.getReference())
                );
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final Occupancy.RemoveEvent ev) {
        final Occupancy occupancyToBeDeleted = ev.getSource();
        switch (ev.getEventPhase()) {
        case VALIDATE:
            final TurnoverReportingConfig configWithTurnoversReported = turnoverReportingConfigRepository
                    .findByOccupancy(occupancyToBeDeleted)
                    .stream()
                    .filter(rc -> !rc.getTurnovers().isEmpty())
                    .findFirst().orElse(null);
            if (configWithTurnoversReported!=null) messageService.warnUser("Cannot remove occupancy: there are turnovers reported.");
            break;
        case EXECUTING:
            turnoverReportingConfigRepository.findByOccupancy(occupancyToBeDeleted)
            .stream()
            .filter(rc->rc.getTurnovers().isEmpty())
            .forEach(rc->repositoryService.removeAndFlush(rc));
            break;
        default:
            break;
        }
    }

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject FactoryService factoryService;

    @Inject CurrencyRepository currencyRepository;

    @Inject RepositoryService repositoryService;

    @Inject MessageService messageService;
}
