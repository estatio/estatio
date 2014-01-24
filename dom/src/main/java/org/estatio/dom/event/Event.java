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

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.danhaywood.isis.wicket.fullcalendar2.applib.CalendarEvent;
import com.danhaywood.isis.wicket.fullcalendar2.applib.CalendarEventable;
import com.google.common.base.Function;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Mandatory;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioMutableObject;
import org.estatio.dom.JdoColumnLength;

/**
 * An event that has or is scheduled to occur at some point in time, pertaining to
 * an {@link EventSubject}.
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
            name = "findBySubject", language = "JDOQL",
            value = "SELECT " +
                    "FROM org.estatio.dom.event.Event " +
                    "WHERE subject == :subject "),
    @javax.jdo.annotations.Query(
            name = "findBySubjectAndSubjectEventType", language = "JDOQL",
            value = "SELECT " +
                    "FROM org.estatio.dom.event.Event " +
                    "WHERE subject == :subject " +
                    "   && subjectEventType == :subjectEventType"),
    @javax.jdo.annotations.Query(
            name = "findInDateRange", language = "JDOQL",
            value = "SELECT " +
                    "FROM org.estatio.dom.event.Event " +
                    "WHERE date >= :rangeStartDate &&" +
                    "date <= :rangeEndDate")
})    
public class Event
        extends EstatioMutableObject<Event> 
        implements CalendarEventable {

    private static final int NUMBER_OF_LINES = 8;

    public Event() {
        super("date, subject, calendarName");
    }

    // //////////////////////////////////////

    private LocalDate date;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Mandatory
    //@Disabled
    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate startDate) {
        this.date = startDate;
    }

    // //////////////////////////////////////

    private EventSubject subject;
    
    /**
     * Polymorphic association to (any implementation of) {@link EventSubject}.
     */
    @javax.jdo.annotations.Persistent(
            extensions = {
                    @Extension(vendorName = "datanucleus",
                            key = "mapping-strategy",
                            value = "per-implementation"),
                    @Extension(vendorName = "datanucleus",
                            key = "implementation-classes",
                            value = "org.estatio.dom.lease.breaks.BreakOption") })
    @javax.jdo.annotations.Columns({
        @javax.jdo.annotations.Column(name = "subjectBreakOptionId", allowsNull="true")
    })
    @Optional // not really, but to be compatible with JDO 
    @Disabled
    @Title(sequence="1")
    public EventSubject getSubject() {
        return subject;
    }

    public void setSubject(final EventSubject subject) {
        this.subject = subject;
    }

    // //////////////////////////////////////

    private String calendarName;

    /**
     * The name of the &quot;calendar&quot; to which this event belongs.
     * 
     * <p>
     * The &quot;calendar&quot; is a string identifier that indicates the nature of this event.  These are expected
     * to be uniquely identifiable for all and any events that might be created.  They therefore typically (always?)
     * include information relating to the type/class of the event's {@link #getSubject() subject}.
     * 
     * <p>
     * For example, an event whose subject is a lease's <tt>FixedBreakOption</tt> has three dates: the 
     * <i>break date</i>, the <i>exercise date</i> and the <i>reminder date</i>.  These therefore correspond to three 
     * different calendar names, respectively <i>Fixed break</i>, <i>Fixed break exercise</i> and 
     * <i>Fixed break exercise reminder</i>.
     */
    @javax.jdo.annotations.Column(allowsNull = "false", length=JdoColumnLength.Event.CALENDAR_NAME)
    @Disabled
    @Title(prepend=": ", sequence="2")
    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(final String calendarName) {
        this.calendarName = calendarName;
    }

    // //////////////////////////////////////

    private String notes;

    @javax.jdo.annotations.Column(allowsNull = "true", length=JdoColumnLength.NOTES)
    @MultiLine(numberOfLines=NUMBER_OF_LINES)
    public String getNotes() {
        return notes;
    }

    public void setNotes(final String description) {
        this.notes = description;
    }

    // //////////////////////////////////////

    @Programmatic
    public CalendarEvent toCalendarEvent() {
        final String eventTitle = getContainer().titleOf(getSubject()) + " " + getCalendarName();
        return new CalendarEvent(getDate().toDateTimeAtStartOfDay(), getCalendarName(), eventTitle);
    }

    // //////////////////////////////////////

    public final static class Functions {
        private Functions(){}
        
        public final static Function<Event, CalendarEvent> TO_CALENDAR_EVENT = new Function<Event, CalendarEvent>() {
            @Override
            public CalendarEvent apply(final Event input) {
                return input.toCalendarEvent();
            }};
        public final static Function<Event, String> GET_CALENDAR_NAME = new Function<Event, String>() {
            @Override
            public String apply(final Event input) {
                return input.getCalendarName();
            }};
    }
}
