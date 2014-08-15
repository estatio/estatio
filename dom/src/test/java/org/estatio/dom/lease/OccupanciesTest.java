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
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.asset.Unit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OccupanciesTest {

    FinderInteraction finderInteraction;

    Occupancies occupancies;

    Lease lease;
    Unit unit;
    LocalDate startDate;

    @Before
    public void setup() {

        lease = new Lease();
        unit = new Unit();
        startDate = new LocalDate(2013, 4, 1);

        occupancies = new Occupancies() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<Occupancy> allInstances() {
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

    public static class FindByLeaseAndUnitAndStartDate extends OccupanciesTest {
        @Test
        public void happyCase() {
            occupancies.findByLeaseAndUnitAndStartDate(lease, unit, startDate);
            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Occupancy.class));
            assertThat(finderInteraction.getQueryName(), is("findByLeaseAndUnitAndStartDate"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("lease"), is((Object) lease));
            assertThat(finderInteraction.getArgumentsByParameterName().get("unit"), is((Object) unit));
            assertThat(finderInteraction.getArgumentsByParameterName().get("startDate"), is((Object) startDate));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
        }
    }

    public static class FindByLease extends OccupanciesTest {
    }

    @Test
    public void happyCase() {
        occupancies.findByLease(lease);
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Occupancy.class));
        assertThat(finderInteraction.getQueryName(), is("findByLease"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("lease"), is((Object) lease));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }

    public static class FindByUnit extends OccupanciesTest {

        @Test
        public void happyCase() {
            occupancies.findByUnit(unit);
            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Occupancy.class));
            assertThat(finderInteraction.getQueryName(), is("findByUnit"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("unit"), is((Object) unit));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

}
