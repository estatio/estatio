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
package org.estatio.dom.lease;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetForTesting;
import org.estatio.dom.asset.Property;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LeaseRepositoryTest {

    FinderInteraction finderInteraction;

    LeaseRepository leaseRepository;

    FixedAsset asset;

    Property property;

    LocalDate date;

    @Before
    public void setup() {

        asset = new FixedAssetForTesting();

        property = new Property();

        leaseRepository = new LeaseRepository() {

            @Override
            protected <T> T uniqueMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.UNIQUE_MATCH);
                return (T) new Lease();
            }

            @Override
            protected List<Lease> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };

        leaseRepository.clockService = new ClockService();
    }

    public static class FindLeaseByReference extends LeaseRepositoryTest {
        @Test
        public void byReferenceWildcard() {

            leaseRepository.findLeaseByReference("*REF?1*");

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.UNIQUE_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Lease.class));
            assertThat(finderInteraction.getQueryName(), is("findByReference"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object) "*REF?1*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }
    }

    public static class findLeases extends LeaseRepositoryTest {

        @Test
        public void byReferenceWildcard() {

            leaseRepository.matchByReferenceOrName("*REF?1*", false);

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Lease.class));
            assertThat(finderInteraction.getQueryName(), is("matchByReferenceOrName"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("referenceOrName"), is((Object) "(?i).*REF.1.*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
        }
    }

    public static class FindLeasesByProperty extends LeaseRepositoryTest {

        @Test
        public void happyCase() {

            leaseRepository.findLeasesByProperty(property);

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Lease.class));
            assertThat(finderInteraction.getQueryName(), is("findByProperty"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("property"), is((Object) property));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));

            // REVIEW: Don't see the real benefit of these tests since the
            // application
            // won't compile if you change the code without refactoring. The
            // only
            // fragile part in the finder is the relation between the query in
            // the
            // finder and the query in the class since these are all strings.

        }
    }

    public static class FindLeasesActiveOnDate extends LeaseRepositoryTest {

        @Test
        public void happyCase() {

            leaseRepository.findByAssetAndActiveOnDate(asset, date);

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Lease.class));
            assertThat(finderInteraction.getQueryName(), is("findByAssetAndActiveOnDate"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("asset"), is((Object) asset));
            assertThat(finderInteraction.getArgumentsByParameterName().get("date"), is((Object) date));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }
    }

    public static class AllLeases extends LeaseRepositoryTest {

        @Test
        public void happyCase() {

            leaseRepository.allLeases();

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
        }

    }

}