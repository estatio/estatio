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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.imports.ChargeImport;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetImportExportService {

    @PostConstruct
    public void init() {
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    @Programmatic
    public Budget importBudget(
            final Budget budget,
            final Blob spreadsheet,
            final boolean importKeyTables,
            final boolean importCharges) {

        // import charges
        if (importCharges) {
            WorksheetSpec chargeSpec = new WorksheetSpec(ChargeImport.class, "charges");
            List<ChargeImport> chargeLines = excelService.fromExcel(spreadsheet, chargeSpec);
            chargeLines.forEach(l -> l.importData(null));
        }

        // import budget items
        WorksheetSpec budgetItemSpec = new WorksheetSpec(BudgetItemImportExport.class, "budgetItems");
        List<BudgetItemImportExport> budgetItemLines = excelService.fromExcel(spreadsheet, budgetItemSpec);
        Property property = propertyRepository.findPropertyByReference(budgetItemLines.get(0).getPropertyReference());
        Budget budgetOfFirstLine = budgetRepository.findByPropertyAndDate(property, budgetItemLines.get(0).getBudgetStartDate());

        // validate budget item lines
        if (!budgetOfFirstLine.equals(budget)) {
            messageService.warnUser("Unexpected budget found on sheet budgetItems");
            return budget;
        }
        if (!allLinesReferToSameBudget(budgetItemLines)) {
            messageService.warnUser("All lines on sheet budgetItems should refer to the same budget");
            return budget;
        }

        // import budget item lines
        BudgetItemImportExport previous = null;
        for (BudgetItemImportExport line : budgetItemLines){
            line.importData(previous);
            previous = line;
        }

        // import partition items
        WorksheetSpec partitionItemSpec = new WorksheetSpec(BudgetPartitionItemImportExport.class, "partitionItems");
        List<BudgetPartitionItemImportExport> partitionItemLines = excelService.fromExcel(spreadsheet, partitionItemSpec);
        property = propertyRepository.findPropertyByReference(partitionItemLines.get(0).getPropertyReference());
        budgetOfFirstLine = budgetRepository.findByPropertyAndDate(property, partitionItemLines.get(0).getBudgetStartDate());

        // validate partition item lines
        if (!budgetOfFirstLine.equals(budget)) {
            messageService.warnUser("Unexpected budget found on sheet partitionItems");
            return budget;
        }
        // just to be sure ...
        budget.removeNewCalculations();
        // import partition item lines
        BudgetPartitionItemImportExport previousRow = null;
        for (BudgetPartitionItemImportExport line : partitionItemLines) {
            line.importData(previousRow);
            previousRow = line;
        }

        // import keytables and direccost tables
        if (importKeyTables) {
            WorksheetSpec keyItemSpec = new WorksheetSpec(KeyItemImportExportLine.class, "keyItems");
            List<KeyItemImportExportLine> keyItemLines = excelService.fromExcel(spreadsheet, keyItemSpec);
            importKeyTables(keyItemLines);

            WorksheetSpec direcCostSpec = new WorksheetSpec(DirectCostLine.class, "directCosts");
            List<DirectCostLine> direcCostLines = excelService.fromExcel(spreadsheet, direcCostSpec);
            importDirectCostTables(direcCostLines);
        }

        return budget;
    }

    private boolean allLinesReferToSameBudget(final List<BudgetItemImportExport> budgetItemLines) {
        final List<String> propRefs = budgetItemLines.stream().map(l -> l.getPropertyReference()).distinct()
                .collect(Collectors.toList());
        final List<LocalDate> startDates = budgetItemLines.stream().map(l -> l.getBudgetStartDate()).distinct()
                .collect(Collectors.toList());
        final List<LocalDate> endDates = budgetItemLines.stream().map(l -> l.getBudgetEndDate()).distinct()
                .collect(Collectors.toList());
        return propRefs.size() == 1 && startDates.size() == 1 && endDates.size() == 1;
    }

    private void importKeyTables(final List<KeyItemImportExportLine> keyItemLines){
        final List<String> distinctKeyTableNames = keyItemLines.stream().map(l -> l.getKeyTableName()).distinct()
                .collect(Collectors.toList());
        distinctKeyTableNames.forEach(ktn->{
            final List<KeyItemImportExportLine> linesForKeyTableName = keyItemLines.stream()
                    .filter(l -> l.getKeyTableName().equals(ktn)).collect(Collectors.toList());
            partitioningTableItemImportExportService.importLines(linesForKeyTableName);
        });
    }

    private void importDirectCostTables(final List<DirectCostLine> directCostLines){
        final List<String> distinctDirectCostTableNames = directCostLines.stream().map(l -> l.getDirectCostTableName()).distinct()
                .collect(Collectors.toList());
        distinctDirectCostTableNames.forEach(dctn->{
            final List<DirectCostLine> linesForDirectCostTableName = directCostLines.stream()
                    .filter(l -> l.getDirectCostTableName().equals(dctn)).collect(Collectors.toList());
            partitioningTableItemImportExportService.importDirectCostLines(linesForDirectCostTableName);
        });
    }

    public List<BudgetItemImportExport> getBudgetItemLines(final Budget budget) {
        List<BudgetItemImportExport> result = new ArrayList<BudgetItemImportExport>();
        if (budget==null){return result;}
        for (BudgetItem item : budget.getItems()) {
            result.add(
                    new BudgetItemImportExport(
                            budget.getProperty().getReference(),
                            budget.getStartDate(),
                            budget.getEndDate(),
                            item.getCharge().getReference(),
                            item.getBudgetedValue(),
                            item.getAuditedValue(),
                            item.getCalculationDescription()
                    )
            );
        }
        return result;
    }

    public List<BudgetPartitionItemImportExport> getPartionItemLines(final Budget budget) {
        List<BudgetPartitionItemImportExport> result = new ArrayList<BudgetPartitionItemImportExport>();
        if (budget==null){return result;}

        for (BudgetItem item : budget.getItems()) {
            result.addAll(createPartitionItemLines(item, BudgetCalculationType.AUDITED));
        }
        for (BudgetItem item : budget.getItems()) {
            result.addAll(createPartitionItemLines(item, BudgetCalculationType.BUDGETED));
        }
        return result;
    }

    public List<KeyItemImportExportLine> getKeyItemLines(final Budget budget) {
        List<KeyItemImportExportLine> result = new ArrayList<>();
        if (budget==null){return result;} // for import from menu where budget unknown
        for (KeyTable keyTable : budget.getKeyTables()){
            result.addAll(partitioningTableItemImportExportService.keyItemsToLines(keyTable.getItems()));
        }
        return result;
    }

    public List<DirectCostLine> getDirectCostLines(final Budget budget){
        List<DirectCostLine> result = new ArrayList<>();
        if (budget==null){return result;} // for import from menu where budget unknown
        for (DirectCostTable directCostTable : budget.getDirectCostTables()){
            result.addAll(partitioningTableItemImportExportService.directCostsToLines(directCostTable.getItems()));
        }
        return result;
    }

    private List<BudgetPartitionItemImportExport> createPartitionItemLines(final BudgetItem item, final BudgetCalculationType budgetCalculationType){

        List<BudgetPartitionItemImportExport> lines = new ArrayList<>();

        String propertyReference = item.getBudget().getProperty().getReference();
        LocalDate budgetStartDate = item.getBudget().getStartDate();
        LocalDate budgetEndDate = item.getBudget().getEndDate();
        String budgetChargeReference = item.getCharge().getReference();

        if (item.getPartitionItems().size()==0 && budgetCalculationType==BudgetCalculationType.BUDGETED){
            // create 1 line
            lines.add(new BudgetPartitionItemImportExport(propertyReference,budgetStartDate,budgetEndDate, budgetChargeReference,null,null,null, null, null, null, null, null, null, null));

        } else {
            // create a line for each partion item
            for (PartitionItem partitionItem : item.getPartitionItems()) {
                final BudgetCalculationType calculationType = partitionItem.getPartitioning().getType();
                if (calculationType == budgetCalculationType) {
                    final PartitioningTableType type;
                    KeyTable keyTable = null;
                    if (partitionItem.getPartitioningTable().getClass().isAssignableFrom(KeyTable.class)) {
                        keyTable = (KeyTable) partitionItem.getPartitioningTable();
                        type = PartitioningTableType.KEY_TABLE;
                    } else {
                        type = PartitioningTableType.DIRECT_COST_TABLE;
                    }
                    lines.add(
                            new BudgetPartitionItemImportExport(
                                    propertyReference,
                                    budgetStartDate,
                                    budgetEndDate,
                                    budgetChargeReference,
                                    partitionItem.getPartitioningTable().getName(),
                                    type == PartitioningTableType.KEY_TABLE ?
                                            keyTable.getFoundationValueType().toString() :
                                            null,
                                    type == PartitioningTableType.KEY_TABLE ?
                                            keyTable.getKeyValueMethod().toString() :
                                            null,
                                    partitionItem.getCharge().getReference(),
                                    partitionItem.getPercentage(),
                                    partitionItem.getFixedBudgetedAmount(),
                                    partitionItem.getFixedAuditedAmount(),
                                    type.toString(),
                                    calculationType,
                                    partitionItem.getBudgetItem().getCalculationDescription())
                    );

                }
            }

        }

        return lines;
    }

    public List<ChargeImport> getCharges(final Budget budget) {

        List<ChargeImport> result = new ArrayList<ChargeImport>();
        if (budget==null){return result;}

        for (BudgetItem item : budget.getItems()) {
            result.add(createChargeImportLine(item.getCharge()));
        }

        for (BudgetItem item : budget.getItems()) {
            for (PartitionItem partitionItem : item.getPartitionItems()){
                if (notInResult(result, partitionItem.getCharge())) {
                    result.add(createChargeImportLine(partitionItem.getCharge()));
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

    private ChargeImport createChargeImportLine(final Charge charge){
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

    @Inject
    private ExcelService excelService;

    @Inject PropertyRepository propertyRepository;

    @Inject BudgetRepository budgetRepository;

    @Inject MessageService messageService;

    @Inject PartitioningTableItemImportExportService partitioningTableItemImportExportService;
}
