package org.estatio.module.turnover.subscriptions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;

import org.estatio.module.currency.dom.CurrencyRepository;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.turnover.contributions.Occupancy_createTurnoverReportingConfig;
import org.estatio.module.turnover.dom.Frequency;
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

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject FactoryService factoryService;

    @Inject CurrencyRepository currencyRepository;
}
