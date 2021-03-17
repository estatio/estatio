/*
 * Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.module.application.contributions.occupancy;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.*;

import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;

/**
 * TODO: this could move to budgetassignment, and be a regular derived collection rather than a mixin
 */
@Mixin(method = "coll")
public class KeyItem_occupancies {

    private final KeyItem keyItem;

    public KeyItem_occupancies(KeyItem keyItem) {
        this.keyItem = keyItem;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<Occupancy> coll() {

        return occupancyRepository.occupanciesByUnitAndInterval(keyItem.getUnit(), keyItem.getPartitioningTable().getBudget().getInterval());

    }

    @Inject
    private OccupancyRepository occupancyRepository;

}
