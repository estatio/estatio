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

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.geography.Countries;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.lease.tags.Brands;
import org.estatio.fixture.lease.tags.BrandsFixture;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BrandsIntegTest extends EstatioIntegrationTest {

    @Inject
    Brands brands;

    @Inject
    Countries countries;

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
        public void xxx() throws Exception {
            // given
            // when
            final List<Brand> results = brands.allBrands();
            // then
            assertThat(results.size()).isEqualTo(3);
            assertThat(results.get(0).getName()).isEqualTo(BrandsFixture.YU_S_NOODLE_JOINT);
        }
    }

    public static class FindUniqueNames extends BrandsIntegTest {

        @Test
        public void xxx() throws Exception {
            // given
            // when
            final List<String> results = brands.findUniqueNames();
            // then
            assertThat(results.size()).isEqualTo(3);
        }
    }

    public static class FindUniqueGroups extends BrandsIntegTest {

        @Test
        public void xxx() throws Exception {
            // given
            // when
            final List<String> uniqueGroups = brands.findUniqueGroups();
            // then
            assertThat(uniqueGroups.size()).isEqualTo(1);
            assertThat(uniqueGroups.get(0)).isEqualTo(BrandsFixture.YU_GROUP);

        }
    }

}