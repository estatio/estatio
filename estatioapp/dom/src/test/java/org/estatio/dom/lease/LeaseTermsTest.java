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

import java.math.BigInteger;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LeaseTermsTest {

    FinderInteraction finderInteraction;

    LeaseTermRepository leaseTermRepository;

    LeaseItem leaseItem;
    BigInteger sequence = BigInteger.TEN;

    LocalDate date = new LocalDate(2013, 4, 1);
    ;

    @Before
    public void setup() {

        leaseItem = new LeaseItem();

        leaseTermRepository = new LeaseTermRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<LeaseTerm> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    public static class FindByLeaseItemAndSequence extends LeaseTermsTest {

        @Test
        public void happyCase() {

            leaseTermRepository.findByLeaseItemAndSequence(leaseItem, sequence);

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));

            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(LeaseTerm.class));
            assertThat(finderInteraction.getQueryName(), is("findByLeaseItemAndSequence"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("leaseItem"), is((Object) leaseItem));
            assertThat(finderInteraction.getArgumentsByParameterName().get("sequence"), is((Object) sequence));

            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }
    }

    public static class LeaseTermsToBeApproved extends LeaseTermsTest {

        @Test
        public void happyCase() {

            leaseTermRepository.allLeaseTermsToBeApproved(date);

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));

            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(LeaseTerm.class));
            assertThat(finderInteraction.getQueryName(), is("findByStatusAndActiveDate"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("status"), is((Object) LeaseTermStatus.NEW));
            assertThat(finderInteraction.getArgumentsByParameterName().get("date"), is((Object) date));

            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }

    }

    public static class AllInvoices extends LeaseTermsTest {

        @Test
        public void allInvoices() {

            leaseTermRepository.allLeaseTerms();

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
        }
    }

    public static class ValidateNewLeaseTerm extends LeaseTermsTest {

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

            // when
            leaseItem.setType(LeaseItemType.SERVICE_CHARGE_BUDGETED);
            // invalid
            testValidate(leaseItem, null, new LocalDate(2014, 1, 1), null, "A term of type SERVICE_CHARGE_BUDGETED should have an end date");
            // valid
            testValidate(leaseItem, null, new LocalDate(2014, 1, 1), new LocalDate(2014, 1, 1), null);

        }

        private void testValidate(LeaseItem leaseItem, LeaseTerm previous, LocalDate startDate, LocalDate endDate, String value) {
            assertThat(leaseTermRepository.validateNewLeaseTerm(leaseItem, previous, startDate, endDate), is(value));
        }

    }

}
