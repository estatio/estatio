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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(menuOrder = "85", repositoryFor = Event.class)
@Hidden
public class Events extends UdoDomainRepositoryAndFactory<Event> {

    public Events() {
        super(Events.class, Event.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    public Event newEvent(final LocalDate date, final EventSubject subject, final String calendarName) {
        final Event event = newTransientInstance(Event.class);
        event.setDate(date);
        event.setSubject(subject);
        event.setCalendarName(calendarName);
        persistIfNotAlready(event);
        return event;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    public List<Event> findEventsBySubject(final EventSubject eventSubject) {
        return allMatches("findBySubject", "subject", eventSubject);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    public Event findEventsBySubjectAndSubjectEventType(
            final EventSubject eventSubject,
            final String subjectEventType) {
        return firstMatch(
                "findBySubjectAndSubjectEventType",
                "subject", eventSubject,
                "subjectEventType", subjectEventType);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    public List<Event> findEventsInDateRange(final LocalDate rangeStartDate, final LocalDate rangeEndDate) {
        return allMatches("findInDateRange", "rangeStartDate", rangeStartDate, "rangeEndDate", rangeEndDate);
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public List<Event> allEvents() {
        return allInstances();
    }

}
