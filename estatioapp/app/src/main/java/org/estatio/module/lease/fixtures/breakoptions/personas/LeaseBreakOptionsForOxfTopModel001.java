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
package org.estatio.module.lease.fixtures.breakoptions.personas;

import org.estatio.module.lease.fixtures.breakoptions.enums.BreakOption_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

public class LeaseBreakOptionsForOxfTopModel001 extends LeaseBreakOptionsAbstract {

    public static final String LEASE_REF = Lease_enum.OxfTopModel001Gb.getRef();

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChildren(this, BreakOption_enum.OxfTopModel001Gb_FIXED, BreakOption_enum.OxfTopModel001Gb_ROLLING);

//        // prereqs
//        executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
//        executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
//        executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb_TA.builder());
//        executionContext.executeChild(this, LeaseItemForServiceChargeBudgeted_enum.OxfTopModel001Gb.builder());
//        executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfTopModel001Gb.builder());
//        executionContext.executeChild(this, LeaseItemForPercentage_enum.OxfTopModel001Gb.builder());
//        executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb.builder());
//        executionContext.executeChild(this, LeaseItemForEntryFee_enum.OxfTopModel001Gb.builder());
//        executionContext.executeChild(this, LeaseItemForTax_enum.OxfTopModel001Gb.builder());
//        executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfTopModel001Gb.builder());
//        executionContext.executeChild(this, LeaseItemForMarketing_enum.OxfTopModel001Gb.builder());
//
//
//        // exec
//        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);
//        newBreakOptionPlusYears(
//                lease, 5, "6m", BreakType.FIXED, BreakExerciseType.MUTUAL, null, executionContext);
//        newBreakOptionAtEndDate(
//                lease, "6m", BreakType.ROLLING, BreakExerciseType.MUTUAL, null, executionContext);
    }

}
