/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.module.budget.dom.keytable;

import lombok.Getter;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public enum PonderingAreaCoefficients {
    DEFAULT(
            Arrays.asList(
                    new Tuple(new BigDecimal("350.0"), BigDecimal.ONE),
                    new Tuple(null, new BigDecimal("0.60"))),
            Arrays.asList(
                    new Tuple(null, new BigDecimal("0.40"))
            )),
    FOR_HYPERMARKET(
            Arrays.asList(
                    new Tuple(new BigDecimal("1000.0"), BigDecimal.ONE),
                    new Tuple(new BigDecimal("1000.0"), new BigDecimal("0.90")),
                    new Tuple(new BigDecimal("2000.0"), new BigDecimal("0.80")),
                    new Tuple(new BigDecimal("2000.0"), new BigDecimal("0.70")),
                    new Tuple(null, new BigDecimal("0.60"))),
            Arrays.asList(
                new Tuple(null, new BigDecimal("0.40"))
            ));

    @Getter
    private List<Tuple> salesCoefficients;

    @Getter
    private List<Tuple> storageCoefficients;

    PonderingAreaCoefficients(final List<Tuple> salesCoefficients, final List<Tuple> storageCoefficients) {
        this.salesCoefficients = salesCoefficients;
        this.storageCoefficients = storageCoefficients;
    }

    public static class Tuple {
        public final BigDecimal area;
        public final BigDecimal coefficient;

        public Tuple(@Nullable BigDecimal area, BigDecimal coefficient) {
            this.area = area;
            this.coefficient = coefficient;
        }
    }
}