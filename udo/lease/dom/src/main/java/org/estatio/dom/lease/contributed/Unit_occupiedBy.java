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

import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.OccupancyRepository;

@Mixin
public class Unit_occupiedBy {

    private final Unit unit;

    public Unit_occupiedBy(Unit unit) {
        this.unit = unit;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Occupancy $$() {
        Optional<Occupancy> ifOccupied = occupancyRepository.findByUnit(unit).stream().filter(Occupancy::isCurrent).findFirst();
        return ifOccupied.isPresent() ? ifOccupied.get() : null;
    }

    @Inject
    private OccupancyRepository occupancyRepository;

}
