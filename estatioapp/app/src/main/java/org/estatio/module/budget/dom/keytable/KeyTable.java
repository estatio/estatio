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
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.xactn.TransactionService3;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.base.dom.distribution.Distributable;
import org.estatio.module.base.dom.distribution.DistributionService;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.InMemBudgetCalculation;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keyitem.KeyItemRepository;
import org.estatio.module.budget.dom.partioning.PartitionItem;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.budgeting.keytable.KeyTable")
@DomainObject(
        autoCompleteRepository = KeyTableRepository.class,
        objectType = "org.estatio.dom.budgeting.keytable.KeyTable"
)
public class KeyTable extends PartitioningTable {

    @Column(allowsNull = "false")
    @Getter @Setter
    private FoundationValueType foundationValueType;

    @Column(allowsNull = "false")
    @Getter @Setter
    private KeyValueMethod keyValueMethod;

    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Column(allowsNull = "false")
    @Getter @Setter
    private Integer precision;

    @CollectionLayout(defaultView = "table")
    @Persistent(mappedBy = "partitioningTable", dependentElement = "true")
    @Getter @Setter
    private SortedSet<KeyItem> items = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public KeyTable generateItems() {

        //delete old items
        deleteItems();
        transactionService3.nextTransaction();

        /*
        create list of input pairs: identifier - sourcevalue
        sourcevalue is determined by FoundationValueType
        */
        List<Distributable> input = new ArrayList<>();

        for (Unit unit : unitRepository.findByProperty(this.getBudget().getProperty())) {

            if (unitIntervalValidForThisKeyTable(unit)) {
                BigDecimal sourceValue;
                if (getFoundationValueType().valueOf(unit) != null) {
                    sourceValue = getFoundationValueType().valueOf(unit);
                } else {
                    sourceValue = BigDecimal.ZERO;
                }
                KeyItem newItem = new KeyItem();
                newItem.setSourceValue(sourceValue);
                newItem.setValue(BigDecimal.ZERO);
                newItem.setUnit(unit);
                newItem.setPartitioningTable(this);
                persistIfNotAlready(newItem);
                input.add(newItem);
            }
        }

        /*
        call distribute method
         */
        distributionService.distribute(input, getKeyValueMethod().divider(this), getPrecision());

        return this;
    }

    public boolean hideGenerateItems() {
        if (getFoundationValueType() == FoundationValueType.MANUAL) {
            return true;
        }
        return false;
    }

    public String disableGenerateItems(){
        return isImmutableReason();
    }

    @Programmatic
    public KeyTable distributeSourceValues() {
        if (getFoundationValueType()!=FoundationValueType.MANUAL) {
            distributionService
                    .distribute(new ArrayList(getItems()), getKeyValueMethod().divider(this), getPrecision());
        }
        return this;
    }

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudget().getProperty().getApplicationTenancy();
    }

    public boolean isValidForKeyValues() {
        return getKeyValueMethod().isValid(this);
    }

    @Programmatic
    private boolean unitIntervalValidForThisKeyTable(final Unit unit) {
        return unit.getInterval().contains(getBudget().getInterval());
    }

    @Programmatic
    public KeyTable createCopyFor(final Budget newBudget) {
        KeyTable newKeyTableCopy = newBudget.createKeyTable(getName(), getFoundationValueType(), getKeyValueMethod());
        for (KeyItem item : getItems()){
            keyItemRepository.newItem(newKeyTableCopy, item.getUnit(), item.getSourceValue(), item.getValue());
        }
        return newKeyTableCopy;
    }

    // //////////////////////////////////////

    @Programmatic
    public KeyTable deleteItems() {
        getBudget().removeNewCalculations();
        for (KeyItem keyItem : getItems()) {
            keyItem.delete();
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
        if (usedInPartitionItem()){
            return "Please remove partition items that use this keytable first";
        }
        return null;
    }

    @Programmatic
    public String isImmutableReason(){
        if (getBudget().getStatus()==Status.RECONCILED) return "The budget is reconciled";
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
        BigDecimal divider = getKeyValueMethod().divider(this);
        List<InMemBudgetCalculation> results = new ArrayList<>();
        Lists.newArrayList(getItems()).stream().forEach(i->{
            results.add(BudgetCalculationRepository
                    .createInMemBudgetCalculation(
                            partitionItem,
                            i,
                            partitionItemValue.multiply(i.getValue())
                                    .divide(divider, MathContext.DECIMAL64)
                                    .setScale(getPrecision(), BigDecimal.ROUND_HALF_UP),
                            type,
                            calculationStartDate,
                            calculationEndDate
                    ));
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
        BigDecimal divider = getKeyValueMethod().divider(this);
        List<InMemBudgetCalculation> results = new ArrayList<>();
        final KeyItem keyItemForUnitIfAny = Lists.newArrayList(getItems()).stream().filter(i -> i.getUnit() == unit).findFirst()
                .orElse(null);
        if (keyItemForUnitIfAny!=null) {
            results.add(BudgetCalculationRepository
                    .createInMemBudgetCalculation(
                            partitionItem,
                            keyItemForUnitIfAny,
                            partitionItemValue.multiply(keyItemForUnitIfAny.getValue())
                                    .divide(divider, MathContext.DECIMAL64)
                                    .setScale(getPrecision(), BigDecimal.ROUND_HALF_UP),
                            type,
                            calculationStartDate,
                            calculationEndDate
                    ));
        }
        return results;
    }

    @Inject
    UnitRepository unitRepository;

    @Inject
    KeyItemRepository keyItemRepository;

    @Inject
    RepositoryService repositoryService;

    @Inject
    DistributionService distributionService;

    @Inject
    TransactionService3 transactionService3;
}
