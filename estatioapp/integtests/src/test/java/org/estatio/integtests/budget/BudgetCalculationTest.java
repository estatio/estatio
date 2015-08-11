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
package org.estatio.integtests.budget;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budget.Budget;
import org.estatio.dom.budget.BudgetContributions;
import org.estatio.dom.budget.Budgets;
import org.estatio.dom.charge.Charges;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetItemForOxf;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMediax002Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMiracl005Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetItemForOxf());
                executionContext.executeChild(this, new LeaseItemAndTermsForOxfMediax002Gb());
                executionContext.executeChild(this, new LeaseItemAndTermsForOxfMiracl005Gb());
                executionContext.executeChild(this, new LeaseItemAndTermsForOxfPoison003Gb());
                executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
            }
        });
    }

    @Inject
    Budgets budgets;

    @Inject
    Properties properties;

    @Inject
    BudgetContributions budgetContributions;

    @Inject
    Charges charges;

    public static class calculate extends BudgetCalculationTest {


        @Test
        public void whenSetUp() throws Exception {
            // Given
            final Property property = properties.findPropertyByReference(_PropertyForOxfGb.REF);
            final Budget budget = budgets.findBudgetByProperty(property).get(0);

            // When


            // Then

            assertThat(budgetContributions.calculatedServiceChargesOnLease(budget).size()).isEqualTo(4);
            assertThat(budgetContributions.calculatedServiceChargesOnLease(budget).get(0).getCalculatedValue()).isEqualTo(new BigDecimal(1784.62).setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(budgetContributions.calculatedServiceChargesOnLease(budget).get(1).getCalculatedValue()).isEqualTo(new BigDecimal(1876.93).setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(budgetContributions.calculatedServiceChargesOnLease(budget).get(2).getCalculatedValue()).isEqualTo(new BigDecimal(1692.31).setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(budgetContributions.calculatedServiceChargesOnLease(budget).get(3).getCalculatedValue()).isEqualTo(new BigDecimal(2061.55).setScale(2, BigDecimal.ROUND_HALF_UP));

        }

        @Test
        public void whenSetUpWithDifferentChargeForFirstItem() throws Exception {
            // Given
            final Property property = properties.findPropertyByReference(_PropertyForOxfGb.REF);
            final Budget budget = budgets.findBudgetByProperty(property).get(0);

            // When

            budget.getBudgetItems().first().setCharge(charges.findByReference(ChargeRefData.IT_RENT));

            // Then

            assertThat(budgetContributions.calculatedServiceChargesOnLease(budget).size()).isEqualTo(4);
            assertThat(budgetContributions.calculatedServiceChargesOnLease(budget).get(0).getCalculatedValue()).isEqualTo(new BigDecimal(1600.00).setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(budgetContributions.calculatedServiceChargesOnLease(budget).get(1).getCalculatedValue()).isEqualTo(new BigDecimal(1600.00).setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(budgetContributions.calculatedServiceChargesOnLease(budget).get(2).getCalculatedValue()).isEqualTo(new BigDecimal(1600.00).setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(budgetContributions.calculatedServiceChargesOnLease(budget).get(3).getCalculatedValue()).isEqualTo(new BigDecimal(1600.00).setScale(2, BigDecimal.ROUND_HALF_UP));

        }




    }

}