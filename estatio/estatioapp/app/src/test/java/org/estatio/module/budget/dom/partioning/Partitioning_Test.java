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
import java.math.MathContext;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.budget.dom.budget.Budget;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Partitioning_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final Partitioning pojo = new Partitioning();
            newPojoTester()
                    .withFixture(pojos(Budget.class, Budget.class))
                    .exercise(pojo);
        }

    }

    public static class FractionOfYear extends Partitioning_Test {

        @Test
        public void testFraction() throws Exception {

            // given
            Partitioning partitioning = new Partitioning();
            // when
            partitioning.setStartDate(new LocalDate(2015,01,01));
            partitioning.setEndDate(new LocalDate(2015,06,30));
            // then
            assertThat(partitioning.getFractionOfYear()).isEqualTo(new BigDecimal("181").divide(new BigDecimal("365"), MathContext.DECIMAL64));

        }

    }

}
