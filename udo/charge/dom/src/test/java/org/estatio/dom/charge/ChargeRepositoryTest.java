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
package org.estatio.dom.charge;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;
import org.estatio.dom.tax.Tax;

import static org.assertj.core.api.Assertions.assertThat;

public class ChargeRepositoryTest {

    FinderInteraction finderInteraction;

    ChargeRepository chargeRepository;

    @Before
    public void setup() {
        chargeRepository = new ChargeRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected <T> T uniqueMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.UNIQUE_MATCH);
                return null;
            }

            @Override
            protected List<Charge> allInstances() {
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

    public static class FindByAtPathAndReference extends ChargeRepositoryTest {

        @Test
        public void happyCase() {

            chargeRepository.findByReference("*REF?1*");

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.UNIQUE_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(Charge.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByReference");
            assertThat(finderInteraction.getArgumentsByParameterName().get("reference")).isEqualTo((Object) "*REF?1*");
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class AllChargeGroups extends ChargeRepositoryTest {

        @Test
        public void happyCase() {

            chargeRepository.allCharges();

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_INSTANCES);
        }

    }

    public static class NewCharge extends ChargeRepositoryTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        private Charge existingCharge;

        private ChargeGroup chargeGroup;
        private Tax tax;

        @Before
        public void setup() {

            chargeGroup = new ChargeGroup();
            tax = new Tax();

            chargeRepository = new ChargeRepository() {
                @Override
                public Charge findByReference(String reference) {
                    return existingCharge;
                }
            };
            chargeRepository.setContainer(mockContainer);
        }

        @Test
        public void newCharge_whenDoesNotExist() {
            final Charge charge = new Charge();

            existingCharge = null;

            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(Charge.class);
                    will(returnValue(charge));

                    oneOf(mockContainer).persist(charge);
                }
            });

            final Charge newCharge = chargeRepository.newCharge(newApplicationTenancy("/it"), "CG-REF", "CG-Name", "CG-Description", tax, chargeGroup);
            assertThat(newCharge.getReference()).isEqualTo("CG-REF");
            assertThat(newCharge.getName()).isEqualTo("CG-Name");
            assertThat(newCharge.getDescription()).isEqualTo("CG-Description");
            assertThat(newCharge.getTax()).isEqualTo(tax);
            assertThat(newCharge.getGroup()).isEqualTo(chargeGroup);
        }

        @Test
        public void newCharge_whenDoesExist() {
            existingCharge = new Charge();

            final Charge newCharge = chargeRepository.newCharge(newApplicationTenancy("/it"), "CG-REF", "Some other description", "Some other code", null, null);
            assertThat(newCharge).isEqualTo(existingCharge);
        }

        private ApplicationTenancy newApplicationTenancy(final String path) {
            ApplicationTenancy applicationTenancy = new ApplicationTenancy();
            applicationTenancy.setPath(path);
            return applicationTenancy;
        }

    }

}