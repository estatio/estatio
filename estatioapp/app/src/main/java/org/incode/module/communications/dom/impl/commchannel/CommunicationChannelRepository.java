package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;
import java.util.Optional;
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

import org.estatio.module.party.dom.Organisation;

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
        type.ensureCompatible(PostalAddress.class);

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
        type.ensureCompatible(EmailAddress.class);

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

        type.ensureCompatible(PhoneOrFaxNumber.class);

        final PhoneOrFaxNumber pn = repositoryService.instantiate(PhoneOrFaxNumber.class);
        pn.setType(type);
        pn.setPhoneNumber(number);
        pn.setOwner(owner);
        repositoryService.persist(pn);
        return pn;
    }

    // //////////////////////////////////////

    /**
     * @param organisation
     * @param externalReferenceIfAny - if available (eg a Coda tag), can uniquely locate an address to allow an update rather than an insert.
     * @param purposeTypeIfAny - includes
     * @param description
     * @param address1
     * @param address2
     * @param address3
     * @param city
     * @param postalCode
     * @param country
     */
    @Programmatic
    public PostalAddress upsertPostalAddress(
            final Organisation organisation,
            final String externalReferenceIfAny,
            final CommunicationChannelPurposeType purposeTypeIfAny,
            final String description,
            final String address1,
            final String address2,
            final String address3,
            final String city,
            final String postalCode,
            final State state,
            final Country country) {

        PostalAddress postalAddress =
                Optional.ofNullable(externalReferenceIfAny)
                .map(externalReference ->
                        postalAddressRepository. findByOwnerAndExternalReference(
                        organisation, externalReference))
                .orElse(null);

        if(postalAddress != null) {

            postalAddress.setAddress1(address1);
            postalAddress.setAddress2(address2);
            postalAddress.setAddress3(address3);
            postalAddress.setCity(city);
            postalAddress.setPostalCode(postalCode);
            postalAddress.setState(state);
            postalAddress.setCountry(country);

        } else {

            postalAddress = newPostal(organisation,
                    CommunicationChannelType.POSTAL_ADDRESS,
                    address1, address2, address3, postalCode, city, state, country);

        }

        postalAddress.setDescription(description);
        postalAddress.setPurpose(purposeTypeIfAny);
        postalAddress.setExternalReference(externalReferenceIfAny);

        return postalAddress;
    }

    @Programmatic
    public PhoneOrFaxNumber upsertPhoneNumber(
            final Organisation organisation,
            final String externalReferenceIfAny,
            final CommunicationChannelPurposeType purposeTypeIfAny,
            final String description,
            final String phoneNumber) {

        return upsertPhoneOrFaxNumber(
                CommunicationChannelType.PHONE_NUMBER,
                organisation, externalReferenceIfAny, purposeTypeIfAny, description, phoneNumber);
    }

    @Programmatic
    public PhoneOrFaxNumber upsertFaxNumber(
            final Organisation organisation,
            final String externalReferenceIfAny,
            final CommunicationChannelPurposeType purposeTypeIfAny,
            final String description,
            final String faxNumber) {

        return upsertPhoneOrFaxNumber(
                CommunicationChannelType.FAX_NUMBER,
                organisation, externalReferenceIfAny, purposeTypeIfAny, description, faxNumber);
    }

    @Programmatic
    public PhoneOrFaxNumber upsertPhoneOrFaxNumber(
            final CommunicationChannelType communicationChannelType,
            final Organisation organisation,
            final String externalReferenceIfAny,
            final CommunicationChannelPurposeType purposeTypeIfAny,
            final String description,
            final String phoneNumber) {

        PhoneOrFaxNumber phoneOrFaxNumber =
                Optional.ofNullable(externalReferenceIfAny)
                        .map(externalReference ->
                                phoneOrFaxNumberRepository.findByOwnerAndExternalReference(
                                        communicationChannelType, organisation, externalReference))
                        .orElse(null);

        if (phoneOrFaxNumber != null) {

            phoneOrFaxNumber.setPhoneNumber(phoneNumber);

        } else {

            phoneOrFaxNumber = newPhoneOrFax(organisation,
                    communicationChannelType,
                    phoneNumber);
        }

        phoneOrFaxNumber.setDescription(description);
        phoneOrFaxNumber.setPurpose(purposeTypeIfAny);
        phoneOrFaxNumber.setExternalReference(externalReferenceIfAny);

        return phoneOrFaxNumber;
    }


    @Programmatic
    public EmailAddress upsertEmailAddress(
            final Organisation organisation,
            final String externalReferenceIfAny,
            final CommunicationChannelPurposeType purposeTypeIfAny,
            final String description,
            final String email) {

        EmailAddress emailAddress =
                Optional.ofNullable(externalReferenceIfAny)
                        .map(externalReference ->
                                emailAddressRepository.findByOwnerAndExternalReference(
                                        organisation, externalReference))
                        .orElse(null);

        if(emailAddress != null) {

            emailAddress.setEmailAddress(email);

        } else {
            emailAddress = newEmail(organisation, CommunicationChannelType.EMAIL_ADDRESS, email);
        }

        emailAddress.setDescription(description);
        emailAddress.setPurpose(purposeTypeIfAny);
        emailAddress.setExternalReference(externalReferenceIfAny);

        return emailAddress;
    }

    // //////////////////////////////////////


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
    PostalAddressRepository postalAddressRepository;
    @Inject
    PhoneOrFaxNumberRepository phoneOrFaxNumberRepository;
    @Inject
    EmailAddressRepository emailAddressRepository;

    @Inject
    RepositoryService repositoryService;

}