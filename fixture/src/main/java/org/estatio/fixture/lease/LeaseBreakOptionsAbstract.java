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
package org.estatio.fixture.lease;

import javax.inject.Inject;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.breaks.BreakExerciseType;
import org.estatio.dom.lease.breaks.BreakType;
import org.joda.time.LocalDate;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class LeaseBreakOptionsAbstract extends FixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected Lease newBreakOptionPlusYears(Lease lease, int years, String notificationPeriodStr, BreakType breakType, BreakExerciseType exerciseType, String description, ExecutionContext executionContext) {
        final LocalDate breakDate = lease.getStartDate().plusYears(years);
        return  newBreakOption(lease, notificationPeriodStr, exerciseType, breakType, description, breakDate, executionContext);
    }

    protected Lease newBreakOptionAtEndDate(Lease lease, String notificationPeriodStr, BreakType breakType, BreakExerciseType breakExerciseType, String description, ExecutionContext executionContext) {
        final LocalDate breakDate = lease.getInterval().endDateExcluding();
        return newBreakOption(lease, notificationPeriodStr, breakExerciseType, breakType, description, breakDate, executionContext);
    }

    protected Lease newBreakOption(Lease lease, String notificationPeriodStr, BreakExerciseType exerciseType, BreakType breakType, String description, LocalDate breakDate, ExecutionContext executionContext) {
        final Lease breakOption = lease.newBreakOption(breakDate, notificationPeriodStr, exerciseType, breakType, description);
        return executionContext.add(this, breakOption);
    }

    // //////////////////////////////////////

    @Inject
    protected Leases leases;

}
