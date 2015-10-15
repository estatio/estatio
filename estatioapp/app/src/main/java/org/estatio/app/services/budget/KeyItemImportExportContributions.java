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

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.actinvoc.ActionInvocationContext;
import org.apache.isis.applib.value.Blob;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.isisaddons.module.excel.dom.ExcelService;

import javax.annotation.PostConstruct;
import java.util.List;

@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY
)
public class KeyItemImportExportContributions {

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
    @MemberOrder(name = "items", sequence = "5")
    public KeyItemImportExportManager bulkUpdateManager(KeyTable keyTable) {

        return new KeyItemImportExportManager(keyTable);

    }


    /**
     * Bulk actions of this type are not yet supported, hence have hidden...
     *
//     * @see https://issues.apache.org/jira/browse/ISIS-705.
     */
    @Action(
            hidden = Where.EVERYWHERE, // ISIS-705
            invokeOn = InvokeOn.OBJECT_AND_COLLECTION
    )
    @ActionLayout(
            contributed = Contributed.AS_ACTION
    )
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Blob export(final KeyItem keyItem) {
        if (actionInvocationContext.isLast()) {
            final List keyItems = actionInvocationContext.getDomainObjects();
            return excelService.toExcel(keyItems, KeyItem.class, "keyItems.xlsx");
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
