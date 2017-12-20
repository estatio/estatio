package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.command.dom.T_backgroundCommands;

import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.comms.CommunicationRepository;

@Mixin(method = "act")
public class CommunicationChannel_findCommunications {

    public static final int MONTHS_PREVIOUS = 24;

    private final CommunicationChannel communicationChannel;

    public CommunicationChannel_findCommunications(final CommunicationChannel communicationChannel) {
        this.communicationChannel = communicationChannel;
    }

    public static class ActionDomainEvent extends T_backgroundCommands.ActionDomainEvent {}

    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    public List<Communication> act(final LocalDate from, final LocalDate to) {

        final DateTime fromDateTime = toDateTime(from);
        final DateTime toDateTime = toDateTime(to).plusDays(1);

        return communicationRepository
                .findByCommunicationChannelAndPendingOrCreatedAtBetween(this.communicationChannel, fromDateTime, toDateTime);
    }

    public LocalDate default0Act() {
        return clockService.now().minusMonths(MONTHS_PREVIOUS);
    }
    public LocalDate default1Act() {
        return clockService.now();
    }

    private static DateTime toDateTime(final LocalDate localDate) {
        return localDate.toDateTimeAtStartOfDay();
    }

    @Inject
    ClockService clockService;

    @Inject
    CommunicationRepository communicationRepository;


}
