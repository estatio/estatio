package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.command.dom.T_backgroundCommands;

import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.comms.CommunicationRepository;

/**
 * Very similar to the implementation of {@link CommunicationChannel_findCommunications}, iterates over all
 * {@link CommunicationChannel}s for said owner.
 */
@Mixin(method = "act")
public class CommunicationChannelOwner_findCommunications {

    public static final int MONTHS_PREVIOUS = CommunicationChannel_findCommunications.MONTHS_PREVIOUS;

    private final CommunicationChannelOwner communicationChannelOwner;

    public CommunicationChannelOwner_findCommunications(final CommunicationChannelOwner communicationChannelOwner) {
        this.communicationChannelOwner = communicationChannelOwner;
    }

    public static class ActionDomainEvent extends T_backgroundCommands.ActionDomainEvent {}

    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    public List<Communication> act(final LocalDate from, final LocalDate to) {

        final DateTime fromDateTime = toDateTime(from);
        final DateTime toDateTime = toDateTime(to).plusDays(1);


        final List<CommunicationChannelOwnerLink> channelLinks =
                communicationChannelRepository.findByOwner(communicationChannelOwner);

        final List<Communication> communications = Lists.newArrayList();
        for (final CommunicationChannelOwnerLink link : channelLinks) {

            final List<Communication> comms = communicationRepository
                    .findByCommunicationChannelAndPendingOrCreatedAtBetween(
                            link.getCommunicationChannel(), fromDateTime, toDateTime);
            communications.addAll(comms);
        }
        communications.sort(Communication.Orderings.createdAtDescending);

        return communications;
    }

    private static DateTime toDateTime(final LocalDate localDate) {
        return localDate.toDateTimeAtStartOfDay();
    }


    public LocalDate default0Act() {
        return clockService.now().minusMonths(MONTHS_PREVIOUS);
    }
    public LocalDate default1Act() {
        return clockService.now();
    }


    @Inject
    ClockService clockService;

    @Inject
    CommunicationRepository communicationRepository;

    @Inject
    CommunicationChannelOwnerLinkRepository communicationChannelRepository;

}
