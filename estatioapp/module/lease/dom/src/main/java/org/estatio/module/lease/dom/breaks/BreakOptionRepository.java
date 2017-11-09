/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

import com.google.common.collect.Iterables;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.base.dom.utils.JodaPeriodUtils;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.breaks.prolongation.ProlongationOption;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = BreakOption.class)
public class BreakOptionRepository extends UdoDomainRepositoryAndFactory<BreakOption> {

    public BreakOptionRepository() {
        super(BreakOptionRepository.class, BreakOption.class);
    }

    @Programmatic
    public Lease newBreakOption(
            final Lease lease,
            final LocalDate breakDate,
            final String notificationPeriod,
            final BreakType breakType,
            final BreakExerciseType breakExerciseType,
            final @Parameter(optionality = Optionality.OPTIONAL) String description
    ) {
        final BreakOption breakOption = newTransientInstance(breakType.getFactoryClass());
        breakOption.setType(breakType);
        breakOption.setLease(lease);
        breakOption.setExerciseType(breakExerciseType);
        breakOption.setBreakDate(breakDate);
        breakOption.setNotificationPeriod(notificationPeriod);
        breakOption.setExerciseDate(breakDate.minus(JodaPeriodUtils.asPeriod(notificationPeriod)));
        breakOption.setDescription(description);
        persist(breakOption);
        return lease;
    }

    @Programmatic
    public String checkNewBreakOptionDuplicate(final Lease lease, final BreakType breakType, final LocalDate breakDate) {
        final Iterable<BreakOption> duplicates =
                Iterables.filter(findByLease(lease),
                        BreakOption.Predicates.whetherTypeAndBreakDate(breakType, breakDate));
        return duplicates.iterator().hasNext() ?
                "This lease already has a " + breakType + " break option for this date" : null;
    }

    @Programmatic
    public Lease newProlongationOption(
            final Lease lease,
            final String prolongationPeriod,
            final String notificationPeriod,
            final @Parameter(optionality = Optionality.OPTIONAL) String description
    ) {
        final ProlongationOption prolongationOption = newTransientInstance(ProlongationOption.class);
        prolongationOption.setType(BreakType.PROLONGATION);
        prolongationOption.setLease(lease);
        prolongationOption.setExerciseType(BreakExerciseType.TENANT);
        prolongationOption.setBreakDate(lease.getEndDate());
        prolongationOption.setNotificationPeriod(notificationPeriod);
        prolongationOption.setExerciseDate(lease.getEndDate().minus(JodaPeriodUtils.asPeriod(notificationPeriod)));
        prolongationOption.setDescription(description);
        persist(prolongationOption);
        return lease;
    }

    @Programmatic
    public List<BreakOption> findByLease(final Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }

    @Programmatic
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

    @Programmatic
    public BreakOption findByLeaseAndTypeAndBreakDateAndExerciseType(
            final Lease lease,
            final BreakType type,
            final LocalDate breakDate,
            final BreakExerciseType exerciseType) {
        return uniqueMatch("findByLeaseAndTypeAndBreakDateAndExerciseType",
                "lease", lease,
                "type", type,
                "breakDate", breakDate,
                "exerciseType", exerciseType
        );
    }

    @Programmatic
    public List<BreakOption> allBreakOptions() {
        return allInstances();
    }
}
