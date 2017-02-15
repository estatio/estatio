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

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Unit;

@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY
)
public class OccupancyContributions {

@Inject
private OccupancyRepository occupancyRepository;

    @CollectionLayout(render = RenderType.EAGERLY)
    @MemberOrder(name = "Occupancies", sequence = "10")
    @Action(semantics = SemanticsOf.SAFE)
    public List<Occupancy> occupancies(final Unit unit) {
        return occupancyRepository.findByUnit(unit);
    }

    // //////////////////////////////////////

    @CollectionLayout(render = RenderType.EAGERLY)
    @ActionLayout(contributed = Contributed.AS_NEITHER) // Disable the cotrubution of this collection because there is a occupancy collection on Lease
    @Action(semantics = SemanticsOf.SAFE)
    public List<Occupancy> occupancies(final Lease lease) {
        return occupancyRepository.findByLease(lease);
    }

}
