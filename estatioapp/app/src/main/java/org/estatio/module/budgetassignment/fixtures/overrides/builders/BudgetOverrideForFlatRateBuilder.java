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

package org.estatio.module.budgetassignment.fixtures.overrides.builders;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideForFlatRate;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideRepository;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideType;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.lease.dom.Lease;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"lease", "invoiceCharge"}, callSuper = false)
@ToString(of={"lease", "invoiceCharge"})
@Accessors(chain = true)
public class BudgetOverrideForFlatRateBuilder extends BuilderScriptAbstract<BudgetOverrideForFlatRate, BudgetOverrideForFlatRateBuilder> {


    @Getter @Setter
    Lease lease;
    @Getter @Setter
    Charge invoiceCharge;

    @Getter @Setter
    BigDecimal valuePerM2;
    @Getter @Setter
    BigDecimal weightedArea;

    @Getter @Setter
    BigDecimal area;
    @Getter @Setter
    LocalDate startDate;
    @Getter @Setter
    LocalDate endDate;
    @Getter @Setter
    Charge incomingCharge;
    @Getter @Setter
    BudgetCalculationType budgetCalculationType;

    @Getter @Setter
    String reason;

    @Getter
    BudgetOverrideForFlatRate object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("lease", executionContext, Lease.class);
        checkParam("invoiceCharge", executionContext, Charge.class);
        checkParam("valuePerM2", executionContext, BigDecimal.class);
        checkParam("weightedArea", executionContext, BigDecimal.class);

        defaultParam("budgetCalculationType", executionContext, BudgetCalculationType.BUDGETED);
        defaultParam("reason", executionContext, BudgetOverrideType.FLATRATE.reason);

        BudgetOverrideForFlatRate budgetOverride = budgetOverrideRepository
                .newBudgetOverrideForFlatRate(
                        valuePerM2,
                        area,
                        lease,
                        startDate,
                        endDate,
                        invoiceCharge,
                        incomingCharge,
                        budgetCalculationType,
                        reason);

        executionContext.addResult(this, budgetOverride);

        object = budgetOverride;
    }

    @Inject
    protected BudgetOverrideRepository budgetOverrideRepository;

}
