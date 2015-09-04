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
import org.estatio.dom.apptenancy.ApplicationTenancyRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PropertiesTest {

    FinderInteraction finderInteraction;
    Properties properties;

    @Before
    public void setup() {
        properties = new Properties() {

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
    }

    public static class FindProperties extends PropertiesTest {

        @Test
        public void happyCase() {

            properties.findProperties("*REF?1*");

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Property.class));
            assertThat(finderInteraction.getQueryName(), is("findByReferenceOrName"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("referenceOrName"), is((Object)"(?i).*REF.1.*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindPropertyByReference extends PropertiesTest {

        @Test
        public void happyCase() {

            properties.findPropertyByReference("*REF?1*");

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Property.class));
            assertThat(finderInteraction.getQueryName(), is("findByReference"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object)"*REF?1*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class AutoComplete extends PropertiesTest {

        @Test
        public void happyCase() {

            properties.autoComplete("X?yz");

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Property.class));
            assertThat(finderInteraction.getQueryName(), is("findByReferenceOrName"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("referenceOrName"), is((Object)"(?i).*X.yz.*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }
    }

    public static class AllProperties extends PropertiesTest {

        @Test
        public void happyCase() {

            properties.allProperties();

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
        }
    }


    public static class NewProperty extends PropertiesTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;
        @Mock
        private ApplicationTenancyRepository mockApplicationTenancyRepository;

        Properties properties;

        @Before
        public void setup() {
            properties = new Properties();
            properties.setContainer(mockContainer);
            properties.applicationTenancyRepository = mockApplicationTenancyRepository;
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
                    oneOf(mockApplicationTenancyRepository).findOrCreatePropertyTenancy(countryApplicationTenancy, "REF-1");
                    will(returnValue(propertyApplicationTenancy));

                    oneOf(mockApplicationTenancyRepository).findOrCreateLocalDefaultTenancy(propertyApplicationTenancy);
                    oneOf(mockApplicationTenancyRepository).findOrCreateLocalTaTenancy(propertyApplicationTenancy);

                    oneOf(mockContainer).newTransientInstance(Property.class);
                    will(returnValue(property));

                    oneOf(mockContainer).persistIfNotAlready(property);
                }
            });

            // when
            final Property newProperty = properties.newProperty("REF-1", "Name-1", PropertyType.CINEMA, null, null, null, countryApplicationTenancy);

            // then
            Assertions.assertThat(newProperty.getReference()).isEqualTo("REF-1");
            Assertions.assertThat(newProperty.getName()).isEqualTo("Name-1");
            Assertions.assertThat(newProperty.getType()).isEqualTo(PropertyType.CINEMA);
            Assertions.assertThat(newProperty.getCountry()).isNull();
            Assertions.assertThat(newProperty.getApplicationTenancyPath()).isEqualTo("/it/REF-1");
        }

        @Test
        public void defaults() {
            assertThat(properties.default2NewProperty(), is(PropertyType.MIXED));
        }

    }

}
