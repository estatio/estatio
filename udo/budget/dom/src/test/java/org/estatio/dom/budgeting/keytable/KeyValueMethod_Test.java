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

package org.estatio.dom.budgeting.keytable;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import org.estatio.dom.budgeting.keyitem.KeyItem;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyValueMethod_Test {

    @Test
    public void testIsNotValid() {

        //given
        KeyValueMethod method = KeyValueMethod.PROMILLE;
        KeyTable keyTable = new KeyTable();
        SortedSet<KeyItem> budgetKeyItems = new TreeSet<>();

        //when

        for(int i = 0; i < 1; i = i+1) {
            KeyItem budgetKeyItem = new KeyItem();
            budgetKeyItem.setValue(BigDecimal.valueOf(999.999));
            budgetKeyItems.add(budgetKeyItem);
        }
        keyTable.setItems(budgetKeyItems);
        keyTable.setPrecision(3);

        // then
        assertThat(method.keySum(keyTable)).isEqualTo(BigDecimal.valueOf(999.999).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(method.isValid(keyTable)).isFalse();


    }

    @Test
    public void testIsValid() {

        //given
        KeyValueMethod method = KeyValueMethod.PROMILLE;
        KeyTable keyTable = new KeyTable();
        SortedSet<KeyItem> budgetKeyItems = new TreeSet<>();

        //when

        for(int i = 0; i < 1; i = i+1) {
                        KeyItem budgetKeyItem = new KeyItem();
                        budgetKeyItem.setValue(BigDecimal.valueOf(999.9999));
                        budgetKeyItems.add(budgetKeyItem);
                    }
        keyTable.setItems(budgetKeyItems);
        keyTable.setPrecision(3);

        // then
        assertThat(method.keySum(keyTable)).isEqualTo(new BigDecimal(1000).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(method.isValid(keyTable)).isTrue();

    }

    @Test
    public void testIsAlsoValid() {

        //given
        KeyValueMethod method = KeyValueMethod.PROMILLE;
        KeyTable keyTable = new KeyTable();
        SortedSet<KeyItem> budgetKeyItems = new TreeSet<>();

        //when

        for(int i = 0; i < 1; i = i+1) {
            KeyItem budgetKeyItem = new KeyItem();
            budgetKeyItem.setValue(BigDecimal.valueOf(1000.0001));
            budgetKeyItems.add(budgetKeyItem);
        }
        keyTable.setItems(budgetKeyItems);
        keyTable.setPrecision(3);

        // then
        assertThat(method.keySum(keyTable)).isEqualTo(new BigDecimal(1000).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(method.isValid(keyTable)).isTrue();

    }

}
