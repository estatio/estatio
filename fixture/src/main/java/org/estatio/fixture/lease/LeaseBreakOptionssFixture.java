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

import org.apache.isis.applib.fixtures.AbstractFixture;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.breaks.BreakExerciseType;
import org.estatio.dom.lease.breaks.BreakType;

public class LeaseBreakOptionssFixture extends AbstractFixture {

    @Override
    public void install() {

        Lease leaseTopModel = leases.findLeaseByReference("OXF-TOPMODEL-001");
        leaseTopModel.newBreakOption(leaseTopModel.getStartDate().plusYears(5), "6m", BreakExerciseType.MUTUAL, BreakType.FIXED, null);
        leaseTopModel.newBreakOption(leaseTopModel.getInterval().endDateExcluding(), "6m", BreakExerciseType.MUTUAL, BreakType.ROLLING, null);

        Lease leaseMediaX = leases.findLeaseByReference("OXF-MEDIAX-002");
        leaseMediaX.newBreakOption(leaseTopModel.getStartDate().plusYears(5), "6m", BreakExerciseType.MUTUAL, BreakType.FIXED, null);
        leaseMediaX.newBreakOption(leaseTopModel.getInterval().endDateExcluding(), "6m", BreakExerciseType.MUTUAL, BreakType.ROLLING, null);

        Lease leasePoison = leases.findLeaseByReference("OXF-POISON-003");
        leasePoison.newBreakOption(leaseTopModel.getStartDate().plusYears(5), "6m", BreakExerciseType.MUTUAL, BreakType.FIXED, null);
        leasePoison.newBreakOption(leaseTopModel.getInterval().endDateExcluding(), "6m", BreakExerciseType.MUTUAL, BreakType.ROLLING, null);

        @SuppressWarnings("unused")
        Lease leasePret = leases.findLeaseByReference("OXF-PRET-004");
    }

    private Leases leases;

    public void injectLeases(final Leases leases) {
        this.leases = leases;
    }

}
