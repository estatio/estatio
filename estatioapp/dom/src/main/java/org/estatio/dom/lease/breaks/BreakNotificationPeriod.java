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
package org.estatio.dom.lease.breaks;

import org.joda.time.LocalDate;

import org.estatio.dom.Titled;
import org.incode.module.base.dom.utils.StringUtils;

public enum BreakNotificationPeriod implements Titled {

    ONE_WEEK(1,TimeUnit.WEEK),
    TWO_WEEKS(2,TimeUnit.WEEK), 
    ONE_MONTH(1,TimeUnit.MONTH),
    TWO_MONTHS(2,TimeUnit.MONTH),
    THREE_MONTHS(3,TimeUnit.MONTH),
    SIX_MONTHS(6,TimeUnit.MONTH),
    ONE_YEAR(1,TimeUnit.YEAR);

    private int num;
    private TimeUnit timeUnit;

    private BreakNotificationPeriod(final int num, final TimeUnit timeUnit) {
        this.num = num;
        this.timeUnit = timeUnit;
    }
    public String title() {
        return StringUtils.enumTitle(this.toString());
    }

    public LocalDate subtractFrom(final LocalDate date) {
        return timeUnit.subtractFrom(date, num);
    }
    public LocalDate addTo(final LocalDate date) {
        return timeUnit.addTo(date, num);
    }
}

enum TimeUnit {
    WEEK {
        @Override
        LocalDate subtractFrom(final LocalDate date, final int num) {
            return date.minusWeeks(num);
        }

        @Override
        LocalDate addTo(final LocalDate date, final int num) {
            return date.plusWeeks(num);
        }
    },
    MONTH {
        @Override
        LocalDate subtractFrom(final LocalDate date, final int num) {
            return date.minusMonths(num);
        }

        @Override
        LocalDate addTo(final LocalDate date, final int num) {
            return date.plusMonths(num);
        }
    }, 
    YEAR {
        @Override
        LocalDate subtractFrom(final LocalDate date, final int num) {
            return date.minusYears(num);
        }

        @Override
        LocalDate addTo(final LocalDate date, final int num) {
            return date.plusYears(num);
        }
    };
    
    abstract LocalDate subtractFrom(final LocalDate date, int num);
    abstract LocalDate addTo(final LocalDate date, int num);
}
