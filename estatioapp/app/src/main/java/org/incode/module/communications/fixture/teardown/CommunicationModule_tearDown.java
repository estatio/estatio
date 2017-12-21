package org.incode.module.communications.fixture.teardown;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerLink;
import org.incode.module.communications.dom.impl.comms.CommChannelRole;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.paperclips.PaperclipForCommunication;

public class CommunicationModule_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(ExecutionContext executionContext) {
        deleteFrom(CommChannelRole.class);
        deleteFrom(PaperclipForCommunication.class);
        deleteFrom(Communication.class);
        deleteFrom(CommunicationChannelOwnerLink.class);
        deleteFrom(CommunicationChannel.class);
    }


}
