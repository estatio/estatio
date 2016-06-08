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
package org.estatio.dom.budgeting.allocation;

import java.math.BigDecimal;
import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.charge.Charge;

@DomainService(repositoryFor = BudgetItemAllocation.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class BudgetItemAllocationRepository extends UdoDomainRepositoryAndFactory<BudgetItemAllocation> {

    public BudgetItemAllocationRepository() {
        super(BudgetItemAllocationRepository.class, BudgetItemAllocation.class);
    }

    // //////////////////////////////////////

    public BudgetItemAllocation newBudgetItemAllocation(
            final Charge charge,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage) {
        BudgetItemAllocation budgetItemAllocation = newTransientInstance(BudgetItemAllocation.class);
        budgetItemAllocation.setCharge(charge);
        budgetItemAllocation.setKeyTable(keyTable);
        budgetItemAllocation.setBudgetItem(budgetItem);
        budgetItemAllocation.setPercentage(percentage.setScale(6, BigDecimal.ROUND_HALF_UP));
        persistIfNotAlready(budgetItemAllocation);
        return budgetItemAllocation;
    }

    public String validateNewBudgetItemAllocation(
            final Charge charge,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage
    ){
        if(findByChargeAndBudgetItemAndKeyTable(charge, budgetItem, keyTable) != null) {
            return "This schedule item already exists";
        }
        return null;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @ActionLayout()
    public List<BudgetItemAllocation> allBudgetItemAllocations() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public List<BudgetItemAllocation> findByBudgetItem(final BudgetItem budgetItem) {
        return allMatches("findByBudgetItem", "budgetItem", budgetItem);
    }

    @Programmatic
    public List<BudgetItemAllocation> findByKeyTable(final KeyTable keyTable) {
        return allMatches("findByKeyTable", "keyTable", keyTable);
    }

    @Programmatic
    public BudgetItemAllocation findByChargeAndBudgetItemAndKeyTable(final Charge charge, final BudgetItem budgetItem, final KeyTable keyTable) {
        return uniqueMatch("findByChargeAndBudgetItemAndKeyTable", "charge", charge, "budgetItem", budgetItem, "keyTable", keyTable);
    }

    @Programmatic
    public BudgetItemAllocation findOrCreateBudgetItemAllocation(final BudgetItem budgetItem, final Charge allocationCharge, final KeyTable keyTable, final BigDecimal percentage){
        final BudgetItemAllocation budgetItemAllocation = findByChargeAndBudgetItemAndKeyTable(allocationCharge, budgetItem, keyTable);
        if (budgetItemAllocation == null) {
            return newBudgetItemAllocation(allocationCharge, keyTable, budgetItem, percentage);
        }
        return budgetItemAllocation;
    }

}
