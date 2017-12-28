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
package org.estatio.module.budgetassignment.integtests.subscriptions;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.partitioning.enums.Partitioning_enum;
import org.estatio.module.budgetassignment.contributions.Budget_Calculate;
import org.estatio.module.budgetassignment.integtests.BudgetAssignmentModuleIntegTestAbstract;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;

public class LeaseTermsForServiceChargeSubscriptions_IntegTest extends BudgetAssignmentModuleIntegTestAbstract {


    LeaseTermForServiceCharge lastServiceChargeTerm;
    Budget topmodelBudget2015;
    LocalDate startDate;

    @Before
    public void setUp() throws Exception {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, Partitioning_enum.OxfPartitioning2015.builder());

            }
        });

    }

    @Test
    public void disable_change_values_when_controlled_by_budget_works() {

        // given
        topmodelBudget2015 = Budget_enum.OxfBudget2015.findUsing(serviceRegistry);
        Assertions.assertThat(topmodelBudget2015).isNotNull();
        startDate = topmodelBudget2015.getStartDate();

        wrap(mixin(Budget_Calculate.class, topmodelBudget2015)).calculate(true);

        lastServiceChargeTerm = (LeaseTermForServiceCharge) LeaseItemForServiceCharge_enum.OxfTopModel001Gb.findUsing(serviceRegistry).getTerms().last();
        Assertions.assertThat(lastServiceChargeTerm).isNotNull();
        Assertions.assertThat(lastServiceChargeTerm.getStartDate()).isEqualTo(startDate);

        // expect
        expectedExceptions.expect(DisabledException.class);
        expectedExceptions.expectMessage("This term is controlled by a budget");

        // when
        wrap(lastServiceChargeTerm).changeValues(null, null);

    }

}