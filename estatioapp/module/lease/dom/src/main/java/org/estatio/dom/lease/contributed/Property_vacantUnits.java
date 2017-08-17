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

package org.estatio.dom.lease.contributed;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.OccupancyRepository;

/**
 * This cannot be inlined (needs to be a mixin) because Property does not know about occupancy.
 */
@Mixin
public class Property_vacantUnits {

    final private Property property;

    public Property_vacantUnits(Property property) {
        this.property = property;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Unit> $$() {
        List<Unit> occupiedUnits = occupancyRepository.findByProperty(property)
                .stream()
                .map(Occupancy::getUnit)
                .collect(Collectors.toList());
        return unitRepository.findByProperty(property)
                .stream()
                .filter(unit -> !occupiedUnits.contains(unit))
                .collect(Collectors.toList());
    }

    @Inject
    private UnitRepository unitRepository;

    @Inject
    private OccupancyRepository occupancyRepository;
}
