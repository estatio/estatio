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
package org.estatio.app.budget;

import java.util.List;

import javax.annotation.PostConstruct;

import org.isisaddons.module.excel.dom.ExcelService;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.actinvoc.ActionInvocationContext;
import org.apache.isis.applib.value.Blob;

import org.estatio.dom.budget.BudgetKeyItem;
import org.estatio.dom.budget.BudgetKeyTable;

@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY
)
public class BudgetKeyItemImportExportContributions {

    @PostConstruct
    public void init() {
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    // //////////////////////////////////////

    // //////////////////////////////////////
    // bulk update manager (action)
    // //////////////////////////////////////

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "Budgets", sequence = "90.1")
    public BudgetKeyItemImportExportManager bulkUpdateManager(BudgetKeyTable budgetKeyTable) {

        return new BudgetKeyItemImportExportManager(budgetKeyTable);

    }


    /**
     * Bulk actions of this type are not yet supported, hence have hidden...
     *
     * @see https://issues.apache.org/jira/browse/ISIS-705.
     */
    @Action(
            hidden = Where.EVERYWHERE, // ISIS-705
            invokeOn = InvokeOn.OBJECT_AND_COLLECTION
    )
    @ActionLayout(
            contributed = Contributed.AS_ACTION
    )
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Blob export(final BudgetKeyItem budgetKeyItem) {
        if (actionInvocationContext.isLast()) {
            final List budgetKeyItems = actionInvocationContext.getDomainObjects();
            return excelService.toExcel(budgetKeyItems, BudgetKeyItem.class, "budgetKeyItems.xlsx");
        } else {
            return null;
        }
    }


    // //////////////////////////////////////
    // Injected Services
    // //////////////////////////////////////

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private ActionInvocationContext actionInvocationContext;

}
