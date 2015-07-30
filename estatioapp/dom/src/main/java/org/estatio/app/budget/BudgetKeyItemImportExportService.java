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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.memento.MementoService;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.dom.budget.BudgetKeyItem;
import org.estatio.dom.budget.BudgetKeyItems;

@DomainService(nature = NatureOfService.DOMAIN)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetKeyItemImportExportService {

    @PostConstruct
    public void init() {
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<BudgetKeyItemImportExportLineItem> items(BudgetKeyItemImportExportManager manager) {
        return Lists.transform(new ArrayList<BudgetKeyItem>(manager.getBudgetKeyTable().getBudgetKeyItems()), toLineItem());
    }

    private Function<BudgetKeyItem, BudgetKeyItemImportExportLineItem> toLineItem() {
        return new Function<BudgetKeyItem, BudgetKeyItemImportExportLineItem>() {
            @Override
            public BudgetKeyItemImportExportLineItem apply(final BudgetKeyItem budgetKeyItem) {
                return new BudgetKeyItemImportExportLineItem(budgetKeyItem);
            }
        };
    }

    // //////////////////////////////////////
    // Injected Services
    // //////////////////////////////////////

    @javax.inject.Inject
    private DomainObjectContainer container;

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private BookmarkService bookmarkService;

    @javax.inject.Inject
    private MementoService mementoService;

    @Inject
    private BudgetKeyItems budgetKeyItems;


}
