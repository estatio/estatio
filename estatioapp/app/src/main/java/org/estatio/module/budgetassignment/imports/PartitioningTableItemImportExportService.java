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
package org.estatio.module.budgetassignment.imports;

import java.util.List;
import java.util.SortedSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.excel.dom.ExcelService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.budget.dom.keyitem.DirectCost;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keyitem.PartitioningTableItem;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN)
public class PartitioningTableItemImportExportService {

    @PostConstruct
    public void init() {
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    @Programmatic
    public List<KeyItemImportExportLineItem> items(KeyItemImportExportManager manager) {
        return Lists.transform(Lists.newArrayList(manager.getKeyTable().getItems()), toLineItem());
    }

    @Programmatic
    public List<KeyItemImportExportLineItem> items(SortedSet<KeyItem> keyItems) {
        return Lists.transform(Lists.newArrayList(keyItems), toLineItem());
    }

    private Function<KeyItem, KeyItemImportExportLineItem> toLineItem() {
        return keyItem -> new KeyItemImportExportLineItem(keyItem, tenandAtStartDateBudget(keyItem));
    }

    @Programmatic
    public List<DirectCostLine> items(DirectCostImportExportManager manager) {
        return Lists.transform(Lists.newArrayList(manager.getDirectCostTable().getItems()), toDirectCostLine());
    }

    @Programmatic
    public List<DirectCostLine> directCosts(SortedSet<DirectCost> directCosts) {
        return Lists.transform(Lists.newArrayList(directCosts), toDirectCostLine());
    }

    private Function<DirectCost, DirectCostLine> toDirectCostLine() {
        return directCost -> new DirectCostLine(directCost, tenandAtStartDateBudget(directCost));
    }

    private Party tenandAtStartDateBudget(final PartitioningTableItem item){
        final LocalDate startAndEndDate = item.getPartitioningTable().getBudget().getStartDate();
        final List<Occupancy> candidates = occupancyRepository.occupanciesByUnitAndInterval(item.getUnit(), LocalDateInterval.including(startAndEndDate, startAndEndDate));
        return candidates.isEmpty() ? null :  candidates.get(0).getLease().getSecondaryParty();
    }

    @javax.inject.Inject
    private ExcelService excelService;

    @Inject
    private OccupancyRepository occupancyRepository;

}
