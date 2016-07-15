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
package org.estatio.integtests.lease.tags;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.lease.tags.BrandMenu;
import org.estatio.dom.lease.tags.BrandRepository;
import org.estatio.fixture.lease.tags.BrandsFixture;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BrandsIntegTest extends EstatioIntegrationTest {

    @Inject
    BrandMenu brandMenu;

    @Inject
    BrandRepository brandRepository;

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new BrandsFixture());
            }
        });

    }

    public static class AllBrands extends BrandsIntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            // when
            final List<Brand> results = brandMenu.allBrands();
            // then
            assertThat(results.size()).isEqualTo(3);
            assertThat(results.get(0).getName()).isEqualTo(BrandsFixture.YU_S_NOODLE_JOINT);
        }
    }

    public static class FindUniqueNames extends BrandsIntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            // when
            final List<String> results = brandRepository.findUniqueNames();
            // then
            assertThat(results.size()).isEqualTo(3);
        }
    }

    public static class FindUniqueGroups extends BrandsIntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            // when
            final List<String> uniqueGroups = brandRepository.findUniqueGroups();
            // then
            assertThat(uniqueGroups.size()).isEqualTo(1);
            assertThat(uniqueGroups.get(0)).isEqualTo(BrandsFixture.YU_GROUP);

        }
    }

    public static class FindUniqueBrand extends BrandsIntegTest {

        @Test
        public void happyCase () throws Exception {
            // given
            // when
            final Brand brand = brandRepository.findUnique(BrandsFixture.YU_S_NOODLE_JOINT,  applicationTenancyRepository.findByPath("/"));
            // then
            assertThat(brand.getName()).isEqualTo(BrandsFixture.YU_S_NOODLE_JOINT);
            assertThat(brand.getApplicationTenancyPath()).isEqualTo("/");

        }

        @Test
        public void sadCase () throws Exception {
            // given
            // when
            final Brand brand = brandRepository.findUnique(BrandsFixture.YU_S_NOODLE_JOINT,  applicationTenancyRepository.findByPath("/FRA"));
            // then
            Assertions.assertThat(brand).isEqualTo(null);

        }

    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public static class NewBrand extends BrandsIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given, when
            Brand brandFra = wrap(brandMenu).newBrand("Test123", null, null, null, applicationTenancyRepository.findByPath("/FRA"));
            Brand brandGbr = wrap(brandMenu).newBrand("Test123", null, null, null, applicationTenancyRepository.findByPath("/GBR"));
            // then
            Assertions.assertThat(brandFra.getName()).isEqualTo("Test123");
            Assertions.assertThat(brandGbr.getName()).isEqualTo("Test123");

        }

        @Test
        public void validationFails() throws Exception {

            // given
            brandRepository.newBrand("test123", null, null, null, applicationTenancyRepository.findByPath("/FRA"));

            // then
            exception.expect(InvalidException.class);
            exception.expectMessage("Brand with name TeSt123 exists already for France");

            // when
            wrap(brandMenu).newBrand("TeSt123", null, null, null, applicationTenancyRepository.findByPath("/FRA"));

        }

    }

}