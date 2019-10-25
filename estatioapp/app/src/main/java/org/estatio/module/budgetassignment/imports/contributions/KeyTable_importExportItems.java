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
package org.estatio.module.budgetassignment.imports.contributions;

import org.apache.isis.applib.annotation.*;

import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budgetassignment.imports.KeyItemImportExportManager;

@Mixin(method="act")
public class KeyTable_importExportItems {

    private final KeyTable keyTable;

    public KeyTable_importExportItems(KeyTable keyTable) {
        this.keyTable = keyTable;
    }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "items", sequence = "5")
    public KeyItemImportExportManager act() {

        return new KeyItemImportExportManager(keyTable);

    }
}
