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
package org.estatio.integtests.numerator;

import java.math.BigInteger;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.asset.EstatioApplicationTenancyRepositoryForProperty;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.invoice.Constants;
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.NumeratorRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class Numerator_IntegTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                executionContext.executeChild(this, new PropertyForOxfGb());
                executionContext.executeChild(this, new PropertyForKalNl());
            }
        });
    }

    @Inject
    NumeratorRepository numeratorRepository;
    @Inject
    PropertyRepository propertyRepository;
    @Inject
    EstatioApplicationTenancyRepositoryForProperty estatioApplicationTenancyRepository;
    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    Property propertyOxf;
    Property propertyKal;

    ApplicationTenancy applicationTenancyOxf;
    ApplicationTenancy applicationTenancyKal;


    @Before
    public void setUp() throws Exception {
        propertyOxf = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
        propertyKal = propertyRepository.findPropertyByReference(PropertyForKalNl.REF);
        applicationTenancyKal = estatioApplicationTenancyRepository.findOrCreateTenancyFor(propertyKal);
        applicationTenancyOxf = estatioApplicationTenancyRepository.findOrCreateTenancyFor(propertyOxf);
    }


    public static class AllNumerators extends Numerator_IntegTest {

        @Test
        public void whenExist() throws Exception {

            numeratorRepository
                    .createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyOxf, "ABC-%05d", new BigInteger("10"),  applicationTenancyOxf);
            numeratorRepository
                    .createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyKal, "DEF-%05d", new BigInteger("100"), applicationTenancyKal);
            numeratorRepository.createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, "ABC-%05d", new BigInteger("1000"), applicationTenancyOxf);

            assertThat(numeratorRepository.allNumerators().size(), is(3));
        }

    }

    public static class FindGlobalNumerator extends Numerator_IntegTest {

        @Test
        public void whenExists() throws Exception {

            // given
            numeratorRepository
                    .createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyOxf, "ABC-%05d", new BigInteger("10"), applicationTenancyOxf);
            numeratorRepository
                    .createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyKal, "DEF-%05d", new BigInteger("100"), applicationTenancyKal);
            numeratorRepository.createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, "ABC-%05d", new BigInteger("1000"), applicationTenancyOxf);

            // when
            Numerator in = numeratorRepository
                    .findGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, applicationTenancyOxf);

            // then
            assertThat(in.getLastIncrement(), is(new BigInteger("1000")));
        }

    }

    public static class FindScopedNumerator extends Numerator_IntegTest {

        @Test
        public void whenExists() throws Exception {

            // given
            numeratorRepository
                    .createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyOxf, "ABC-%05d", new BigInteger("10"), applicationTenancyOxf);
            numeratorRepository
                    .createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyKal, "DEF-%05d", new BigInteger("100"), applicationTenancyKal);
            numeratorRepository.createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, "ABC-%05d", new BigInteger("1000"), applicationTenancyOxf);

            // when
            Numerator in = numeratorRepository
                    .findScopedNumeratorIncludeWildCardMatching(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyOxf, applicationTenancyOxf);

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
                    .createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyKal, "AAA-%05d", new BigInteger("100"), wildCardAppTenancy);

            appTenToBefound = applicationTenancyRepository.newTenancy("France/property/FR03", "/FRA/ABC/FR03", null);
            appTenNotToBefound1 = applicationTenancyRepository.newTenancy("France/property/FR02", "/FRA/ABC/FR02", null);
            appTenNotToBefound2 = applicationTenancyRepository.newTenancy("France/no property", "/FRA", null);

            // when
            Numerator inToBeFound = numeratorRepository
                    .findScopedNumeratorIncludeWildCardMatching(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyKal, appTenToBefound);
            Numerator inNotToBeFound1 = numeratorRepository
                    .findScopedNumeratorIncludeWildCardMatching(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyKal, appTenNotToBefound1);
            Numerator inNotToBeFound2 = numeratorRepository
                    .findScopedNumeratorIncludeWildCardMatching(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyKal, appTenNotToBefound2);

            // then
            assertThat(inToBeFound.getLastIncrement(), is(new BigInteger("100")));
            assertNull(inNotToBeFound1);
            assertNull(inNotToBeFound2);

        }

    }

    public static class Increment extends Numerator_IntegTest {

        private Numerator scopedNumerator;
        private Numerator scopedNumerator2;
        private Numerator globalNumerator;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            scopedNumerator = numeratorRepository
                    .createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyOxf, "ABC-%05d", new BigInteger("10"),applicationTenancyOxf );
            scopedNumerator2 = numeratorRepository
                    .createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyKal, "DEF-%05d", new BigInteger("100"),applicationTenancyKal );
            globalNumerator = numeratorRepository
                    .createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, "ABC-%05d", new BigInteger("1000"), applicationTenancyOxf);
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