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

package org.estatio.module.lease.dom.breaks.prolongation;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.Period;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.base.dom.utils.JodaPeriodUtils;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.breaks.BreakExerciseType;
import org.estatio.module.lease.dom.breaks.BreakOptionRepository;
import org.estatio.module.lease.dom.breaks.BreakType;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = ProlongationOption.class)
public class ProlongationOptionRepository extends UdoDomainRepositoryAndFactory<ProlongationOption> {

    public ProlongationOptionRepository() {
        super(ProlongationOptionRepository.class, ProlongationOption.class);
    }

    @Programmatic
    public ProlongationOption newProlongationOption(
            final Lease lease,
            final String prolongationPeriod,
            final String notificationPeriod,
            final String description
    ) {
        final ProlongationOption prolongationOption = newTransientInstance();
        prolongationOption.setType(BreakType.PROLONGATION);
        prolongationOption.setLease(lease);
        prolongationOption.setExerciseType(BreakExerciseType.TENANT);
        prolongationOption.setBreakDate(lease.getEndDate());
        prolongationOption.setProlongationPeriod(prolongationPeriod);
        prolongationOption.setNotificationPeriod(notificationPeriod);
        if (notificationPeriod != null) {
            prolongationOption.setExerciseDate(lease.getEndDate().minus(JodaPeriodUtils.asPeriod(notificationPeriod)));
        } else
        {
            prolongationOption.setExerciseDate(lease.getEndDate());
        }
        prolongationOption.setDescription(description);
        persist(prolongationOption);

        return prolongationOption;
    }

    @Programmatic
    public String validateNewProlongation(
            final Lease lease,
            final String prolongationPeriod,
            final String notificationPeriod,
            final String description
    ){
        if (checkProlongationAndNotificationPeriodStr(prolongationPeriod, notificationPeriod)!=null) return checkProlongationAndNotificationPeriodStr(prolongationPeriod, notificationPeriod);
        return breakOptionRepository.checkNewBreakOptionDuplicate(lease, BreakType.PROLONGATION, lease.getEndDate());
    }

    @Programmatic
    public String checkProlongationAndNotificationPeriodStr(final String prolongationPeriod, final String notificationPeriod){
        final Period prolongationPeriodJoda = JodaPeriodUtils.asPeriod(prolongationPeriod);
        if (prolongationPeriod!=null && prolongationPeriodJoda == null) {
            return "Prolongation period format not recognized";
        }
        final Period notificationPeriodJoda = JodaPeriodUtils.asPeriod(notificationPeriod);
        if (notificationPeriod!=null && notificationPeriodJoda == null) {
            return "Notification period format not recognized";
        }
        return null;
    }

    @Programmatic
    public List<ProlongationOption> findByLease(
            final Lease lease) {
        return allMatches("findByLease",
                "lease", lease
        );
    }

    @Inject
    BreakOptionRepository breakOptionRepository;

}
