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

package org.estatio.fixture.budget;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.estatio.module.budgeting.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeasesForBudNl;

public class BudgetOverridesForBud extends BudgetOverrideAbstact {

    public static final LocalDate BUDGET_2015_START_DATE = new LocalDate(2015, 01, 01);

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new EstatioBaseLineFixture());
        executionContext.executeChild(this, new LeasesForBudNl());

        // exec
        Lease leaseForPoison = leaseRepository.findLeaseByReference(LeasesForBudNl.REF1);
        Lease leaseForMiracle = leaseRepository.findLeaseByReference(LeasesForBudNl.REF2);
        Charge invoiceCharge1 = chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE);
        Charge incomingCharge1 = chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_1);

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
