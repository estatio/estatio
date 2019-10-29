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

package org.estatio.module.budget.dom.budget;

import java.util.List;

import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.asset.dom.Property;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetRepository_Test {

    public static class NewBudget extends BudgetRepository_Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private FactoryService mockFactoryService;

        @Mock
        private RepositoryService mockRepositoryService;

        BudgetRepository budgetRepository;

        @Before
        public void setup() {
            budgetRepository = new BudgetRepository();
            budgetRepository.factoryService = mockFactoryService;
            budgetRepository.repositoryService = mockRepositoryService;
        }

        @Test
        public void newBudget() {

            //given
            Property property = new Property();
            LocalDate startDate = new LocalDate(2016, 01, 02);
            LocalDate endDate = new LocalDate(2016, 12, 30);
            LocalDate startOfYear = new LocalDate(2016,01,01);
            LocalDate endOfYear = new LocalDate(2016,12,31);
            final Budget budget = new Budget();

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockFactoryService).instantiate(Budget.class);
                    will(returnValue(budget));
                    oneOf(mockRepositoryService).persist(budget);
                }

            });

            //when
            Budget newBudget = budgetRepository.newBudget(property, startDate, endDate);

            //then
            assertThat(newBudget.getProperty()).isEqualTo(property);
            assertThat(newBudget.getStartDate()).isEqualTo(startDate);
            assertThat(newBudget.getEndDate()).isEqualTo(endDate);
            assertThat(newBudget.getBudgetYearInterval()).isEqualTo(new LocalDateInterval(startOfYear, endOfYear));
            assertThat(newBudget.getBudgetYearInterval().days()).isEqualTo(366);
            assertThat(newBudget.getBudgetYearInterval().contains(startOfYear)).isEqualTo(true);
            assertThat(newBudget.getBudgetYearInterval().contains(endOfYear)).isEqualTo(true);

        }

        @Test
        public void validateNewBudgetTest() {

            int year;

            // given
            Property property = new Property();

            // when
            year = 1999;
            // then
            assertThat(budgetRepository.validateNewBudget(property, year)).isEqualTo("This is not a valid year");

            // when
            year = 3001;
            // then
            assertThat(budgetRepository.validateNewBudget(property, year)).isEqualTo("This is not a valid year");

        }

    }

}
