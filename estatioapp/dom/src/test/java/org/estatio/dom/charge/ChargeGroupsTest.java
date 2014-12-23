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
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChargeGroupsTest {

    FinderInteraction finderInteraction;

    ChargeGroups chargeGroups;

    @Before
    public void setup() {
        chargeGroups = new ChargeGroups() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<ChargeGroup> allInstances() {
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

    public static class FindByReference extends ChargeGroupsTest {

        @Test
        public void happyCase() {

            chargeGroups.findChargeGroup("*REF?1*");

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(ChargeGroup.class));
            assertThat(finderInteraction.getQueryName(), is("findByReference"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object) "*REF?1*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class AllChargeGroups extends ChargeGroupsTest {

        @Test
        public void happyCase() {

            chargeGroups.allChargeGroups();

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
        }

    }

    public static class NewChargeGroup extends ChargeGroupsTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        @Before
        public void setup() {
            chargeGroups = new ChargeGroups();
            chargeGroups.setContainer(mockContainer);
        }

        @Test
        public void newChargeGroup() {
            final ChargeGroup chargeGroup = new ChargeGroup();

            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(ChargeGroup.class);
                    will(returnValue(chargeGroup));

                    oneOf(mockContainer).persist(chargeGroup);
                }
            });

            final ChargeGroup newChargeGroup = chargeGroups.createChargeGroup("REF-1", "desc-1");
            assertThat(newChargeGroup.getReference(), is("REF-1"));
            assertThat(newChargeGroup.getName(), is("desc-1"));
        }

    }
}