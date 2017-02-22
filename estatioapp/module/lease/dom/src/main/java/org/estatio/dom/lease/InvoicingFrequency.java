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
package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Ordering;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

import org.incode.module.base.dom.utils.StringUtils;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.dom.invoice.InvoicingInterval;
import org.estatio.dom.utils.CalendarUtils;

public enum InvoicingFrequency {

    WEEKLY_IN_ADVANCE(
            Frequency.WEEKLY,
            PaidIn.ADVANCE,
            BigDecimal.valueOf(7), BigDecimal.valueOf(365.25)),
    WEEKLY_IN_ARREARS(
            Frequency.WEEKLY,
            PaidIn.ARREARS,
            BigDecimal.valueOf(7), BigDecimal.valueOf(365.25)),
    MONTHLY_IN_ADVANCE(
            Frequency.MONTHLY,
            PaidIn.ADVANCE,
            BigDecimal.valueOf(1), BigDecimal.valueOf(12)),
    MONTHLY_IN_ARREARS(
            Frequency.MONTHLY,
            PaidIn.ARREARS,
            BigDecimal.valueOf(1), BigDecimal.valueOf(12)),
    QUARTERLY_IN_ADVANCE(
            Frequency.QUARTERLY,
            PaidIn.ADVANCE,
            BigDecimal.valueOf(3), BigDecimal.valueOf(12)),
    QUARTERLY_IN_ADVANCE_PLUS1M(
            Frequency.QUARTERLY_PLUS1M,
            PaidIn.ADVANCE,
            BigDecimal.valueOf(3), BigDecimal.valueOf(12)),
    QUARTERLY_IN_ADVANCE_PLUS2M(
            Frequency.QUARTERLY_PLUS2M,
            PaidIn.ADVANCE,
            BigDecimal.valueOf(3), BigDecimal.valueOf(12)),
    QUARTERLY_IN_ARREARS(
            Frequency.QUARTERLY,
            PaidIn.ARREARS,
            BigDecimal.valueOf(3), BigDecimal.valueOf(12)),
    SEMI_YEARLY_IN_ADVANCE(
            Frequency.SEMI_YEARLY,
            PaidIn.ADVANCE,
            BigDecimal.valueOf(1), BigDecimal.valueOf(2)),
    SEMI_YEARLY_IN_ARREARS(
            Frequency.SEMI_YEARLY,
            PaidIn.ARREARS,
            BigDecimal.valueOf(1), BigDecimal.valueOf(2)),
    YEARLY_IN_ADVANCE(
            Frequency.YEARLY,
            PaidIn.ADVANCE,
            BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
    YEARLY_IN_ARREARS(
            Frequency.YEARLY,
            PaidIn.ARREARS,
            BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
    FIXED_IN_ADVANCE(
            null,
            PaidIn.ADVANCE,
            BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
    FIXED_IN_ARREARS(
            null,
            PaidIn.ARREARS,
            BigDecimal.valueOf(1), BigDecimal.valueOf(1));

    static enum PaidIn {
        ADVANCE,
        ARREARS
    }

    private InvoicingFrequency(
            final Frequency frequency,
            final PaidIn paidIn,
            final BigDecimal numerator,
            final BigDecimal denominator) {

        this.rrule = frequency == null ? null : frequency.getRrule();
        this.numerator = numerator;
        this.denominator = denominator;
        this.paidIn = paidIn;
    }

    private final String rrule;
    private final PaidIn paidIn;
    private final BigDecimal numerator;
    private final BigDecimal denominator;

    private LocalDate dueDateOfInterval(final Interval interval) {
        if (interval == null) {
            return null;
        }
        return paidIn == PaidIn.ADVANCE ? new LocalDate(interval.getStartMillis()) : new LocalDate(interval.getEndMillis()).minusDays(1);
    }

    private LocalDate dueDateOfInterval(final LocalDateInterval interval) {
        if (interval == null) {
            return null;
        }
        return paidIn == PaidIn.ADVANCE ? interval.startDate() : interval.endDate();
    }

    public InvoicingInterval intervalContaining(final LocalDate date) {
        Interval interval = CalendarUtils.intervalContaining(date, rrule);
        return new InvoicingInterval(interval, dueDateOfInterval(interval));
    }

    public InvoicingInterval intervalMatching(final LocalDate startDate) {
        final Interval interval = CalendarUtils.intervalMatching(startDate, this.rrule);
        if (interval == null) {
            return null;
        }
        return new InvoicingInterval(interval, dueDateOfInterval(interval));
    }

    public List<InvoicingInterval> intervalsInRange(final LocalDateInterval interval) {
        return intervalsInRange(interval.startDate(), interval.endDateExcluding());
    }

    public List<InvoicingInterval> intervalsInRange(final LocalDate periodStartDate, final LocalDate nextPeriodStartDate) {
        List<InvoicingInterval> invoicingIntervals = new ArrayList<>();
        for (Interval interval : CalendarUtils.intervalsInRange(periodStartDate, nextPeriodStartDate, this.rrule)) {
            invoicingIntervals.add(new InvoicingInterval(interval, dueDateOfInterval(interval)));
        }
        return invoicingIntervals;
    }

    public List<InvoicingInterval> intervalsInDueDateRange(
            final LocalDate periodStartDate,
            final LocalDate periodEndDate) {
        List<InvoicingInterval> invoicingIntervals = new ArrayList<>();
        if (periodEndDate.compareTo(periodStartDate) > 0) {
            for (Interval interval : CalendarUtils.intervalsInRange(periodStartDate, periodEndDate, this.rrule)) {
                LocalDate dueDate = dueDateOfInterval(interval);
                if (dueDate.compareTo(periodEndDate) < 0) {
                    invoicingIntervals.add(new InvoicingInterval(interval, dueDate));
                }
            }
        }
        return invoicingIntervals;
    }

    public List<InvoicingInterval> intervalsInDueDateRange(
            final LocalDateInterval rangeInterval,
            final LocalDateInterval sourceInterval) {
        List<InvoicingInterval> invoicingIntervals = new ArrayList<>();
        if (rrule == null) {
            LocalDate dueDateOfSourceInterval = dueDateOfInterval(sourceInterval);
            if (rangeInterval.contains(dueDateOfSourceInterval)) {
                invoicingIntervals.add(new InvoicingInterval(sourceInterval, dueDateOfSourceInterval));
            }
        } else {
            for (Interval interval : CalendarUtils.intervalsInRange(
                    rangeInterval.startDate(),
                    rangeInterval.endDateExcluding(),
                    this.rrule)) {
                LocalDate dueDate = dueDateOfInterval(interval);
                if (rangeInterval.contains(dueDate)) {
                    invoicingIntervals.add(new InvoicingInterval(interval, dueDate));
                }
            }
        }
        return invoicingIntervals;
    }

    public BigDecimal annualMultiplier() {
        return numerator.divide(denominator, MathContext.DECIMAL64);
    }

    public final static Ordering<InvoicingFrequency> ORDERING_BY_TYPE =
            Ordering.<InvoicingFrequency> natural().nullsFirst();

    public String title() {
        return StringUtils.enumTitle(this.name());
    }


    public static class Meta {

        public final static int MAX_LEN = 30;

        private Meta() {}


    }

    public PaidIn getPaidIn(){
        return paidIn;
    }

}
