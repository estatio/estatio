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

package org.estatio.dom.budgeting.budgetcalculation;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.partioning.PartitionItem;
import org.estatio.dom.charge.Charge;

public class BudgetCalculation_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final BudgetCalculation pojo = new BudgetCalculation();
            newPojoTester()
                    .withFixture(pojos(PartitionItem.class, PartitionItem.class))
                    .withFixture(pojos(KeyItem.class, KeyItem.class))
                    .withFixture(pojos(Charge.class, Charge.class))
                    .withFixture(pojos(Unit.class, Unit.class))
                    .withFixture(pojos(Budget.class, Budget.class))
                    .exercise(pojo);
        }

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private RepositoryService repositoryService;

    @Test
    public void removeWithStatusNew(){

        // given
        BudgetCalculation calculation = new BudgetCalculation();
        calculation.repositoryService = repositoryService;
        calculation.setStatus(Status.NEW);

        // expect
        context.checking(new Expectations() {
            {
                oneOf(repositoryService).removeAndFlush(calculation);
            }

        });

        // when
        calculation.removeWithStatusNew();
    }

    @Test
    public void doNotremoveWithStatusAssigned(){

        // given
        BudgetCalculation value = new BudgetCalculation();
        value.repositoryService = repositoryService;
        value.setStatus(Status.ASSIGNED);

        // when
        value.removeWithStatusNew();

        // then
        /*nothing*/
    }

}
