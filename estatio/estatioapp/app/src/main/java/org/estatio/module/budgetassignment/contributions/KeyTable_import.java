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
package org.estatio.module.budgetassignment.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budgetassignment.imports.KeyItemImportExportLine;
import org.estatio.module.budgetassignment.imports.PartitioningTableItemImportExportService;

@Mixin(method="act")
public class KeyTable_import {

    private final KeyTable keyTable;

    public KeyTable_import(KeyTable keyTable) {
        this.keyTable = keyTable;
    }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "items", sequence = "5")
    public KeyTable act(final Blob filename) {
        final List<KeyItemImportExportLine> lines = excelService
                .fromExcel(filename, KeyItemImportExportLine.class, "keyItems");
        return partitioningTableItemImportExportService.importLines(lines);
    }

    @Inject ExcelService excelService;

    @Inject PartitioningTableItemImportExportService partitioningTableItemImportExportService;

}
