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
package org.estatio.dom.index;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.apache.commons.lang3.ObjectUtils;

import org.apache.isis.applib.annotation.Programmatic;

public class IndexationService {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    @Programmatic
    public void indexate(final Indexable input) {

        final IndexationResult indexationResult;
        indexationResult = indexateToResult(input);
        indexationResult.apply(input);
    }

    private IndexationResult indexateToResult(final Indexable input) {
        final Index index = input.getIndex();
        if (index == null) {
            return IndexationResult.NULL;
        }
        return indexateWithIndexToResult(input);
    }

    private IndexationResult indexateWithIndexToResult(final Indexable input) {
        final Index index = input.getIndex();
        BigDecimal indexedValue = null;
        BigDecimal indexationPercentage = null;
        index.initialize(input);
        final BigDecimal baseIndexValue = input.getBaseIndexValue();
        final BigDecimal nextIndexValue = input.getNextIndexValue();
        final BigDecimal rebaseFactor = input.getRebaseFactor();
        final BigDecimal baseValue = input.getBaseValue();
        final BigDecimal levellingPercentage =
                input.getLevellingPercentage() == null ? ONE_HUNDRED : input.getLevellingPercentage();
        if (baseIndexValue != null && nextIndexValue != null) {
            final BigDecimal indexationFactor = nextIndexValue
                    .divide(baseIndexValue, MathContext.DECIMAL64)
                    .multiply(rebaseFactor, MathContext.DECIMAL64).setScale(3, RoundingMode.HALF_EVEN);
            indexationPercentage = (indexationFactor
                    .subtract(BigDecimal.ONE))
                    .multiply(ONE_HUNDRED).setScale(1, RoundingMode.HALF_EVEN);
            final BigDecimal levellingFactor = indexationPercentage
                    .multiply(levellingPercentage.divide(ONE_HUNDRED))
                    .divide(ONE_HUNDRED)
                    .add(BigDecimal.ONE);
            if (baseValue != null) {
                indexedValue = baseValue
                        .multiply(levellingFactor)
                        .setScale(2, RoundingMode.HALF_EVEN);
            }
        }
        return new IndexationResult(indexedValue, indexationPercentage, baseIndexValue, nextIndexValue);
    }

    static class IndexationResult {

        public static final IndexationResult NULL = new IndexationResult(null, null, null, null);

        private BigDecimal indexedValue = null;
        private BigDecimal indexationPercentage = null;
        private BigDecimal baseIndexValue = null;
        private BigDecimal nextIndexValue = null;

        public IndexationResult(
                final BigDecimal indexedValue,
                final BigDecimal indexationPercentage,
                final BigDecimal baseIndexValue,
                final BigDecimal nextIndexValue) {
            this.indexedValue = indexedValue;
            this.indexationPercentage = indexationPercentage;
            this.baseIndexValue = baseIndexValue;
            this.nextIndexValue = nextIndexValue;
        }

        public void apply(final Indexable indexable) {
            indexable.setBaseIndexValue(baseIndexValue);
            indexable.setIndexationPercentage(indexationPercentage);
            indexable.setNextIndexValue(nextIndexValue);
            indexable.setIndexedValue(
                    // Don't apply when negative indexation.
                    // Probably configurable in the future
                    ObjectUtils.compare(indexationPercentage, BigDecimal.ZERO) < 0 ?
                            indexable.getBaseValue() :
                            indexedValue);
        }
    }

}
