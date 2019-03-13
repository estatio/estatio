package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.country.dom.impl.Country;

/**
 * Domain service acting as repository for finding existing {@link PostalAddress postal address}es.
 */
@DomainService(repositoryFor = PostalAddress.class, nature = NatureOfService.DOMAIN)
public class PostalAddressRepository {

    public String getId() {
        return "incodeCommunications.PostalAddressRepository";
    }

    public String iconName() {
        return PostalAddress.class.getSimpleName();
    }


    // //////////////////////////////////////

    @Programmatic
    public PostalAddress findByOwnerAndExternalReference(
            final CommunicationChannelOwner owner,
            final String externalReference) {

        return communicationChannelOwnerLinkRepository.findByOwnerAndCommunicationChannelTypeAndExternalReference(
                owner, CommunicationChannelType.POSTAL_ADDRESS, PostalAddress.class, externalReference );
    }

    @Programmatic
    public PostalAddress findByAddress(
            final CommunicationChannelOwner owner, 
            final String address1, 
            final String postalCode, 
            final String city, 
            final Country country) {

        // TODO: rewrite to use JDK8 streams
        final List<CommunicationChannelOwnerLink> links =
                communicationChannelOwnerLinkRepository.findByOwnerAndCommunicationChannelType(owner, CommunicationChannelType.POSTAL_ADDRESS);
        final Iterable<PostalAddress> postalAddresses =
                Iterables.transform(
                        links,
                        CommunicationChannelOwnerLink.Functions.communicationChannel(PostalAddress.class));
        final Optional<PostalAddress> postalAddressIfFound =
                Iterables.tryFind(postalAddresses, PostalAddress.Predicates.equalTo(address1, postalCode, city, country));
        return postalAddressIfFound.orNull();
    }

    @Programmatic
    public PostalAddress findByAddress(
            final CommunicationChannelOwner owner,
            final String address1,
            final String address2,
            final String address3,
            final String postalCode,
            final String city,
            final Country country) {

        final List<CommunicationChannelOwnerLink> links =
                communicationChannelOwnerLinkRepository.findByOwnerAndCommunicationChannelType(owner, CommunicationChannelType.POSTAL_ADDRESS);
        final Iterable<PostalAddress> postalAddresses =
                Iterables.transform(
                        links,
                        CommunicationChannelOwnerLink.Functions.communicationChannel(PostalAddress.class));
        final Optional<PostalAddress> postalAddressIfFound =
                Iterables.tryFind(postalAddresses, PostalAddress.Predicates.equalTo(address1, address2, address3, postalCode, city, country));
        return postalAddressIfFound.orNull();
    }


    // //////////////////////////////////////

    @Inject
    CommunicationChannelOwnerLinkRepository communicationChannelOwnerLinkRepository;

}
