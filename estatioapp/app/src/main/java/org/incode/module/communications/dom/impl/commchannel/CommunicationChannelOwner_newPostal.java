package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.State;

@Mixin(method = "act")
public class CommunicationChannelOwner_newPostal {

    private final CommunicationChannelOwner owner;

    public CommunicationChannelOwner_newPostal(final CommunicationChannelOwner owner) {
        this.owner = owner;
    }

    @MemberOrder(name = "CommunicationChannels", sequence = "1")
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public CommunicationChannelOwner act(
            @ParameterLayout(named = "Type")
            final CommunicationChannelType type,
            final Country country,
            @Parameter(optionality = Optionality.OPTIONAL)
            final State state,
            @ParameterLayout(named = "Line 1")
            final String addressLine1,
            @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(named = "Line 2")
            final String addressLine2,
            @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(named = "Line 3")
            final String addressLine3,
            @ParameterLayout(named = "Postal code")
            final String postalCode,
            @ParameterLayout(named = "City")
            final String city
    ) {
        return communicationChannelOwnerService.newPostal(this.owner, type, country, state, addressLine1, addressLine2, addressLine3, postalCode, city);
    }

    public List<CommunicationChannelType> choices0Act() {
        return communicationChannelOwnerService.choices1NewPostal();
    }

    public CommunicationChannelType default0Act() {
        return communicationChannelOwnerService.default1NewPostal();
    }

    public Country default1Act() {
        return communicationChannelOwnerService.default2NewPostal();
    }

    public List<State> choices2Act(
            final CommunicationChannelType type,
            final Country country) {
        return communicationChannelOwnerService.choices3NewPostal(this.owner, type, country);
    }

    public State default2Act() {
        return communicationChannelOwnerService.default3NewPostal();
    }


    @Inject
    CommunicationChannelOwnerService communicationChannelOwnerService;
}
