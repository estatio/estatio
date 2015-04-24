/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
import javax.annotation.PostConstruct;
import org.isisaddons.module.poly.dom.PolymorphicAssociationLink;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CommunicationChannelOwnerLink.class
)
public class CommunicationChannelOwnerLinks {

    //region > init
    PolymorphicAssociationLink.Factory<CommunicationChannel,CommunicationChannelOwner,CommunicationChannelOwnerLink,CommunicationChannelOwnerLink.InstantiateEvent> linkFactory;

    @PostConstruct
    public void init() {
        linkFactory = container.injectServicesInto(
                new PolymorphicAssociationLink.Factory<>(
                        this,
                        CommunicationChannel.class,
                        CommunicationChannelOwner.class,
                        CommunicationChannelOwnerLink.class,
                        CommunicationChannelOwnerLink.InstantiateEvent.class
                ));

    }
    //endregion

    //region > findByCommunicationChannel (programmatic)
    @Programmatic
    public CommunicationChannelOwnerLink findByCommunicationChannel(final CommunicationChannel communicationChannel) {
        return container.firstMatch(
                new QueryDefault<>(CommunicationChannelOwnerLink.class,
                        "findByCommunicationChannel",
                        "communicationChannel", communicationChannel));
    }
    //endregion

    //region > findByOwner (programmatic)
    @Programmatic
    public List<CommunicationChannelOwnerLink> findByOwner(final CommunicationChannelOwner owner) {
        if(owner == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(owner);
        if(bookmark == null) {
            return null;
        }
        return container.allMatches(
                new QueryDefault<>(CommunicationChannelOwnerLink.class,
                        "findByOwner",
                        "ownerObjectType", bookmark.getObjectType(),
                        "ownerIdentifier", bookmark.getIdentifier()));
    }
    //endregion

    //region > findByOwnerAndCommunicationChannelType (programmatic)
    @Programmatic
    public List<CommunicationChannelOwnerLink> findByOwnerAndCommunicationChannelType(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType communicationChannelType) {
        if(owner == null) {
            return null;
        }
        if(communicationChannelType == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(owner);
        if(bookmark == null) {
            return null;
        }
        return container.allMatches(
                new QueryDefault<>(CommunicationChannelOwnerLink.class,
                        "findByOwnerAndCommunicationChannelType",
                        "ownerObjectType", bookmark.getObjectType(),
                        "ownerIdentifier", bookmark.getIdentifier(),
                        "communicationChannelType", communicationChannelType));
    }
    //endregion

    //region > createLink (programmatic)
    @Programmatic
    public CommunicationChannelOwnerLink createLink(final CommunicationChannel communicationChannel, final CommunicationChannelOwner owner) {
        final CommunicationChannelOwnerLink link = linkFactory.createLink(communicationChannel, owner);
        // copy over the type, to support subsequent querying.
        link.setCommunicationChannelType(communicationChannel.getType());
        return link;
    }
    //endregion


    //region > injected services

    @javax.inject.Inject
    private DomainObjectContainer container;

    @javax.inject.Inject
    private BookmarkService bookmarkService;

    //endregion

}
