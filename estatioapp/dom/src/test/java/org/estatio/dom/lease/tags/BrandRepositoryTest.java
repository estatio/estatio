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
package org.estatio.dom.lease.tags;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BrandRepositoryTest {

    FinderInteraction finderInteraction;

    BrandRepository brandRepository;

    @Before
    public void setup() {

        brandRepository = new BrandRepository() {
            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<Brand> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }

            @Override
            protected <T> T uniqueMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.UNIQUE_MATCH);
                return null;
            }
        };

    }

    public static class FindUnique extends BrandRepositoryTest {

        String name;
        String atPath;
        ApplicationTenancy applicationTenancy;

        @Before
        public void setUp() {
            name= "SOME_NAME";
            atPath = "/SOME/PATH";
            applicationTenancy = new ApplicationTenancy();
            applicationTenancy.setPath(atPath);
        }


        @Test
        public void findUnique() {
            brandRepository.findUnique(name, applicationTenancy);
            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.UNIQUE_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Brand.class));
            assertThat(finderInteraction.getQueryName(), is("findByNameAndAtPath"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("name"), is(name));
            assertThat(finderInteraction.getArgumentsByParameterName().get("atPath"), is(atPath));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }
    }

    public static class MatchByName extends BrandRepositoryTest {
        @Test
        public void byReferenceWildcard() {
            brandRepository.matchByName("*REF?1*");
            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Brand.class));
            assertThat(finderInteraction.getQueryName(), is("matchByName"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("name"), is((Object) "(?i).*REF.1.*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }
    }

    public static class FindByNameLowerCaseAndAppTenancy extends BrandRepositoryTest {

        String name;
        String atPath;
        ApplicationTenancy applicationTenancy;

        @Before
        public void setUp() {
            name= "SOME_NAME";
            atPath = "/SOME/PATH";
            applicationTenancy = new ApplicationTenancy();
            applicationTenancy.setPath(atPath);
        }

        @Test
        public void findByNameLowerCaseAndAppTenancy() {
            brandRepository.findByNameLowerCaseAndAppTenancy(name, applicationTenancy);
            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Brand.class));
            assertThat(finderInteraction.getQueryName(), is("findByNameLowerCaseAndAppTenancy"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("name"), is(name.toLowerCase()));
            assertThat(finderInteraction.getArgumentsByParameterName().get("atPath"), is(atPath));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }
    }

    public static class AutoComplete extends BrandRepositoryTest {

        @Test
        public void byReference() {
            brandRepository.autoComplete("REF1");
            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Brand.class));
            assertThat(finderInteraction.getQueryName(), is("matchByName"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("name"), is((Object) "(?i).*REF1.*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

}
