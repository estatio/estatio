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
package org.estatio.module.party.dom;

import java.util.TreeSet;

import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import static org.assertj.core.api.Assertions.assertThat;

public class Organisation_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new Organisation());
        }
    }

    public static class Title extends Organisation_Test {

        @Mock
        Organisation mockOrganisation;

        @Test
        public void happy_case() throws Exception {
            final OrganisationPreviousName opn = new OrganisationPreviousName(mockOrganisation,
                    "Acme Corp.", new LocalDate(2007, 6, 5));

            assertThat(opn.title()).isEqualTo("Acme Corp. until 05-Jun-2007");
        }

    }

    public static class OrganisationPreviousNameCompareTo extends Organisation_Test {

        @Mock
        Organisation mockOrganisation;

        private OrganisationPreviousName firstOrganisationPreviousName;

        private OrganisationPreviousName secondOrganisationPreviousName;

        @Before
        public void setUp() throws Exception {
            firstOrganisationPreviousName = new OrganisationPreviousName(mockOrganisation, "Alpha", LocalDate.now());
            secondOrganisationPreviousName = new OrganisationPreviousName(mockOrganisation, "Beta", LocalDate.now());
        }

        @Test
        public void endDateOfFirstLaterThanSecond() throws Exception {
            firstOrganisationPreviousName.setEndDate(new LocalDate(2015, 1, 1));
            secondOrganisationPreviousName.setEndDate(new LocalDate(2014, 1, 1));

            assertThat(firstOrganisationPreviousName.compareTo(secondOrganisationPreviousName)).isEqualTo(1);
        }

        @Test
        public void endDateOfFirstBeforeSecond() throws Exception {
            firstOrganisationPreviousName.setEndDate(new LocalDate(2014, 1, 1));
            secondOrganisationPreviousName.setEndDate(new LocalDate(2015, 1, 1));

            assertThat(firstOrganisationPreviousName.compareTo(secondOrganisationPreviousName)).isEqualTo(-1);
        }

        @Test
        public void equalEndDates() throws Exception {
            firstOrganisationPreviousName.setEndDate(new LocalDate(2015, 1, 1));
            secondOrganisationPreviousName.setEndDate(new LocalDate(2015, 1, 1));

            // On equal end dates, sort alphabetically by name
            assertThat(firstOrganisationPreviousName.compareTo(secondOrganisationPreviousName)).isEqualTo(-1);

            TreeSet<OrganisationPreviousName> previousNames = new TreeSet<>();
            previousNames.add(secondOrganisationPreviousName);
            previousNames.add(firstOrganisationPreviousName);

            assertThat(previousNames.first().getName()).isEqualToIgnoringCase("Alpha");
        }
    }
}