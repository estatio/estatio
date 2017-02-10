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

import javax.annotation.PostConstruct;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.actinvoc.ActionInvocationContext;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.budget.dom.budget.Budget;

@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY
)
public class BudgetImportExportContributions {

    @PostConstruct
    public void init() {
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            publishing = Publishing.DISABLED
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public BudgetImportExportManager importExportBudget(Budget budget) {

        return new BudgetImportExportManager(budget);

    }

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private ActionInvocationContext actionInvocationContext;

}
