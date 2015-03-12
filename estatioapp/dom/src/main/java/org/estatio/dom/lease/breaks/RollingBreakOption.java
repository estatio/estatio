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

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@DomainObject(
        objectType = "lease.RollingBreakOption"
)
public class RollingBreakOption
        extends BreakOption {

    private static final String CALENDAR_NAME_ROLLING_BREAK_EXERCISE = "Rolling break exercise";

    /**
     * Dynamically rename {@link #getExerciseDate()} to be
     * &quot;Earliest exercise date&quot; in the UI.
     * 
     * <p>
     * NB: implemented this way because the alternative (override and using
     * <tt>@Named</tt> annotation) resulted in an infinite stacktrace, resultant
     * from the JDO enhancement.
     */
    public static String nameExerciseDate() {
        return "Earliest exercise date";
    }

    // //////////////////////////////////////

    @Programmatic
    @Override
    public Set<String> getCalendarNames() {
        return Sets.newHashSet(CALENDAR_NAME_ROLLING_BREAK_EXERCISE);
    }

    // //////////////////////////////////////

    @Override
    public LocalDate getCurrentBreakDate() {
        final LocalDate notificationDate = laterOf(getExerciseDate(), getClockService().now());
        return notificationDate.plus(getNotificationPeriodJoda());
    }

    private static LocalDate laterOf(final LocalDate d1, final LocalDate d2) {
        return d1.compareTo(d2) < 0 ? d1 : d2;
    }

    // //////////////////////////////////////

    @Override
    protected void createEvents() {
        // don't create an 'break date' event, since changes on a day-by-day basis
        createEvent(getExerciseDate(), this, CALENDAR_NAME_ROLLING_BREAK_EXERCISE);
    }

}
