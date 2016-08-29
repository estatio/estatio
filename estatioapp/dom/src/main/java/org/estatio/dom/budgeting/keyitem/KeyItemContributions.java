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
package org.estatio.dom.budgeting.keyitem;

import org.apache.isis.applib.annotation.*;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.lease.OccupancyRepository;
import org.estatio.dom.lease.Occupancy;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class KeyItemContributions {

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public KeyItem newItem(
            final KeyTable keyTable,
            final Unit unit,
            final BigDecimal sourceValue,
            final BigDecimal keyValue) {

        return keyItemRepository.newItem(keyTable, unit, sourceValue, keyValue);
    }

    public String validateNewItem(
            final KeyTable keyTable,
            final Unit unit,
            final BigDecimal sourceValue,
            final BigDecimal keyValue) {

        return keyItemRepository.validateNewItem(keyTable, unit, sourceValue, keyValue);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<Occupancy> occupancies(final KeyItem keyItem) {

        return occupancyRepository.occupanciesByUnitAndInterval(keyItem.getUnit(), keyItem.getKeyTable().getBudget().getInterval());

    }

    @Inject
    private OccupancyRepository occupancyRepository;

    @Inject
    private KeyItemRepository keyItemRepository;

}
