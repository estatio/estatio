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

import org.joda.time.LocalDate;

import org.estatio.dom.utils.CalendarUtils;
import org.incode.module.base.dom.utils.StringUtils;


public enum LeaseTermFrequency {

    YEARLY("RRULE:FREQ=YEARLY;INTERVAL=1"),
    YEARLY_3("RRULE:FREQ=YEARLY;INTERVAL=3"),
    NO_FREQUENCY(null);

    private LeaseTermFrequency(final String rrule) {
        this.rrule = rrule;
    }
    
    
    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    private String rrule;

    public String rrule() {
        return rrule;
    }

    // //////////////////////////////////////

    public LocalDate nextDate(final LocalDate date) {
        return CalendarUtils.nextDate(date, this.rrule);
    }


    public static class Meta {

        public final static int MAX_LEN = 30;

        private Meta() {}

    }

}
