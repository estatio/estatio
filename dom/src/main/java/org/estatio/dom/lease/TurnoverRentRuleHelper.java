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
package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TurnoverRentRuleHelper {

    private String rules[];

    public TurnoverRentRuleHelper(final String rule) {
        if (rule != null && rule.trim().length() != 0) {
            rules = rule.split(";");
        }
    }

    public boolean isValid() {
        return (rules != null && isValidRule());
    }

    private boolean isValidRule() {
        // check for uneven rules
        if (rules == null || rules.length % 2 == 0) {
            return false;
        }
        // check for numeric values
        for (String rule : rules) {
            if (!isNumeric(rule)) {
                return false;
            }
        }
        return true;
    }

    public BigDecimal calculateRent(final BigDecimal turnover) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal prevCap = BigDecimal.ZERO;
        BigDecimal cap = BigDecimal.ZERO;
        BigDecimal percentage;
        BigDecimal base;
        if (isValid() && turnover != null) {
            for (int i = 0; i < rules.length; i = i + 2) {
                base = BigDecimal.ZERO;
                if (i == rules.length - 1) {
                    // the last or single item
                    percentage = new BigDecimal(rules[i]).divide(LeaseConstants.PERCENTAGE_DIVISOR);
                    if (turnover.compareTo(prevCap) > 0) {
                        base = turnover.subtract(prevCap);
                    }
                } else {
                    percentage = new BigDecimal(rules[i + 1]).divide(LeaseConstants.PERCENTAGE_DIVISOR);
                    cap = new BigDecimal(rules[i]);
                    if (turnover.compareTo(cap) > 0) {
                        base = cap.subtract(prevCap);
                    } else {
                        if (turnover.compareTo(prevCap) > 0) {
                            base = turnover.subtract(prevCap);
                        }
                    }
                }
                total = total.add(base.multiply(percentage).setScale(2, RoundingMode.HALF_UP));
                prevCap = cap;
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public static boolean isNumeric(final String str) {
        // match a number with optional '-' and decimal.
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
