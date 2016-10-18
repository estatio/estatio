package org.incode.module.communications.dom.impl.commchannel;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

public abstract class CommunicationChannelOwner_communicationChannelTitlesAbstract {

    private final CommunicationChannelOwner owner;
    private String separator;
    private final CommunicationChannelType communicationChannelType;

    public CommunicationChannelOwner_communicationChannelTitlesAbstract(
            final CommunicationChannelOwner owner,
            final String separator,
            final CommunicationChannelType communicationChannelType) {
        this.owner = owner;
        this.separator = separator;
        this.communicationChannelType = communicationChannelType;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Property(hidden = Where.OBJECT_FORMS)
    public String $$() {
        return communicationChannelTitleService.channelTitleJoined(owner, communicationChannelType,
                separator);
    }

    @Inject
    CommunicationChannelTitleService communicationChannelTitleService;
}
