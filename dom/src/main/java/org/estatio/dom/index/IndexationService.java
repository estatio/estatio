/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import org.apache.isis.applib.annotation.NotContributed;

public class IndexationService {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    @NotContributed
    public void indexate(final Indexable input) {
        Index index = input.getIndex();

        BigDecimal indexationFactor;
        BigDecimal rebaseFactor;

        BigDecimal indexedValue = null;
        BigDecimal indexationPercentage = null;
        BigDecimal baseIndexValue = null;
        BigDecimal nextIndexValue = null;
        BigDecimal baseValue = null;
        BigDecimal levellingPercentage = null;
        BigDecimal levellingFactor = null;

        if (index != null) {
            index.initialize(input);
            baseIndexValue = input.getBaseIndexValue();
            nextIndexValue = input.getNextIndexValue();
            rebaseFactor = input.getRebaseFactor();
            baseValue = input.getBaseValue();
            levellingPercentage = input.getLevellingPercentage() == null ? ONE_HUNDRED : input.getLevellingPercentage();

            if (baseIndexValue != null && nextIndexValue != null) {
                indexationFactor =
                        nextIndexValue
                                .divide(baseIndexValue, MathContext.DECIMAL64)
                                .multiply(rebaseFactor, MathContext.DECIMAL64).setScale(3, RoundingMode.HALF_EVEN);
                indexationPercentage = (
                        indexationFactor
                                .subtract(BigDecimal.ONE))
                                .multiply(ONE_HUNDRED).setScale(1, RoundingMode.HALF_EVEN);

                levellingFactor =
                        indexationPercentage
                                .multiply(levellingPercentage.divide(ONE_HUNDRED))
                                .divide(ONE_HUNDRED)
                                .add(BigDecimal.ONE);

                
                if (baseValue != null) {
                    indexedValue =
                            baseValue
                                    .multiply(levellingFactor)
                                    .setScale(2, RoundingMode.HALF_EVEN);

                }
            }
        }

        input.setBaseIndexValue(baseIndexValue);
        input.setNextIndexValue(nextIndexValue);
        input.setIndexationPercentage(indexationPercentage);
        input.setIndexedValue(indexedValue);
    }

}
