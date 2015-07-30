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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetKeyValueMethodTest {

    @Test
    public void testCalculateDefault() {

        BudgetKeyValueMethod method = BudgetKeyValueMethod.DEFAULT;
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(1))).isEqualTo(new BigDecimal(1));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(10))).isEqualTo(new BigDecimal("0.1"));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(100))).isEqualTo(new BigDecimal("0.01"));
    }

    @Test
    public void testCalculateThousand() {

        BudgetKeyValueMethod method = BudgetKeyValueMethod.PROMILLE;
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(1)).equals(new BigDecimal(1000)));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(10))).isEqualTo(new BigDecimal(100));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(100))).isEqualTo(new BigDecimal(10));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(1000))).isEqualTo(new BigDecimal(1));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(10000))).isEqualTo(new BigDecimal(0.1).setScale(1, BigDecimal.ROUND_HALF_DOWN));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(100000))).isEqualTo(new BigDecimal(0.01).setScale(2, BigDecimal.ROUND_HALF_DOWN));

    }

    @Test
    public void testCalculateHundred() {

        BudgetKeyValueMethod method = BudgetKeyValueMethod.PERCENT;
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(1)).equals(new BigDecimal(100)));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(10))).isEqualTo(new BigDecimal(10));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(100))).isEqualTo(new BigDecimal(1));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(1000))).isEqualTo(new BigDecimal(0.1).setScale(1, BigDecimal.ROUND_HALF_DOWN));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(10000))).isEqualTo(new BigDecimal(0.01).setScale(2, BigDecimal.ROUND_HALF_DOWN));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(100000))).isEqualTo(new BigDecimal(0.001).setScale(3, BigDecimal.ROUND_HALF_DOWN));

    }

}
