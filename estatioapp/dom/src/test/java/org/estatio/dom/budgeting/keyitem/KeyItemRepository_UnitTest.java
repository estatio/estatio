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

package org.estatio.dom.budgeting.keyitem;

import java.math.BigDecimal;

import org.junit.Test;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.keytable.KeyTable;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyItemRepository_UnitTest {

    KeyItemRepository keyItemRepository = new KeyItemRepository();

    @Test
    public void zeroSourceValue() {

        //given
        KeyTable keyTable = new KeyTable();
        Unit unit = new Unit();
        BigDecimal sourcevalue = BigDecimal.ZERO;
        BigDecimal keyValue = new BigDecimal(10);

        //when
        String validateNewBudgetKeyItem = keyItemRepository.validateNewItem(
                keyTable,
                unit,
                sourcevalue,
                keyValue);

        //then
        assertThat(validateNewBudgetKeyItem).isEqualTo("sourceValue cannot be zero or less than zero");
    }

    @Test
    public void negativeSourceValue() {

        //given
        KeyTable keyTable = new KeyTable();
        Unit unit = new Unit();
        BigDecimal sourcevalue = BigDecimal.valueOf(-0.001);
        BigDecimal keyValue = new BigDecimal(10);

        //when
        String validateNewBudgetKeyItem = keyItemRepository.validateNewItem(
                keyTable,
                unit,
                sourcevalue,
                keyValue);

        //then
        assertThat(validateNewBudgetKeyItem).isEqualTo("sourceValue cannot be zero or less than zero");
    }

    @Test
    public void negativeKeyValue() {

        //given
        KeyTable keyTable = new KeyTable();
        Unit unit = new Unit();
        BigDecimal sourcevalue = new BigDecimal(1);
        BigDecimal keyValue = BigDecimal.valueOf(-0.001);

        //when
        String validateNewBudgetKeyItem = keyItemRepository.validateNewItem(
                keyTable,
                unit,
                sourcevalue,
                keyValue);

        //then
        assertThat(validateNewBudgetKeyItem).isEqualTo("keyValue cannot be less than zero");
    }

}
