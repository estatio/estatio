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
import java.util.SortedSet;

import javax.annotation.PostConstruct;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.budget.dom.keyitem.KeyItem;

@DomainService(nature = NatureOfService.DOMAIN)
public class KeyItemImportExportService {

    @PostConstruct
    public void init() {
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<KeyItemImportExportLineItem> items(KeyItemImportExportManager manager) {
        return Lists.transform(new ArrayList<KeyItem>(manager.getKeyTable().getItems()), toLineItem());
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<KeyItemImportExportLineItem> items(SortedSet<KeyItem> keyItems) {
        return Lists.transform(new ArrayList<KeyItem>(keyItems), toLineItem());
    }

    private Function<KeyItem, KeyItemImportExportLineItem> toLineItem() {
        return new Function<KeyItem, KeyItemImportExportLineItem>() {
            @Override
            public KeyItemImportExportLineItem apply(final KeyItem keyItem) {
                return new KeyItemImportExportLineItem(keyItem);
            }
        };
    }

    @javax.inject.Inject
    private ExcelService excelService;

}
