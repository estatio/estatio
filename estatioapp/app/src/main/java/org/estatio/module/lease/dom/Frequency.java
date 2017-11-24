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
package org.estatio.module.lease.dom;

import java.math.BigDecimal;

public enum Frequency {

    WEEKLY(
            "RRULE:FREQ=WEEKLY;INTERVAL=1",
            BigDecimal.valueOf(7),
            BigDecimal.valueOf(365.25)),
    MONTHLY(
            "RRULE:FREQ=MONTHLY;INTERVAL=1",
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(12)),
    QUARTERLY(
            "RRULE:FREQ=MONTHLY;INTERVAL=3",
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(12)),
    QUARTERLY_PLUS1M(
            "RRULE:FREQ=MONTHLY;INTERVAL=3;BYMONTH=2,5,8,11",
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(12)),
    QUARTERLY_PLUS2M(
            "RRULE:FREQ=MONTHLY;INTERVAL=3;BYMONTH=3,6,9,12",
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(12)),
    SEMI_YEARLY(
            "RRULE:FREQ=MONTHLY;INTERVAL=6",
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2)),
    YEARLY(
            "RRULE:FREQ=YEARLY;INTERVAL=1",
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(1)),
    YEARLY_PLUS6M(
            "RRULE:FREQ=YEARLY;INTERVAL=1;BYMONTH=7",
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(1)),
    FIXED(
            null,
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(1));

    private Frequency(
            final String rrule,
            final BigDecimal numerator,
            final BigDecimal denominator) {
        this.rrule = rrule;
        this.numerator = numerator;
        this.denominator = denominator;
    }

    private final String rrule;
    private final BigDecimal numerator;
    private final BigDecimal denominator;

    public String getRrule() {
        return rrule;
    }
}
