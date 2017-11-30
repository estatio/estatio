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

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budgetassignment.fixtures.overrides.BudgetOverrideAbstact;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

public class BudgetOverridesForBud extends BudgetOverrideAbstact {

    public static final LocalDate BUDGET_2015_START_DATE = new LocalDate(2015, 01, 01);

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChildT(this, Lease_enum.BudPoison001Nl.toFixtureScript());
        executionContext.executeChildT(this, Lease_enum.BudMiracle002Nl.toFixtureScript());
        executionContext.executeChildT(this, Lease_enum.BudHello003Nl.toFixtureScript());
        executionContext.executeChildT(this, Lease_enum.BudDago004Nl.toFixtureScript());
        executionContext.executeChildT(this, Lease_enum.BudNlBank004Nl.toFixtureScript());
        executionContext.executeChildT(this, Lease_enum.BudHyper005Nl.toFixtureScript());
        executionContext.executeChildT(this, Lease_enum.BudHello006Nl.toFixtureScript());

        // exec
        Lease leaseForPoison = Lease_enum.BudPoison001Nl.findUsing(serviceRegistry);
        Lease leaseForMiracle = Lease_enum.BudMiracle002Nl.findUsing(serviceRegistry);
        Charge invoiceCharge1 = Charge_enum.NlServiceCharge.findUsing(serviceRegistry);
        Charge incomingCharge1 = Charge_enum.NlIncomingCharge1.findUsing(serviceRegistry);

        createBudgetOverrideForMax(
                new BigDecimal("350.00"),
                leaseForPoison,
                BUDGET_2015_START_DATE,
                null,
                invoiceCharge1,
                incomingCharge1,
                BudgetCalculationType.BUDGETED,
                executionContext);

        createBudgetOverrideForFlateRate(
                new BigDecimal("12.5"),
                new BigDecimal("90"),
                leaseForMiracle,
                BUDGET_2015_START_DATE,
                null,
                invoiceCharge1,
                null,
                BudgetCalculationType.BUDGETED,
                executionContext);
    }


    @Inject
    protected LeaseRepository leaseRepository;

    @Inject
    protected ChargeRepository chargeRepository;

}
