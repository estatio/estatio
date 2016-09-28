package org.estatio.app.mixins.commchannels;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.communicationchannel.CommunicationChannelOwner_phoneNumberTitles;
import org.estatio.dom.party.relationship.PartyRelationshipView;

@Mixin
public class PartyRelationshipView_phoneNumbers extends CommunicationChannelOwner_phoneNumberTitles {
    public PartyRelationshipView_phoneNumbers(final PartyRelationshipView party) {
        super(party.getTo(), ", ");
    }
}
