package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.State;
import org.incode.module.country.dom.impl.StateRepository;

/**
 * Domain service that contributes actions to create a new
 * {@link #newPostal(CommunicationChannelOwner, CommunicationChannelType, Country, State, String, String, String, String, String)
 * postal address},
 * {@link #newEmail(CommunicationChannelOwner, CommunicationChannelType, String)
 * email} or
 * {@link #newPhoneOrFax(CommunicationChannelOwner, CommunicationChannelType, String)
 * phone/fax}, and contributes a collection to list the
 * {@link #communicationChannels(CommunicationChannelOwner) communication
 * channels} of a particular {@link CommunicationChannelOwner}.
 */
public abstract class CommunicationChannelOwner_newChannelContributions  {

    private final Class<? extends CommunicationChannelOwner_newChannelContributions> serviceType;

    protected CommunicationChannelOwner_newChannelContributions(final Class<? extends CommunicationChannelOwner_newChannelContributions> serviceType) {
        this.serviceType = serviceType;
    }

    public CommunicationChannelOwner_newChannelContributions() {
        this(CommunicationChannelOwner_newChannelContributions.class);
    }


    // //////////////////////////////////////

    @Programmatic
    public String getId() {
        return getClass().getName();
    }

    public String iconName() {
        return serviceType.getSimpleName();
    }

    // //////////////////////////////////////


    @MemberOrder(name = "CommunicationChannels", sequence = "1")
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public CommunicationChannelOwner newPostal(
            final CommunicationChannelOwner owner,
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
        communicationChannelRepository.newPostal(owner, type, addressLine1, addressLine2, null, postalCode, city, state, country);
        return owner;
    }

    public List<CommunicationChannelType> choices1NewPostal() {
        return CommunicationChannelType.matching(PostalAddress.class);
    }

    public CommunicationChannelType default1NewPostal() {
        return choices1NewPostal().get(0);
    }

    public Country default2NewPostal() {
        return countryRepository.allCountries().get(0);
    }

    public List<State> choices3NewPostal(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final Country country) {
        return stateRepository.findStatesByCountry(country);
    }

    public State default3NewPostal() {
        final Country country = default2NewPostal();
        final List<State> statesInCountry = stateRepository.findStatesByCountry(country);
        return statesInCountry.size() > 0 ? statesInCountry.get(0) : null;
    }

    // //////////////////////////////////////

    @MemberOrder(name = "CommunicationChannels", sequence = "2")
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public CommunicationChannelOwner newEmail(
            final CommunicationChannelOwner owner,
            @ParameterLayout(named = "Type")
            final CommunicationChannelType type,
            @ParameterLayout(named = "Address")
            final String address) {
        communicationChannelRepository.newEmail(owner, type, address);
        return owner;
    }

    public List<CommunicationChannelType> choices1NewEmail() {
        return CommunicationChannelType.matching(EmailAddress.class);
    }

    public CommunicationChannelType default1NewEmail() {
        return choices1NewEmail().get(0);
    }

    public String validateNewEmail(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String address) {
        // TODO: validate email address format
        return null;
    }

    // //////////////////////////////////////

    @MemberOrder(name = "CommunicationChannels", sequence = "3")
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(named = "New Phone/Fax", contributed = Contributed.AS_ACTION)
    public CommunicationChannelOwner newPhoneOrFax(
            final CommunicationChannelOwner owner,
            @ParameterLayout(named = "Type")
            final CommunicationChannelType type,
            @ParameterLayout(named = "Number")
            final String number) {
        communicationChannelRepository.newPhoneOrFax(owner, type, number);
        return owner;
    }

    public List<CommunicationChannelType> choices1NewPhoneOrFax() {
        return CommunicationChannelType.matching(PhoneOrFaxNumber.class);
    }

    public CommunicationChannelType default1NewPhoneOrFax() {
        return choices1NewPhoneOrFax().get(0);
    }

    public String validateNewPhoneOrFax(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String number) {
        // TODO: validate phone number format
        return null;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public SortedSet<CommunicationChannel> communicationChannels(final CommunicationChannelOwner owner) {
        return communicationChannelRepository.findByOwner(owner);
    }

    // //////////////////////////////////////

    @Inject
    public CommunicationChannelRepository communicationChannelRepository;

    @Inject
    StateRepository stateRepository;

    @Inject
    CountryRepository countryRepository;

}
