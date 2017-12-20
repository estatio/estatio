package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.command.dom.T_backgroundCommands;

import org.incode.module.communications.dom.impl.comms.Communication;

/**
 * Very similar to the implementation of {@link CommunicationChannel_recentCommunications}, iterates over all
 * {@link CommunicationChannel}s for said owner.
 */
@Mixin(method = "coll")
public class CommunicationChannelOwner_recentCommunications {

    public static final int MONTHS_PREVIOUS = CommunicationChannel_recentCommunications.MONTHS_PREVIOUS;

    private final CommunicationChannelOwner communicationChannelOwner;

    public CommunicationChannelOwner_recentCommunications(final CommunicationChannelOwner communicationChannelOwner) {
        this.communicationChannelOwner = communicationChannelOwner;
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

        final List<CommunicationChannelOwnerLink> channelLinks =
                communicationChannelRepository.findByOwner(communicationChannelOwner);

        final List<Communication> communications = Lists.newArrayList();
        for (final CommunicationChannelOwnerLink link : channelLinks) {
            final List<Communication> comms = provider.findFor(link.getCommunicationChannel(), MONTHS_PREVIOUS);
            communications.addAll(comms);
        }
        communications.sort(Communication.Orderings.createdAtDescending);

        return communications;
    }


    @Inject
    CommunicationChannel_recentCommunications.Provider provider;

    @Inject
    CommunicationChannelOwnerLinkRepository communicationChannelRepository;


}
