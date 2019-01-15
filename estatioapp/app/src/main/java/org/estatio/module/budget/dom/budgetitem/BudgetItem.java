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
package org.estatio.module.budget.dom.budgetitem;

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.module.budget.dom.api.PartitionItemCreator;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.Status;
import org.estatio.module.budget.dom.keytable.KeyTableRepository;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.budget.dom.keytable.PartitioningTableRepository;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.PartitionItemRepository;
import org.estatio.module.budget.dom.partioning.PartitioningRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByBudget", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.budgetitem.BudgetItem " +
                        "WHERE budget == :budget "),
        @Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.budgetitem.BudgetItem " +
                        "WHERE budget.property == :property " +
                        "ORDER BY budget.startDate DESC "),
        @Query(
                name = "findByBudgetAndCharge", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.budgetitem.BudgetItem " +
                        "WHERE budget == :budget && charge == :charge"),
        @Query(
            name = "findByPropertyAndChargeAndStartDate", language = "JDOQL",
            value = "SELECT " +
                    "FROM org.estatio.module.budget.dom.budgetitem.BudgetItem " +
                    "WHERE budget.property == :property "
                    + "&& charge == :charge "
                    + "&& budget.startDate == :startDate")
})
@Unique(name = "BudgetItem_budget_charge_UNQ", members = { "budget", "charge" })
@DomainObject(
        objectType = "org.estatio.dom.budgeting.budgetitem.BudgetItem"
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class BudgetItem extends UdoDomainObject2<BudgetItem>
        implements WithApplicationTenancyProperty, PartitionItemCreator {

    public BudgetItem() {
        super("budget, charge");
    }

    public BudgetItem(final Budget budget, final Charge charge) {
        this();
        setBudget(budget);
        setCharge(charge);
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getBudget())
                .withName(getCharge().getReference())
                .toString();
    }

    @Column(name="budgetId", allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private Budget budget;

    @Persistent(mappedBy = "budgetItem", dependentElement = "true")
    @CollectionLayout(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private SortedSet<BudgetItemValue> values = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public BudgetItem newValue(final BigDecimal value, final LocalDate date){
        return newValue(value, date, BudgetCalculationType.ACTUAL);
    }

    public LocalDate default1NewValue(final BigDecimal value, final LocalDate date){
        return getBudget().getStartDate();
    }

    public String validateNewValue(final BigDecimal value, final LocalDate date){
        return budgetItemValueRepository.validateNewBudgetItemValue(this, value, date, BudgetCalculationType.ACTUAL);
    }

    public String disableNewValue(){
        return budgetItemValueRepository.findByBudgetItemAndType(this, BudgetCalculationType.ACTUAL).size()>0 ? "Audited value already entered" : null;
    }

    @Programmatic
    public BudgetItem newValue(final BigDecimal value, final LocalDate date, final BudgetCalculationType type){
        budgetItemValueRepository.newBudgetItemValue(this, value, date, type);
        return this;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public BigDecimal getBudgetedValue() {
        if (budgetItemValueRepository.findByBudgetItemAndType(this, BudgetCalculationType.BUDGETED).size() > 0) {
            return budgetItemValueRepository.findByBudgetItemAndType(this, BudgetCalculationType.BUDGETED).get(0).getValue().setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public BigDecimal getAuditedValue(){
        if (budgetItemValueRepository.findByBudgetItemAndType(this, BudgetCalculationType.ACTUAL).size() > 0) {
            return budgetItemValueRepository.findByBudgetItemAndType(this, BudgetCalculationType.ACTUAL).get(0).getValue();
        } else {
            return null;
        }
    }

    @Column(name="chargeId", allowsNull = "false")
    @Getter @Setter
    private Charge charge;

    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    private String calculationDescription;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<PartitionItem> getPartitionItems(){
        return partitionItemRepository.findByBudgetItem(this);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public PartitionItem createPartitionItemForBudgeting(
            final Charge charge,
            final PartitioningTable partitioningTable,
            final BigDecimal percentage,
            final BigDecimal fixedBudgetedValue) {
        return partitionItemRepository.newPartitionItem(getBudget().getPartitioningForBudgeting(), charge, partitioningTable, this, percentage, fixedBudgetedValue, null);
    }

    public List<Charge> choices0CreatePartitionItemForBudgeting() {
        return chargeRepository.allOutgoing();
    }

    public List<PartitioningTable> choices1CreatePartitionItemForBudgeting() {
        return partitioningTableRepository.findByBudget(getBudget())
                .stream()
                .collect(Collectors.toList());
    }

    public String disableCreatePartitionItemForBudgeting(){
        return isAssignedForTypeReason(BudgetCalculationType.BUDGETED);
    }

    @Programmatic
    public BudgetItem createCopyFor(final Budget budget) {
        // only copies of budgeted values are made
        BudgetItem newBudgetItemCopy = budget.newBudgetItem(getBudgetedValue(), getCharge());
        for (PartitionItem partitionItem : partitionItemRepository.findByBudgetItem(this)){
            // only copies of budgeted items are made
            if (partitionItem.getPartitioning().getType()==BudgetCalculationType.BUDGETED) {
                String keyTableName = partitionItem.getPartitioningTable().getName();
                PartitioningTable correspondingTableOnbudget = partitioningTableRepository.findByBudgetAndName(budget, keyTableName);
                newBudgetItemCopy.createPartitionItemForBudgeting(partitionItem.getCharge(), correspondingTableOnbudget, partitionItem.getPercentage(), partitionItem.getFixedBudgetedAmount());
            }
        }
        return newBudgetItemCopy;
    }

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudget().getApplicationTenancy();
    }

    @Programmatic
    public BudgetItem updateOrCreateBudgetItemValue(final BigDecimal value, final LocalDate date, final BudgetCalculationType type){
        if (value!=null && !isAssignedForType(type)) {
            budgetItemValueRepository.updateOrCreateBudgetItemValue(value, this, date, type);
        }
        return this;
    }

    @Programmatic
    public PartitionItem updateOrCreatePartitionItem(final Charge charge, final PartitioningTable partitioningTable, final BigDecimal percentage, final BigDecimal fixedBudgetedAmount, final BigDecimal fixedAuditedAmount){
        return partitionItemRepository.updateOrCreatePartitionItem(getBudget().getPartitioningForBudgeting(), this, charge, partitioningTable, percentage, fixedBudgetedAmount, fixedAuditedAmount);
    }

    @Programmatic
    public boolean isAssignedForType(final BudgetCalculationType budgetCalculationType){
        for (BudgetCalculation calculation : budgetCalculationRepository.findByBudgetItemAndCalculationType(this, budgetCalculationType)) {
            if (calculation.getStatus() == Status.ASSIGNED){
                return true;
            }
        }
        return false;
    }

    @Programmatic
    public String isAssignedForTypeReason(final BudgetCalculationType budgetCalculationType){
        switch (budgetCalculationType) {

        case BUDGETED:
            return isAssignedForType(budgetCalculationType) ? "This item has been assigned" : null;

        case ACTUAL:
            return isAssignedForType(budgetCalculationType) ? "This item has been reconciled" : null;
        }
        return null;
    }

    @Inject
    private BudgetItemRepository budgetItemRepository;

    @Inject
    private PartitionItemRepository partitionItemRepository;

    @Inject
    private PartitioningRepository partitioningRepository;

    @Inject
    private KeyTableRepository keyTableRepository;

    @Inject PartitioningTableRepository partitioningTableRepository;

    @Inject
    BudgetItemValueRepository budgetItemValueRepository;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    BudgetCalculationRepository budgetCalculationRepository;

}