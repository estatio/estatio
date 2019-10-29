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
package org.estatio.module.asset.app;

import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.asset.dom.EstatioApplicationTenancyRepositoryForProperty;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.PropertyType;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyMenu2_Test {

    public static class NewProperty extends PropertyMenu2_Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private EstatioApplicationTenancyRepositoryForProperty mockEstatioApplicationTenancyRepository;

        @Mock
        private RepositoryService mockRepositoryService;
        @Mock
        private FactoryService mockFactoryService;

        PropertyRepository propertyRepository;
        PropertyMenu propertyMenu;

        @Before
        public void setup() {
            propertyRepository = new PropertyRepository();
            propertyRepository.repositoryService = mockRepositoryService;
            propertyRepository.factoryService = mockFactoryService;

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

                    oneOf(mockFactoryService).instantiate(Property.class);
                    will(returnValue(property));

                    oneOf(mockRepositoryService).persist(property);
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
