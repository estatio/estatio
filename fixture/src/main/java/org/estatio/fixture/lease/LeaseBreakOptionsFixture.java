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
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;

public class LeaseBreakOptionsFixture extends SimpleFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        Lease leaseTopModel = leases.findLeaseByReference("OXF-TOPMODEL-001");

        final Lease bo1 = leaseTopModel.newBreakOption(leaseTopModel.getStartDate().plusYears(5), "6m", BreakExerciseType.MUTUAL, BreakType.FIXED, null);
        executionContext.add(this, bo1);
        final Lease bo2 = leaseTopModel.newBreakOption(leaseTopModel.getInterval().endDateExcluding(), "6m", BreakExerciseType.MUTUAL, BreakType.ROLLING, null);
        executionContext.add(this, bo2);

        // hmmm.... looks like a mistake here to use leaseTopModel
        Lease leaseMediaX = leases.findLeaseByReference("OXF-MEDIAX-002");
        final Lease bo3 = leaseMediaX.newBreakOption(leaseTopModel.getStartDate().plusYears(5), "6m", BreakExerciseType.MUTUAL, BreakType.FIXED, null);
        executionContext.add(this, bo3);
        final Lease bo4 = leaseMediaX.newBreakOption(leaseTopModel.getInterval().endDateExcluding(), "6m", BreakExerciseType.MUTUAL, BreakType.ROLLING, null);
        executionContext.add(this, bo4);

        // hmmm.... looks like a mistake here to use leaseTopModel
        Lease leasePoison = leases.findLeaseByReference("OXF-POISON-003");
        final Lease bo5 = leasePoison.newBreakOption(leaseTopModel.getStartDate().plusYears(5), "6m", BreakExerciseType.MUTUAL, BreakType.FIXED, null);
        executionContext.add(this, bo5);
        final Lease bo6 = leasePoison.newBreakOption(leaseTopModel.getInterval().endDateExcluding(), "6m", BreakExerciseType.MUTUAL, BreakType.ROLLING, null);
        executionContext.add(this, bo6);

        @SuppressWarnings("unused")
        Lease leasePret = leases.findLeaseByReference("OXF-PRET-004");
    }

    // //////////////////////////////////////

    @Inject
    private Leases leases;

}
