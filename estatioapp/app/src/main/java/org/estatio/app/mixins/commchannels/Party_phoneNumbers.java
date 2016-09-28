package org.estatio.app.mixins.commchannels;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.communicationchannel.CommunicationChannelOwner_phoneNumberTitles;
import org.estatio.dom.party.Party;

@Mixin
public class Party_phoneNumbers extends CommunicationChannelOwner_phoneNumberTitles {

    public Party_phoneNumbers(final Party party) {
        super(party, " | ");
    }

}
