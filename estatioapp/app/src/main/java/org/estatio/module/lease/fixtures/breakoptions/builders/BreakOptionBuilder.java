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
package org.estatio.module.lease.fixtures.breakoptions.builders;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.breaks.BreakExerciseType;
import org.estatio.module.lease.dom.breaks.BreakOption;
import org.estatio.module.lease.dom.breaks.BreakOptionRepository;
import org.estatio.module.lease.dom.breaks.BreakType;
import org.estatio.module.lease.dom.breaks.Lease_breakOptionContributions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"lease", "years", "breakType", "exerciseType"}, callSuper = false)
@ToString(of={"lease", "years", "breakType", "exerciseType"})
@Accessors(chain = true)
public class BreakOptionBuilder extends BuilderScriptAbstract<BreakOption, BreakOptionBuilder> {

    @Getter @Setter
    Lease lease;
    @Getter @Setter
    SortOf sortOf;
    /**
     * Not required if #sortOf is END_DATE
     */
    @Getter @Setter
    Integer years;
    @Getter @Setter
    String notificationPeriodStr;
    @Getter @Setter
    BreakType breakType;
    @Getter @Setter
    BreakExerciseType exerciseType;
    @Getter @Setter
    String description;

    public enum SortOf {
        PLUS_YEARS,
        END_DATE
    }


    @Getter
    BreakOption object;


    @Override
    protected void execute(final ExecutionContext ec) {

        checkParam("lease", ec, Lease.class);
        checkParam("sortOf", ec, SortOf.class);
        if(sortOf == SortOf.PLUS_YEARS) {
            checkParam("years", ec, Integer.class);
        }
        checkParam("notificationPeriodStr", ec, String.class);
        checkParam("breakType", ec, BreakType.class);
        checkParam("exerciseType", ec, BreakExerciseType.class);

        // description not required

        LocalDate breakDate = breakDateFor(this.lease, this.sortOf, this.years);

        final BreakOption breakOption =
                newBreakOption(this.lease, breakDate, notificationPeriodStr, exerciseType, breakType, description, ec);

        object = breakOption;
    }

    public static LocalDate breakDateFor(
            final Lease lease,
            final SortOf sortOf,
            final Integer years) {
        return sortOf == SortOf.PLUS_YEARS
                            ? lease.getStartDate().plusYears(years)
                            : lease.getInterval().endDateExcluding();
    }

    private BreakOption newBreakOption(
            Lease lease,
            LocalDate breakDate,
            String notificationPeriodStr,
            BreakExerciseType exerciseType,
            BreakType breakType,
            String description,
            ExecutionContext executionContext) {
        breakOptionContributions
                .newBreakOption(lease, breakDate, notificationPeriodStr, breakType, exerciseType, description);

        final BreakOption breakOption = breakOptionRepository.findByLeaseAndTypeAndBreakDateAndExerciseType(lease, breakType, breakDate, exerciseType);
        executionContext.addResult(this, breakOption);

        return breakOption;
    }

    // //////////////////////////////////////

    @Inject
    protected LeaseRepository leaseRepository;

    @Inject
    Lease_breakOptionContributions breakOptionContributions;

    @Inject
    BreakOptionRepository breakOptionRepository;

}
