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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.lease.Lease;

@Hidden
public class BreakOptions extends EstatioDomainService<BreakOption> {

    public BreakOptions() {
        super(BreakOptions.class, BreakOption.class);
    }
    
    // //////////////////////////////////////
    /**
     * 
     * @param lease
     * @param breakDate
     * @param notificationPeriodStr
     * @param notificationDate
     * @param breakType
     * @param breakExerciseType
     * @return
     * 
     * Wrapper to create a BreakOption, used by the API
     */
    @Programmatic
    Lease newBreakOption(
          final Lease lease,
          final LocalDate breakDate, 
          final String notificationPeriodStr,
          final LocalDate notificationDate,
          final BreakType breakType,
            final BreakExerciseType breakExerciseType) {
        
        
//        
//        if (breakType.equals(BreakType.FIXED)) {
//            lease.newFixedBreakOption(breakDate, notificationPeriodStr, breakExerciseType);
//        } else
//        {
//            LocalDate earliestNotificationDate;
//            lease.newRollingBreakOption(earliestNotificationDate, notificationPeriodStr, breakExerciseType);
//        }
//        
//        final FixedBreakOption breakOption = newTransientInstance(FixedBreakOption.class);
//        breakOption.setLease(lease);
//        breakOption.setExerciseType(breakExerciseType);
//        final LocalDate date = breakDate;
//        breakOption.setNotificationPeriod(notificationPeriodStr);
//        breakOption.setBreakDate(date);
//        
//        final Period notificationPeriodJoda = JodaPeriodUtils.asPeriod(notificationPeriodStr);
//        final LocalDate lastNotificationDate = date.minus(notificationPeriodJoda);
//        breakOption.setNotificationDate(lastNotificationDate);
//        
//        persistIfNotAlready(breakOption);
        return lease;
    }


}
