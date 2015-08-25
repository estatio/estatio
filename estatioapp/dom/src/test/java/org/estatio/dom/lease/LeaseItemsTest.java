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

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.budget.ChargeForTesting;
import org.estatio.dom.charge.Charge;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LeaseItemsTest {

    FinderInteraction finderInteraction;

    LeaseItems leaseItems;

    @Before
    public void setup() {


        leaseItems = new LeaseItems() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<LeaseItem> allInstances() {
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


    public static class AllLeaseItems extends LeaseItemsTest {

        @Test
        public void happyCase() {

            leaseItems.allLeaseItems();

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
        }

    }

    public static class findByLeaseAndTypeAndCharge extends LeaseItemsTest {

        @Test
        public void happyCase() {

            Lease lease = new Lease();
            LeaseItemType leaseItemType = LeaseItemType.SERVICE_CHARGE;
            Charge charge = new ChargeForTesting();

            leaseItems.findByLeaseAndTypeAndCharge(lease, leaseItemType, charge);

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(LeaseItem.class));
            assertThat(finderInteraction.getQueryName(), is("findByLeaseAndTypeAndCharge"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("lease"), is((Object) lease));
            assertThat(finderInteraction.getArgumentsByParameterName().get("type"), is((Object) leaseItemType));
            assertThat(finderInteraction.getArgumentsByParameterName().get("charge"), is((Object) charge));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
        }

    }

}