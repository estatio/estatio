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
package org.estatio.module.lease.integtests.tags;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.lease.dom.occupancy.tags.BrandGroupViewModel;
import org.estatio.module.lease.dom.occupancy.tags.BrandRepository;
import org.estatio.module.lease.fixtures.brands.enums.Brand_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class BrandRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    BrandRepository brandRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext ec) {
                ec.executeChildren(this,
                        Brand_enum.Yu_s_Noodle_Joint,
                        Brand_enum.Yu_s_Cleaning_Services,
                        Brand_enum.Happy_ValLey);
            }
        });

    }

    public static class AllBrandRepository extends BrandRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            // when
            final List<Brand> results = brandRepository.allBrands();
            // then
            assertThat(results.size()).isEqualTo(3);
            assertThat(results.get(0).getName()).isEqualTo(Brand_enum.Yu_s_Noodle_Joint.getName());
        }
    }

    public static class FindUniqueNames extends BrandRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            // when
            final List<String> results = brandRepository.findUniqueNames();
            // then
            assertThat(results.size()).isEqualTo(3);
        }
    }

    public static class FindUniqueGroups extends BrandRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            // when
            final List<String> uniqueGroups = brandRepository.findUniqueGroups("yU g");
            // then
            assertThat(uniqueGroups.size()).isEqualTo(1);
            assertThat(uniqueGroups.get(0)).isEqualTo(Brand_enum.Yu_s_Noodle_Joint.getGroup()); // choose any one

        }
    }

    public static class AutoCompleteBrandGroup extends BrandRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            // when
            final List<BrandGroupViewModel> groups = brandRepository.autoCompleteBrandGroup("yU g");

            // then
            assertThat(groups).hasSize(2);
            assertThat(groups)
                    .extracting(BrandGroupViewModel::getGroup)
                    .containsExactly(Brand_enum.Yu_s_Noodle_Joint.getGroup(), "yU g");
        }
    }

    public static class FindUniqueBrand extends BrandRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            // when
            final Brand brand = brandRepository.findUnique(Brand_enum.Yu_s_Noodle_Joint.getName(), applicationTenancyRepository.findByPath("/"));
            // then
            assertThat(brand.getName()).isEqualTo(Brand_enum.Yu_s_Noodle_Joint.getName());
            assertThat(brand.getApplicationTenancyPath()).isEqualTo("/");

        }

        @Test
        public void sadCase() throws Exception {
            // given
            // when
            final Brand brand = brandRepository.findUnique(Brand_enum.Yu_s_Noodle_Joint.getName(), applicationTenancyRepository.findByPath("/FRA"));
            // then
            Assertions.assertThat(brand).isEqualTo(null);

        }

    }

    public static class NewBrand extends BrandRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {

            // given, when
            Brand brandFra = brandRepository.newBrand("Test123", null, null, null, applicationTenancyRepository.findByPath("/FRA"));
            Brand brandGbr = brandRepository.newBrand("Test123", null, null, null, applicationTenancyRepository.findByPath("/GBR"));
            // then
            Assertions.assertThat(brandFra.getName()).isEqualTo("Test123");
            Assertions.assertThat(brandGbr.getName()).isEqualTo("Test123");

        }

    }

}