package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.State;

@DomainService(repositoryFor = CommunicationChannel.class, nature = NatureOfService.DOMAIN)
public class CommunicationChannelRepository {

    public String getId() {
        return "incodeCommunications.CommunicationChannelRepository";
    }

    public String iconName() {
        return CommunicationChannel.class.getSimpleName();
    }

    // //////////////////////////////////////


    @Programmatic
    public PostalAddress newPostal(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String address1,
            final String address2,
            final String address3,
            final String postalCode,
            final String city,
            final State state,
            final Country country
            ) {
        final PostalAddress pa = repositoryService.instantiate(PostalAddress.class);
        pa.setType(type);
        pa.setAddress1(address1);
        pa.setAddress2(address2);
        pa.setAddress3(address3);
        pa.setCity(city);
        pa.setPostalCode(postalCode);
        pa.setState(state);
        pa.setCountry(country);
        pa.setOwner(owner);
        repositoryService.persist(pa);
        return pa;
    }

    @Programmatic
    public EmailAddress newEmail(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String address) {
        final EmailAddress ea = repositoryService.instantiate(EmailAddress.class);
        ea.setType(type);
        ea.setEmailAddress(address);
        ea.setOwner(owner);
        repositoryService.persist(ea);
        return ea;
    }

    @Programmatic
    public PhoneOrFaxNumber newPhoneOrFax(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String number) {
        final PhoneOrFaxNumber pn = repositoryService.instantiate(PhoneOrFaxNumber.class);
        pn.setType(type);
        pn.setPhoneNumber(number);
        pn.setOwner(owner);
        repositoryService.persist(pn);
        return pn;
    }

    @Programmatic
    public CommunicationChannel findByReferenceAndType(
            final String reference, final CommunicationChannelType type) {
        return this.repositoryService.firstMatch(
                new QueryDefault<>(
                        CommunicationChannel.class,
                        "findByReferenceAndType",
                        "reference", reference,
                        "type", type));
    }

    @Programmatic
    public SortedSet<CommunicationChannel> findByOwner(final CommunicationChannelOwner owner) {
        final List<CommunicationChannelOwnerLink> links = communicationChannelOwnerLinkRepository.findByOwner(owner);
        return Sets.newTreeSet(
                Iterables.transform(links, CommunicationChannelOwnerLink.Functions.communicationChannel()));
    }

    @Programmatic
    public SortedSet<CommunicationChannel> findByOwnerAndType(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type) {
        final List<CommunicationChannelOwnerLink> links =
                communicationChannelOwnerLinkRepository.findByOwnerAndCommunicationChannelType(owner, type);
        return Sets.newTreeSet(Iterables.transform(
                links, CommunicationChannelOwnerLink.Functions.communicationChannel()));
    }

    @Programmatic
    public SortedSet<CommunicationChannel> findOtherByOwnerAndType(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final CommunicationChannel exclude) {
        final SortedSet<CommunicationChannel> communicationChannels = findByOwnerAndType(owner, type);
        communicationChannels.remove(exclude);
        return communicationChannels;
    }

    // //////////////////////////////////////

    @Inject
    CommunicationChannelOwnerLinkRepository communicationChannelOwnerLinkRepository;

    @Inject
    RepositoryService repositoryService;

}