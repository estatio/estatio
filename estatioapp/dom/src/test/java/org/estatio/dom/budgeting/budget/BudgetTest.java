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

package org.estatio.dom.budgeting.budget;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.incode.module.base.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.PropertyForTesting;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final Budget pojo = new Budget();
            newPojoTester()
                    .withFixture(pojos(Property.class, PropertyForTesting.class))
                    .exercise(pojo);
        }

    }

    public static class TitleTest extends BudgetTest {

        @Test
        public void title() {
            // given
            final Property property = new Property();
            property.setName("Property");
            property.setReference("PROP");

            final Budget budget = new Budget();
            budget.setProperty(property);
            LocalDate startDate = new LocalDate(2015, 1, 1);
            LocalDate endDate = new LocalDate(2016, 1, 1);
            budget.setStartDate(startDate);
            budget.setEndDate(endDate);

            // when
            String budgetTitle = budget.title();

            // then
            assertThat(budgetTitle).isEqualTo(
                    property.getClass().getSimpleName()
                            + " [" + property.getReference() + "]"
                            + " > " + new LocalDateInterval(startDate, endDate).toString());
        }
    }

    public static class GetTargetChargesTest extends BudgetTest {

        List<Charge> charges;

        @Test
        public void noCharges() {

            // given
            Budget budget = new Budget();
            // when
            charges = budget.getInvoiceCharges();

            // then
            assertThat(charges).hasSize(0);

        }

        @Test
        public void withNoCharges() {

            // given
            Budget budget = new Budget();
            budget.getItems().add(createItemFor(budget, "1"));
            budget.getItems().add(createItemFor(budget, "2"));

            // when
            charges = budget.getInvoiceCharges();

            // then
            assertThat(charges).hasSize(2);
            assertThat(charges.get(0).getReference()).isEqualTo("target1");

        }

        public void withDoubleCharges() {

            // given
            Budget budget = new Budget();
            budget.getItems().add(createItemFor(budget, "0"));
            budget.getItems().addAll(createTwoItemsWithSameAllocationFor(budget, "1", "2"));

            // when
            charges = budget.getInvoiceCharges();

            // then
            assertThat(budget.getItems()).hasSize(3);
            assertThat(charges).hasSize(2);
            assertThat(charges.get(0).getReference()).isEqualTo("target0");
            assertThat(charges.get(1).getReference()).isEqualTo("target2");

        }

        private BudgetItem createItemFor(final Budget budget, final String uniqueString) {

            BudgetItem newItem = new BudgetItem();
            Charge charge = new Charge();
            charge.setReference(uniqueString);
            newItem.setCharge(charge);

            BudgetItemAllocation allocation = new BudgetItemAllocation();
            Charge targetCharge = new Charge();
            targetCharge.setReference("target".concat(uniqueString));
            allocation.setCharge(targetCharge);
            allocation.setBudgetItem(newItem);

            newItem.getBudgetItemAllocations().add(allocation);

            return newItem;
        }

        private List<BudgetItem> createTwoItemsWithSameAllocationFor(final Budget budget, final String str1, final String str2) {

            BudgetItem newItem1 = new BudgetItem();
            Charge charge1 = new Charge();
            charge1.setReference(str1);
            newItem1.setCharge(charge1);

            BudgetItem newItem2 = new BudgetItem();
            Charge charge2 = new Charge();
            charge2.setReference(str2);
            newItem2.setCharge(charge2);

            BudgetItemAllocation allocation = new BudgetItemAllocation();
            Charge targetCharge = new Charge();
            targetCharge.setReference("target".concat(str1));
            allocation.setCharge(targetCharge);
            allocation.setBudgetItem(newItem1);

            newItem1.getBudgetItemAllocations().add(allocation);
            newItem2.getBudgetItemAllocations().add(allocation);

            return Arrays.asList(newItem1, newItem2);
        }

    }

}
