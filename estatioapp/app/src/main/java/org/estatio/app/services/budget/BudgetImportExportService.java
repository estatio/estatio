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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.viewmodels.BudgetImportExport;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetImportExportService {

    @PostConstruct
    public void init() {
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
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

        if (item.getBudgetItemAllocations().size()==0){
            // create 1 line
            lines.add(new BudgetImportExport(propertyReference,budgetStartDate,budgetEndDate, budgetChargeReference,budgetedValue,auditedValue,null,null,null));

        } else {
            // create a line for each allocation
            for (BudgetItemAllocation allocation : item.getBudgetItemAllocations()) {
                String keyTableName = allocation.getKeyTable().getName();
                String allocationChargeReference = allocation.getCharge().getReference();
                BigDecimal percentage = allocation.getPercentage();
                lines.add(new BudgetImportExport(propertyReference, budgetStartDate, budgetEndDate, budgetChargeReference, budgetedValue, auditedValue, keyTableName, allocationChargeReference, percentage));
            }

        }

        return lines;
    }

    @Inject
    private ExcelService excelService;

}
