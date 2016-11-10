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
package org.estatio.app.menus.asset;

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
import org.estatio.dom.asset.EstatioApplicationTenancyRepositoryForProperty;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.PropertyType;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyMenu2Test {

    FinderInteraction finderInteraction;
    PropertyMenu propertyMenu;
    PropertyRepository propertyRepository;

    @Before
    public void setup() {
        propertyRepository = new PropertyRepository() {

            @Override
            protected <T> T uniqueMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.UNIQUE_MATCH);
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

    public static class FindPropertyMenu extends PropertyMenu2Test {

        @Test
        public void happyCase() {

            propertyMenu.findProperties("*REF?1*");

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(Property.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByReferenceOrName");
            assertThat(finderInteraction.getArgumentsByParameterName().get("referenceOrName")).isEqualTo((Object)"(?i).*REF.1.*");
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class FindPropertyByReference extends PropertyMenu2Test {

        @Test
        public void happyCase() {

            propertyRepository.findPropertyByReference("*REF?1*");

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.UNIQUE_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(Property.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByReference");
            assertThat(finderInteraction.getArgumentsByParameterName().get("reference")).isEqualTo((Object)"*REF?1*");
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class AutoComplete extends PropertyMenu2Test {

        @Test
        public void happyCase() {

            propertyRepository.autoComplete("X?yz");

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(Property.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByReferenceOrName");
            assertThat(finderInteraction.getArgumentsByParameterName().get("referenceOrName")).isEqualTo((Object)"(?i).*X.yz.*");
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }
    }

    public static class AllPropertyMenu extends PropertyMenu2Test {

        @Test
        public void happyCase() {

            propertyMenu.allProperties();

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_INSTANCES);
        }
    }


    public static class NewProperty extends PropertyMenu2Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;
        @Mock
        private EstatioApplicationTenancyRepositoryForProperty mockEstatioApplicationTenancyRepository;

        PropertyRepository propertyRepository;
        PropertyMenu propertyMenu;

        @Before
        public void setup() {
            propertyRepository = new PropertyRepository();
            propertyRepository.setContainer(mockContainer);

            propertyMenu = new PropertyMenu();
            propertyMenu.propertyRepository = propertyRepository;

            propertyRepository.setEstatioApplicationTenancyRepository(mockEstatioApplicationTenancyRepository);
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
                    oneOf(mockEstatioApplicationTenancyRepository).findOrCreateTenancyFor(property);
                    will(returnValue(propertyApplicationTenancy));

                    oneOf(mockContainer).newTransientInstance(Property.class);
                    will(returnValue(property));

                    oneOf(mockContainer).persistIfNotAlready(property);
                }
            });

            // when
            final Property newProperty = propertyMenu.newProperty("REF-1", "Name-1", PropertyType.CINEMA, null, null, null);

            // then
            assertThat(newProperty.getReference()).isEqualTo("REF-1");
            assertThat(newProperty.getName()).isEqualTo("Name-1");
            assertThat(newProperty.getType()).isEqualTo(PropertyType.CINEMA);
            assertThat(newProperty.getCountry()).isNull();
            assertThat(newProperty.getApplicationTenancyPath()).isEqualTo("/it/REF-1");
        }

        @Test
        public void defaults() {
            assertThat(propertyMenu.default2NewProperty()).isEqualTo(PropertyType.MIXED);
        }

    }

}
