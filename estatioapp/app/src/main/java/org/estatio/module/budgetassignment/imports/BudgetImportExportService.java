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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budgetassignment.dom.BudgetService;
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

    public List<BudgetImportExport> lines(BudgetImportExportManager manager) {

        List<BudgetImportExport> result = new ArrayList<BudgetImportExport>();
        if (manager.getBudget()==null){return result;}

        for (BudgetItem item : manager.getBudget().getItems()) {
            result.addAll(createLines(item, BudgetCalculationType.AUDITED));
        }
        for (BudgetItem item : manager.getBudget().getItems()) {
            result.addAll(createLines(item, BudgetCalculationType.BUDGETED));
        }
        return result;
    }

    private List<BudgetImportExport> createLines(final BudgetItem item, final BudgetCalculationType budgetCalculationType){

        List<BudgetImportExport> lines = new ArrayList<>();

        String propertyReference = item.getBudget().getProperty().getReference();
        LocalDate budgetStartDate = item.getBudget().getStartDate();
        LocalDate budgetEndDate = item.getBudget().getEndDate();
        String budgetChargeReference = item.getCharge().getReference();
        BigDecimal budgetedValue = item.getBudgetedValue();
        BigDecimal auditedValue = item.getAuditedValue();

        if (item.getPartitionItems().size()==0 && budgetCalculationType==BudgetCalculationType.BUDGETED){
            // create 1 line
            lines.add(new BudgetImportExport(propertyReference,budgetStartDate,budgetEndDate, budgetChargeReference,budgetedValue,auditedValue,null,null,null, null, null, null, null, null, null,null));

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
                            new BudgetImportExport(
                                    propertyReference,
                                    budgetStartDate,
                                    budgetEndDate,
                                    budgetChargeReference,
                                    budgetedValue,
                                    auditedValue,
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
                                    item.getCalculationDescription(),
                                    type.toString(),
                                    calculationType)
                    );

                }
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
        WorksheetSpec spec2 = new WorksheetSpec(KeyItemImportExportLine.class, "keyItems");
        WorksheetSpec spec3 = new WorksheetSpec(DirectCostLine.class, "directCosts");
        List<List<?>> objects =
                excelService.fromExcel(spreadsheet, Arrays.asList(spec1, spec2, spec3));

        List<BudgetImportExport> lineItems = (List<BudgetImportExport>) objects.get(0);

        Property property = propertyRepository.findPropertyByReference(lineItems.get(0).getPropertyReference());
        Budget budgetOfFirstLine = budgetRepository.findByPropertyAndDate(property, lineItems.get(0).getBudgetStartDate());
        if (budgetOfFirstLine.equals(budget)) {

            budget.removeNewCalculations();
            if (budgetService.budgetCannotBeRemovedReason(budget)==null) {
                budget.removeAllBudgetItems();
            } else {
                // seems redundant because we also delete when importing using BudgetImportExport#importData; we need this code here, because we want to remove all Partitioning tables
                for (BudgetItem budgetItem : budget.getItems()) {
                    for (PartitionItem pItem : budgetItem.getPartitionItems()){
                        pItem.remove();
                    }
                }
            }
            budget.removeAllPartitioningTables();


            // import budget and items
            BudgetImportExport previousRow = null;
            for (BudgetImportExport lineItem : lineItems) {
                lineItem.importData(previousRow).get(0);
                previousRow = lineItem;
            }


            // import keyTables
            importKeyTables((List<KeyItemImportExportLine>) objects.get(1));

            // import directCosts
            importDirectCostTables((List<DirectCostLine>) objects.get(2));

        } else {

            messageService.warnUser("Budget found in import does not equal budget on import manager");

        }

        return budget;
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

    @Programmatic
    public Budget updateBudget(final Budget budget, final Blob spreadsheet) {
        // TODO: implement
        return budget;
    }

    @Inject
    private ExcelService excelService;

    @Inject PropertyRepository propertyRepository;

    @Inject BudgetRepository budgetRepository;

    @Inject MessageService messageService;

    @Inject BudgetService budgetService;

    @Inject PartitioningTableItemImportExportService partitioningTableItemImportExportService;
}
