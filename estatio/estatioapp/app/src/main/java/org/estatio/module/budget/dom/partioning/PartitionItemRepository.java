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
package org.estatio.module.budget.dom.partioning;

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

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.charge.dom.Charge;

@DomainService(repositoryFor = PartitionItem.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class PartitionItemRepository extends UdoDomainRepositoryAndFactory<PartitionItem> {

    public PartitionItemRepository() {
        super(PartitionItemRepository.class, PartitionItem.class);
    }

    // //////////////////////////////////////

    public PartitionItem newPartitionItem(
            final Partitioning partitioning,
            final Charge charge,
            final PartitioningTable partitioningTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage,
            final BigDecimal fixedBudgetedAmount,
            final BigDecimal fixedAuditedAmount) {
        PartitionItem partitionItem = factoryService.instantiate(PartitionItem.class);
        partitionItem.setPartitioning(partitioning);
        partitionItem.setCharge(charge);
        partitionItem.setPartitioningTable(partitioningTable);
        partitionItem.setBudgetItem(budgetItem);
        partitionItem.setPercentage(percentage.setScale(6, BigDecimal.ROUND_HALF_UP));
        partitionItem.setFixedBudgetedAmount(fixedBudgetedAmount);
        partitionItem.setFixedAuditedAmount(fixedAuditedAmount);
        repositoryService.persist(partitionItem);
        return partitionItem;
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @ActionLayout()
    public List<PartitionItem> allPartitionItems() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public List<PartitionItem> findByBudgetItem(final BudgetItem budgetItem) {
        return allMatches("findByBudgetItem", "budgetItem", budgetItem);
    }

    @Programmatic
    public PartitionItem findUnique(final Partitioning partitioning, final Charge charge, final BudgetItem budgetItem, final PartitioningTable partitioningTable) {
        return uniqueMatch("findUnique", "partitioning", partitioning, "charge", charge, "budgetItem", budgetItem, "partitioningTable", partitioningTable);
    }

    @Programmatic
    public PartitionItem findOrCreatePartitionItem(final Partitioning partitioning, final BudgetItem budgetItem, final Charge invoiceCharge, final PartitioningTable partitioningTable, final BigDecimal percentage, final BigDecimal fixedBudgetedAmount,
            final BigDecimal fixedAuditedAmount){
        final PartitionItem partitionItem = findUnique(partitioning, invoiceCharge, budgetItem, partitioningTable);
        if (partitionItem == null) {
            return newPartitionItem(partitioning, invoiceCharge, partitioningTable, budgetItem, percentage, fixedBudgetedAmount, fixedAuditedAmount);
        }
        return partitionItem;
    }

    @Programmatic
    public PartitionItem updateOrCreatePartitionItem(final Partitioning partitioning, final BudgetItem budgetItem, final Charge invoiceCharge, final PartitioningTable partitioningTable, final BigDecimal percentage, final BigDecimal fixedBudgetedAmount,
            final BigDecimal fixedAuditedAmount){
        final PartitionItem partitionItem = findUnique(partitioning, invoiceCharge, budgetItem, partitioningTable);
        if (partitionItem == null) {
            return newPartitionItem(partitioning, invoiceCharge, partitioningTable, budgetItem, percentage, fixedBudgetedAmount, fixedAuditedAmount);
        } else {
            partitionItem.setPercentage(percentage);
            partitionItem.setFixedBudgetedAmount(fixedBudgetedAmount);
            partitionItem.setFixedAuditedAmount(fixedAuditedAmount);
        }
        return partitionItem;
    }

    public List<PartitionItem> findByPartitioningTable(final PartitioningTable partitioningTable){
        return allMatches("findByPartitioningTable", "partitioningTable", partitioningTable);
    }

}
