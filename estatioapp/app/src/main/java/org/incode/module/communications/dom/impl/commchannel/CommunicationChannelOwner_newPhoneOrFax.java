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
        return ccoNewChannelContributions.newPhoneOrFax(this.owner, type, number);
    }

    public List<CommunicationChannelType> choices0NewPhoneOrFax() {
        return ccoNewChannelContributions.choices1NewPhoneOrFax();
    }

    public CommunicationChannelType default0NewPhoneOrFax() {
        return ccoNewChannelContributions.default1NewPhoneOrFax();
    }

    public String validateNewPhoneOrFax(
            final CommunicationChannelType type,
            final String number) {
        return ccoNewChannelContributions.validateNewPhoneOrFax(this.owner, type, number);
    }


    @Inject
    CommunicationChannelOwnerService ccoNewChannelContributions;
}
