package org.estatio.dom.party.relationship.contributed;

import java.util.List;
import java.util.SortedSet;
import javax.inject.Inject;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.TypicalLength;
import org.apache.isis.applib.annotation.Where;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.party.relationship.PartyRelationshipView;

@DomainService
@Hidden
public class PartyRelationShipViewCommunicationChannelContributions {

    @ActionSemantics(Of.SAFE)
    @NotContributed(As.ACTION)
    @Hidden(where = Where.OBJECT_FORMS)
    @TypicalLength(JdoColumnLength.PHONE_NUMBER)
    public String phoneNumbers(final PartyRelationshipView prv) {
        return StringUtils.join(channelTitles(prv, CommunicationChannelType.PHONE_NUMBER), ", ");
    }

    @ActionSemantics(Of.SAFE)
    @NotContributed(As.ACTION)
    @Hidden(where = Where.OBJECT_FORMS)
    @TypicalLength(JdoColumnLength.EMAIL_ADDRESS)
    public String emailAddresses(final PartyRelationshipView prv) {
        return StringUtils.join(channelTitles(prv, CommunicationChannelType.EMAIL_ADDRESS), ", ");
    }

    private List<String> channelTitles(final PartyRelationshipView prv, final CommunicationChannelType type) {
        final SortedSet<CommunicationChannel> results = communicationChannels.findByOwnerAndType(prv.getTo(), type);
        return Lists.newArrayList(Iterables.transform(results, DomainObjectContainerFunctions.titleOfUsing(container)));
    }

    @Inject
    private CommunicationChannels communicationChannels;

    @Inject
    private DomainObjectContainer container;

}
