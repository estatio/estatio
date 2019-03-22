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
package org.estatio.module.application.imports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keyitem.DirectCost;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.PartitioningTableRepository;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budgetassignment.imports.DirectCostLine;
import org.estatio.module.budgetassignment.imports.KeyItemImportExportLineItem;
import org.estatio.module.budgetassignment.imports.Status;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.imports.ChargeImport;

// TODO: need to untangle this and push back down to budget module
@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetImportExportService {

    @PostConstruct
    public void init() {
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    public List<BudgetImportExport> lines(BudgetImportExportManager manager) {

        List<BudgetImportExport> result = new ArrayList<BudgetImportExport>();
        if (manager.getBudget()==null){return result;}

        for (BudgetItem item : manager.getBudget().getItems()) {
            result.addAll(createLines(item, manager));
        }
        return result;
    }

    private List<BudgetImportExport> createLines(final BudgetItem item, final BudgetImportExportManager manager){

        List<BudgetImportExport> lines = new ArrayList<>();

        String propertyReference = manager.getBudget().getProperty().getReference();
        LocalDate budgetStartDate = manager.getBudget().getStartDate();
        LocalDate budgetEndDate = manager.getBudget().getEndDate();
        String budgetChargeReference = item.getCharge().getReference();
        BigDecimal budgetedValue = item.getBudgetedValue();
        BigDecimal auditedValue = item.getAuditedValue();

        if (item.getPartitionItems().size()==0){
            // create 1 line
            lines.add(new BudgetImportExport(propertyReference,budgetStartDate,budgetEndDate, budgetChargeReference,budgetedValue,auditedValue,null,null,null, null, null, null, null, null, null));

        } else {
            // create a line for each partion item
            for (PartitionItem partitionItem : item.getPartitionItems()) {
                final PartitioningTableType type;
                KeyTable keyTable = null;
                if (partitionItem.getPartitioningTable().getClass().isAssignableFrom(KeyTable.class)) {
                    keyTable = (KeyTable) partitionItem.getPartitioningTable();
                    type = PartitioningTableType.KEY_TABLE;
                } else {
                    type = PartitioningTableType.DIRECT_COST_TABLE;
                }
                lines.add(
                        new BudgetImportExport(
                                propertyReference,
                                budgetStartDate,
                                budgetEndDate,
                                budgetChargeReference,
                                budgetedValue,
                                auditedValue,
                                partitionItem.getPartitioningTable().getName(),
                                type == PartitioningTableType.KEY_TABLE ? keyTable.getFoundationValueType().toString() : null,
                                type == PartitioningTableType.KEY_TABLE ? keyTable.getKeyValueMethod().toString() : null,
                                partitionItem.getCharge().getReference(),
                                partitionItem.getPercentage(),
                                partitionItem.getFixedBudgetedAmount(),
                                partitionItem.getFixedAuditedAmount(),
                                item.getCalculationDescription(),
                                type.toString())
                );

            }

        }

        return lines;
    }

    public List<ChargeImport> charges(BudgetImportExportManager manager) {

        List<ChargeImport> result = new ArrayList<ChargeImport>();
        if (manager.getBudget()==null){return result;}

        for (BudgetItem item : manager.getBudget().getItems()) {
            result.add(createCharge(item.getCharge()));
        }

        for (BudgetItem item : manager.getBudget().getItems()) {
            for (PartitionItem partitionItem : item.getPartitionItems()){
                if (notInResult(result, partitionItem.getCharge())) {
                    result.add(createCharge(partitionItem.getCharge()));
                }
            }
        }
        return result;
    }

    private boolean notInResult(List<ChargeImport> list, Charge charge){
        for (ChargeImport line : list){
            if (line.getReference().equals(charge.getReference())){
                return false;
            }
        }
        return true;
    }

    private ChargeImport createCharge(final Charge charge){
        return new ChargeImport(
                charge.getAtPath(),
                charge.getReference(),
                charge.getName(),
                charge.getDescription(),
                charge.getTax() !=null ? charge.getTax().getReference() : null,
                charge.getGroup()!=null ? charge.getGroup().getReference() : null,
                charge.getGroup()!=null ? charge.getGroup().getName() : null,
                charge.getApplicability().name(),
                charge.getParent()!=null ? charge.getParent().getReference() : null
        );
    }

    @Programmatic
    public Budget importBudget(
            final Budget budget,
            final Blob spreadsheet) {

        WorksheetSpec chargeSpec = new WorksheetSpec(ChargeImport.class, "charges");
        List<ChargeImport> chargeLines = excelService.fromExcel(spreadsheet, chargeSpec);
        chargeLines.forEach(l->l.importData(null));


        WorksheetSpec spec1 = new WorksheetSpec(BudgetImportExport.class, "budget");
        WorksheetSpec spec2 = new WorksheetSpec(KeyItemImportExportLineItem.class, "keyItems");
        WorksheetSpec spec3 = new WorksheetSpec(DirectCostLine.class, "directCosts");
        List<List<?>> objects =
                excelService.fromExcel(spreadsheet, Arrays.asList(spec1, spec2, spec3));

        List<BudgetImportExport> lineItems = (List<BudgetImportExport>) objects.get(0);

        Property property = propertyRepository.findPropertyByReference(lineItems.get(0).getPropertyReference());
        Budget budgetOfFirstLine = budgetRepository.findByPropertyAndDate(property, lineItems.get(0).getBudgetStartDate());
        if (budgetOfFirstLine.equals(budget)) {

            budget.removeNewCalculations();
            budget.removeAllBudgetItems();
            budget.removeAllPartitioningTables();

            // import budget and items
            BudgetImportExport previousRow = null;
            for (BudgetImportExport lineItem : lineItems) {
                lineItem.importData(previousRow).get(0);
                previousRow = lineItem;
            }

            // import keyTables
            importKeyTables(lineItems, objects, budget);

            // import directCosts
            importDirectCostTables(lineItems, objects, budget);

        } else {

            messageService.warnUser("Budget found in import does not equal budget on import manager");

        }

        return budget;
    }

    private Budget getBudgetUsingFirstLine(List<BudgetImportExport> lineItems){
        BudgetImportExport firstLine = lineItems.get(0);
        Property property = propertyRepository.findPropertyByReference(firstLine.getPropertyReference());
        return budgetRepository.findByPropertyAndStartDate(property, firstLine.getBudgetStartDate());
    }

    private void importKeyTables(final List<BudgetImportExport> budgetItemLines, final List<List<?>> objects, final Budget budget){

        List<KeyTable> keyTablesToImport = keyTablesToImport(budgetItemLines, budget);
        List<KeyItemImportExportLineItem> keyItemLines = (List<KeyItemImportExportLineItem>) objects.get(1);

        // filter case where no key items are filled in
        if (keyItemLines.size() == 0) {return;}

        for (KeyTable keyTable : keyTablesToImport){
            List<KeyItemImportExportLineItem> itemsToImportForKeyTable = new ArrayList<>();
            for (KeyItemImportExportLineItem keyItemLine : keyItemLines){
                if (keyItemLine.getKeyTableName().equals(keyTable.getName())){
                    itemsToImportForKeyTable.add(new KeyItemImportExportLineItem(keyItemLine));
                }
            }
            for (KeyItem keyItem : keyTable.getItems()) {
                Boolean keyItemFound = false;
                for (KeyItemImportExportLineItem lineItem : itemsToImportForKeyTable){
                    if (lineItem.getUnitReference().equals(keyItem.getUnit().getReference())){
                        keyItemFound = true;
                        break;
                    }
                }
                if (!keyItemFound) {
                    KeyItemImportExportLineItem deletedItem = new KeyItemImportExportLineItem(keyItem, null);
                    deletedItem.setStatus(Status.DELETED);
                    itemsToImportForKeyTable.add(deletedItem);
                }
            }
            for (KeyItemImportExportLineItem item : itemsToImportForKeyTable){
                serviceRegistry2.injectServicesInto(item);
                item.validate();
                item.apply();
            }
        }
    }

    private List<KeyTable> keyTablesToImport(final List<BudgetImportExport> lineItems, final Budget budget){
        List<KeyTable> result = new ArrayList<>();
        for (BudgetImportExport lineItem :lineItems) {
            if (PartitioningTableType.valueOf(lineItem.getTableType()) == PartitioningTableType.KEY_TABLE) {
                KeyTable foundKeyTable = (KeyTable) partitioningTableRepository.findByBudgetAndName(budget, lineItem.getPartitioningTableName());
                if (foundKeyTable != null && !result.contains(foundKeyTable)) {
                    result.add(foundKeyTable);
                }
            }
        }
        return result;
    }

    private void importDirectCostTables(final List<BudgetImportExport> budgetItemLines, final List<List<?>> objects, final Budget budget){

        List<DirectCostTable> tablesToImport = directCostTablesToImport(budgetItemLines, budget);
        List<DirectCostLine> lines = (List<DirectCostLine>) objects.get(2);

        // filter case where no key items are filled in
        if (lines.size() == 0) {return;}

        for (DirectCostTable table : tablesToImport){
            List<DirectCostLine> itemsToImportForTable = new ArrayList<>();
            for (DirectCostLine line : lines){
                if (line.getDirectCostTableName().equals(table.getName())){
                    itemsToImportForTable.add(new DirectCostLine(line));
                }
            }
            for (DirectCost directCost : table.getItems()) {
                Boolean itemFound = false;
                for (DirectCostLine lineItem : itemsToImportForTable){
                    if (lineItem.getUnitReference().equals(directCost.getUnit().getReference())){
                        itemFound = true;
                        break;
                    }
                }
                if (!itemFound) {
                    DirectCostLine deletedItem = new DirectCostLine(directCost, null);
                    deletedItem.setStatus(Status.DELETED);
                    itemsToImportForTable.add(deletedItem);
                }
            }
            for (DirectCostLine item : itemsToImportForTable){
                serviceRegistry2.injectServicesInto(item);
                item.validate();
                item.apply();
            }
        }
    }

    private List<DirectCostTable> directCostTablesToImport(final List<BudgetImportExport> lineItems, final Budget budget){
        List<DirectCostTable> result = new ArrayList<>();
        for (BudgetImportExport lineItem :lineItems) {
            if (PartitioningTableType.valueOf(lineItem.getTableType()) == PartitioningTableType.DIRECT_COST_TABLE) {
                DirectCostTable foundTable = (DirectCostTable) partitioningTableRepository.findByBudgetAndName(budget, lineItem.getPartitioningTableName());
                if (foundTable != null && !result.contains(foundTable)) {
                    result.add(foundTable);
                }
            }
        }
        return result;
    }

    @Inject
    private ExcelService excelService;

    @Inject PropertyRepository propertyRepository;

    @Inject BudgetRepository budgetRepository;

    @Inject ServiceRegistry2 serviceRegistry2;

    @Inject MessageService messageService;

    @Inject PartitioningTableRepository partitioningTableRepository;

}
