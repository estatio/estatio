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

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.breaks.BreakExerciseType;
import org.estatio.dom.lease.breaks.BreakOptions;
import org.estatio.dom.lease.breaks.BreakType;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.services.clock.ClockService;

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
        final Lease lease = leases.findLeaseByReference(reference);
        final LocalDate now = clockService.now();
        breakOptions.newBreakOption(lease, now.plusMonths(6), "3m", BreakType.FIXED, BreakExerciseType.LANDLORD, null);
        breakOptions.newBreakOption(lease, now.plusMonths(12), "3m", BreakType.FIXED, BreakExerciseType.MUTUAL, null);
        breakOptions.newBreakOption(lease, now.plusMonths(24), "3m", BreakType.FIXED, BreakExerciseType.TENANT, null);
        breakOptions.newBreakOption(lease, now.plusMonths(24), "3m", BreakType.ROLLING, BreakExerciseType.TENANT, null);
        fixtureResults.addResult(this, "lease", lease);
    }

    // //////////////////////////////////////

    private Leases leases;

    public final void injectLeases(final Leases leases) {
        this.leases = leases;
    }

    @Inject
    private BreakOptions breakOptions;

    private ClockService clockService;

    public final void injectClockService(ClockService clockService) {
        this.clockService = clockService;
    }

}
