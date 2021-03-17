package org.incode.module.communications.dom.impl.comms;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.communications.dom.spi.CurrentUserEmailAddressProvider;

@DomainService(
        repositoryFor = Communication.class,
        objectType = "incodeCommunications.CommunicationRepository",
        nature = NatureOfService.DOMAIN
)
public class CommunicationRepository  {

    public String iconName() {
        return Communication.class.getSimpleName();
    }


    @Programmatic
    public Communication createEmail(
            final String subject,
            final String atPath,
            final EmailAddress to,
            final String ccIfAny,
            final String cc2IfAny,
            final String cc3IfAny,
            final String bccIfAny,
            final String bcc2IfAny) {
        final DateTime createdAt = clockService.nowAsDateTime();

        final Communication communication = Communication.newEmail(atPath, subject, createdAt);
        serviceRegistry2.injectServicesInto(communication);

        communication.addCorrespondent(CommChannelRoleType.TO, to);
        communication.addCorrespondentIfAny(CommChannelRoleType.CC, ccIfAny);
        communication.addCorrespondentIfAny(CommChannelRoleType.CC, cc2IfAny);
        communication.addCorrespondentIfAny(CommChannelRoleType.CC, cc3IfAny);
        communication.addCorrespondentIfAny(CommChannelRoleType.BCC, bccIfAny);
        communication.addCorrespondentIfAny(CommChannelRoleType.BCC, bcc2IfAny);

        final String currentUserEmailAddress = currentUserEmailAddressProvider.currentUserEmailAddress();
        communication.addCorrespondentIfAny(CommChannelRoleType.PREPARED_BY, currentUserEmailAddress);

        repositoryService.persist(communication);

        return communication;
    }

    @Programmatic
    public Communication createPostal(
            final String subject,
            final String atPath,
            final PostalAddress to) {

        final DateTime createdAt = clockService.nowAsDateTime();
        final Communication communication = Communication.newPostal(atPath, subject, createdAt);

        serviceRegistry2.injectServicesInto(communication);

        communication.addCorrespondent(CommChannelRoleType.TO, to);

        repositoryService.persist(communication);
        return communication;
    }

    @Programmatic
    public List<Communication> findByCommunicationChannelAndPendingOrCreatedAtBetween(
            final CommunicationChannel communicationChannel,
            final DateTime fromDateTime,
            final DateTime toDateTime) {
        final List<Communication> communications =
                Lists.newArrayList(
                    repositoryService.allMatches(
                        new QueryDefault<>(Communication.class,
                                "findByCommunicationChannelAndPendingOrCreatedAtBetween",
                                "communicationChannel", communicationChannel,
                                "from", fromDateTime,
                                "to", toDateTime))
                );

        communications.sort(Communication.Orderings.createdAtDescending);

        return communications;
    }

    @Programmatic
    public List<Communication> findByCommunicationChannel(
            final CommunicationChannel communicationChannel) {
        final List<Communication> communications =
                Lists.newArrayList(
                        repositoryService.allMatches(
                                new QueryDefault<>(Communication.class,
                                        "findByCommunicationChannel",
                                        "communicationChannel", communicationChannel))
                );

        communications.sort(Communication.Orderings.createdAtDescending);

        return communications;
    }

    @Inject
    CurrentUserEmailAddressProvider currentUserEmailAddressProvider;

    @Inject
    ClockService clockService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    RepositoryService repositoryService;

}
