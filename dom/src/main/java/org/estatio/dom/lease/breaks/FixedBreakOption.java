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
package org.estatio.dom.lease.breaks;

import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;

import org.estatio.dom.event.Event;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
//no @DatastoreIdentity nor @Version, since inherited from supertype
public class FixedBreakOption
        extends BreakOption {


    private static final String SUBJECT_EVENT_TYPE_BREAK_DATE = "Break date";
    private static final String SUBJECT_EVENT_TYPE_LAST_NOTIFICATION_DATE = "Last notification date";
    private static final String SUBJECT_EVENT_TYPE_REMINDER_DATE = "Reminder date";

    /**
     * Dynamically ename {@link #getNotificationDate()} to be {@link #SUBJECT_EVENT_TYPE_LAST_NOTIFICATION_DATE} in 
     * the UI.
     * 
     * <p>
     * For a {@link FixedBreakOption}, the {@link #getNotificationDate()} is the last (final) date when notice 
     * can be given for the {@link #getLease() lease} to be terminated.
     */
    public static String nameNotificationDate() {
        return SUBJECT_EVENT_TYPE_LAST_NOTIFICATION_DATE;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate breakDate;

    /**
     * The date when the {@link #getLease() lease} can be terminated (assuming that the notice
     * was given on or before the {@link #getNotificationDate() notification date}).
     */
    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    public LocalDate getBreakDate() {
        return breakDate;
    }

    public void setBreakDate(final LocalDate breakDate) {
        this.breakDate = breakDate;
    }
    

    // //////////////////////////////////////
    
    @javax.jdo.annotations.Persistent
    private LocalDate reminderDate;

    /**
     * An optional reminder for the {@link #getNotificationDate() notification date}).
     */
    @javax.jdo.annotations.Column(allowsNull="true")
    @Disabled(reason="Use action to set/clear a reminder date")
    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(final LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }
    
    // //////////////////////////////////////

    
    /**
     * Creates/updates/deletes a corresponding {@link Event} with a 
     * {@link Event#getSubjectEventType() subject event type} of {@link #SUBJECT_EVENT_TYPE_REMINDER_DATE}.
     */
    @Named("Update")
    public FixedBreakOption updateReminderDate(
            final @Optional @Named(SUBJECT_EVENT_TYPE_REMINDER_DATE) 
                  @DescribedAs("Reminder for notification (leave blank to clear)") LocalDate reminderDate) {
        setReminderDate(reminderDate);
        final Event reminderEvent = 
                events.findEventsBySubjectAndSubjectEventType(this, SUBJECT_EVENT_TYPE_REMINDER_DATE);
        if(reminderDate != null) {
            if(reminderEvent == null) {
                // create...
                createEvent(getReminderDate(), this, SUBJECT_EVENT_TYPE_REMINDER_DATE);
            } else {
                // update...
                reminderEvent.setDate(reminderDate);
            }
        } else {
            if(reminderEvent != null) {
                // delete...
                removeIfNotAlready(reminderEvent);
            }
        }
        return this;
    }
    public LocalDate default0UpdateReminderDate() {
        return getNotificationDate().minusWeeks(2);
    }
    public String disableUpdateReminderDate(final LocalDate reminderDate) {
        return getExerciseType() == BreakExerciseType.TENANT? 
                "Can only set reminders for "
                + BreakExerciseType.LANDLORD
                + " and "
                + BreakExerciseType.MUTUAL
                + " break options": null;
    }
    public String validateUpdateReminderDate(final LocalDate reminderDate) {
        if(reminderDate == null) {
            return null;
        }
        return reminderDate.compareTo(getNotificationDate())>=0
                ? "Reminder must be before notification date"
                : null;
    }
    
    // //////////////////////////////////////


    public void persisting() {
        createEvent(getBreakDate(), this, SUBJECT_EVENT_TYPE_BREAK_DATE);
        createEvent(getNotificationDate(), this, SUBJECT_EVENT_TYPE_LAST_NOTIFICATION_DATE);
    }


    
}
