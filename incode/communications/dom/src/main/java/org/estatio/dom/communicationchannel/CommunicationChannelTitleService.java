package org.estatio.dom.communicationchannel;

import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.title.TitleService;

import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelOwner;
import org.estatio.dom.communicationchannel.CommunicationChannelRepository;
import org.estatio.dom.communicationchannel.CommunicationChannelType;

@DomainService(nature = NatureOfService.DOMAIN)
public class CommunicationChannelTitleService {

    @Programmatic
    public String channelTitleJoined(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String separator) {
        final List<String> channelTitles = channelTitlesFor(owner, type);
        return StringUtils.join(channelTitles, separator);
    }

    private List<String> channelTitlesFor(
            CommunicationChannelOwner owner,
            final CommunicationChannelType type) {
        final SortedSet<CommunicationChannel> results = communicationChannelRepository.findByOwnerAndType(owner, type);
        return results.stream().map(input -> titleService.titleOf(input)).collect(Collectors.toList());
    }

    @Inject
    private CommunicationChannelRepository communicationChannelRepository;

    @Inject
    private TitleService titleService;

}
