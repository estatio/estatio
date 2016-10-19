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

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEvent;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEventable;

import org.incode.module.base.dom.types.NotesType;
import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.event.types.CalendarNameType;

import lombok.Getter;
import lombok.Setter;

/**
 * An event that has or is scheduled to occur at some point in time, pertaining
 * to an {@link EventSource}.
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "IncodeEvent" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findInDateRange", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.event.Event " +
                    "WHERE date >= :rangeStartDate " +
                    "   && date <= :rangeEndDate")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.event.Event"
)
public class Event
        extends UdoDomainObject2<Event>
        implements CalendarEventable, WithApplicationTenancyProperty {

    private static final int NUMBER_OF_LINES = 8;

    public Event() {
        super("date, source, calendarName");
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getSource())
                .withName(getCalendarName())
                .toString();
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getSource().getApplicationTenancy();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(optionality = Optionality.MANDATORY)
    @Getter @Setter
    private LocalDate date;

    // //////////////////////////////////////
    
    /**
     * Polymorphic association to (any implementation of) {@link EventSource}.
     */
    @Property(
            editing = Editing.DISABLED,
            hidden = Where.PARENTED_TABLES,
            notPersisted = true
    )
    public EventSource getSource() {
        final EventSourceLink link = getSourceLink();
        return link != null? link.getPolymorphicReference(): null;
    }

    @Programmatic
    public void setSource(final EventSource eventSource) {
        removeSourceLink();
        eventSourceLinkRepository.createLink(this, eventSource);
    }

    private void removeSourceLink() {
        final EventSourceLink eventSourceLink = getSourceLink();
        if(eventSourceLink != null) {
            getContainer().remove(eventSourceLink);
        }
    }

    private EventSourceLink getSourceLink() {
        if (!getContainer().isPersistent(this)) {
            return null;
        }
        return eventSourceLinkRepository.findByEvent(this);
    }

    // //////////////////////////////////////

    /**
     * The name of the &quot;calendar&quot; to which this event belongs.
     *
     * <p>
     * The &quot;calendar&quot; is a string identifier that indicates the nature
     * of this event. These are expected to be uniquely identifiable for all and
     * any events that might be created. They therefore typically (always?)
     * include information relating to the type/class of the event's
     * {@link #getSource() subject}.
     *
     * <p>
     * For example, an event whose subject is a lease's
     * <tt>FixedBreakOption</tt> has three dates: the <i>break date</i>, the
     * <i>exercise date</i> and the <i>reminder date</i>. These therefore
     * correspond to three different calendar names, respectively <i>Fixed
     * break</i>, <i>Fixed break exercise</i> and <i>Fixed break exercise
     * reminder</i>.
     */
    @javax.jdo.annotations.Column(allowsNull = "false", length = CalendarNameType.Meta.MAX_LEN)
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private String calendarName;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = NotesType.Meta.MAX_LEN)
    @PropertyLayout(multiLine = NUMBER_OF_LINES)
    @Getter @Setter
    private String notes;

    // //////////////////////////////////////

    @Programmatic
    public CalendarEvent toCalendarEvent() {
        final String eventTitle = getContainer().titleOf(getSource()) + " " + getCalendarName();
        return new CalendarEvent(getDate().toDateTimeAtStartOfDay(), getCalendarName(), eventTitle);
    }

    // //////////////////////////////////////

    public final static class Functions {
        private Functions() {
        }

        public final static Function<Event, CalendarEvent> TO_CALENDAR_EVENT = new Function<Event, CalendarEvent>() {
            @Override
            public CalendarEvent apply(final Event input) {
                return input.toCalendarEvent();
            }
        };
        public final static Function<Event, String> GET_CALENDAR_NAME = new Function<Event, String>() {
            @Override
            public String apply(final Event input) {
                return input.getCalendarName();
            }
        };
    }

    public Event changeNotes(final @ParameterLayout(multiLine = NUMBER_OF_LINES) String notes) {
        setNotes(notes);

        return this;
    }

    public String default0ChangeNotes() {
        return getNotes();
    }

    @Override
    public String toString() {
        // TODO: have (temporarily?) removed source from this, cos hitting an infinite loop :-(
        return UDO_OBJECT_CONTRACTS.toStringOf(this, "date, calendarName");
    }

    @Inject
    private EventSourceLinkRepository eventSourceLinkRepository;

}
