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
package org.estatio.module.lease.dom.breaks;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEventable;

import org.incode.module.base.dom.types.DescriptionType;
import org.incode.module.base.dom.utils.JodaPeriodUtils;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.AbstractInterval;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.module.event.dom.Event;
import org.estatio.module.event.dom.EventRepository;
import org.estatio.module.event.dom.EventSource;
import org.estatio.module.lease.dom.Lease;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a condition upon which the {@link Lease} can be terminated.
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"  // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.dom.lease.breaks.BreakOption"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Unique(
        name = "BreakOption_lease_type_breakDate_exerciseType_exerciseDate_UNQ",
        members = { "lease", "type", "breakDate", "exerciseType", "exerciseDate" })
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.breaks.BreakOption "
                        + "WHERE lease == :lease"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndTypeAndBreakDateAndExerciseType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.breaks.BreakOption "
                        + "WHERE lease == :lease "
                        + "&& type == :type"
                        + "&& breakDate == :breakDate "
                        + "&& exerciseType == :exerciseType ") })
@DomainObject(editing = Editing.DISABLED)
public abstract class BreakOption
        extends UdoDomainObject2<BreakOption>
        implements EventSource, WithApplicationTenancyProperty {

    public BreakOption() {
        super("lease, type, exerciseType, breakDate, exerciseDate");
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getLease())
                .withName(getExerciseType())
                .withName(getBreakDate())
                .toString();
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Inherited from Lease; determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getLease().getApplicationTenancy();
    }

    // //////////////////////////////////////


    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Lease lease;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = BreakType.Meta.MAX_LEN)
    @Getter @Setter
    private BreakType type;

    // //////////////////////////////////////

    /**
     * The date on which this break option can be exercised, meaning that the
     * other party to the lease is notified (also known as <i>notification
     * date</i>).
     *
     * <p>
     * The exact semantics depend upon the particular {@link #getType() type}
     * (subclass) of option:
     * <ul>
     * <li>
     * In the case of the {@link FixedBreakOption}, the notification date is the
     * <i>last</i> date that an option can be exercised (it can also be
     * exercised any date prior to the notification date).</li>
     * <li>
     * In the case of a {@link RollingBreakOption}, the notification date is the
     * <i>first</i> date at which a option can be exercised (it can also be
     * exercised any date after the notification date).</li>
     * </ul>
     *
     * <p>
     * To avoid misunderstandings in the UI, both subtypes rename the property
     * (using Isis' <tt>@Named</tt> annotation).
     */
    @javax.jdo.annotations.Column(allowsNull = "false")
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate exerciseDate;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = BreakExerciseType.Meta.MAX_LEN)
    @Getter @Setter
    private BreakExerciseType exerciseType;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = NotificationPeriodType.Meta.MAX_LEN)
    @Getter @Setter
    private String notificationPeriod;

    /**
     * Convenience for subclasses.
     */
    protected Period getNotificationPeriodJoda() {
        return JodaPeriodUtils.asPeriod(getNotificationPeriod());
    }

    // //////////////////////////////////////

    /**
     * The date when the {@link #getLease() lease} can be terminated (assuming
     * that the notice was given on or before the {@link #getExerciseDate()}
     * exercise date}).
     */
    @javax.jdo.annotations.Column(allowsNull = "false")
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate breakDate;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = DescriptionType.Meta.MAX_LEN)
    @Property(hidden = Where.PARENTED_TABLES, optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String description;

    // //////////////////////////////////////

    public BreakOption change(
            final BreakType type,
            final BreakExerciseType exerciseType,
            final @Parameter(optionality = Optionality.OPTIONAL) String description) {
        setType(type);
        setExerciseType(exerciseType);
        setDescription(description);

        // re-create events
        removeExistingEvents();
        createEvents();

        return this;
    }

    private void removeExistingEvents() {
        // remove existing events
        for (final Event event : findEvents()) {
            eventRepository.remove(event);
        }
    }

    public BreakType default0Change() {
        return getType();
    }

    public BreakExerciseType default1Change() {
        return getExerciseType();
    }

    public String default2Change() {
        return getDescription();
    }

    @MemberOrder(name = "breakDate", sequence = "1")
    public BreakOption changeDates(
            final LocalDate breakDate,
            final LocalDate excerciseDate) {
        setBreakDate(breakDate);
        setExerciseDate(excerciseDate);
        LocalDateInterval ldi = new LocalDateInterval(excerciseDate, breakDate, AbstractInterval.IntervalEnding.EXCLUDING_END_DATE);
        final String s = JodaPeriodUtils.asSimpleString(new Period(ldi.asInterval(), PeriodType.yearMonthDay()));
        setNotificationPeriod(s);

        // re-create events
        removeExistingEvents();
        createEvents();

        return this;
    }

    public LocalDate default0ChangeDates() {
        return getBreakDate();
    }

    public LocalDate default1ChangeDates() {
        return getExerciseDate();
    }

    public String validateChangeDates(
            final LocalDate breakDate,
            final LocalDate excerciseDate) {
        return null;
    }

    @MemberOrder(name = "notificationPeriod", sequence = "1")
    public BreakOption changeNotificationPeriod(
            final String notificationPeriod
    ){
        final Period p = JodaPeriodUtils.asPeriod(notificationPeriod);
        setExerciseDate(getBreakDate().minus(p));
        setNotificationPeriod(notificationPeriod);

        // re-create events
        removeExistingEvents();
        createEvents();
        return this;
    }

    public String default0ChangeNotificationPeriod() {
        return getNotificationPeriod();
    }

    public String validateChangeNotificationPeriod(
            final String notificationPeriod
    ) {
        if (notificationPeriod != null && JodaPeriodUtils.asPeriod(notificationPeriod) == null) {
            return "Notification period is not valid, examples 1y, 1y2m, 30d";
        }
        return null;
    }


    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void remove(final String reason) {
        for (Event event : findEvents()) {
            eventRepository.remove(event);
        }

        getContainer().remove(this);
        getContainer().flush();

        return;
    }

    // //////////////////////////////////////

    /**
     * The date when the {@link #getLease() lease} can be terminated.
     *
     * <p>
     * In the case of an {@link FixedBreakOption}, this is a fixed date. In the
     * case of a {@link RollingBreakOption}, this date will be fixed until the
     * {@link RollingBreakOption#getExerciseDate()}  notification
     * date} has been reached; thereafter it will change on a daily basis (being
     * the current date plus the {@link #getNotificationPeriod() notification
     * period}.
     */
    public abstract LocalDate getCurrentBreakDate();

    // //////////////////////////////////////


    /**
     * to display in fullcalendar2
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Programmatic
    @Override
    public ImmutableMap<String, CalendarEventable> getCalendarEvents() {
        final ImmutableMap eventsByCalendarName = Maps.uniqueIndex(
                findEvents(), Event.Functions.GET_CALENDAR_NAME);
        return eventsByCalendarName;
    }

    private List<Event> findEvents() {
        return eventRepository.findBySource(this);
    }

    // //////////////////////////////////////

    /**
     * For subclasses to call, eg in their <tt>persisting()</tt> callback
     * methods.
     */
    protected Event createEvent(final LocalDate date, final EventSource subject, final String subjectEventType) {
        return eventRepository.newEvent(date, subject, subjectEventType);
    }

    // //////////////////////////////////////

    public static final class Predicates {
        private Predicates() {
        }

        public static Predicate<BreakOption> whetherTypeAndBreakDate(
                final BreakType type, final LocalDate breakDate) {
            return com.google.common.base.Predicates.and(
                    whetherType(type), whetherBreakDate(breakDate));
        }

        public static Predicate<BreakOption> whetherType(final BreakType type) {
            return new Predicate<BreakOption>() {
                @Override
                public boolean apply(final BreakOption breakOption) {
                    return Objects.equal(breakOption.getType(), type);
                }
            };
        }

        public static Predicate<BreakOption> whetherBreakDate(final LocalDate breakDate) {
            return new Predicate<BreakOption>() {
                @Override
                public boolean apply(final BreakOption breakOption) {
                    return Objects.equal(breakOption.getBreakDate(), breakDate);
                }
            };
        }
    }

    // //////////////////////////////////////

    public void persisted() {
        createEvents();
    }

    protected void createEvents() {
        throw new IllegalAccessError("Subclass must override");
    }

    // //////////////////////////////////////

    @Inject
    protected EventRepository eventRepository;


    // //////////////////////////////////////


    public static class NotificationPeriodType {

        private NotificationPeriodType() {}

        public static class Meta {

            public static final int MAX_LEN = 20;

            private Meta() {}

        }

    }
}
