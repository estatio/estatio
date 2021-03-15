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
package org.estatio.module.event.dom;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Function;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Title;

import org.isisaddons.module.poly.dom.PolymorphicAssociationLink;

import org.estatio.module.event.dom.types.CalendarNameType;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByEvent", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.event.dom.EventSourceLink "
                        + "WHERE event == :event"),
        @javax.jdo.annotations.Query(
                name = "findBySource", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.event.dom.EventSourceLink "
                        + "WHERE sourceObjectType == :sourceObjectType "
                        + "   && sourceIdentifier == :sourceIdentifier "),
        @javax.jdo.annotations.Query(
                name = "findBySourceAndCalendarName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.event.dom.EventSourceLink "
                        + "WHERE sourceObjectType == :sourceObjectType "
                        + "   && sourceIdentifier == :sourceIdentifier "
                        + "   && calendarName == :calendarName")
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "EventSourceLink_main_idx",
                members = { "sourceObjectType", "sourceIdentifier", "event" })
})
@javax.jdo.annotations.Unique(name="EventSourceLink_event_source_UNQ", members = {"event","sourceObjectType","sourceIdentifier"})
@DomainObject(
        objectType = "event.EventSourceLink"    // TODO: migrate to IncodeEvent in future
)
public abstract class EventSourceLink extends PolymorphicAssociationLink<Event, EventSource, EventSourceLink> {


    public static class InstantiateEvent
            extends PolymorphicAssociationLink.InstantiateEvent<Event, EventSource, EventSourceLink> {

        public InstantiateEvent(final Object source, final Event subject, final EventSource owner) {
            super(EventSourceLink.class, source, subject, owner);
        }
    }

    //region > constructor
    public EventSourceLink() {
        super("{polymorphicReference} has {subject}");
    }
    //endregion

    //region > SubjectPolymorphicReferenceLink API

    /**
     * The subject of the pattern, which (perhaps confusingly in this instance) is actually the
     * {@link #getEvent() event}.
     */
    @Override
    @Programmatic
    public Event getSubject() {
        return getEvent();
    }

    @Override
    @Programmatic
    public void setSubject(final Event subject) {
        setEvent(subject);
    }

    @Override
    @Programmatic
    public String getPolymorphicObjectType() {
        return getSourceObjectType();
    }

    @Override
    @Programmatic
    public void setPolymorphicObjectType(final String polymorphicObjectType) {
        setSourceObjectType(polymorphicObjectType);
    }

    @Override
    @Programmatic
    public String getPolymorphicIdentifier() {
        return getSourceIdentifier();
    }

    @Override
    @Programmatic
    public void setPolymorphicIdentifier(final String polymorphicIdentifier) {
        setSourceIdentifier(polymorphicIdentifier);
    }
    //endregion

    //region > event (property)
    @Column(
            allowsNull = "false",
            name = "eventId"
    )
    @Getter @Setter
    private Event event;
    //endregion

    //region > sourceObjectType (property)
    @Column(allowsNull = "false", length = 255)
    @Getter @Setter
    private String sourceObjectType;
    //endregion

    //region > sourceIdentifier (property)
    @Column(allowsNull = "false", length = 255)
    @Getter @Setter
    private String sourceIdentifier;
    //endregion

    //region > calendarName (property)
    /**
     * Copy of the {@link #getEvent() event}'s {@link Event#getCalendarName() calendar name}.
     *
     * <p>
     *     To support querying.  This is an immutable property of {@link Event} so
     *     it is safe to copy.
     * </p>
     */
    @javax.jdo.annotations.Column(allowsNull = "false", length= CalendarNameType.Meta.MAX_LEN)
    @Property(editing = Editing.DISABLED)
    @Title(prepend=": ", sequence="2")
    @Getter @Setter
    private String calendarName;
    //endregion

    // //////////////////////////////////////

    public static class Functions {
        public static Function<EventSourceLink, Event> event() {
            return event(Event.class);
        }
        public static <T extends Event> Function<EventSourceLink, T> event(Class<T> cls) {
            return new Function<EventSourceLink, T>() {
                @Override
                public T apply(final EventSourceLink input) {
                    return (T)input.getEvent();
                }
            };
        }
        public static Function<EventSourceLink, EventSource> owner() {
            return source(EventSource.class);
        }

        public static <T extends EventSource> Function<EventSourceLink, T> source(final Class<T> cls) {
            return new Function<EventSourceLink, T>() {
                @Override
                public T apply(final EventSourceLink input) {
                    return (T)input.getPolymorphicReference();
                }
            };
        }
    }
}
