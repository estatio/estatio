package org.estatio.dom.communicationchannel;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.JdoColumnLength;

public abstract class CommunicationChannelOwner_emailAddressTitles {

    private final CommunicationChannelOwner owner;
    private String separator;

    public CommunicationChannelOwner_emailAddressTitles(final CommunicationChannelOwner owner, final String separator) {
        this.owner = owner;
        this.separator = separator;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Property(hidden = Where.OBJECT_FORMS)
    @PropertyLayout(typicalLength = JdoColumnLength.PHONE_NUMBER)
    public String $$() {
        return communicationChannelTitleService.channelTitleJoined(owner, CommunicationChannelType.EMAIL_ADDRESS,
                separator);
    }

    @Inject
    CommunicationChannelTitleService communicationChannelTitleService;
}
