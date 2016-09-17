/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.dom.budgetassignment;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Occupancy;

@DomainService(repositoryFor = ServiceChargeItem.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class ServiceChargeItemRepository extends UdoDomainRepositoryAndFactory<ServiceChargeItem> {

    public ServiceChargeItemRepository() {
        super(ServiceChargeItemRepository.class, ServiceChargeItem.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public ServiceChargeItem newServiceChargeItem(
            final Occupancy occupancy,
            final Charge charge) {
        ServiceChargeItem serviceChargeItem = newTransientInstance();
        serviceChargeItem.setOccupancy(occupancy);
        serviceChargeItem.setCharge(charge);
        persistIfNotAlready(serviceChargeItem);

        return serviceChargeItem;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public ServiceChargeItem findOrCreateServiceChargeItem(
            final Occupancy occupancy,
            final Charge charge) {
        ServiceChargeItem item = findUnique(occupancy,charge);
        return item == null ? newServiceChargeItem(occupancy, charge) : item;
    }

    public List<ServiceChargeItem> findByOccupancy(final Occupancy occupancy){
        return allMatches("findByOccupancy", "occupancy", occupancy);
    }

    public ServiceChargeItem findUnique(final Occupancy occupancy, final Charge charge){
        return uniqueMatch("findByOccupancyAndCharge",
                "occupancy", occupancy,
                "charge", charge
        );
    }

    public List<ServiceChargeItem> allServiceChargeItems() {
        return allInstances();
    }

}
