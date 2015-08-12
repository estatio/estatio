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
package org.estatio.dom.budget;

import java.math.BigDecimal;
import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.Unit;

@DomainService(repositoryFor = BudgetKeyItem.class, nature = NatureOfService.VIEW)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetKeyItems extends UdoDomainRepositoryAndFactory<BudgetKeyItem> {

    public BudgetKeyItems() {
        super(BudgetKeyItems.class, BudgetKeyItem.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public BudgetKeyItem newBudgetKeyItem(
            final BudgetKeyTable budgetKeyTable,
            final Unit unit,
            final BigDecimal keyValue) {
        BudgetKeyItem budgetKeyItem = newTransientInstance();
        budgetKeyItem.setBudgetKeyTable(budgetKeyTable);
        budgetKeyItem.setUnit(unit);
        budgetKeyItem.setTargetValue(keyValue);
        persistIfNotAlready(budgetKeyItem);

        return budgetKeyItem;
    }

    @Programmatic
    public BudgetKeyItem newBudgetKeyItem(
            final BudgetKeyTable budgetKeyTable,
            final Unit unit,
            final BigDecimal sourceValue,
            final BigDecimal keyValue) {
        BudgetKeyItem budgetKeyItem = newTransientInstance();
        budgetKeyItem.setBudgetKeyTable(budgetKeyTable);
        budgetKeyItem.setUnit(unit);
        budgetKeyItem.setSourceValue(sourceValue);
        budgetKeyItem.setTargetValue(keyValue);
        persistIfNotAlready(budgetKeyItem);

        return budgetKeyItem;
    }

    // //////////////////////////////////////

    @Programmatic
    public BudgetKeyItem findByBudgetKeyTableAndUnit(BudgetKeyTable budgetKeyTable, Unit unit){
        return uniqueMatch("findByBudgetKeyTableAndUnit", "budgetKeyTable", budgetKeyTable, "unit", unit);
    }


    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<BudgetKeyItem> allBudgetKeyItems() {
        return allInstances();
    }
}
