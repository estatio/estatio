package org.estatio.dom.communicationchannel;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

@javax.jdo.annotations.Discriminator("org.estatio.dom.communicationchannel.CommunicationChannelForTesting")
public class CommunicationChannelForTesting extends CommunicationChannel {

    public String getName() {
        return null;
    }

}
