package org.estatio.app.mixins.commchannels;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner_phoneNumberTitles;
import org.estatio.module.party.dom.Party;

@Mixin(method = "prop")
public class Party_phoneNumbers extends CommunicationChannelOwner_phoneNumberTitles {

    public Party_phoneNumbers(final Party party) {
        super(party, " | ");
    }

}
