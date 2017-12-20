package org.incode.platform.dom.communications.integtests.dom.communications.dom.demowithnotes;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner_phoneNumberTitles;
import org.incode.platform.dom.communications.integtests.demo.dom.demowithnotes.DemoObjectWithNotes;

@Mixin(method = "prop")
public class DemoObjectWithNotes_phoneNumbers extends
        CommunicationChannelOwner_phoneNumberTitles {

    public DemoObjectWithNotes_phoneNumbers(final DemoObjectWithNotes demoCustomer) {
        super(demoCustomer, " | ");
    }

}
