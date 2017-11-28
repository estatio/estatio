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
package org.estatio.module.numerator.integtests;

import java.math.BigInteger;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.isisaddons.module.base.platform.fixturesupport.DataEnumPersist;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.numerator.integtests.dom.NumeratorExampleObject;
import org.estatio.module.numerator.integtests.dom.NumeratorExampleObject_enum;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class NumeratorRepository_IntegTest extends NumeratorModuleIntegTestAbstract {

    @Inject
    NumeratorRepository numeratorRepository;
    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    NumeratorExampleObject propertyOxf;
    NumeratorExampleObject propertyKal;

    ApplicationTenancy applicationTenancyOxf;
    ApplicationTenancy applicationTenancyKal;

    @Before
    public void setUp() throws Exception {
        runFixtureScript(new DataEnumPersist<>(NumeratorExampleObject_enum.class));


        applicationTenancyKal = ApplicationTenancy_enum.NlKal.findUsing(serviceRegistry);
        applicationTenancyOxf = ApplicationTenancy_enum.GbOxf.findUsing(serviceRegistry);

        propertyKal = NumeratorExampleObject_enum.Kal.findUsing(serviceRegistry);
        propertyOxf = NumeratorExampleObject_enum.Oxf.findUsing(serviceRegistry);
    }


    public static class AllNumerators extends NumeratorRepository_IntegTest {

        @Test
        public void whenExist() throws Exception {

            numeratorRepository
                    .createScopedNumerator("Invoice number", propertyOxf, "ABC-%05d", new BigInteger("10"),  applicationTenancyOxf);
            numeratorRepository
                    .createScopedNumerator("Invoice number", propertyKal, "DEF-%05d", new BigInteger("100"), applicationTenancyKal);
            numeratorRepository.createGlobalNumerator("Collection number", "ABC-%05d", new BigInteger("1000"), applicationTenancyOxf);

            assertThat(numeratorRepository.allNumerators().size(), is(3));
        }

    }

    public static class FindGlobalNumerator extends NumeratorRepository_IntegTest {

        @Test
        public void whenExists() throws Exception {

            // given
            numeratorRepository
                    .createScopedNumerator("Invoice number", propertyOxf, "ABC-%05d", new BigInteger("10"),  applicationTenancyOxf);
            numeratorRepository
                    .createScopedNumerator("Invoice number", propertyKal, "DEF-%05d", new BigInteger("100"), applicationTenancyKal);
            numeratorRepository.createGlobalNumerator("Collection number", "ABC-%05d", new BigInteger("1000"), applicationTenancyOxf);

            // when
            Numerator in = numeratorRepository
                    .findGlobalNumerator("Collection number", applicationTenancyOxf);

            // then
            assertThat(in.getLastIncrement(), is(new BigInteger("1000")));
        }

    }

    public static class FindScopedNumerator extends NumeratorRepository_IntegTest {

        @Test
        public void whenExists() throws Exception {

            // given
            numeratorRepository
                    .createScopedNumerator("Invoice number", propertyOxf, "ABC-%05d", new BigInteger("10"),  applicationTenancyOxf);
            numeratorRepository
                    .createScopedNumerator("Invoice number", propertyKal, "DEF-%05d", new BigInteger("100"), applicationTenancyKal);
            numeratorRepository.createGlobalNumerator("Collection number", "ABC-%05d", new BigInteger("1000"), applicationTenancyOxf);

            // when
            Numerator in = numeratorRepository
                    .findScopedNumeratorIncludeWildCardMatching("Invoice number", propertyOxf, applicationTenancyOxf);

            // then
            assertThat(in.getLastIncrement(), is(new BigInteger("10")));
        }

        ApplicationTenancy wildCardAppTenancy;
        ApplicationTenancy appTenToBefound;
        ApplicationTenancy appTenNotToBefound1;
        ApplicationTenancy appTenNotToBefound2;

        @Test
        public void withWildCard() throws Exception {

            // given
            wildCardAppTenancy = applicationTenancyRepository.newTenancy("France/wildcard/FR03", "/FRA/%/FR03", null);
            numeratorRepository
                    .createScopedNumerator("Invoice number", propertyKal, "AAA-%05d", new BigInteger("100"), wildCardAppTenancy);

            appTenToBefound = applicationTenancyRepository.newTenancy("France/property/FR03", "/FRA/ABC/FR03", null);
            appTenNotToBefound1 = applicationTenancyRepository.newTenancy("France/property/FR02", "/FRA/ABC/FR02", null);
            appTenNotToBefound2 = applicationTenancyRepository.newTenancy("France/no property", "/FRA", null);

            // when
            Numerator inToBeFound = numeratorRepository
                    .findScopedNumeratorIncludeWildCardMatching("Invoice number", propertyKal, appTenToBefound);
            Numerator inNotToBeFound1 = numeratorRepository
                    .findScopedNumeratorIncludeWildCardMatching("Invoice number", propertyKal, appTenNotToBefound1);
            Numerator inNotToBeFound2 = numeratorRepository
                    .findScopedNumeratorIncludeWildCardMatching("Invoice number", propertyKal, appTenNotToBefound2);

            // then
            assertThat(inToBeFound.getLastIncrement(), is(new BigInteger("100")));
            assertNull(inNotToBeFound1);
            assertNull(inNotToBeFound2);

        }

    }

    public static class Increment extends NumeratorRepository_IntegTest {

        private Numerator scopedNumerator;
        private Numerator scopedNumerator2;
        private Numerator globalNumerator;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            scopedNumerator = numeratorRepository
                    .createScopedNumerator("Invoice number", propertyOxf, "ABC-%05d", new BigInteger("10"),  applicationTenancyOxf);
            scopedNumerator2 = numeratorRepository
                    .createScopedNumerator("Invoice number", propertyKal, "DEF-%05d", new BigInteger("100"), applicationTenancyKal);
            globalNumerator = numeratorRepository
                    .createGlobalNumerator("Collection number", "ABC-%05d", new BigInteger("1000"), applicationTenancyOxf);


        }

        @Test
        public void forScopedNumerator() throws Exception {

            // given
            assertThat(scopedNumerator.getLastIncrement(), is(new BigInteger("10")));

            // when
            assertThat(scopedNumerator.nextIncrementStr(), is("ABC-00011"));

            // then
            assertThat(scopedNumerator.getLastIncrement(), is(new BigInteger("11")));
        }

        @Test
        public void forGlobalNumerator() throws Exception {

            // givem
            //globalNumerator = numeratorRepository.findGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME);
            assertThat(globalNumerator.getLastIncrement(), is(new BigInteger("1000")));

            // when
            assertThat(globalNumerator.nextIncrementStr(), is("ABC-01001"));

            // then
            assertThat(globalNumerator.getLastIncrement(), is(new BigInteger("1001")));
        }

    }

}