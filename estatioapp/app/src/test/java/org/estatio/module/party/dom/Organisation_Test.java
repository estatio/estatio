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

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.party.app.services.OrganisationNameNumberViewModel;

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

    @Test
    public void setChamberOfCommerceCodeIfNotAlready_works() throws Exception {

        // given
        final String chamberOfCommerceCode = "some code 123";
        Organisation organisation = new Organisation();

        // when
        organisation.setChamberOfCommerceCodeIfNotAlready(chamberOfCommerceCode);
        // then
        Assertions.assertThat(organisation.getChamberOfCommerceCode()).isEqualTo(chamberOfCommerceCode);

        // and when
        organisation.setChamberOfCommerceCodeIfNotAlready("some other code 456");
        // then still
        Assertions.assertThat(organisation.getChamberOfCommerceCode()).isEqualTo(chamberOfCommerceCode);

    }

    @Mock
    OrganisationPreviousNameRepository mockOrganisationPreviousNameRepository;

    @Test
    public void change_name_unsets_verified_flag() throws Exception {

        // given
        Organisation organisation = new Organisation();
        organisation.organisationPreviousNameRepository = mockOrganisationPreviousNameRepository;
        final String first_name = "First Name";
        organisation.setName(first_name);
        organisation.setVerified(true);

        final LocalDate previousNameEndDate = new LocalDate(2017, 1, 1);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrganisationPreviousNameRepository).newOrganisationPreviousName(first_name, previousNameEndDate);
        }});

        // when
        final String new_name = "New Name";
        organisation.changeName(new_name, previousNameEndDate);

        // then
        assertThat(organisation.isVerified()).isFalse();
        assertThat(organisation.getName()).isEqualTo(new_name);

    }

    @Test
    public void change_name_with_same_name_does_not_unset_verified_flag() throws Exception {

        // given
        Organisation organisation = new Organisation();
        final String first_name = "Same Name";
        organisation.setName(first_name);
        organisation.setVerified(true);

        final LocalDate previousNameEndDate = new LocalDate(2017, 1, 1);

        // when
        final String new_name = "Same Name";
        organisation.changeName(new_name, previousNameEndDate);

        // then
        assertThat(organisation.isVerified()).isTrue();

    }

    @Test
    public void change_chamber_of_commerce_code_unsets_verified_flag() throws Exception {

        // given
        Organisation organisation = new Organisation();
        final String first_code = "123456789";
        organisation.setChamberOfCommerceCode(first_code);
        organisation.setVerified(true);

        // when
        final String new_code = "987654321";
        organisation.change(null, null, new_code);

        // then
        assertThat(organisation.isVerified()).isFalse();
        assertThat(organisation.getChamberOfCommerceCode()).isEqualTo(new_code);

    }

    @Test
    public void change_chamber_of_commerce_code_with_same_code_does_not_unset_verified_flag() throws Exception {

        // given
        Organisation organisation = new Organisation();
        final String first_code = "123456789";
        organisation.setChamberOfCommerceCode(first_code);
        organisation.setVerified(true);

        // when
        final String new_code = "123456789";
        organisation.change(null, null, new_code);

        // then
        assertThat(organisation.isVerified()).isTrue();

    }

    @Mock ClockService mockClockService;

    @Test
    public void verify_works() throws Exception {

        // given
        Organisation organisation = new Organisation();
        organisation.clockService = mockClockService;
        organisation.organisationPreviousNameRepository = mockOrganisationPreviousNameRepository;

        final String looked_up_name = "Looked Up Name";
        final String chamberOfCommerceCode = "123456789";
        final LocalDate previousNameEndDate = new LocalDate(2017, 01,01);
        OrganisationNameNumberViewModel vm = new OrganisationNameNumberViewModel(looked_up_name, chamberOfCommerceCode, previousNameEndDate.plusDays(1));


        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrganisationPreviousNameRepository).newOrganisationPreviousName(null, previousNameEndDate);
        }});

        // when
        organisation.verify(vm);

        // then
        assertThat(organisation.getName()).isEqualTo(looked_up_name);
        assertThat(organisation.getChamberOfCommerceCode()).isEqualTo(chamberOfCommerceCode);
        assertThat(organisation.isVerified()).isTrue();

    }

    @Test
    public void verify_does_not_change_chamber_of_commerce_code_when_code_already_present() throws Exception {

        // given
        Organisation organisation = new Organisation();
        organisation.clockService = mockClockService;
        organisation.organisationPreviousNameRepository = mockOrganisationPreviousNameRepository;

        final String oldChamberOfCommerceCode = "123456789";
        organisation.setChamberOfCommerceCode(oldChamberOfCommerceCode);

        final String looked_up_name = "Looked Up Name";
        final String newChamberOfCommerceCode = "987654321";

        final LocalDate previousNameEndDate = new LocalDate(2017, 01,01);

        OrganisationNameNumberViewModel vm = new OrganisationNameNumberViewModel(looked_up_name, newChamberOfCommerceCode, previousNameEndDate.plusDays(1));


        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrganisationPreviousNameRepository).newOrganisationPreviousName(null, previousNameEndDate);
        }});

        // when
        organisation.verify(vm);

        // then
        assertThat(organisation.getName()).isEqualTo(looked_up_name);
        assertThat(organisation.getChamberOfCommerceCode()).isEqualTo(oldChamberOfCommerceCode);
        assertThat(organisation.isVerified()).isTrue();

    }

}