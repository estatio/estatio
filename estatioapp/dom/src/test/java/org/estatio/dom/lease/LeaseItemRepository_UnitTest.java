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

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;
import org.estatio.dom.charge.Charge;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseItemRepository_UnitTest {

    FinderInteraction finderInteraction;

    LeaseItemRepository leaseItemRepository;

    @Before
    public void setup() {

        leaseItemRepository = new LeaseItemRepository() {

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

    public static class AllLeaseItems extends LeaseItemRepository_UnitTest {

        @Test
        public void happyCase() {

            leaseItemRepository.allLeaseItems();

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_INSTANCES);
        }

    }

    public static class findByLeaseAndTypeAndCharge extends LeaseItemRepository_UnitTest {

        @Test
        public void happyCase() {

            Lease lease = new Lease();
            LeaseItemType leaseItemType = LeaseItemType.SERVICE_CHARGE;
            Charge charge = new Charge();

            leaseItemRepository.findByLeaseAndTypeAndCharge(lease, leaseItemType, charge);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(LeaseItem.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByLeaseAndTypeAndCharge");
            assertThat(finderInteraction.getArgumentsByParameterName().get("lease")).isEqualTo((Object) lease);
            assertThat(finderInteraction.getArgumentsByParameterName().get("type")).isEqualTo((Object) leaseItemType);
            assertThat(finderInteraction.getArgumentsByParameterName().get("charge")).isEqualTo((Object) charge);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(3);
        }

    }

}