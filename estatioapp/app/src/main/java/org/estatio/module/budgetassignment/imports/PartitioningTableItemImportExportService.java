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
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.xactn.TransactionService3;

import org.isisaddons.module.excel.dom.ExcelService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.keyitem.DirectCost;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keyitem.PartitioningTableItem;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.KeyTable;
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
    public List<KeyItemImportExportLine> keyItemsToLines(SortedSet<KeyItem> keyItems) {
        return Lists.transform(Lists.newArrayList(keyItems), toLineItem());
    }

    private Function<KeyItem, KeyItemImportExportLine> toLineItem() {
        return keyItem -> new KeyItemImportExportLine(keyItem, tenantAtStartDateBudget(keyItem));
    }

    @Programmatic
    public List<DirectCostLine> directCostsToLines(SortedSet<DirectCost> directCosts) {
        return Lists.transform(Lists.newArrayList(directCosts), toDirectCostLine());
    }

    private Function<DirectCost, DirectCostLine> toDirectCostLine() {
        return directCost -> new DirectCostLine(directCost, tenantAtStartDateBudget(directCost));
    }

    private Party tenantAtStartDateBudget(final PartitioningTableItem item){
        final LocalDate startAndEndDate = item.getPartitioningTable().getBudget().getStartDate();
        final List<Occupancy> candidates = occupancyRepository.occupanciesByUnitAndInterval(item.getUnit(), LocalDateInterval.including(startAndEndDate, startAndEndDate));
        return candidates.isEmpty() ? null :  candidates.get(0).getLease().getSecondaryParty();
    }

    public KeyTable importLines(final List<KeyItemImportExportLine> lines){
        if (!allLinesHaveSameKeyTableName(lines)){
            messageService.warnUser("Import failed; all lines should have the same key table name");
            return null;
        }
        final KeyTable keyTableIfAny = lines.get(0).getKeyTable();
        if (!allLinesValid(lines)) {
            messageService.warnUser("Import failed; invalid lines found");
            return keyTableIfAny !=null ? keyTableIfAny : null;
        }
        // when we get to here, there is a keytable
        if (keyTableIfAny.isImmutableReason()!=null){
            messageService.warnUser(keyTableIfAny.isImmutableReason());
            return keyTableIfAny;
        }
        keyTableIfAny.deleteItems();
        lines.forEach(l->l.importData());
        transactionService3.nextTransaction();
        keyTableIfAny.distributeSourceValues();
        return keyTableIfAny;
    }

    private boolean allLinesValid(final List<KeyItemImportExportLine> lines) {
        for (KeyItemImportExportLine line : lines){
            if (line.reasonInValid()!=null) {
                messageService.warnUser(line.reasonInValid());
                return false;
            }
        }
        return true;
    }

    public boolean allLinesHaveSameKeyTableName(final List<KeyItemImportExportLine> lines) {
        if (lines.isEmpty()) return true;
        final String keyTableName = lines.get(0).getKeyTableName();
        if (keyTableName==null) return false;
        for (KeyItemImportExportLine line : lines){
            if (!keyTableName.equals(line.getKeyTableName())) return false;
        }
        return true;
    }

    public DirectCostTable importDirectCostLines(final List<DirectCostLine> lines){
        if (!allLinesHaveSameDirectCostTableName(lines)){
            messageService.warnUser("Import failed; all lines should have the same direct cost table name");
            return null;
        }
        final DirectCostTable directCostTableIfAny = lines.get(0).getDirectCostTable();
        if (!this.allDirectCostLinesValid(lines)) {
            messageService.warnUser("Import failed; invalid lines found");
            return directCostTableIfAny !=null ? directCostTableIfAny : null;
        }
        // when we get to here, there is a directCostTable
        if (directCostTableIfAny.getBudget().getStatus()== Status.NEW){
            directCostTableIfAny.deleteItems();
        }
        if (directCostTableIfAny.getBudget().getStatus()== Status.ASSIGNED && !directCostTableIfAny.usedInPartitionItemForBudgeted()){
            directCostTableIfAny.deleteItems();
        }
        lines.forEach(l->l.importData());
        return directCostTableIfAny;
    }

    private boolean allDirectCostLinesValid(final List<DirectCostLine> lines) {
        for (DirectCostLine line : lines){
            if (line.reasonInValid()!=null) {
                messageService.warnUser(line.reasonInValid());
                return false;
            }
        }
        return true;
    }

    public boolean allLinesHaveSameDirectCostTableName(final List<DirectCostLine> lines) {
        if (lines.isEmpty()) return true;
        final String tableName = lines.get(0).getDirectCostTableName();
        if (tableName==null) return false;
        for (DirectCostLine line : lines){
            if (!tableName.equals(line.getDirectCostTableName())) return false;
        }
        return true;
    }

    @Inject
    private ExcelService excelService;

    @Inject
    private OccupancyRepository occupancyRepository;

    @Inject
    private MessageService messageService;

    @Inject
    private TransactionService3 transactionService3;

}
