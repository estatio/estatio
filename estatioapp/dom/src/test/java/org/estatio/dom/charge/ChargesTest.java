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
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.tax.Tax;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChargesTest {

    FinderInteraction finderInteraction;

    Charges charges;

    @Before
    public void setup() {
        charges = new Charges() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
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


    public static class FindByAtPathAndReference extends ChargesTest {


        @Test
        public void happyCase() {

            charges.findByReference("*REF?1*");

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Charge.class));
            assertThat(finderInteraction.getQueryName(), is("findByReference"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object) "*REF?1*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }


    public static class AllChargeGroups extends ChargesTest {

        @Test
        public void happyCase() {

            charges.allCharges();

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
        }

    }

    public static class NewCharge extends ChargesTest {

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

            charges = new Charges() {
                @Override
                public Charge findByReference(String reference) {
                    return existingCharge;
                }
            };
            charges.setContainer(mockContainer);
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

            final Charge newCharge = charges.newCharge(newApplicationTenancy("/it"), "CG-REF", "CG-Name", "CG-Description", tax, chargeGroup);
            assertThat(newCharge.getReference(), is("CG-REF"));
            assertThat(newCharge.getName(), is("CG-Name"));
            assertThat(newCharge.getDescription(), is("CG-Description"));
            assertThat(newCharge.getTax(), is(tax));
            assertThat(newCharge.getGroup(), is(chargeGroup));
        }

        @Test
        public void newCharge_whenDoesExist() {
            existingCharge = new Charge();

            final Charge newCharge = charges.newCharge(newApplicationTenancy("/it"), "CG-REF", "Some other description", "Some other code", null, null);
            assertThat(newCharge, is(existingCharge));
        }

        private ApplicationTenancy newApplicationTenancy(final String path) {
            ApplicationTenancy applicationTenancy = new ApplicationTenancy();
            applicationTenancy.setPath(path);
            return applicationTenancy;
        }

    }

}