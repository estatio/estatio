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
package org.estatio.app.services.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.charge.dom.impmgr.ChargeImport;
import org.estatio.dom.budgetassignment.override.BudgetOverride;
import org.estatio.dom.budgetassignment.override.BudgetOverrideForFixed;
import org.estatio.dom.budgetassignment.override.BudgetOverrideForFlatRate;
import org.estatio.dom.budgetassignment.override.BudgetOverrideForMax;
import org.estatio.dom.budgetassignment.override.BudgetOverrideRepository;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.partioning.PartitionItem;
import org.estatio.dom.charge.Charge;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;

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
            lines.add(new BudgetImportExport(propertyReference,budgetStartDate,budgetEndDate, budgetChargeReference,budgetedValue,auditedValue,null,null,null, null, null));

        } else {
            // create a line for each partion item
            for (PartitionItem partitionItem : item.getPartitionItems()) {
                String keyTableName = partitionItem.getKeyTable().getName();
                String foundationValueType = partitionItem.getKeyTable().getFoundationValueType().toString();
                String keyValueMethod = partitionItem.getKeyTable().getKeyValueMethod().toString();
                String allocationChargeReference = partitionItem.getCharge().getReference();
                BigDecimal percentage = partitionItem.getPercentage();
                lines.add(new BudgetImportExport(propertyReference, budgetStartDate, budgetEndDate, budgetChargeReference, budgetedValue, auditedValue, keyTableName, foundationValueType, keyValueMethod, allocationChargeReference, percentage));
            }

        }

        return lines;
    }

    public List<BudgetOverrideImportExport> overrides(BudgetImportExportManager manager) {

        List<BudgetOverrideImportExport> result = new ArrayList<BudgetOverrideImportExport>();
        if (manager.getBudget()==null){return result;}

        for (Lease lease : leaseRepository.findByAssetAndActiveOnDate(manager.getBudget().getProperty(), manager.getBudget().getStartDate())) {
            for (BudgetOverride override : budgetOverrideRepository.findByLease(lease)) {
                result.addAll(createOverrides(override, manager));
            }
        }

        return result;
    }

    private List<BudgetOverrideImportExport> createOverrides(final BudgetOverride override, final BudgetImportExportManager manager){
        List<BudgetOverrideImportExport> result = new ArrayList<>();

        String incomingChargeRef;
        if (override.getIncomingCharge()==null){
            incomingChargeRef=null;
        } else {
            incomingChargeRef = override.getIncomingCharge().getReference();
        }

        String typeName;
        if (override.getType()==null){
            typeName=null;
        } else {
            typeName=override.getType().name();
        }

        BigDecimal maxValue = BigDecimal.ZERO;
        if (override.getClass()== BudgetOverrideForMax.class){
            BudgetOverrideForMax o = (BudgetOverrideForMax) override;
            maxValue = maxValue.add(o.getMaxValue());
        }

        BigDecimal fixedValue = BigDecimal.ZERO;
        if (override.getClass()== BudgetOverrideForFixed.class){
            BudgetOverrideForFixed o = (BudgetOverrideForFixed) override;
            fixedValue = fixedValue.add(o.getFixedValue());
        }

        BigDecimal valuePerM2 = BigDecimal.ZERO;
        BigDecimal weightedArea = BigDecimal.ZERO;
        if (override.getClass()== BudgetOverrideForFlatRate.class){
            BudgetOverrideForFlatRate o = (BudgetOverrideForFlatRate) override;
            valuePerM2 = valuePerM2.add(o.getValuePerM2());
            weightedArea = weightedArea.add(o.getWeightedArea());
        }

        result.add(new BudgetOverrideImportExport(
                override.getLease().getReference(),
                override.getStartDate(),
                override.getEndDate(),
                override.getInvoiceCharge().getReference(),
                incomingChargeRef,
                typeName,
                override.getReason(),
                override.getClass().getSimpleName(),
                maxValue,
                fixedValue,
                valuePerM2,
                weightedArea
        ));
        return result;
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
                charge.getTax().getReference(),
                charge.getGroup().getReference(),
                charge.getGroup().getName(),
                charge.getApplicability().name(),
                charge.getParent()!=null ? charge.getParent().getReference() : null
        );
    }

    @Inject
    private BudgetOverrideRepository budgetOverrideRepository;

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private ExcelService excelService;

}
