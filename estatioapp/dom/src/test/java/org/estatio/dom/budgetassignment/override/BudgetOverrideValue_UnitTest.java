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

package org.estatio.dom.budgetassignment.override;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.dom.budgeting.budgetcalculation.Status;

public class BudgetOverrideValue_UnitTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final BudgetOverrideValue pojo = new BudgetOverrideValue();
            newPojoTester()
                    .withFixture(pojos(BudgetOverride.class, BudgetOverrideForFixed.class))
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
        BudgetOverrideValue value = new BudgetOverrideValue();
        value.repositoryService = repositoryService;
        value.setStatus(Status.NEW);

        // expect
        context.checking(new Expectations() {
            {
                oneOf(repositoryService).removeAndFlush(value);
            }

        });

        // when
        value.removeWithStatusNew();
    }

    @Test
    public void doNotremoveWithStatusAssigned(){

        // given
        BudgetOverrideValue value = new BudgetOverrideValue();
        value.repositoryService = repositoryService;
        value.setStatus(Status.ASSIGNED);

        // when
        value.removeWithStatusNew();

        // then
        /*nothing*/
    }

}
