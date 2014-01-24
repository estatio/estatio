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

import java.util.concurrent.Callable;

import org.joda.time.LocalDate;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.breaks.BreakExerciseType;
import org.estatio.dom.lease.breaks.BreakType;
import org.estatio.services.clock.ClockService;

public class CreateBreakOptions implements Callable<Object> {

    @Override
    public Object call() throws Exception {
        final Lease lease = leases.findLeaseByReference("OXF-TOPMODEL-001");
        lease.newBreakOption(new LocalDate(clockService.now().plusMonths(6)), "3m", BreakExerciseType.LANDLORD, BreakType.FIXED, null);
        lease.newBreakOption(new LocalDate(clockService.now().plusMonths(12)), "3m", BreakExerciseType.MUTUAL, BreakType.FIXED, null);
        lease.newBreakOption(new LocalDate(clockService.now().plusMonths(24)), "3m", BreakExerciseType.TENANT, BreakType.FIXED, null);
        lease.newBreakOption(new LocalDate(clockService.now().plusMonths(24)), "3m", BreakExerciseType.TENANT, BreakType.ROLLING, null);
        return lease;
    }

    // //////////////////////////////////////

    private Leases leases;

    public final void injectLeases(final Leases leases) {
        this.leases = leases;
    }

    private ClockService clockService;

    public final void injectClockService(ClockService clockService) {
        this.clockService = clockService;
    }
}
