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

package org.estatio.module.budget.dom.partioning;

import java.math.BigDecimal;

import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.charge.dom.Charge;

import static org.assertj.core.api.Assertions.assertThat;

public class PartitionItemRepository_Test {

    public static class FindOrCreatePartitionItemNew extends PartitionItemRepository_Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private FactoryService mockFactoryService;

        @Mock
        private RepositoryService mockRepositoryService;

        PartitionItemRepository partitionItemRepository1;

        @Before
        public void setup() {
            partitionItemRepository1 = new PartitionItemRepository() {
                @Override
                public PartitionItem findUnique(final Partitioning partitioning, final Charge charge, final BudgetItem budgetItem, final PartitioningTable keyTable) {
                    return null;
                }
            };
            partitionItemRepository1.factoryService = mockFactoryService;
            partitionItemRepository1.repositoryService = mockRepositoryService;
        }

        @Test
        public void findOrCreateXxx() throws Exception {

            final KeyTable keyTable = new KeyTable();
            final Partitioning partitioning = new Partitioning();
            final Charge charge = new Charge();
            final BudgetItem budgetItem = new BudgetItem();
            final BigDecimal percentage = new BigDecimal("100.000000");
            final PartitionItem partitionItem = new PartitionItem();
            final BigDecimal fixedBudgetedAmount = new BigDecimal("123.45");
            final BigDecimal fixedAuditedAmount = new BigDecimal("234.56");

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockFactoryService).instantiate(PartitionItem.class);
                    will(returnValue(partitionItem));
                    oneOf(mockRepositoryService).persist(partitionItem);
                }

            });

            // when
            PartitionItem newPartitionItem = partitionItemRepository1.findOrCreatePartitionItem(partitioning, budgetItem, charge, keyTable, percentage, fixedBudgetedAmount, fixedAuditedAmount);

            // then
            assertThat(newPartitionItem.getPartitioning()).isEqualTo(partitioning);
            assertThat(newPartitionItem.getCharge()).isEqualTo(charge);
            assertThat(newPartitionItem.getBudgetItem()).isEqualTo(budgetItem);
            assertThat(newPartitionItem.getPartitioningTable()).isEqualTo(keyTable);
            assertThat(newPartitionItem.getPercentage()).isEqualTo(percentage);
            assertThat(newPartitionItem.getFixedBudgetedAmount()).isEqualTo(fixedBudgetedAmount);
            assertThat(newPartitionItem.getFixedAuditedAmount()).isEqualTo(fixedAuditedAmount);

        }

    }

}
