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

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseTermRepository_Test {

    LeaseTermRepository leaseTermRepository;
    LeaseItem leaseItem;

    @Before
    public void setup() {
        leaseItem = new LeaseItem();
        leaseTermRepository = new LeaseTermRepository();
    }

    public static class ValidateNewLeaseTerm extends LeaseTermRepository_Test {

        @Before
        public void setUp(){
            leaseItem.setType(LeaseItemType.RENT); // or any other type except deposit
        }


        @Test
        public void validate() {
            // valid
            testValidate(leaseItem, null, new LocalDate(2014, 1, 1), new LocalDate(2014, 1, 1), null);
            // valid
            testValidate(leaseItem, null, new LocalDate(2014, 1, 1), new LocalDate(2013, 12, 31), null);
            // invalid interval
            testValidate(leaseItem, null, new LocalDate(2014, 1, 1), new LocalDate(2013, 12, 30), "From 2014-01-01 to 2013-12-30 is not a valid interval");

            final LeaseTermForTesting previous = new LeaseTermForTesting();
            previous.setStartDate(new LocalDate(2014, 1, 1));
            // valid
            testValidate(leaseItem, previous, new LocalDate(2014, 1, 1), new LocalDate(2014, 12, 31), null);
            // start date before start date of previous
            testValidate(leaseItem, previous, new LocalDate(2013, 12, 31), new LocalDate(2014, 12, 31), "Start date must be on or after 2014-01-01");

            // when
            leaseItem.setType(LeaseItemType.TAX);
            // invalid
            testValidate(leaseItem, null, new LocalDate(2014, 1, 1), null, "A term of type TAX should have an end date");
            // valid
            testValidate(leaseItem, null, new LocalDate(2014, 1, 1), new LocalDate(2014, 1, 1), null);

        }

        private void testValidate(LeaseItem leaseItem, LeaseTerm previous, LocalDate startDate, LocalDate endDate, String value) {
            assertThat(leaseTermRepository.validateNewLeaseTerm(leaseItem, previous, startDate, endDate)).isEqualTo(value);
        }

    }

}
