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
import java.math.MathContext;

import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.estatio.dom.utils.CalendarUtils;
import org.estatio.dom.valuetypes.LocalDateInterval;

public enum InvoicingFrequency {

    WEEKLY_IN_ADVANCE(
            "RRULE:FREQ=WEEKLY;INTERVAL=1",
            PaidIn.ADVANCE,
            BigDecimal.valueOf(7), BigDecimal.valueOf(365.25)),
    WEEKLY_IN_ARREARS(
            "RRULE:FREQ=WEEKLY;INTERVAL=1",
            PaidIn.ARREARS, 
            BigDecimal.valueOf(7), BigDecimal.valueOf(365.25)),
    MONTHLY_IN_ADVANCE(
            "RRULE:FREQ=MONTHLY;INTERVAL=1",
            PaidIn.ADVANCE, 
            BigDecimal.valueOf(1), BigDecimal.valueOf(12)), 
    MONTHLY_IN_ARREARS(
            "RRULE:FREQ=MONTHLY;INTERVAL=1",
            PaidIn.ARREARS, 
            BigDecimal.valueOf(1), BigDecimal.valueOf(12)), 
    QUARTERLY_IN_ADVANCE(
            "RRULE:FREQ=MONTHLY;INTERVAL=3",
            PaidIn.ADVANCE, 
            BigDecimal.valueOf(3), BigDecimal.valueOf(12)), 
    QUARTERLY_IN_ADVANCE_PLUS1M(
            "RRULE:FREQ=MONTHLY;INTERVAL=3;BYMONTH=2,5,8,11",
            PaidIn.ADVANCE, 
            BigDecimal.valueOf(3), BigDecimal.valueOf(12)), 
    QUARTERLY_IN_ARREARS(
            "RRULE:FREQ=MONTHLY;INTERVAL=3",
            PaidIn.ARREARS, 
            BigDecimal.valueOf(3), BigDecimal.valueOf(12)), 
    SEMI_YEARLY_IN_ADVANCE(
            "RRULE:FREQ=MONTHLY;INTERVAL=6",
            PaidIn.ADVANCE, 
            BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
    SEMI_YEARLY_IN_ARREARS(
            "RRULE:FREQ=MONTHLY;INTERVAL=6",
            PaidIn.ARREARS, 
            BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
    YEARLY_IN_ADVANCE(
            "RRULE:FREQ=YEARLY;INTERVAL=1",
            PaidIn.ADVANCE, 
            BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
    YEARLY_IN_ARREARS(
            "RRULE:FREQ=YEARLY;INTERVAL=1",
            PaidIn.ARREARS, 
            BigDecimal.valueOf(1), BigDecimal.valueOf(1));

    static enum PaidIn {
        ADVANCE,
        ARREARS
    }
    private InvoicingFrequency(
            final String rrule, 
            final PaidIn paidIn, 
            final BigDecimal numerator, 
            final BigDecimal denominator){
        this.rrule = rrule;
        this.numerator = numerator;
        this.denominator = denominator;
        this.paidIn = paidIn;
    }

    private final String rrule;
    
    private final PaidIn paidIn;
    private final BigDecimal numerator;
    private final BigDecimal denominator;
    

    public String getRrule() {
        return rrule;
    }

    public Boolean isInAdvance() {
        return paidIn == PaidIn.ADVANCE;
    }

    public LocalDate getDueDate(final LocalDate date){
        LocalDateInterval interval = new LocalDateInterval(CalendarUtils.intervalContaining(date, rrule));
        return interval.startDate();
    }
    
    public LocalDate getNextDueDate(final LocalDate date){
        LocalDateInterval interval = new LocalDateInterval(CalendarUtils.intervalContaining(date, rrule));
        return interval.endDateExcluding();
    }
    
    public BigDecimal annualMultiplier(){
        return numerator.divide(denominator, MathContext.DECIMAL64);
    }

    public final static Ordering<InvoicingFrequency> ORDERING_BY_TYPE = 
            Ordering.<InvoicingFrequency> natural().nullsFirst();
}
