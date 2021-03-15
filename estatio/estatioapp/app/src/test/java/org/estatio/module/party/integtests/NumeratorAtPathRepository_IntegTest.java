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
package org.estatio.module.party.integtests;

import java.math.BigInteger;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.setup.PersonaEnumPersistAll;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;

import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.integtests.NumeratorModuleIntegTestAbstract;
import org.estatio.module.party.dom.NumeratorAtPathRepository;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.numerator.integtests.dom.NumeratorExampleObject;
import org.estatio.module.numerator.integtests.dom.NumeratorExampleObject_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class NumeratorAtPathRepository_IntegTest extends NumeratorModuleIntegTestAbstract {

    @Inject
    NumeratorAtPathRepository numeratorAtPathRepository;
    @Inject
    NumeratorRepository numeratorRepository;

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    NumeratorExampleObject exampleObjectOxf;
    NumeratorExampleObject exampleObjectKal;

    ApplicationTenancy applicationTenancyOxf;
    ApplicationTenancy applicationTenancyKal;

    @Before
    public void setUp() throws Exception {
        runFixtureScript(new PersonaEnumPersistAll<>(NumeratorExampleObject_enum.class));
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, ApplicationTenancy_enum.NlKal);
                executionContext.executeChild(this, ApplicationTenancy_enum.GbOxf);
            }
        });

        applicationTenancyKal = ApplicationTenancy_enum.NlKal.findUsing(serviceRegistry);
        applicationTenancyOxf = ApplicationTenancy_enum.GbOxf.findUsing(serviceRegistry);

        exampleObjectKal = NumeratorExampleObject_enum.Kal.findUsing(serviceRegistry);
        exampleObjectOxf = NumeratorExampleObject_enum.Oxf.findUsing(serviceRegistry);
    }

    public static class AllNumerators extends NumeratorAtPathRepository_IntegTest {

        @Test
        public void whenExist() throws Exception {

            numeratorAtPathRepository
                    .createScopedNumerator("Invoice number", exampleObjectOxf, "ABC-%05d", new BigInteger("10"), applicationTenancyOxf);
            numeratorAtPathRepository
                    .createScopedNumerator("Invoice number", exampleObjectKal, "DEF-%05d", new BigInteger("100"), applicationTenancyKal);
            numeratorAtPathRepository.createGlobalNumerator("Collection number", "ABC-%05d", new BigInteger("1000"), applicationTenancyOxf);

            assertThat(numeratorRepository.allNumerators()).hasSize(3);
        }

    }

    public static class FindGlobalNumerator extends NumeratorAtPathRepository_IntegTest {

        @Test
        public void whenExists() throws Exception {

            // given
            numeratorAtPathRepository
                    .createScopedNumerator("Invoice number", exampleObjectOxf, "ABC-%05d", new BigInteger("10"), applicationTenancyOxf);
            numeratorAtPathRepository
                    .createScopedNumerator("Invoice number", exampleObjectKal, "DEF-%05d", new BigInteger("100"), applicationTenancyKal);
            numeratorAtPathRepository.createGlobalNumerator("Collection number", "ABC-%05d", new BigInteger("1000"), applicationTenancyOxf);

            // when
            Numerator in = numeratorAtPathRepository
                    .findGlobalNumerator("Collection number", applicationTenancyOxf);

            // then
            assertThat(in.getLastIncrement()).isEqualTo(new BigInteger("1000"));
        }

    }

