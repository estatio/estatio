package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(repositoryFor = EmailAddress.class, nature = NatureOfService.DOMAIN)
public class EmailAddressRepository {

    public String getId() {
        return "incodeCommunications.EmailAddressRepository";
    }

    public String iconName() {
        return EmailAddress.class.getSimpleName();
    }


    // //////////////////////////////////////

    @Programmatic
    public EmailAddress findByOwnerAndExternalReference(
            final CommunicationChannelOwner owner,
            final String externalReference) {

        return communicationChannelOwnerLinkRepository.findByOwnerAndCommunicationChannelTypeAndExternalReference(
                owner, CommunicationChannelType.EMAIL_ADDRESS, EmailAddress.class, externalReference );
    }

    @Programmatic
    public EmailAddress findByEmailAddress(
            final CommunicationChannelOwner owner, 
            final String emailAddress) {

        final List<CommunicationChannelOwnerLink> links =
                communicationChannelOwnerLinkRepository.findByOwnerAndCommunicationChannelType(owner, CommunicationChannelType.EMAIL_ADDRESS);
        final Iterable<EmailAddress> emailAddresses =
                Iterables.transform(
                        links,
                        CommunicationChannelOwnerLink.Functions.communicationChannel(EmailAddress.class));
        final Optional<EmailAddress> emailAddressIfFound =
                Iterables.tryFind(emailAddresses, EmailAddress.Predicates.equalTo(emailAddress));
        return emailAddressIfFound.orNull();
    }

    @Inject
    CommunicationChannelOwnerLinkRepository communicationChannelOwnerLinkRepository;


}
