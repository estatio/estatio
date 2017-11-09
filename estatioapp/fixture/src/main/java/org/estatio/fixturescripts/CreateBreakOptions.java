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
package org.estatio.fixturescripts;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.breaks.BreakExerciseType;
import org.estatio.module.lease.dom.breaks.Lease_breakOptionContributions;
import org.estatio.module.lease.dom.breaks.BreakType;
import org.estatio.module.application.fixtures.lease.LeaseForOxfTopModel001Gb;

public class CreateBreakOptions extends DiscoverableFixtureScript {

    private String reference;

    public CreateBreakOptions() {
        this(LeaseForOxfTopModel001Gb.REF);
    }

    public CreateBreakOptions(String reference) {
        this.reference = reference;
    }

    @Override
    protected void execute(ExecutionContext fixtureResults) {
        final Lease lease = leaseRepository.findLeaseByReference(reference);
        final LocalDate now = clockService.now();
        breakOptionContributions.newBreakOption(lease, now.plusMonths(6), "3m", BreakType.FIXED, BreakExerciseType.LANDLORD, null);
        breakOptionContributions.newBreakOption(lease, now.plusMonths(12), "3m", BreakType.FIXED, BreakExerciseType.MUTUAL, null);
        breakOptionContributions.newBreakOption(lease, now.plusMonths(24), "3m", BreakType.FIXED, BreakExerciseType.TENANT, null);
        breakOptionContributions.newBreakOption(lease, now.plusMonths(24), "3m", BreakType.ROLLING, BreakExerciseType.TENANT, null);
        fixtureResults.addResult(this, "lease", lease);
    }

    // //////////////////////////////////////

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private Lease_breakOptionContributions breakOptionContributions;

    @Inject
    private ClockService clockService;

}
