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

package org.estatio.dom.budget;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetKeyValueMethodTest {

    @Test
    public void testIsNotValid() {

        //given
        BudgetKeyValueMethod method = BudgetKeyValueMethod.PROMILLE;
        BudgetKeyTable budgetKeyTable = new BudgetKeyTable();
        SortedSet<BudgetKeyItem> budgetKeyItems = new TreeSet<BudgetKeyItem>();

        //when

        for(int i = 0; i < 1; i = i+1) {
            BudgetKeyItem budgetKeyItem = new BudgetKeyItem();
            budgetKeyItem.setTargetValue(new BigDecimal(999.999));
            budgetKeyItems.add(budgetKeyItem);
        }
        budgetKeyTable.setBudgetKeyItems(budgetKeyItems);
        budgetKeyTable.setNumberOfDigits(3);

        // then
        assertThat(method.keySum(budgetKeyTable)).isEqualTo(new BigDecimal(999.999).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(method.isValid(budgetKeyTable)).isEqualTo(false);


    }

    @Test
    public void testIsValid() {

        //given
        BudgetKeyValueMethod method = BudgetKeyValueMethod.PROMILLE;
        BudgetKeyTable budgetKeyTable = new BudgetKeyTable();
        SortedSet<BudgetKeyItem> budgetKeyItems = new TreeSet<BudgetKeyItem>();

        //when

        for(int i = 0; i < 1; i = i+1) {
                        BudgetKeyItem budgetKeyItem = new BudgetKeyItem();
                        budgetKeyItem.setTargetValue(new BigDecimal(999.9999));
                        budgetKeyItems.add(budgetKeyItem);
                    }
        budgetKeyTable.setBudgetKeyItems(budgetKeyItems);
        budgetKeyTable.setNumberOfDigits(3);

        // then
        assertThat(method.keySum(budgetKeyTable)).isEqualTo(new BigDecimal(1000).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(method.isValid(budgetKeyTable)).isEqualTo(true);

    }

    @Test
    public void testIsAlsoValid() {

        //given
        BudgetKeyValueMethod method = BudgetKeyValueMethod.PROMILLE;
        BudgetKeyTable budgetKeyTable = new BudgetKeyTable();
        SortedSet<BudgetKeyItem> budgetKeyItems = new TreeSet<BudgetKeyItem>();

        //when

        for(int i = 0; i < 1; i = i+1) {
            BudgetKeyItem budgetKeyItem = new BudgetKeyItem();
            budgetKeyItem.setTargetValue(new BigDecimal(1000.0001));
            budgetKeyItems.add(budgetKeyItem);
        }
        budgetKeyTable.setBudgetKeyItems(budgetKeyItems);
        budgetKeyTable.setNumberOfDigits(3);

        // then
        assertThat(method.keySum(budgetKeyTable)).isEqualTo(new BigDecimal(1000).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(method.isValid(budgetKeyTable)).isEqualTo(true);

    }

}
