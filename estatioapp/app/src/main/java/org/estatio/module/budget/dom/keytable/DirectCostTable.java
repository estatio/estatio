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

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.InMemBudgetCalculation;
import org.estatio.module.budget.dom.keyitem.DirectCost;
import org.estatio.module.budget.dom.keyitem.DirectCostRepository;
import org.estatio.module.budget.dom.partioning.PartitionItem;

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

    @CollectionLayout(defaultView = "table")
    @Persistent(mappedBy = "partitioningTable", dependentElement = "true")
    @Getter @Setter
    private SortedSet<DirectCost> items = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public DirectCost newDirectCost(
            final Unit unit,
            final BigDecimal budgetedValue,
            @Parameter(optionality = Optionality.OPTIONAL)
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
        return isImmutableForBudgetedValueReason();
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
        return isImmutableForBudgetedValueReason();
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
            newKeyTableCopy.newDirectCost(item.getUnit(), item.getBudgetedCost(), null);
        }
        return newKeyTableCopy;
    }

    @Programmatic
    public DirectCostTable deleteItems() {
        for (DirectCost directCost : getItems()) {
            getBudget().removeNewCalculations();
            repositoryService.removeAndFlush(directCost);
        }
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Budget remove(){
        Budget budgetToReturn = getBudget();
        repositoryService.removeAndFlush(this);
        return budgetToReturn;
    }

    public String disableRemove(){
        if (super.usedInPartitionItem()){
            return "Please remove partition items that use this table first";
        }
        return null;
    }

    private String isImmutableForBudgetedValueReason(){
        if (getBudget().getStatus()== Status.RECONCILED) return "The budget is reconciled";
        if (getBudget().getStatus()== Status.ASSIGNED && usedInPartitionItemForBudgeted()) return "The budget is assigned";
        return null;
    }

    @Programmatic
    @Override
    public List<InMemBudgetCalculation> calculateInMemFor(
            final PartitionItem partitionItem,
            final BigDecimal partitionItemValue,
            final BudgetCalculationType type,
            final LocalDate calculationStartDate,
            final LocalDate calculationEndDate) {
        List<InMemBudgetCalculation> results = new ArrayList<>();
        Lists.newArrayList(getItems()).stream().forEach(i->{
            BigDecimal value = type==BudgetCalculationType.BUDGETED ? i.getBudgetedCost() : i.getAuditedCost();
            if (value!=null){
                results.add(
                        BudgetCalculationRepository.createInMemBudgetCalculation(
                                partitionItem,
                                i,
                                value,
                                type,
                                calculationStartDate,
                                calculationEndDate
                        )
                );
            }
        });
        return results;
    }

    @Programmatic
    @Override
    public List<InMemBudgetCalculation> calculateInMemForUnit(
            final PartitionItem partitionItem,
            final BigDecimal partitionItemValue,
            final BudgetCalculationType type,
            final Unit unit,
            final LocalDate calculationStartDate,
            final LocalDate calculationEndDate) {
        List<InMemBudgetCalculation> results = new ArrayList<>();
        final DirectCost directCostForUnitIfAny = Lists.newArrayList(getItems()).stream().filter(i -> i.getUnit() == unit).findFirst()
                .orElse(null);
        if (directCostForUnitIfAny!=null){
            results.add(
                    BudgetCalculationRepository.createInMemBudgetCalculation(
                            partitionItem,
                            directCostForUnitIfAny,
                            type==BudgetCalculationType.BUDGETED ? directCostForUnitIfAny.getBudgetedCost() : directCostForUnitIfAny.getAuditedCost(),
                            type,
                            calculationStartDate,
                            calculationEndDate
                    )
            );
        }
        return results;
    }

    @Inject
    DirectCostRepository directCostRepository;

    @Inject
    RepositoryService repositoryService;

    @Inject
    UnitRepository unitRepository;


}
