package org.estatio.module.application.contributions;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner_phoneNumberTitles;
import org.estatio.module.party.dom.relationship.PartyRelationshipView;

@Mixin(method = "prop")
public class PartyRelationshipView_phoneNumbers extends CommunicationChannelOwner_phoneNumberTitles {
    public PartyRelationshipView_phoneNumbers(final PartyRelationshipView party) {
        super(party.getTo(), ", ");
    }
}
