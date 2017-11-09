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

package org.estatio.module.budgetassignment.fixtures;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.budgetassignment.dom.override.BudgetOverride;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideForFixed;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideForFlatRate;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideForMax;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideRepository;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideType;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.lease.dom.Lease;

public abstract class BudgetOverrideAbstact extends FixtureScript {


    protected BudgetOverride createBudgetOverrideForFlateRate(
            final BigDecimal valuePerM2,
            final BigDecimal area,
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge invoiceCharge,
            final Charge incomingCharge,
            final BudgetCalculationType type,
            final ExecutionContext fixtureResults){
        BudgetOverrideForFlatRate budgetOverride = budgetOverrideRepository
                .newBudgetOverrideForFlatRate(
                        valuePerM2,
                        area,
                        lease,
                        startDate,
                        endDate,
                        invoiceCharge,
                        incomingCharge,
                        type,
                        BudgetOverrideType.FLATRATE.reason);
        return fixtureResults.addResult(this, budgetOverride);
    }

    protected BudgetOverride createBudgetOverrideForMax(
            final BigDecimal ceilingValue,
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge invoiceCharge,
            final Charge incomingCharge,
            final BudgetCalculationType type,
            final ExecutionContext fixtureResults){
        BudgetOverrideForMax budgetOverride = budgetOverrideRepository
                .newBudgetOverrideForMax(
                        ceilingValue,
                        lease,
                        startDate,
                        endDate,
                        invoiceCharge,
                        incomingCharge,
                        type,
                        BudgetOverrideType.CEILING.reason);
        return fixtureResults.addResult(this, budgetOverride);
    }

    protected BudgetOverride createBudgetOverrideForFixed(
            final BigDecimal fixedValue,
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge invoiceCharge,
            final Charge incomingCharge,
            final BudgetCalculationType type,
            final ExecutionContext fixtureResults){
        BudgetOverrideForFixed budgetOverride = budgetOverrideRepository
                .newBudgetOverrideForFixed(
                        fixedValue,
                        lease,
                        startDate,
                        endDate,
                        invoiceCharge,
                        incomingCharge,
                        type,
                        "agreed on fixed amount");
        return fixtureResults.addResult(this, budgetOverride);
    }

    @Inject
    protected BudgetOverrideRepository budgetOverrideRepository;

}
