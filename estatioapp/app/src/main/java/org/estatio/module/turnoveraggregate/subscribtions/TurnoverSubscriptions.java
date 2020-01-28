package org.estatio.module.turnoveraggregate.subscribtions;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.background.BackgroundService2;
import org.apache.isis.applib.services.scratchpad.Scratchpad;

import org.estatio.module.index.dom.IndexValue;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForIndexableRepository;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.entry.Turnover_enter;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationService;

@DomainService(nature = NatureOfService.DOMAIN)
@DomainServiceLayout(menuOrder = "1")
public class TurnoverSubscriptions extends org.apache.isis.applib.AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final TurnoverRepository.TurnoverUpsertEvent ev) {
            switch (ev.getEventPhase()) {
            case EXECUTED:
                Turnover turnoverChanged = (Turnover) ev.getSource();
                backgroundService2.execute(turnoverAggregationService).aggregate(turnoverChanged);
                break;
            default:
                break;
            }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final Turnover_enter.TurnoverEnterEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            Turnover turnoverChanged = (Turnover) ev.getSource();
            backgroundService2.execute(turnoverAggregationService).aggregate(turnoverChanged);
            break;
        default:
            break;
        }
    }

    @Inject
    TurnoverAggregationService turnoverAggregationService;

    @Inject
    BackgroundService2 backgroundService2;


}
