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
package org.estatio.dom.event;

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
import org.estatio.dom.communicationchannel.CommunicationChannelOwnerLink;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CommunicationChannelOwnerLink.class
)
public class EventSourceLinks {

    //region > init
    PolymorphicAssociationLink.Factory<Event,EventSource,EventSourceLink,EventSourceLink.InstantiateEvent> linkFactory;

    @PostConstruct
    public void init() {
        linkFactory = container.injectServicesInto(
                new PolymorphicAssociationLink.Factory<>(
                        this,
                        Event.class,
                        EventSource.class,
                        EventSourceLink.class,
                        EventSourceLink.InstantiateEvent.class
                ));

    }
    //endregion

    //region > findByEvent (programmatic)
    @Programmatic
    public EventSourceLink findByEvent(final Event event) {
        return container.firstMatch(
                new QueryDefault<>(EventSourceLink.class,
                        "findByEvent",
                        "event", event));
    }
    //endregion

    //region > findBySource (programmatic)
    @Programmatic
    public List<EventSourceLink> findBySource(final EventSource eventSource) {
        if(eventSource == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(eventSource);
        if(bookmark == null) {
            return null;
        }
        return container.allMatches(
                new QueryDefault<>(EventSourceLink.class,
                        "findBySource",
                        "sourceObjectType", bookmark.getObjectType(),
                        "sourceIdentifier", bookmark.getIdentifier()));
    }
    //endregion

    //region > findBySourceAndCalendarName (programmatic)
    @Programmatic
    public EventSourceLink findBySourceAndCalendarName(
            final EventSource eventSource,
            final String calendarName) {
        if(eventSource == null) {
            return null;
        }
        if(calendarName == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(eventSource);
        if(bookmark == null) {
            return null;
        }
        return container.firstMatch(
                new QueryDefault<>(EventSourceLink.class,
                        "findBySourceAndCalendarName",
                        "sourceObjectType", bookmark.getObjectType(),
                        "sourceIdentifier", bookmark.getIdentifier(),
                        "calendarName", calendarName));
    }
    //endregion

    //region > createLink (programmatic)
    @Programmatic
    public EventSourceLink createLink(final Event event, final EventSource eventSource) {
        final EventSourceLink link = linkFactory.createLink(event, eventSource);

        // copy over the calendar name (to support querying)
        link.setCalendarName(event.getCalendarName());

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
