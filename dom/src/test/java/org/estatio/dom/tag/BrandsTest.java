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
package org.estatio.dom.tag;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.lease.tags.Brands;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BrandsTest {

    FinderInteraction finderInteraction;

    Brands brands;

    @Before
    public void setup() {

        brands = new Brands() {

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
        };
    }

    public static class MatchByName extends BrandsTest {
        @Test
        public void byReferenceWildcard() {
            brands.matchByName("*REF?1*");
            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Brand.class));
            assertThat(finderInteraction.getQueryName(), is("matchByName"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("name"), is((Object)"(?i).*REF.1.*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }
    }

    public static class AutoComplete extends BrandsTest {

        @Test
        public void byReference() {
            brands.autoComplete("REF1");
            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Brand.class));
            assertThat(finderInteraction.getQueryName(), is("matchByName"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("name"), is((Object)"(?i).*REF1.*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

}
