package org.estatio.app.mixins.commchannels;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.title.TitleService;

import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelRepository;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.party.Party;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class PartyCommunicationChannelContributions {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Property(hidden = Where.OBJECT_FORMS)
    public String phoneNumbers(Party party) {
        return StringUtils.join(channelTitle(party, CommunicationChannelType.PHONE_NUMBER, 0), " | ");
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Property(hidden = Where.OBJECT_FORMS)
    public String emailAddresses(Party party) {
        return StringUtils.join(channelTitle(party, CommunicationChannelType.EMAIL_ADDRESS, 0), " | ");
    }

    private List<String> channelTitle(Party party, final CommunicationChannelType type, final int index) {
        final SortedSet<CommunicationChannel> results = communicationChannelRepository.findByOwnerAndType(party, type);
        return Lists.newArrayList(Iterables.transform(results, input -> titleService.titleOf(input)));
    }

    @Inject
    private CommunicationChannelRepository communicationChannelRepository;

    @Inject
    private TitleService titleService;

}
