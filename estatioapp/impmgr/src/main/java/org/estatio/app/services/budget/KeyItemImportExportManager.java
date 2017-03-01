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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.KeyItemImportExportManager"
)
public class KeyItemImportExportManager {

    //region > constructors, title
    public KeyItemImportExportManager() {
    }

    public KeyItemImportExportManager(KeyItemImportExportManager keyItemImportExportManager) {
        this.keyTable = keyItemImportExportManager.getKeyTable();
        this.fileName = keyItemImportExportManager.getFileName();
    }

    public KeyItemImportExportManager(final KeyTable keyTable) {
        this.keyTable = keyTable;
        this.fileName = keyTable.getName().concat(" - ").concat("export.xlsx");
    }

    public String title() {
        return "Import export key items";
    }

    //endregion


    @Getter @Setter
    private KeyTable keyTable;

    @Getter @Setter
    private String fileName;

    //region > changeFileName (action)
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Change File Name")
    @MemberOrder(name = "fileName", sequence = "1")
    public KeyItemImportExportManager changeFileName(final String fileName) {
        this.setFileName(fileName);
        return new KeyItemImportExportManager(this);
    }

    public String default0ChangeFileName() {
        return getFileName();
    }

    //endregion


    @SuppressWarnings("unchecked")
    @Collection
    public List<KeyItemImportExportLineItem> getKeyItems() {
        return keyItemImportExportService.items(this);
    }

    //region > export (action)
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    @MemberOrder(name = "keyItems", sequence = "1")
    public Blob export() {
        final String fileName = withExtension(getFileName(), ".xlsx");
        WorksheetSpec spec = new WorksheetSpec(KeyItemImportExportLineItem.class, "keyItems");
        WorksheetContent worksheetContent = new WorksheetContent(getKeyItems(), spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    public String disableExport() {
        return getFileName() == null ? "file name is required" : null;
    }

    private static String withExtension(final String fileName, final String fileExtension) {
        return fileName.endsWith(fileExtension) ? fileName : fileName + fileExtension;
    }
    //endregion

    //region > import (action)

    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Import", cssClassFa = "fa-upload")
    @MemberOrder(name = "keyItems", sequence = "2")
    public List<KeyItemImportExportLineItem> importBlob(
            @Parameter(fileAccept = ".xlsx")
            @ParameterLayout(named = "Excel spreadsheet")
            final Blob spreadsheet) {
        WorksheetSpec spec = new WorksheetSpec(KeyItemImportExportLineItem.class, "keyItems");
        List<KeyItemImportExportLineItem> lineItems =
                excelService.fromExcel(spreadsheet, spec);
        container.informUser(lineItems.size() + " items imported");

        List<KeyItemImportExportLineItem> newItems = new ArrayList<>();
        for (KeyItemImportExportLineItem item : lineItems) {
            item.validate();
            newItems.add(new KeyItemImportExportLineItem(item));
        }
        for (KeyItem keyItem : keyTable.getItems()) {
            Boolean keyItemFound = false;
            for (KeyItemImportExportLineItem lineItem : newItems){
                if (lineItem.getUnitReference().equals(keyItem.getUnit().getReference())){
                    keyItemFound = true;
                    break;
                }
            }
            if (!keyItemFound) {
                KeyItemImportExportLineItem deletedItem = new KeyItemImportExportLineItem(keyItem);
                deletedItem.setStatus(Status.DELETED);
                newItems.add(deletedItem);
            }
        }
        return newItems;
    }

    //endregion

    //region > injected services


    @javax.inject.Inject
    private DomainObjectContainer container;

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private KeyItemImportExportService keyItemImportExportService;

    @Inject
    private KeyTableRepository keyTableRepository;

    //endregion

}
