/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Mandatory;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Optional;
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
                    "   && subjectEventType == :subjectEventType")
})    
public class Event
        extends EstatioMutableObject<Event> {

    public Event() {
        super("date, subject, subjectEventType");
    }

    // //////////////////////////////////////

    private LocalDate date;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Mandatory
    @Disabled
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

    private String subjectEventType;

    /**
     * The nature of this event, as defined by the event's {@link #getSubject() subject}.
     * 
     * <p>
     * For example, a lease's <tt>BreakOption</tt> has three dates: the break date, the notification
     * date and the confirmation date.  These therefore correspond to three different types with respect
     * to a <tt>BreakOption</tt>.
     */
    @javax.jdo.annotations.Column(allowsNull = "false", length=JdoColumnLength.Event.TYPE)
    @Disabled
    @Title(prepend=": ", sequence="2")
    public String getSubjectEventType() {
        return subjectEventType;
    }

    public void setSubjectEventType(final String subjectEventType) {
        this.subjectEventType = subjectEventType;
    }

    // //////////////////////////////////////

    private String notes;

    @javax.jdo.annotations.Column(allowsNull = "true", length=JdoColumnLength.NOTES)
    @MultiLine(numberOfLines=8)
    public String getNotes() {
        return notes;
    }

    public void setNotes(final String description) {
        this.notes = description;
    }


}
