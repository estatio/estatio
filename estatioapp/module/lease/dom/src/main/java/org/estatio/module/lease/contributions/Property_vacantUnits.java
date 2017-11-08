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

package org.estatio.module.lease.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;

@Mixin
public class Property_vacantUnits {

    final private Property property;

    public Property_vacantUnits(Property property) {
        this.property = property;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Unit> $$() {
        return unitRepository.findByProperty(property)
                .stream()
                .filter(unit -> !occupiedUnits().contains(unit) && (unit.getEndDate()==null || unit.getEndDate().isAfter(clockService.now())))
                .collect(Collectors.toList());
    }

    List<Unit> occupiedUnits(){
        return occupancyRepository.findByProperty(property)
                .stream()
                .filter(x->x.getEndDate()==null || x.getEndDate().isAfter(clockService.now()))
                .map(Occupancy::getUnit)
                .collect(Collectors.toList());
    }

    @Inject
    UnitRepository unitRepository;

    @Inject
    OccupancyRepository occupancyRepository;

    @Inject
    ClockService clockService;
}
