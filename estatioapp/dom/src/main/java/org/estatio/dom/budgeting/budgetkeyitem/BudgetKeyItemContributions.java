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
package org.estatio.dom.budgeting.budgetkeyitem;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.budgetkeytable.BudgetKeyTable;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetKeyItemContributions {

     // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public BudgetKeyItem newBudgetKeyItem(
            final BudgetKeyTable budgetKeyTable,
            final Unit unit,
            @ParameterLayout(named = "keyValue")
            final BigDecimal keyValue) {

        return budgetKeyItems.newBudgetKeyItem(budgetKeyTable, unit, keyValue);
    }

    public String validateNewBudgetKeyItem(
            final BudgetKeyTable budgetKeyTable,
            final Unit unit,
            final BigDecimal keyValue) {

        if (keyValue.compareTo(BigDecimal.ZERO) < 0) {
            return "keyValue cannot be less than zero";
        }

        return null;
    }

    @Inject
    private BudgetKeyItems budgetKeyItems;

    // //////////////////////////////////////

}
