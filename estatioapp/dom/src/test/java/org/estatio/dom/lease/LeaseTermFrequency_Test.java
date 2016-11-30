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
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseTermFrequency_Test {

    public static class NextDate extends LeaseTermFrequency_Test {

        @Test
        public void whenYearly() {
            assertThat(LeaseTermFrequency.YEARLY.nextDate(new LocalDate(2012, 1, 1))).isEqualTo(new LocalDate(2013, 1, 1));
            assertThat(LeaseTermFrequency.YEARLY.nextDate(new LocalDate(2012, 4, 15))).isEqualTo(new LocalDate(2013, 4, 15));
        }

        @Test
        public void whenNoFrequency() {
            assertThat(LeaseTermFrequency.NO_FREQUENCY.nextDate(new LocalDate(2012, 1, 1))).isNull();
            assertThat(LeaseTermFrequency.NO_FREQUENCY.nextDate(new LocalDate(2012, 4, 15))).isNull();
        }

    }
}