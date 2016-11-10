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
package org.estatio.dom.asset;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class FixedAssetRoleTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final FixedAssetRole pojo = new FixedAssetRole();
            newPojoTester()
                    .withFixture(pojos(FixedAsset.class, FixedAssetForTesting.class))
                    .withFixture(pojos(Party.class, PartyForTesting.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(pojo);
        }

    }

    public static class CompareTo extends ComparableContractTest_compareTo<FixedAssetRole> {

        private FixedAsset asset1;
        private FixedAsset asset2;

        private Party party1;
        private Party party2;

        @Before
        public void setUp() throws Exception {
            asset1 = new FixedAssetForTesting();
            asset2 = new FixedAssetForTesting();
            asset1.setName("A");
            asset2.setName("B");
            party1 = new PartyForTesting();
            party2 = new PartyForTesting();
            party1.setName("A");
            party2.setName("B");
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<FixedAssetRole>> orderedTuples() {
            return listOf(
                    listOf(
                            newFixedAssetRole(null, null, null, null),
                            newFixedAssetRole(asset1, null, null, null),
                            newFixedAssetRole(asset1, null, null, null),
                            newFixedAssetRole(asset2, null, null, null))
                    ,listOf(
                            newFixedAssetRole(asset1, new LocalDate(2012,4,2), null, null),
                            newFixedAssetRole(asset1, new LocalDate(2012,3,1), null, null),
                            newFixedAssetRole(asset1, new LocalDate(2012,3,1), null, null),
                            newFixedAssetRole(asset1, null, null, null))
                    ,listOf(
                            newFixedAssetRole(asset1, new LocalDate(2012,4,2), null, null),
                            newFixedAssetRole(asset1, new LocalDate(2012,4,2), FixedAssetRoleType.ASSET_MANAGER, null),
                            newFixedAssetRole(asset1, new LocalDate(2012,4,2), FixedAssetRoleType.ASSET_MANAGER, null),
                            newFixedAssetRole(asset1, new LocalDate(2012,4,2), FixedAssetRoleType.PROPERTY_CONTACT, null))
                    ,listOf(
                            newFixedAssetRole(asset1, new LocalDate(2012,4,2), FixedAssetRoleType.ASSET_MANAGER, null),
                            newFixedAssetRole(asset1, new LocalDate(2012,4,2), FixedAssetRoleType.ASSET_MANAGER, party1),
                            newFixedAssetRole(asset1, new LocalDate(2012,4,2), FixedAssetRoleType.ASSET_MANAGER, party1),
                            newFixedAssetRole(asset1, new LocalDate(2012,4,2), FixedAssetRoleType.ASSET_MANAGER, party2))
            );
        }

        private FixedAssetRole newFixedAssetRole(FixedAsset asset, LocalDate startDate, FixedAssetRoleType type, Party party) {
            final FixedAssetRole far = new FixedAssetRole();
            far.setAsset(asset);
            far.setParty(party);
            far.setStartDate(startDate);
            far.setType(type);
            return far;
        }

    }

}