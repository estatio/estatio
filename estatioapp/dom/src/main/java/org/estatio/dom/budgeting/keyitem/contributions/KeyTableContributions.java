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
package org.estatio.dom.budgeting.keyitem.contributions;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keyitem.KeyItems;
import org.estatio.dom.budgeting.keytable.KeyTable;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class KeyTableContributions {

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "items", sequence = "1")
    public KeyItem newItem(
            final KeyTable keyTable,
            final Unit unit,
            @ParameterLayout(named = "sourceValue")
            final BigDecimal sourceValue,
            @ParameterLayout(named = "keyValue")
            final BigDecimal keyValue) {

        return keyItems.newItem(keyTable, unit, sourceValue, keyValue);
    }

    public String validateNewItem(
            final KeyTable keyTable,
            final Unit unit,
            final BigDecimal sourceValue,
            final BigDecimal keyValue) {

        return keyItems.validateNewItem(keyTable, unit, sourceValue, keyValue);
    }

    @Inject
    private KeyItems keyItems;

    // //////////////////////////////////////

}
