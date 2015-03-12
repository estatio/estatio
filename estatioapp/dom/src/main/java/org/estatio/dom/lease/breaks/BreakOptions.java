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

import java.util.List;

import com.google.common.collect.Iterables;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.utils.JodaPeriodUtils;

@DomainService(repositoryFor = BreakOption.class, nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class BreakOptions extends UdoDomainRepositoryAndFactory<BreakOption> {

    public BreakOptions() {
        super(BreakOptions.class, BreakOption.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Lease newBreakOption(
            final Lease lease,
            final @ParameterLayout(named = "Break date") LocalDate breakDate,
            final @ParameterLayout(named = "Notification period", describedAs = "Notification period in a text format. Example 6y5m2d") String notificationPeriodStr,
            final BreakType breakType,
            final BreakExerciseType breakExerciseType,
            final @ParameterLayout(named = "Description") @Parameter(optionality = Optionality.OPTIONAL) String description
            ) {
        final BreakOption breakOption = newTransientInstance(breakType.getFactoryClass());
        breakOption.setType(breakType);
        breakOption.setLease(lease);
        breakOption.setExerciseType(breakExerciseType);
        breakOption.setBreakDate(breakDate);
        breakOption.setNotificationPeriod(notificationPeriodStr);
        breakOption.setExerciseDate(breakDate.minus(JodaPeriodUtils.asPeriod(notificationPeriodStr)));
        breakOption.setDescription(description);
        persist(breakOption);
        return lease;
    }

    public LocalDate default1NewBreakOption() {
        // REVIEW: this is just a guess as to a reasonable default
        return getClockService().now().plusYears(2);
    }

    public String default2NewBreakOption() {
        return "3m";
    }

    public BreakType default3NewBreakOption() {
        return BreakType.FIXED;
    }

    public BreakExerciseType default4NewBreakOption() {
        return BreakExerciseType.TENANT;
    }

    public String validateNewBreakOption(
            final Lease lease,
            final LocalDate breakDate,
            final String notificationPeriodStr,
            final BreakType breakType,
            final BreakExerciseType breakExerciseType,
            final String description) {

        final Period notificationPeriodJoda = JodaPeriodUtils.asPeriod(notificationPeriodStr);
        if (notificationPeriodJoda == null) {
            return "Notification period format not recognized";
        }
        final LocalDate notificationDate = breakDate.minus(notificationPeriodJoda);
        return checkNewBreakOptionDuplicate(lease, BreakType.FIXED, notificationDate);
    }

    private String checkNewBreakOptionDuplicate(final Lease lease, final BreakType breakType, final LocalDate breakDate) {
        final Iterable<BreakOption> duplicates =
                Iterables.filter(findByLease(lease),
                        BreakOption.Predicates.whetherTypeAndBreakDate(breakType, breakDate));
        return duplicates.iterator().hasNext() ?
                "This lease already has a " + breakType + " break option for this date" : null;
    }

    // //////////////////////////////////////

    public void copyBreakOptions(final Lease lease, final Lease newLease, final LocalDate startDate) {
        for (BreakOption option : findByLease(lease)) {
            if (option.getBreakDate().isAfter(startDate)) {
                newBreakOption(
                        newLease,
                        option.getBreakDate(),
                        option.getNotificationPeriod(),
                        option.getType(),
                        option.getExerciseType(),
                        option.getDescription());
            }
        }
    }

    // //////////////////////////////////////

    @Programmatic
    public List<BreakOption> findByLease(final Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }

    // //////////////////////////////////////

    public List<BreakOption> allBreakOptions() {
        return allInstances();
    }

}
