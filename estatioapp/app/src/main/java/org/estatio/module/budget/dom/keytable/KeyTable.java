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
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
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
import org.estatio.module.budget.dom.Distributable;
import org.estatio.module.budget.dom.DistributionService;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keyitem.KeyItemRepository;
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
@javax.jdo.annotations.Discriminator("org.estatio.dom.budgeting.keytable.KeyTable")
@DomainObject(
        autoCompleteRepository = KeyTableRepository.class,
        objectType = "org.estatio.dom.budgeting.keytable.KeyTable"
)
public class KeyTable extends PartitioningTable {

    @Column(name = "foundationValueTypeId", allowsNull = "false")
    @Getter @Setter
    private FoundationValueType foundationValueType;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public KeyTable changeFoundationValueType(
            final FoundationValueType foundationValueType) {
        setFoundationValueType(foundationValueType);
        return this;
    }

    public FoundationValueType default0ChangeFoundationValueType(final FoundationValueType foundationValueType) {
        return getFoundationValueType();
    }

    public String validateChangeFoundationValueType(final FoundationValueType foundationValueType) {
        if (foundationValueType.equals(null)) {
            return "Foundation value type can't be empty";
        }
        return null;
    }

    public String disableChangeFoundationValueType(){
        return isAssignedReason();
    }

    @Column(name = "keyValueMethodId", allowsNull = "false")
    @Getter @Setter
    private KeyValueMethod keyValueMethod;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public KeyTable changeKeyValueMethod(
            final KeyValueMethod keyValueMethod) {
        setKeyValueMethod(keyValueMethod);
        return this;
    }

    public KeyValueMethod default0ChangeKeyValueMethod(final KeyValueMethod keyValueMethod) {
        return getKeyValueMethod();
    }

    public String validateChangeKeyValueMethod(final KeyValueMethod keyValueMethod) {
        if (keyValueMethod.equals(null)) {
            return "Key value method can't be empty";
        }
        return null;
    }

    public String disableChangeKeyValueMethod(){
        return isAssignedReason();
    }

    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Column(allowsNull = "false")
    @Getter @Setter
    private Integer precision;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(hidden = Where.EVERYWHERE)
    public KeyTable changePrecision(
            final Integer numberOfDigits) {
        setPrecision(numberOfDigits);
        return this;
    }

    public Integer default0ChangePrecision(final Integer numberOfDigits) {
        return getPrecision();
    }

    public String validateChangePrecision(final Integer numberOfDigits) {
        if (numberOfDigits < 0 || numberOfDigits > 6) {
            return "Number Of Digits must have a value between 0 and 6";
        }
        return null;
    }

    public String disableChangePrecision(){
        return isAssignedReason();
    }

    @CollectionLayout(render = RenderType.EAGERLY)
    @Persistent(mappedBy = "keyTable", dependentElement = "true")
    @Getter @Setter
    private SortedSet<KeyItem> items = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public KeyItem newItem(
            final Unit unit,
            final BigDecimal sourceValue,
            final BigDecimal keyValue) {

        return keyItemRepository.newItem(this, unit, sourceValue, keyValue);
    }

    public String validateNewItem(
            final Unit unit,
            final BigDecimal sourceValue,
            final BigDecimal keyValue) {

        return keyItemRepository.validateNewItem(this, unit, sourceValue, keyValue);
    }

    public String disableNewItem(){
        return isAssignedReason();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public KeyTable generateItems() {

        //delete old items
        deleteItems();

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
                newItem.setKeyTable(this);
                persistIfNotAlready(newItem);
                input.add(newItem);
            }
        }

        /*
        call distribute method
         */
        DistributionService distributionService = new DistributionService();
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
        return isAssignedReason();
    }

    // //////////////////////////////////////

    @MemberOrder(name = "items", sequence = "4")
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public KeyTable distributeSourceValues() {

        DistributionService distributionService = new DistributionService();
        distributionService.distribute(new ArrayList(getItems()), getKeyValueMethod().divider(this), getPrecision());

        return this;
    }

    public String disableDistributeSourceValues(){
        return isAssignedReason();
    }

    // //////////////////////////////////////

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudget().getProperty().getApplicationTenancy();
    }

    // //////////////////////////////////////
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public boolean isValid() {
        return (this.isValidForKeyValues() && this.isValidForUnits());
    }

    public boolean isValidForKeyValues() {
        return getKeyValueMethod().isValid(this);
    }

    @PropertyLayout(hidden = Where.EVERYWHERE)
    public boolean isValidForUnits() {
        for (KeyItem item : this.getItems()) {
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
    public KeyTable createCopyFor(final Budget newBudget) {
        KeyTable newKeyTableCopy = newBudget.createKeyTable(getName(), getFoundationValueType(), getKeyValueMethod());
        for (KeyItem item : getItems()){
            newKeyTableCopy.newItem(item.getUnit(), item.getSourceValue(), item.getValue());
        }
        return newKeyTableCopy;
    }

    // //////////////////////////////////////

    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public KeyTable deleteItems() {
        for (KeyItem keyItem : getItems()) {
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
            return "Please remove partition items that use this keytable first";
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

    // //////////////////////////////////////

    @Inject
    UnitRepository unitRepository;

    @Inject
    KeyItemRepository keyItemRepository;

    @Inject
    RepositoryService repositoryService;

    @Inject
    PartitionItemRepository partitionItemRepository;

}
