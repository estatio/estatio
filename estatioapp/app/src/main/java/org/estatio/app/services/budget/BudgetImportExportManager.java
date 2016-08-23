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

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.ViewModelLayout;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.viewmodels.BudgetImportExport;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL
)
@DomainObjectLayout(
        named = "Import Export manager for budget",
        bookmarking = BookmarkPolicy.AS_ROOT
)
@ViewModelLayout(paged = -1)
public class BudgetImportExportManager {

    public String title() {
        return "Import / Export manager for budget";
    }

    public BudgetImportExportManager() {
        this.name = "Budget Import / Export";
        this.fileName = "export.xlsx";
    }

    public BudgetImportExportManager(Budget budget){
        this();
        this.budget = budget;
    }

    public BudgetImportExportManager(BudgetImportExportManager budgetImportExportManager){
        this.fileName = budgetImportExportManager.getFileName();
        this.name = budgetImportExportManager.getName();
        this.budget = budgetImportExportManager.getBudget();
    }

    @Getter @Setter
    private String name;
    @Getter @Setter
    private Budget budget;
    @Getter @Setter
    private String fileName;


    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Change File Name")
    @MemberOrder(name = "fileName", sequence = "1")
    public BudgetImportExportManager changeFileName(final String fileName) {
        this.setFileName(fileName);
        return new BudgetImportExportManager(this);
    }

    public String default0ChangeFileName() {
        return getFileName();
    }

    @SuppressWarnings("unchecked")
    @Collection()
    @CollectionLayout(
            render = RenderType.EAGERLY
    )
    public List<BudgetImportExport> getLines() {
        return budgetImportExportService.lines(this);
    }



    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout()
    @CollectionLayout(paged = -1)
    public List<BudgetImportExport> importBudget(
            @ParameterLayout(named = "Excel spreadsheet") final Blob spreadsheet) {
        List<BudgetImportExport> lineItems =
                excelService.fromExcel(spreadsheet, BudgetImportExport.class);
        return lineItems;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob exportBudget() {
        final String fileName = withExtension(getFileName(), ".xlsx");
        return excelService.toExcel(getLines(), BudgetImportExport.class, fileName);
    }

    public String disableExportBudget() {
        return getFileName() == null ? "file name is required" : null;
    }

    private static String withExtension(final String fileName, final String fileExtension) {
        return fileName.endsWith(fileExtension) ? fileName : fileName + fileExtension;
    }

    @Inject
    private DomainObjectContainer container;

    @Inject
    private ExcelService excelService;

    @Inject
    private BudgetImportExportService budgetImportExportService;

}
