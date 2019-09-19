package org.incode.module.communications.dom.impl.commchannel;

import java.util.SortedSet;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

@Mixin(method = "coll")
public class CommunicationChannelOwner_communicationChannels {

    private final CommunicationChannelOwner owner;

    public CommunicationChannelOwner_communicationChannels(final CommunicationChannelOwner owner) {
        this.owner = owner;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(defaultView = "table")
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public SortedSet<CommunicationChannel> coll() {
        return ccoNewChannelContributions.communicationChannels(this.owner);
    }


    @Inject
    CommunicationChannelOwnerService ccoNewChannelContributions;
}
