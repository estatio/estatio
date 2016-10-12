package org.incode.module.communications.dom.impl.commchannel;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

public abstract class CommunicationChannelOwner_phoneNumberTitles {


    private final CommunicationChannelOwner owner;
    private String separator;

    public CommunicationChannelOwner_phoneNumberTitles(final CommunicationChannelOwner owner, final String separator) {
        this.owner = owner;
        this.separator = separator;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Property(hidden = Where.OBJECT_FORMS)
    @PropertyLayout(typicalLength = CommunicationChannel.PhoneNumberType.MAX_LEN)
    public String $$() {
        return communicationChannelTitleService.channelTitleJoined(owner, CommunicationChannelType.EMAIL_ADDRESS,
                separator);
    }

    @Inject
    CommunicationChannelTitleService communicationChannelTitleService;

}
