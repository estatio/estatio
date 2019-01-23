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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.imports.ChargeImport;
import org.estatio.module.lease.dom.LeaseRepository;

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

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private ExcelService excelService;

}
