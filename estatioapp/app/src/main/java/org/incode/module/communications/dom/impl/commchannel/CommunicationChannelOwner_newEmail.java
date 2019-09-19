package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

@Mixin(method = "act")
public class CommunicationChannelOwner_newEmail {

    private final CommunicationChannelOwner owner;

    public CommunicationChannelOwner_newEmail(final CommunicationChannelOwner owner) {
        this.owner = owner;
    }

    @MemberOrder(name = "CommunicationChannels", sequence = "2")
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public CommunicationChannelOwner act(
            @ParameterLayout(named = "Type")
            final CommunicationChannelType type,
            @ParameterLayout(named = "Address")
            final String address) {
        return ccoNewChannelContributions.newEmail(this.owner, type, address);
    }

    public List<CommunicationChannelType> choices0NewEmail() {
        return ccoNewChannelContributions.choices1NewEmail();
    }

    public CommunicationChannelType default0NewEmail() {
        return ccoNewChannelContributions.default1NewEmail();
    }

    public String validateNewEmail(
            final CommunicationChannelType type,
            final String address) {
        return ccoNewChannelContributions.validateNewEmail(this.owner, type, address);
    }


    @Inject
    CommunicationChannelOwnerService ccoNewChannelContributions;
}
