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
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.budgetkeytable.BudgetKeyTable;

@DomainService(repositoryFor = BudgetKeyItem.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class BudgetKeyItems extends UdoDomainRepositoryAndFactory<BudgetKeyItem> {

    public BudgetKeyItems() {
        super(BudgetKeyItems.class, BudgetKeyItem.class);
    }

    // //////////////////////////////////////

    public BudgetKeyItem newBudgetKeyItem(
            final BudgetKeyTable budgetKeyTable,
            final Unit unit,
            final BigDecimal sourceValue,
            final BigDecimal keyValue) {
        BudgetKeyItem budgetKeyItem = newTransientInstance();
        budgetKeyItem.setBudgetKeyTable(budgetKeyTable);
        budgetKeyItem.setUnit(unit);
        budgetKeyItem.setSourceValue(sourceValue);
        budgetKeyItem.setValue(keyValue);
        persistIfNotAlready(budgetKeyItem);

        return budgetKeyItem;
    }

    public String validateNewBudgetKeyItem(
            final BudgetKeyTable budgetKeyTable,
            final Unit unit,
            final BigDecimal sourceValue,
            final BigDecimal keyValue) {

        if (sourceValue.compareTo(BigDecimal.ZERO) <= 0) {
            return "sourceValue cannot be zero or less than zero";
        }

        if (keyValue.compareTo(BigDecimal.ZERO) < 0) {
            return "keyValue cannot be less than zero";
        }

        return null;
    }

    // //////////////////////////////////////

    @Programmatic
    public BudgetKeyItem findByBudgetKeyTableAndUnit(BudgetKeyTable budgetKeyTable, Unit unit){
        return uniqueMatch("findByBudgetKeyTableAndUnit", "budgetKeyTable", budgetKeyTable, "unit", unit);
    }


    // //////////////////////////////////////

    @Programmatic
    public List<BudgetKeyItem> allBudgetKeyItems() {
        return allInstances();
    }
}
