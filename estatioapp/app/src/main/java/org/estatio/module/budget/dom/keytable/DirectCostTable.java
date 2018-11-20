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
package org.estatio.module.budget.dom.keytable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.keyitem.DirectCost;
import org.estatio.module.budget.dom.keyitem.DirectCostRepository;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.PartitionItemRepository;
import org.estatio.module.budget.dom.partioning.Partitioning;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.budgeting.keytable.DirectCostTable")
@DomainObject(
        autoCompleteRepository = DirectCostTableRepository.class,
        objectType = "org.estatio.dom.budgeting.keytable.DirectCostTable"
)
public class DirectCostTable extends PartitioningTable {

    @CollectionLayout(render = RenderType.EAGERLY)
    @Persistent(mappedBy = "directCostTable", dependentElement = "true")
    @Getter @Setter
    private SortedSet<DirectCost> items = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public DirectCost newDirectCost(
            final Unit unit,
            final BigDecimal budgetedValue,
            final BigDecimal auditedValue) {

        return directCostRepository.newDirectCost(this, unit, budgetedValue, auditedValue);
    }

    public String validateNewDirectCost(
            final Unit unit,
            final BigDecimal budgetedValue,
            final BigDecimal auditedValue) {

        return directCostRepository.validateDirectCost(this, unit, budgetedValue, auditedValue);
    }

    public String disableNewDirectCost(){
        return isAssignedReason();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public DirectCostTable generateItems() {

        //delete old items
        deleteItems();

        for (Unit unit : unitRepository.findByProperty(this.getBudget().getProperty())) {

            if (unitIntervalValidForThisKeyTable(unit)) {
                directCostRepository.newDirectCost(this, unit, BigDecimal.ZERO, null);
            }
        }

        return this;
    }

    public boolean hideGenerateItems() {
        return false;
    }

    public String disableGenerateItems(){
        return isAssignedReason();
    }


    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudget().getProperty().getApplicationTenancy();
    }

    // //////////////////////////////////////
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public boolean isValid() {
        return (this.isValidForUnits());
    }

    @PropertyLayout(hidden = Where.EVERYWHERE)
    public boolean isValidForUnits() {
        for (DirectCost item : this.getItems()) {
            if (!this.unitIntervalValidForThisKeyTable(item.getUnit())) {
                return false;
            }
        }
        return true;
    }

    @Programmatic
    private boolean unitIntervalValidForThisKeyTable(final Unit unit) {
        return unit.getInterval().contains(getBudget().getInterval());
    }

    @Programmatic
    public DirectCostTable createCopyFor(final Budget newBudget) {
        DirectCostTable newKeyTableCopy = newBudget.createDirectCostTable(getName());
        for (DirectCost item : getItems()){
            newKeyTableCopy.newDirectCost(item.getUnit(), item.getBudgetedValue(), null);
        }
        return newKeyTableCopy;
    }

    // //////////////////////////////////////

    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public DirectCostTable deleteItems() {
        for (DirectCost keyItem : getItems()) {
            removeIfNotAlready(keyItem);
        }

        return this;
    }

    @Programmatic
    public List<PartitionItem> usedInPartitionItems(){
        List<PartitionItem> result = new ArrayList<>();
        for (Partitioning partitioning : getBudget().getPartitionings()) {
            for (PartitionItem partitionItem : partitioning.getItems()) {
                if (partitionItem.getPartitioningTable()==this){
                    result.add(partitionItem);
                }
            }
        }
        return result;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Budget remove(){
        Budget budgetToReturn = getBudget();
        repositoryService.removeAndFlush(this);
        return budgetToReturn;
    }

    public String disableRemove(){
        if (!usedInPartitionItems().isEmpty()){
            return "Please remove partition items that use this table first";
        }
        return null;
    }

    private String isAssignedReason(){
        if (isAssignedForTypeReason(BudgetCalculationType.ACTUAL)!=null){
            return isAssignedForTypeReason(BudgetCalculationType.ACTUAL);
        }
        return isAssignedForTypeReason(BudgetCalculationType.BUDGETED);
    }

    String isAssignedForTypeReason(final BudgetCalculationType budgetCalculationType){
        for (PartitionItem partitionItem : partitionItemRepository.findByPartitioningTable(this)){
            if (partitionItem.getBudgetItem().isAssignedForType(budgetCalculationType)){
                return partitionItem.getBudgetItem().isAssignedForTypeReason(budgetCalculationType);
            }
        }
        return null;
    }

    @Inject
    DirectCostRepository directCostRepository;

    @Inject
    RepositoryService repositoryService;

    @Inject
    PartitionItemRepository partitionItemRepository;

    @Inject
    UnitRepository unitRepository;

}
