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
package org.estatio.dom.lease.breaks;

import java.util.Set;

import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.event.Event;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
// no @DatastoreIdentity nor @Version, since inherited from supertype
@DomainObject(
        objectType = "lease.FixedBreakOption"
)
public class FixedBreakOption
        extends BreakOption {

    private static final String CALENDAR_NAME_FIXED_BREAK = "Fixed break date";
    private static final String CALENDAR_NAME_FIXED_BREAK_EXERCISE = "Fixed break exercise";
    private static final String CALENDAR_NAME_FIXED_BREAK_EXERCISE_REMINDER = "Fixed break exercise reminder";

    // //////////////////////////////////////

    @Programmatic
    @Override
    public Set<String> getCalendarNames() {
        return Sets.newHashSet(
                CALENDAR_NAME_FIXED_BREAK,
                CALENDAR_NAME_FIXED_BREAK_EXERCISE,
                CALENDAR_NAME_FIXED_BREAK_EXERCISE_REMINDER);
    }

    /**
     * Dynamically name {@link #getExerciseDate()} to be
     * {@link #CALENDAR_NAME_FIXED_BREAK_EXERCISE} in the UI.
     * 
     * <p>
     * For a {@link FixedBreakOption}, the {@link #getExerciseDate()} is the
     * last (final) date when notice can be given for the {@link #getLease()
     * lease} to be terminated.
     * 
     * <p>
     * NB: implemented this way because the alternative (override and using
     * <tt>@Named</tt> annotation) resulted in an infinite stacktrace, resultant
     * from the JDO enhancement.
     */
    public static String nameExerciseDate() {
        return "Last exercise date";
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate reminderDate;

    /**
     * An optional reminder for the {@link #getExerciseDate() notification date}
     * ).
     */
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Disabled(reason = "Use action to set/clear a reminder date")
    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(final LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    // //////////////////////////////////////

    @Override
    public LocalDate getCurrentBreakDate() {
        return getBreakDate().isBefore(getClockService().now()) ? getBreakDate() : null;
    }

    // //////////////////////////////////////

    /**
     * Creates/updates/deletes a corresponding {@link Event} with a
     * {@link Event#getCalendarName() calendar name} of
     * {@link #CALENDAR_NAME_FIXED_BREAK_EXERCISE_REMINDER}.
     */
    @Named("Update")
    public FixedBreakOption updateReminderDate(
            final @Optional @Named(CALENDAR_NAME_FIXED_BREAK_EXERCISE_REMINDER) 
            @DescribedAs("Reminder to exercise (or leave blank to clear)") LocalDate reminderDate) {
        setReminderDate(reminderDate);
        final Event reminderEvent =
                events.findBySourceAndCalendarName(this, CALENDAR_NAME_FIXED_BREAK_EXERCISE_REMINDER);
        if (reminderDate != null) {
            if (reminderEvent == null) {
                // create...
                createEvent(getReminderDate(), this, CALENDAR_NAME_FIXED_BREAK_EXERCISE_REMINDER);
            } else {
                // update...
                reminderEvent.setDate(reminderDate);
            }
        } else {
            if (reminderEvent != null) {
                // delete...
                removeIfNotAlready(reminderEvent);
            }
        }
        return this;
    }

    public LocalDate default0UpdateReminderDate() {
        return getExerciseDate().minusWeeks(2);
    }

    public String disableUpdateReminderDate(final LocalDate reminderDate) {
        return getExerciseType() == BreakExerciseType.TENANT ?
                "Can only set reminders for "
                        + BreakExerciseType.LANDLORD
                        + " and "
                        + BreakExerciseType.MUTUAL
                        + " break options" : null;
    }

    public String validateUpdateReminderDate(final LocalDate reminderDate) {
        if (reminderDate == null) {
            return null;
        }
        return reminderDate.compareTo(getExerciseDate()) >= 0
                ? "Reminder must be before exercise date"
                : null;
    }

    // //////////////////////////////////////

    @Override
    protected void createEvents() {
        createEvent(getBreakDate(), this, CALENDAR_NAME_FIXED_BREAK);
        createEvent(getExerciseDate(), this, CALENDAR_NAME_FIXED_BREAK_EXERCISE);
    }

}
