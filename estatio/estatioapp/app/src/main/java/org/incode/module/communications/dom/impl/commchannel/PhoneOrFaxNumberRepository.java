package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(repositoryFor = PhoneOrFaxNumber.class, nature = NatureOfService.DOMAIN)
public class PhoneOrFaxNumberRepository {

    public String getId() {
        return "incodeCommunications.PhoneOrFaxNumberRepository";
    }

    public String iconName() {
        return PhoneOrFaxNumber.class.getSimpleName();
    }


    // //////////////////////////////////////

    @Programmatic
    public PhoneOrFaxNumber findPhoneNumberByOwnerAndExternalReference(
            final CommunicationChannelOwner owner,
            final String externalReference) {

        final CommunicationChannelType communicationChannelType = CommunicationChannelType.PHONE_NUMBER;
        return findByOwnerAndExternalReference(communicationChannelType, owner, externalReference);
    }

    @Programmatic
    public PhoneOrFaxNumber findFaxNumberByOwnerAndExternalReference(
            final CommunicationChannelOwner owner,
            final String externalReference) {

        final CommunicationChannelType communicationChannelType = CommunicationChannelType.FAX_NUMBER;
        return findByOwnerAndExternalReference(communicationChannelType, owner, externalReference);
    }

    @Programmatic
    public PhoneOrFaxNumber findByOwnerAndExternalReference(
            final CommunicationChannelType communicationChannelType,
            final CommunicationChannelOwner owner, final String externalReference) {
        return communicationChannelOwnerLinkRepository.findByOwnerAndCommunicationChannelTypeAndExternalReference(
                owner, communicationChannelType, PhoneOrFaxNumber.class, externalReference);
    }


    @Programmatic
    public PhoneOrFaxNumber findByPhoneOrFaxNumber(
            final CommunicationChannelOwner owner,
            final String phoneNumber) {

        final Optional<PhoneOrFaxNumber> phoneNumberIfFound = findByPhoneOrFaxNumber(owner, phoneNumber, CommunicationChannelType.PHONE_NUMBER);
        if(phoneNumberIfFound.isPresent()) {
            return phoneNumberIfFound.get();
        }

        final Optional<PhoneOrFaxNumber> faxNumberIfFound = findByPhoneOrFaxNumber(owner, phoneNumber, CommunicationChannelType.FAX_NUMBER);
        return faxNumberIfFound.orNull();
    }

    private Optional<PhoneOrFaxNumber> findByPhoneOrFaxNumber(
            final CommunicationChannelOwner owner, final String phoneNumber, final CommunicationChannelType communicationChannelType) {
        final List<CommunicationChannelOwnerLink> links =
                communicationChannelOwnerLinkRepository.findByOwnerAndCommunicationChannelType(owner, communicationChannelType);
        final Iterable<PhoneOrFaxNumber> phoneOrFaxNumbers =
                Iterables.transform(
                        links,
                        CommunicationChannelOwnerLink.Functions.communicationChannel(PhoneOrFaxNumber.class));
        return Iterables.tryFind(phoneOrFaxNumbers, PhoneOrFaxNumber.Predicates.equalTo(phoneNumber, communicationChannelType));
    }

    // //////////////////////////////////////

    @Inject
    CommunicationChannelOwnerLinkRepository communicationChannelOwnerLinkRepository;

}
