/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.communicationchannel;

import java.util.List;
import javax.inject.Inject;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(menuOrder = "70", repositoryFor = EmailAddress.class)
@Hidden
public class EmailAddresses extends UdoDomainRepositoryAndFactory<EmailAddress> {

    public EmailAddresses() {
        super(EmailAddresses.class, EmailAddress.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public EmailAddress findByEmailAddress(
            final CommunicationChannelOwner owner, 
            final String emailAddress) {

        final List<CommunicationChannelOwnerLink> links =
                communicationChannelOwnerLinks.findByOwnerAndCommunicationChannelType(owner, CommunicationChannelType.EMAIL_ADDRESS);
        final Iterable<EmailAddress> emailAddresses =
                Iterables.transform(
                        links,
                        CommunicationChannelOwnerLink.Functions.communicationChannel(EmailAddress.class));
        final Optional<EmailAddress> emailAddressIfFound =
                Iterables.tryFind(emailAddresses, EmailAddress.Predicates.equalTo(emailAddress));
        return emailAddressIfFound.orNull();
    }

    @Inject
    CommunicationChannelOwnerLinks communicationChannelOwnerLinks;

}
