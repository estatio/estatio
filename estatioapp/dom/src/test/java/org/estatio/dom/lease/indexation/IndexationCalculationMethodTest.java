/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.lease.indexation;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;

import org.estatio.dom.index.Indexable;
import org.estatio.dom.lease.LeaseTermForIndexable;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexationCalculationMethodTest {

    private static Indexable getIndexableWith(double baseValue, double baseIndexValue, double nextIndexValue, double rebaseFactor, double levellingPercentage) {
        LeaseTermForIndexable indexable;
        indexable = new LeaseTermForIndexable();
        //indexable.setIndex(mockIndex);
        indexable.setBaseValue(BigDecimal.valueOf(baseValue));
        indexable.setBaseIndexValue(BigDecimal.valueOf(baseIndexValue));
        indexable.setNextIndexValue(BigDecimal.valueOf(nextIndexValue));
        indexable.setRebaseFactor(BigDecimal.valueOf(rebaseFactor));
        indexable.setLevellingPercentage(BigDecimal.valueOf(levellingPercentage));
        return indexable;
    }

    public static class Indexate extends IndexationCalculationMethodTest {

        @Test
        public void happyCase() {
            tester(IndexationCalculationMethod.ITALY, 250000.00, 122.2, 111.1, 1.234, 100, BigDecimal.valueOf(12.2), BigDecimal.valueOf(280500).setScale(2));
            tester(IndexationCalculationMethod.FRANCE, 92471.50, 106.28, 108.38, 1.000, 100, BigDecimal.valueOf(1.976), BigDecimal.valueOf(94298.66).setScale(2));
            tester(IndexationCalculationMethod.FRANCE, 23050.29, 1012.00, 1632.00, 1.000, 100, BigDecimal.valueOf(61.265), BigDecimal.valueOf(37172.01).setScale(2));
        }

        @Test
        public void roundingErrors() {
            tester(IndexationCalculationMethod.ITALY, 250000.00, 110.0, 115.0, 1.000, 75, BigDecimal.valueOf(4.5), BigDecimal.valueOf(258437.50).setScale(2));
        }

        @Test
        @Ignore
        public void notNegativeIndexation() {
            tester(IndexationCalculationMethod.ITALY, 250000.00, 115.0, 110.0, 1.000, 75, BigDecimal.valueOf(-4.3), new BigDecimal("250000.0"));
        }


        private void tester(
                final IndexationCalculationMethod method,
                final double baseValue,
                final double baseIndexValue,
                final double nextIndexValue,
                final double rebaseFactor,
                final int levellingPercentage,
                final BigDecimal expectedIndexationPercentage,
                final BigDecimal expectedIndexedValue) {
            final LeaseTermForIndexable indexable = (LeaseTermForIndexable) getIndexableWith(
                    baseValue, baseIndexValue, nextIndexValue, rebaseFactor, levellingPercentage);

            final IndexationResult indexationResult = method.calc(indexable);
            indexationResult.apply(indexable);

            assertThat(indexable.getIndexationPercentage()).isEqualTo(expectedIndexationPercentage);
            assertThat(indexable.getIndexedValue()).isEqualTo(expectedIndexedValue);
        }


    }
}