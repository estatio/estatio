package org.estatio.dom.party.relationship.contributed;

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
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.title.TitleService;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelRepository;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.party.relationship.PartyRelationshipView;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class PartyRelationShipViewCommunicationChannelContributions {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Property(hidden = Where.OBJECT_FORMS)
    @PropertyLayout(typicalLength = JdoColumnLength.PHONE_NUMBER)
    public String phoneNumbers(final PartyRelationshipView prv) {
        return StringUtils.join(channelTitles(prv, CommunicationChannelType.PHONE_NUMBER), ", ");
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Property(hidden = Where.OBJECT_FORMS)
    @PropertyLayout(typicalLength = JdoColumnLength.EMAIL_ADDRESS)
    public String emailAddresses(final PartyRelationshipView prv) {
        return StringUtils.join(channelTitles(prv, CommunicationChannelType.EMAIL_ADDRESS), ", ");
    }

    private List<String> channelTitles(final PartyRelationshipView prv, final CommunicationChannelType type) {
        final SortedSet<CommunicationChannel> results = communicationChannelRepository.findByOwnerAndType(prv.getTo(), type);
        return Lists.newArrayList(Iterables.transform(results, input -> titleService.titleOf(input)));
    }

    @Inject
    private CommunicationChannelRepository communicationChannelRepository;

    @Inject
    private TitleService titleService;

}
