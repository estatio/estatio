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

package org.estatio.dom.lease.breaks.prolongation;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.base.dom.utils.JodaPeriodUtils;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.breaks.BreakExerciseType;
import org.estatio.dom.lease.breaks.BreakType;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = ProlongationOption.class)
public class ProlongationOptionRepository extends UdoDomainRepositoryAndFactory<ProlongationOption> {

    public ProlongationOptionRepository() {
        super(ProlongationOptionRepository.class, ProlongationOption.class);
    }

    @Programmatic
    public Lease newProlongationOption(
            final Lease lease,
            final String prolongationPeriod,
            final String notificationPeriod,
            final String description
    ) {
        if (findByLease(lease) == null) {
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
        }
        return lease;
    }

    @Programmatic
    public String validateNewProlongation(
            final Lease lease,
            final String prolongationPeriod,
            final String notificationPeriod,
            final String description
    ){
        return findByLease(lease) == null ? null : "A pronlongation option for this lease already exists";
    }

    @Programmatic
    public ProlongationOption findByLease(
            final Lease lease) {
        return uniqueMatch("findByLease",
                "lease", lease
        );
    }

}
