package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.command.dom.T_backgroundCommands;

import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.comms.CommunicationRepository;

@Mixin(method = "coll")
public class CommunicationChannel_recentCommunications {

    public static final int MONTHS_PREVIOUS = 6;

    private final CommunicationChannel communicationChannel;

    public CommunicationChannel_recentCommunications(final CommunicationChannel communicationChannel) {
        this.communicationChannel = communicationChannel;
    }

    public static class ActionDomainEvent extends T_backgroundCommands.ActionDomainEvent {}

    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @CollectionLayout(
            defaultView = "table"
    )
    public List<Communication> coll() {
        return provider.findFor(this.communicationChannel, MONTHS_PREVIOUS);
    }

    private static DateTime toDateTime(final LocalDate localDate) {
        return localDate.toDateTimeAtStartOfDay();
    }


    /**
     * Factored out so can be injected elsewhere.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class Provider {

        @Programmatic
        public List<Communication> findFor(final CommunicationChannel communicationChannel, final int monthsPrevious) {

            LocalDate now = clockService.now();

            final DateTime fromDateTime = toDateTime(now).minusMonths(monthsPrevious);
            final DateTime toDateTime = toDateTime(now).plusDays(1);

            return communicationRepository.findByCommunicationChannelAndPendingOrCreatedAtBetween(communicationChannel, fromDateTime, toDateTime);
        }

        @Inject
        ClockService clockService;

        @Inject
        CommunicationRepository communicationRepository;

    }


    @Inject
    Provider provider;

}
