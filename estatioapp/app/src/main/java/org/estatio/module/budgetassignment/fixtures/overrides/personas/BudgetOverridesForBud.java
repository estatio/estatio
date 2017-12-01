/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.estatio.module.budgetassignment.fixtures.overrides.personas;

import org.estatio.module.budgetassignment.fixtures.overrides.BudgetOverrideAbstact;
import org.estatio.module.budgetassignment.fixtures.overrides.enums.BudgetOverrideForFlatRate_enum;
import org.estatio.module.budgetassignment.fixtures.overrides.enums.BudgetOverrideForMax_enum;

public class BudgetOverridesForBud extends BudgetOverrideAbstact {

//    private static final LocalDate BUDGET_2015_START_DATE = new LocalDate(2015, 01, 01);

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChildT(this, BudgetOverrideForFlatRate_enum.BudMiracle002Nl_2015.toBuilderScript());
        executionContext.executeChildT(this, BudgetOverrideForMax_enum.BudPoison001Nl_2015.toBuilderScript());
    }

//    @Override
//    protected void execute(ExecutionContext executionContext) {
//
//        // prereqs
//        executionContext.executeChildT(this, Lease_enum.BudPoison001Nl.toBuilderScript());
//        executionContext.executeChildT(this, Lease_enum.BudMiracle002Nl.toBuilderScript());
//        executionContext.executeChildT(this, Lease_enum.BudHello003Nl.toBuilderScript());
//        executionContext.executeChildT(this, Lease_enum.BudDago004Nl.toBuilderScript());
//        executionContext.executeChildT(this, Lease_enum.BudNlBank004Nl.toBuilderScript());
//        executionContext.executeChildT(this, Lease_enum.BudHyper005Nl.toBuilderScript());
//        executionContext.executeChildT(this, Lease_enum.BudHello006Nl.toBuilderScript());
//
//        // exec
//        Lease leaseForPoison = Lease_enum.BudPoison001Nl.findUsing(serviceRegistry);
//        Lease leaseForMiracle = Lease_enum.BudMiracle002Nl.findUsing(serviceRegistry);
//        Charge invoiceCharge1 = Charge_enum.NlServiceCharge.findUsing(serviceRegistry);
//        Charge incomingCharge1 = Charge_enum.NlIncomingCharge1.findUsing(serviceRegistry);
//
//        createBudgetOverrideForMax(
//                bd("350.00"),
//                leaseForPoison,
//                BUDGET_2015_START_DATE,
//                null,
//                invoiceCharge1,
//                incomingCharge1,
//                BudgetCalculationType.BUDGETED,
//                executionContext);
//
//        createBudgetOverrideForFlateRate(
//                bd("12.5"),
//                bd("90"),
//                leaseForMiracle,
//                BUDGET_2015_START_DATE,
//                null,
//                invoiceCharge1,
//                null,
//                BudgetCalculationType.BUDGETED,
//                executionContext);
//    }
//
//
//    @Inject
//    protected LeaseRepository leaseRepository;
//
//    @Inject
//    protected ChargeRepository chargeRepository;

}
