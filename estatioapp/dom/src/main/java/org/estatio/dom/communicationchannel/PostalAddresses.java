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
import org.estatio.dom.geography.Country;

/**
 * Domain service acting as repository for finding existing {@link PostalAddress postal address}es.
 */
@DomainService(menuOrder = "70", repositoryFor = PostalAddress.class)
@Hidden
public class PostalAddresses 
        extends UdoDomainRepositoryAndFactory<PostalAddress> {

    public PostalAddresses() {
        super(PostalAddresses.class, PostalAddress.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public PostalAddress findByAddress(
            final CommunicationChannelOwner owner, 
            final String address1, 
            final String postalCode, 
            final String city, 
            final Country country) {

        final List<CommunicationChannelOwnerLink> links =
                communicationChannelOwnerLinks.findByOwnerAndCommunicationChannelType(owner, CommunicationChannelType.POSTAL_ADDRESS);
        final Iterable<PostalAddress> postalAddresses =
                Iterables.transform(
                        links,
                        CommunicationChannelOwnerLink.Functions.communicationChannel(PostalAddress.class));
        final Optional<PostalAddress> postalAddressIfFound =
                Iterables.tryFind(postalAddresses, PostalAddress.Predicates.equalTo(address1, postalCode, city, country));
        return postalAddressIfFound.orNull();
    }

    @Inject
    CommunicationChannelOwnerLinks communicationChannelOwnerLinks;

}
