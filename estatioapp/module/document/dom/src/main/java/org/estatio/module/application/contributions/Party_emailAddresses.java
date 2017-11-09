package org.estatio.module.application.contributions;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner_emailAddressTitles;
import org.estatio.module.party.dom.Party;

@Mixin(method = "prop")
public class Party_emailAddresses extends CommunicationChannelOwner_emailAddressTitles {
    public Party_emailAddresses(final Party party) {
        super(party, " | ");
    }
}
