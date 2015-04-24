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
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEvent;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEventable;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;

/**
 * An event that has or is scheduled to occur at some point in time, pertaining
 * to an {@link EventSource}.
 */
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
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
@DomainObject(editing = Editing.DISABLED)
public class Event
        extends EstatioDomainObject<Event>
        implements CalendarEventable, WithApplicationTenancyProperty {

    private static final int NUMBER_OF_LINES = 8;

    public Event() {
        super("date, source, calendarName");
    }

    // //////////////////////////////////////

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getSource().getApplicationTenancy();
    }

    // //////////////////////////////////////

    private LocalDate date;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(optionality = Optionality.MANDATORY)
    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate startDate) {
        this.date = startDate;
    }

    // //////////////////////////////////////

    
    /**
     * Polymorphic association to (any implementation of) {@link EventSource}.
     */
    @Property(
            editing = Editing.DISABLED,
            hidden = Where.PARENTED_TABLES,
            notPersisted = true
    )
    @Title(sequence = "1")
    public EventSource getSource() {
        final EventSourceLink link = getSourceLink();
        return link != null? link.getPolymorphicReference(): null;
    }

    @Programmatic
    public void setSource(final EventSource eventSource) {
        removeSourceLink();
        eventSourceLinks.createLink(this, eventSource);
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
        return eventSourceLinks.findByEvent(this);
    }


    // //////////////////////////////////////

    private String calendarName;

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
    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.Event.CALENDAR_NAME)
    @Title(prepend = ": ", sequence = "2")
    @Property(editing = Editing.DISABLED)
    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(final String calendarName) {
        this.calendarName = calendarName;
    }

    // //////////////////////////////////////

    private String notes;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.NOTES)
    @PropertyLayout(multiLine = NUMBER_OF_LINES)
    public String getNotes() {
        return notes;
    }

    public void setNotes(final String description) {
        this.notes = description;
    }

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

    public Event changeNotes(final @ParameterLayout(named = "Notes", multiLine = NUMBER_OF_LINES) String notes) {
        setNotes(notes);

        return this;
    }

    public String default0ChangeNotes() {
        return getNotes();
    }



    @Inject
    private EventSourceLinks eventSourceLinks;

}
