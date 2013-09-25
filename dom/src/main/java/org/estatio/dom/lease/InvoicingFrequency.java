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

import com.google.common.collect.Ordering;

public enum InvoicingFrequency {

    WEEKLY_IN_ADVANCE("RRULE:FREQ=WEEKLY;INTERVAL=1",true, BigDecimal.valueOf(7), BigDecimal.valueOf(365.25)),
    WEEKLY_IN_ARREARS("RRULE:FREQ=WEEKLY;INTERVAL=1",false, BigDecimal.valueOf(7), BigDecimal.valueOf(365.25)),
    MONTHLY_IN_ADVANCE("RRULE:FREQ=MONTHLY;INTERVAL=1",true, BigDecimal.valueOf(1), BigDecimal.valueOf(12)), 
    MONTHLY_IN_ARREARS("RRULE:FREQ=MONTHLY;INTERVAL=1",false, BigDecimal.valueOf(1), BigDecimal.valueOf(12)), 
    QUARTERLY_IN_ADVANCE("RRULE:FREQ=MONTHLY;INTERVAL=3",true, BigDecimal.valueOf(3), BigDecimal.valueOf(12)), 
    QUARTERLY_IN_ADVANCE_PLUS1M("RRULE:FREQ=MONTHLY;INTERVAL=3;BYMONTH=2,5,8,11",true, BigDecimal.valueOf(3), BigDecimal.valueOf(12)), 
    QUARTERLY_IN_ARREARS("RRULE:FREQ=MONTHLY;INTERVAL=3",false, BigDecimal.valueOf(3), BigDecimal.valueOf(12)), 
    SEMI_YEARLY_IN_ADVANCE("RRULE:FREQ=MONTHLY;INTERVAL=6",true, BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
    SEMI_YEARLY_IN_ARREARS("RRULE:FREQ=MONTHLY;INTERVAL=6",false, BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
    YEARLY_IN_ADVANCE("RRULE:FREQ=YEARLY;INTERVAL=1",true, BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
    YEARLY_IN_ARREARS("RRULE:FREQ=YEARLY;INTERVAL=1",false, BigDecimal.valueOf(1), BigDecimal.valueOf(1));

    private InvoicingFrequency(
            final String rrule, final Boolean inAdvance, 
            final BigDecimal numerator, 
            final BigDecimal denominator){
        this.rrule = rrule;
        this.numerator = numerator;
        this.denominator = denominator;
        this.inAdvance = inAdvance;
    }

    private final String rrule;
    
    private final Boolean inAdvance;
    private final BigDecimal numerator;
    private final BigDecimal denominator;
    

    public String getRrule() {
        return rrule;
    }

    public Boolean isInAdvance() {
        return inAdvance;
    }
    public BigDecimal getNumerator() {
        return numerator;
    }
    public BigDecimal getDenominator() {
        return denominator;
    }

    public final static Ordering<InvoicingFrequency> ORDERING_BY_TYPE = 
            Ordering.<InvoicingFrequency> natural().nullsFirst();
}
