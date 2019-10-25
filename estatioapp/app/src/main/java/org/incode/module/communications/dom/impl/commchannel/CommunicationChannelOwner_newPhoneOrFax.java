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
public class CommunicationChannelOwner_newPhoneOrFax {

    private final CommunicationChannelOwner owner;

    public CommunicationChannelOwner_newPhoneOrFax(final CommunicationChannelOwner owner) {
        this.owner = owner;
    }

    @MemberOrder(name = "CommunicationChannels", sequence = "3")
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(named = "New Phone/Fax", contributed = Contributed.AS_ACTION)
    public CommunicationChannelOwner act(
            @ParameterLayout(named = "Type")
            final CommunicationChannelType type,
            @ParameterLayout(named = "Number")
            final String number) {
        return communicationChannelOwnerService.newPhoneOrFax(this.owner, type, number);
    }

    public List<CommunicationChannelType> choices0Act() {
        return communicationChannelOwnerService.choices1NewPhoneOrFax();
    }

    public CommunicationChannelType default0Act() {
        return communicationChannelOwnerService.default1NewPhoneOrFax();
    }

    public String validateAct(
            final CommunicationChannelType type,
            final String number) {
        return communicationChannelOwnerService.validateNewPhoneOrFax(this.owner, type, number);
    }


    @Inject
    CommunicationChannelOwnerService communicationChannelOwnerService;
}
