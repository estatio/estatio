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

package org.estatio.budget.dom.partioning;

import java.math.BigDecimal;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;

import org.estatio.budget.dom.budgetitem.BudgetItem;
import org.estatio.budget.dom.keytable.KeyTable;
import org.estatio.dom.charge.Charge;

import static org.assertj.core.api.Assertions.assertThat;

public class PartitionItemRepository_Test {

    FinderInteraction finderInteraction;

    PartitionItemRepository partitionItemRepository;

    @Before
    public void setup() {
        partitionItemRepository = new PartitionItemRepository() {

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
            protected List<PartitionItem> allInstances() {
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

    public static class FindByPartitionItem extends PartitionItemRepository_Test {

        @Test
        public void happyCase() {

            BudgetItem budgetItem = new BudgetItem();
            partitionItemRepository.findByBudgetItem(budgetItem);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(PartitionItem.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByBudgetItem");
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItem")).isEqualTo((Object) budgetItem);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class FindByKeyTable extends PartitionItemRepository_Test {

        @Test
        public void happyCase() {

            KeyTable keyTable = new KeyTable();
            partitionItemRepository.findByKeyTable(keyTable);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(PartitionItem.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByKeyTable");
            assertThat(finderInteraction.getArgumentsByParameterName().get("keyTable")).isEqualTo((Object) keyTable);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class FindUnique extends PartitionItemRepository_Test {

        @Test
        public void happyCase() {

            Partitioning partitioning = new Partitioning();
            Charge charge = new Charge();
            BudgetItem budgetItem = new BudgetItem();
            KeyTable keyTable = new KeyTable();
            partitionItemRepository.findUnique(partitioning, charge, budgetItem, keyTable);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.UNIQUE_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(PartitionItem.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findUnique");
            assertThat(finderInteraction.getArgumentsByParameterName().get("partitioning")).isEqualTo((Object) partitioning);
            assertThat(finderInteraction.getArgumentsByParameterName().get("charge")).isEqualTo((Object) charge);
            assertThat(finderInteraction.getArgumentsByParameterName().get("budgetItem")).isEqualTo((Object) budgetItem);
            assertThat(finderInteraction.getArgumentsByParameterName().get("keyTable")).isEqualTo((Object) keyTable);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(4);
        }

    }

    public static class FindOrCreatePartitionItemNew extends PartitionItemRepository_Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        PartitionItemRepository partitionItemRepository1;

        @Before
        public void setup() {
            partitionItemRepository1 = new PartitionItemRepository() {
                @Override
                public PartitionItem findUnique(final Partitioning partitioning, final Charge charge, final BudgetItem budgetItem, final KeyTable keyTable) {
                    return null;
                }
            };
            partitionItemRepository1.setContainer(mockContainer);
        }

        @Test
        public void findOrCreateXxx() throws Exception {

            final KeyTable keyTable = new KeyTable();
            final Partitioning partitioning = new Partitioning();
            final Charge charge = new Charge();
            final BudgetItem budgetItem = new BudgetItem();
            final BigDecimal percentage = new BigDecimal("100.000000");
            final PartitionItem partitionItem = new PartitionItem();

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(PartitionItem.class);
                    will(returnValue(partitionItem));
                    oneOf(mockContainer).persistIfNotAlready(partitionItem);
                }

            });

            // when
            PartitionItem newPartitionItem = partitionItemRepository1.findOrCreatePartitionItem(partitioning, budgetItem, charge, keyTable, percentage);

            // then
            assertThat(newPartitionItem.getPartitioning()).isEqualTo(partitioning);
            assertThat(newPartitionItem.getCharge()).isEqualTo(charge);
            assertThat(newPartitionItem.getBudgetItem()).isEqualTo(budgetItem);
            assertThat(newPartitionItem.getKeyTable()).isEqualTo(keyTable);
            assertThat(newPartitionItem.getPercentage()).isEqualTo(percentage);

        }

    }

}
