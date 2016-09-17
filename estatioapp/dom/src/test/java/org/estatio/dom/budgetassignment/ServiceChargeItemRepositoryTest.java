/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.estatio.dom.budgetassignment;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Occupancy;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceChargeItemRepositoryTest {

    FinderInteraction finderInteraction;

    ServiceChargeItemRepository serviceChargeItemRepo;

    @Before
    public void setup() {
        serviceChargeItemRepo = new ServiceChargeItemRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected <T> T uniqueMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.UNIQUE_MATCH);
                return null;
            }

            @Override
            protected List<ServiceChargeItem> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderInteraction.FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    public static class FindByOccupancy extends ServiceChargeItemRepositoryTest {

        @Test
        public void happyCase() {

            Occupancy occupancy = new Occupancy();
            serviceChargeItemRepo.findByOccupancy(occupancy);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(ServiceChargeItem.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByOccupancy");
            assertThat(finderInteraction.getArgumentsByParameterName().get("occupancy")).isEqualTo((Object) occupancy);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class Findunique extends ServiceChargeItemRepositoryTest {

        @Test
        public void happyCase() {

            Occupancy occupancy = new Occupancy();
            Charge charge = new Charge();
            serviceChargeItemRepo.findUnique(occupancy, charge);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.UNIQUE_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(ServiceChargeItem.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByOccupancyAndCharge");
            assertThat(finderInteraction.getArgumentsByParameterName().get("occupancy")).isEqualTo((Object) occupancy);
            assertThat(finderInteraction.getArgumentsByParameterName().get("charge")).isEqualTo((Object) charge);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(2);
        }

    }


    public static class NewServiceChargeItem extends ServiceChargeItemRepositoryTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;

        ServiceChargeItemRepository serviceChargeItemRepository;

        @Before
        public void setup() {
            serviceChargeItemRepository = new ServiceChargeItemRepository();
            serviceChargeItemRepository.setContainer(mockContainer);
        }

        @Test
        public void newServiceChargeItem() {

            //given
            Occupancy occupancy = new Occupancy();
            Charge charge = new Charge();
            final ServiceChargeItem serviceChargeItem = new ServiceChargeItem();

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(ServiceChargeItem.class);
                    will(returnValue(serviceChargeItem));
                    oneOf(mockContainer).persistIfNotAlready(serviceChargeItem);
                }

            });

            //when
            ServiceChargeItem newScItem = serviceChargeItemRepository.newServiceChargeItem(occupancy, charge);

            //then
            assertThat(newScItem.getOccupancy()).isEqualTo(occupancy);
            assertThat(newScItem.getCharge()).isEqualTo(charge);

        }

    }

}
