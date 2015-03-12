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
package org.estatio.dom.event;

import java.util.List;
import javax.inject.Inject;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Event.class)
public class Events extends UdoDomainRepositoryAndFactory<Event> {

    public Events() {
        super(Events.class, Event.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Event> findBySource(final EventSource eventSource) {
        final List<EventSourceLink> links = eventSourceLinks.findBySource(eventSource);
        return Lists.newArrayList(
                Iterables.transform(links, EventSourceLink.Functions.event()));
    }

    @Programmatic
    public Event findBySourceAndCalendarName(
            final EventSource eventSource,
            final String calendarName) {
        final EventSourceLink link = eventSourceLinks.findBySourceAndCalendarName(eventSource, calendarName);
        return link != null? link.getEvent(): null;
    }

    @Programmatic
    public Event newEvent(final LocalDate date, final EventSource eventSource, final String calendarName) {
        final Event event = newTransientInstance(Event.class);
        event.setDate(date);
        event.setCalendarName(calendarName);
        event.setSource(eventSource);
        persistIfNotAlready(event);

        return event;
    }


    @Programmatic
    public void remove(Event event) {
        final EventSourceLink link = eventSourceLinks.findByEvent(event);
        removeIfNotAlready(link);
        getContainer().flush();
        removeIfNotAlready(event);
        getContainer().flush();
    }


    @Programmatic
    public List<Event> findEventsInDateRange(final LocalDate rangeStartDate, final LocalDate rangeEndDate) {
        return allMatches("findInDateRange", "rangeStartDate", rangeStartDate, "rangeEndDate", rangeEndDate);
    }

    @Programmatic
    public List<Event> allEvents() {
        return allInstances();
    }


    @Inject
    private EventSourceLinks eventSourceLinks;
}
