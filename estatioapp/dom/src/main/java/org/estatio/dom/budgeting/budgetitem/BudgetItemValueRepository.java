/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.budgeting.budgetitem;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;

@DomainService(repositoryFor = BudgetItemValue.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class BudgetItemValueRepository extends UdoDomainRepositoryAndFactory<BudgetItemValue> {

    public BudgetItemValueRepository() {
        super(BudgetItemValueRepository.class, BudgetItemValue.class);
    }

    public BudgetItemValue newBudgetItemValue(
            final BudgetItem budgetItem,
            final BigDecimal value,
            final LocalDate date,
            final BudgetCalculationType type) {
        BudgetItemValue budgetItemValue = newTransientInstance();
        budgetItemValue.setBudgetItem(budgetItem);
        budgetItemValue.setValue(value);
        budgetItemValue.setDate(date);
        budgetItemValue.setType(type);

        persistIfNotAlready(budgetItemValue);

        return budgetItemValue;
    }

    public String validateNewBudgetItemValue(
            final BudgetItem budgetItem,
            final BigDecimal value,
            final LocalDate date,
            final BudgetCalculationType type) {
        if (type == BudgetCalculationType.BUDGETED && findByBudgetItemAndType(budgetItem, type).size() > 0){
            return "Only one value of type BUDGETED is allowed";
        }
        if (type == BudgetCalculationType.ACTUAL && findByBudgetItemAndType(budgetItem, type).size() > 0){
            return "Only one value of type ACTUAL is supported at the moment";
        }
        if (value == null){
            return "Value cannot be empty";
        }
        return null;
    }

    public List<BudgetItemValue> findByBudgetItemAndType(final BudgetItem budgetItem, final BudgetCalculationType type) {
        return allMatches("findByBudgetItemAndType", "budgetItem", budgetItem, "type", type);
    }

    public BudgetItemValue findUnique(final BudgetItem budgetItem, final LocalDate date, final BudgetCalculationType type){
        return uniqueMatch("findUnique", "budgetItem", budgetItem, "date", date, "type", type);
    }

    public List<BudgetItemValue> allBudgetItemValues() {
        return allInstances();
    }

    public BudgetItemValue updateOrCreateBudgetItemValue(final BigDecimal value, final BudgetItem budgetItem, final LocalDate date, final BudgetCalculationType type) {
        BudgetItemValue itemValue = findUnique(budgetItem, date, type);
        if (itemValue != null) {
            itemValue.setValue(value);
        } else {
            itemValue = newBudgetItemValue(budgetItem, value, date, type);
        }
        return itemValue;
    }

    public String validateUpdateOrCreateBudgetItemValue(final BigDecimal value, final BudgetItem budgetItem, final LocalDate date, final BudgetCalculationType type) {
        if (findUnique(budgetItem, date, type) == null) {
            return validateNewBudgetItemValue(budgetItem, value, date, type);
        }
        return null;
    }

    @Inject
    private IsisJdoSupport isisJdoSupport;


}
