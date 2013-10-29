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

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
//no @DatastoreIdentity nor @Version, since inherited from supertype
public class RollingBreakOption
        extends BreakOption {


    private static final String SUBJECT_EVENT_TYPE_EARLIEST_NOTIFICATION_DATE = "Earliest notification date";

    
    /**
     * Dynamically rename {@link #getNotificationDate()} to be {@link #SUBJECT_EVENT_TYPE_LAST_NOTIFICATION_DATE} in 
     * the UI.
     * 
     * <p>
     * For a {@link RollingBreakOption}, the {@link #getNotificationDate()} is the earliest date when notice can be 
     * given for the {@link #getLease() lease} to be terminated.
     */
    public static String nameNotificationDate() {
        return SUBJECT_EVENT_TYPE_EARLIEST_NOTIFICATION_DATE;
    }


    // //////////////////////////////////////


    public LocalDate getBreakDate() {
        final LocalDate notificationDate = laterOf(getNotificationDate(), getClockService().now());
        return notificationDate.plus(getNotificationPeriodJoda());
    }

    private static LocalDate laterOf(final LocalDate d1, final LocalDate d2) {
        return d1.compareTo(d2) <0? d1: d2;
    }


    // //////////////////////////////////////

    public void persisting() {
        // don't create an 'break date' event, since changes on a day-by-day basis 
        createEvent(getNotificationDate(), this, SUBJECT_EVENT_TYPE_EARLIEST_NOTIFICATION_DATE);
    }

}
