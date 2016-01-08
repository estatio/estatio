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
package org.estatio.dom.utils;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * General-purpose math utilities.
 */
public final class MathUtils {
    
    private MathUtils() {}
    
    public static BigDecimal round(final BigDecimal input, final int precision) {
        final MathContext mc = new MathContext(precision+1);
        return input.round(mc);
    }

    public static boolean isZeroOrNull(final BigDecimal input) {
        return input == null ? true : input.compareTo(BigDecimal.ZERO) == 0;
    }

    public static boolean isNotZeroOrNull(final BigDecimal input) {
        return !isZeroOrNull(input);
    }

    public static BigDecimal firstNonZero(final BigDecimal... values) {
        for (BigDecimal value : values) {
            if (value != null && value.compareTo(BigDecimal.ZERO) != 0) {
                return value;
            }
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal max(BigDecimal... input){
        BigDecimal max = BigDecimal.ZERO;
        for (BigDecimal value : input){
            if (value != null){
                max = max.max(value);
            }
        }
        return max;
    }
}
