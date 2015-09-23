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

package org.estatio.dom.budgeting.budgetitem;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.ChargeForTesting;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetForTesting;
import org.estatio.dom.charge.Charge;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by jodo on 30/04/15.
 */
public class BudgetItemsTest {

    FinderInteraction finderInteraction;

    BudgetItems budgetItems;

    @Before
    public void setup() {
        budgetItems = new BudgetItems() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected <T> T uniqueMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.UNIQUE_MATCH);
                return null;
            }

            @Override
            protected List<BudgetItem> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderInteraction.FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    public static class FindByBudget extends BudgetItemsTest {

        @Test
        public void happyCase() {

            Budget budget = new BudgetForTesting();
            budgetItems.findByBudget(budget);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetItem.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudget"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budget"), is((Object) budget));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindByBudgetAndCharge extends BudgetItemsTest {

        @Test
        public void happyCase() {

            Budget budget = new Budget();
            Charge charge = new Charge();
            budgetItems.findByBudgetAndCharge(budget, charge);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.UNIQUE_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetItem.class));
            assertThat(finderInteraction.getQueryName(), is("findByBudgetAndCharge"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("budget"), is((Object) budget));
            assertThat(finderInteraction.getArgumentsByParameterName().get("charge"), is((Object) charge));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }

    }

    public static class FindByPropertyAndChargeAndStartDate extends BudgetItemsTest {

        @Test
        public void happyCase() {

            Property property = new Property();
            Charge charge = new Charge();
            LocalDate startDate = new LocalDate();
            budgetItems.findByPropertyAndChargeAndStartDate(property, charge, startDate);

            assertThat(finderInteraction.getFinderMethod(), is(FinderInteraction.FinderMethod.UNIQUE_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(BudgetItem.class));
            assertThat(finderInteraction.getQueryName(), is("findByPropertyAndChargeAndStartDate"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("property"), is((Object) property));
            assertThat(finderInteraction.getArgumentsByParameterName().get("charge"), is((Object) charge));
            assertThat(finderInteraction.getArgumentsByParameterName().get("startDate"), is((Object) startDate));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
        }

    }

    public static class ValidateNewBudgetItem extends BudgetItemsTest{

        @Test
        public void testValidateNewBudgetItem() {
            //given
            Budget budget = new BudgetForTesting();
            Charge charge = new ChargeForTesting();

            //when
            BigDecimal negativeValue = new BigDecimal(-0.01);
            BigDecimal zeroValue = BigDecimal.ZERO;
            BigDecimal positiveValue = new BigDecimal(0.01);
            //then
            assertThat(budgetItems.validateNewBudgetItem(budget,negativeValue,charge), is("Value can't be negative"));
            assertThat(budgetItems.validateNewBudgetItem(budget,zeroValue,charge), is(nullValue()));
            assertThat(budgetItems.validateNewBudgetItem(budget, positiveValue, charge), is(nullValue()));
        }

    }

}
