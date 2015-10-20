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
package org.estatio.dom.asset;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PropertyMenuTest {

    FinderInteraction finderInteraction;
    PropertyMenu propertyMenu;
    PropertyRepository propertyRepository;

    @Before
    public void setup() {
        propertyRepository = new PropertyRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return (T) new Property();
            }
            @Override
            protected List<Property> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }
            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
        propertyMenu = new PropertyMenu();
        propertyMenu.propertyRepository = propertyRepository;
    }

    public static class FindPropertyMenu extends PropertyMenuTest {

        @Test
        public void happyCase() {

            propertyMenu.findProperties("*REF?1*");

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Property.class));
            assertThat(finderInteraction.getQueryName(), is("findByReferenceOrName"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("referenceOrName"), is((Object)"(?i).*REF.1.*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindPropertyByReference extends PropertyMenuTest {

        @Test
        public void happyCase() {

            propertyRepository.findPropertyByReference("*REF?1*");

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Property.class));
            assertThat(finderInteraction.getQueryName(), is("findByReference"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object)"*REF?1*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class AutoComplete extends PropertyMenuTest {

        @Test
        public void happyCase() {

            propertyRepository.autoComplete("X?yz");

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Property.class));
            assertThat(finderInteraction.getQueryName(), is("findByReferenceOrName"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("referenceOrName"), is((Object)"(?i).*X.yz.*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }
    }

    public static class AllPropertyMenu extends PropertyMenuTest {

        @Test
        public void happyCase() {

            propertyMenu.allProperties();

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
        }
    }


    public static class NewProperty extends PropertyMenuTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;
        @Mock
        private EstatioApplicationTenancyRepository mockEstatioApplicationTenancyRepository;

        PropertyRepository propertyRepository;
        PropertyMenu propertyMenu;

        @Before
        public void setup() {
            propertyRepository = new PropertyRepository();
            propertyRepository.setContainer(mockContainer);

            propertyMenu = new PropertyMenu();
            propertyMenu.propertyRepository = propertyRepository;

            propertyRepository.estatioApplicationTenancyRepository = mockEstatioApplicationTenancyRepository;
        }


        @Test
        public void newProperty() {
            // given
            final ApplicationTenancy countryApplicationTenancy = new ApplicationTenancy();
            countryApplicationTenancy.setPath("/it");

            final Property property = new Property();
            final ApplicationTenancy propertyApplicationTenancy = new ApplicationTenancy();
            propertyApplicationTenancy.setPath("/it/REF-1");
            propertyApplicationTenancy.setName("REF-1 (Italy)");

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockEstatioApplicationTenancyRepository).findOrCreatePropertyTenancy(countryApplicationTenancy, "REF-1");
                    will(returnValue(propertyApplicationTenancy));

                    oneOf(mockEstatioApplicationTenancyRepository).findOrCreateLocalDefaultTenancy(propertyApplicationTenancy);
                    oneOf(mockEstatioApplicationTenancyRepository).findOrCreateLocalTaTenancy(propertyApplicationTenancy);

                    oneOf(mockContainer).newTransientInstance(Property.class);
                    will(returnValue(property));

                    oneOf(mockContainer).persistIfNotAlready(property);
                }
            });

            // when
            final Property newProperty = propertyMenu.newProperty("REF-1", "Name-1", PropertyType.CINEMA, null, null, null, countryApplicationTenancy);

            // then
            Assertions.assertThat(newProperty.getReference()).isEqualTo("REF-1");
            Assertions.assertThat(newProperty.getName()).isEqualTo("Name-1");
            Assertions.assertThat(newProperty.getType()).isEqualTo(PropertyType.CINEMA);
            Assertions.assertThat(newProperty.getCountry()).isNull();
            Assertions.assertThat(newProperty.getApplicationTenancyPath()).isEqualTo("/it/REF-1");
        }

        @Test
        public void defaults() {
            assertThat(propertyMenu.default2NewProperty(), is(PropertyType.MIXED));
        }

    }

}
