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

package org.estatio.module.budgeting.dom.partioning;

import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;

import org.estatio.module.budgeting.dom.budget.Budget;
import org.estatio.module.budgeting.dom.budgetcalculation.BudgetCalculationType;

import static org.assertj.core.api.Assertions.assertThat;

public class PartitioningRepository_Test {

    FinderInteraction finderInteraction;

    PartitioningRepository partitioningRepository;

    @Before
    public void setup() {
        partitioningRepository = new PartitioningRepository() {

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
            protected List<Partitioning> allInstances() {
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

    public static class FindUnique extends PartitioningRepository_Test {

        @Test
        public void happyCase() {

            Budget budget = new Budget();
            BudgetCalculationType type = BudgetCalculationType.BUDGETED;
            LocalDate date = new LocalDate();
            partitioningRepository.findUnique(budget, type, date);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.UNIQUE_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(Partitioning.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findUnique");
            assertThat(finderInteraction.getArgumentsByParameterName().get("budget")).isEqualTo((Object) budget);
            assertThat(finderInteraction.getArgumentsByParameterName().get("type")).isEqualTo((Object) type);
            assertThat(finderInteraction.getArgumentsByParameterName().get("startDate")).isEqualTo((Object) date);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(3);
        }

    }

    public static class FindByBudgetAndType extends PartitioningRepository_Test {

        @Test
        public void happyCase() {

            Budget budget = new Budget();
            BudgetCalculationType type = BudgetCalculationType.BUDGETED;
            LocalDate date = new LocalDate();
            partitioningRepository.findByBudgetAndType(budget, type);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(Partitioning.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByBudgetAndType");
            assertThat(finderInteraction.getArgumentsByParameterName().get("budget")).isEqualTo((Object) budget);
            assertThat(finderInteraction.getArgumentsByParameterName().get("type")).isEqualTo((Object) type);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(2);
        }

    }

    public static class NewPartitioning extends PartitioningRepository_Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        PartitioningRepository partitioningRepository;

        @Before
        public void setup() {
            partitioningRepository = new PartitioningRepository();
            partitioningRepository.setContainer(mockContainer);
        }

        @Test
        public void newPartitioning() throws Exception {

            final Partitioning partitioning = new Partitioning();
            final Budget budget = new Budget();
            final BudgetCalculationType type = BudgetCalculationType.BUDGETED;
            final LocalDate startDate = new LocalDate();
            final LocalDate endDate = new LocalDate();

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(Partitioning.class);
                    will(returnValue(partitioning));
                    oneOf(mockContainer).persistIfNotAlready(partitioning);
                }

            });

            // when
            Partitioning newPartitioning = partitioningRepository.newPartitioning(budget, startDate, endDate, type);

            // then
            assertThat(newPartitioning.getBudget()).isEqualTo(budget);
            assertThat(newPartitioning.getStartDate()).isEqualTo(startDate);
            assertThat(newPartitioning.getEndDate()).isEqualTo(endDate);
            assertThat(newPartitioning.getType()).isEqualTo(type);

        }

        @Test
        public void validateNewPartitioning() throws Exception {

            // given
            final Partitioning partitioning = new Partitioning();
            final Budget budget = new Budget();
            final BudgetCalculationType type = BudgetCalculationType.BUDGETED;
            final LocalDate startDate = new LocalDate();
            final LocalDate endDate = new LocalDate();

            // when
            partitioningRepository = new PartitioningRepository(){

                Partitioning somePartitioning = new Partitioning();
                @Override
                public Partitioning findUnique(final Budget budget, final BudgetCalculationType type, final LocalDate startDate){
                    return somePartitioning;
                }
                @Override
                public List<Partitioning> findByBudgetAndType(final Budget budget, final BudgetCalculationType type){
                    return Arrays.asList(somePartitioning);
                }
            };

            // then
            assertThat(partitioningRepository.validateNewPartitioning(budget, startDate, endDate, type)).isEqualTo("This partitioning already exists");

            // and when
            partitioningRepository = new PartitioningRepository(){
                @Override
                public Partitioning findUnique(final Budget budget, final BudgetCalculationType type, final LocalDate startDate){
                    return null;
                }
                Partitioning somePartitioning = new Partitioning();
                @Override
                public List<Partitioning> findByBudgetAndType(final Budget budget, final BudgetCalculationType type){
                    return Arrays.asList(somePartitioning);
                }
            };

            // then
            assertThat(partitioningRepository.validateNewPartitioning(budget, startDate, endDate, type)).isEqualTo("Only one partitioning of type BUDGETED is supported");

        }

    }
}