//    public static class FindScopedNumerator extends NumeratorRepository_IntegTest {
//
//        @Test
//        public void whenExists() throws Exception {
//
//            // given
//            numeratorAtPathRepository
//                    .createScopedNumerator("Invoice number", exampleObjectOxf, "ABC-%05d", new BigInteger("10"), applicationTenancyOxf);
//            numeratorAtPathRepository
//                    .createScopedNumerator("Invoice number", exampleObjectKal, "DEF-%05d", new BigInteger("100"), applicationTenancyKal);
//            numeratorAtPathRepository.createGlobalNumerator("Collection number", "ABC-%05d", new BigInteger("1000"), applicationTenancyOxf);
//
//            // when
//            Numerator in = numeratorAtPathRepository
//                    .findScopedNumeratorIncludeWildCardMatching("Invoice number", exampleObjectOxf, applicationTenancyOxf);
//
//            // then
//            assertThat(in.getLastIncrement()).isEqualTo(new BigInteger("10"));
//        }
//
//        ApplicationTenancy wildCardAppTenancy;
//        ApplicationTenancy appTenToBefound;
//        ApplicationTenancy appTenNotToBefound1;
//        ApplicationTenancy appTenNotToBefound2;
//
//        @Test
//        public void withWildCard() throws Exception {
//
//            // given
//            wildCardAppTenancy = applicationTenancyRepository.newTenancy("France/wildcard/FR03", "/FRA/%/FR03", null);
//            numeratorAtPathRepository
//                    .createScopedNumerator("Invoice number", exampleObjectKal, "AAA-%05d", new BigInteger("100"), wildCardAppTenancy);
//
//            appTenToBefound = applicationTenancyRepository.newTenancy("France/property/FR03", "/FRA/ABC/FR03", null);
//            appTenNotToBefound1 = applicationTenancyRepository.newTenancy("France/property/FR02", "/FRA/ABC/FR02", null);
//            appTenNotToBefound2 = applicationTenancyRepository.newTenancy("France/no property", "/FRA", null);
//
//            // when
//            Numerator inToBeFound = numeratorAtPathRepository
//                    .findScopedNumeratorIncludeWildCardMatching("Invoice number", exampleObjectKal, appTenToBefound);
//            Numerator inNotToBeFound1 = numeratorAtPathRepository
//                    .findScopedNumeratorIncludeWildCardMatching("Invoice number", exampleObjectKal, appTenNotToBefound1);
//            Numerator inNotToBeFound2 = numeratorAtPathRepository
//                    .findScopedNumeratorIncludeWildCardMatching("Invoice number", exampleObjectKal, appTenNotToBefound2);
//
//            // then
//            assertThat(inToBeFound.getLastIncrement()).isEqualTo(new BigInteger("100"));
//            assertThat(inNotToBeFound1).isNull();
//            assertThat(inNotToBeFound2).isNull();
//
//        }
//
//    }

    public static class Increment extends NumeratorAtPathRepository_IntegTest {

        private Numerator scopedNumerator;
        private Numerator scopedNumerator2;
        private Numerator globalNumerator;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            scopedNumerator = numeratorAtPathRepository
                    .createScopedNumerator("Invoice number", exampleObjectOxf, "ABC-%05d", new BigInteger("10"), applicationTenancyOxf);
            scopedNumerator2 = numeratorAtPathRepository
                    .createScopedNumerator("Invoice number", exampleObjectKal, "DEF-%05d", new BigInteger("100"), applicationTenancyKal);
            globalNumerator = numeratorAtPathRepository
                    .createGlobalNumerator("Collection number", "ABC-%05d", new BigInteger("1000"), applicationTenancyOxf);

        }

        @Test
        public void forScopedNumerator() throws Exception {

            // given
            assertThat(scopedNumerator.getLastIncrement()).isEqualTo(new BigInteger("10"));

            // when
            assertThat(scopedNumerator.nextIncrementStr()).isEqualTo("ABC-00011");

            // then
            assertThat(scopedNumerator.getLastIncrement()).isEqualTo(new BigInteger("11"));
        }

        @Test
        public void forGlobalNumerator() throws Exception {

            // givem
            //globalNumerator = numeratorAtPathRepository.findGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME);
            assertThat(globalNumerator.getLastIncrement()).isEqualTo(new BigInteger("1000"));

            // when
            assertThat(globalNumerator.nextIncrementStr()).isEqualTo("ABC-01001");

            // then
            assertThat(globalNumerator.getLastIncrement()).isEqualTo(new BigInteger("1001"));
        }

    }

}