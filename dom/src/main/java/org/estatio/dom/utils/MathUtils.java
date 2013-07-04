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
package org.estatio.dom.utils;

import java.math.BigDecimal;
import java.math.MathContext;

public class MathUtils {
	
	private MathUtils() {}
	
    public static BigDecimal round(BigDecimal input, int precision) {
        MathContext mc = new MathContext(precision+1);
        return input.round(mc);
    }

    public static boolean isZeroOrNull(BigDecimal input) {
        if (input == null) return true;
        if (input.compareTo(BigDecimal.ZERO) == 0) return true;
        return false;
    }

    public static boolean isNotZeroOrNull(BigDecimal input) {
        return !isZeroOrNull(input);
    }
}
