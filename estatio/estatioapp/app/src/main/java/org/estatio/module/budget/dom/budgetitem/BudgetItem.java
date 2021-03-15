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
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
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
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.budget.dom.keytable.PartitioningTableRepository;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.PartitionItemRepository;
import org.estatio.module.budget.dom.partioning.Partitioning;
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
        if (budgetItemValueRepository.findByBudgetItemAndType(this, BudgetCalculationType.AUDITED).size() > 0) {
            return budgetItemValueRepository.findByBudgetItemAndType(this, BudgetCalculationType.AUDITED).get(0).getValue();
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
    public BudgetItem createPartitionItemForBudgeting(
            final Charge charge,
            final PartitioningTable partitioningTable,
            final BigDecimal percentage,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal fixedBudgetedValue) {
        partitionItemRepository.newPartitionItem(getBudget().getPartitioningForBudgeting(), charge, partitioningTable, this, percentage, fixedBudgetedValue, null);
        return this;
    }

    public List<Charge> choices0CreatePartitionItemForBudgeting() {
        return chargeRepository.allOutgoing();
    }

    public List<PartitioningTable> choices1CreatePartitionItemForBudgeting() {
        return partitioningTableRepository.findByBudget(getBudget())
                .stream()
                .collect(Collectors.toList());
    }

    public String validateCreatePartitionItemForBudgeting(
            final Charge charge,
            final PartitioningTable partitioningTable,
            final BigDecimal percentage,
            final BigDecimal fixedBudgetedValue){
        if (partitionItemRepository.findUnique(getBudget().getPartitioningForBudgeting(), charge, this, partitioningTable)!=null) return "This partition item exists already";
        return null;
    }

    public String disableCreatePartitionItemForBudgeting(){
        return isAssignedForTypeReason(BudgetCalculationType.BUDGETED);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public BudgetItem createPartitionItemForReconciliation(
            final Charge charge,
            final PartitioningTable partitioningTable,
            final BigDecimal percentage,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal fixedAuditedValue) {
        partitionItemRepository.newPartitionItem(getBudget().getPartitioningForReconciliation(), charge, partitioningTable, this, percentage, null, fixedAuditedValue);
        return this;
    }

    public List<Charge> choices0CreatePartitionItemForReconciliation() {
        return chargeRepository.allOutgoing();
    }

    public List<PartitioningTable> choices1CreatePartitionItemForReconciliation() {
        return partitioningTableRepository.findByBudget(getBudget())
                .stream()
                .collect(Collectors.toList());
    }

    public String validateCreatePartitionItemForReconciliation(
            final Charge charge,
            final PartitioningTable partitioningTable,
            final BigDecimal percentage,
            final BigDecimal fixedAuditedValue){
        if (partitionItemRepository.findUnique(getBudget().getPartitioningForReconciliation(), charge, this, partitioningTable)!=null) return "This partition item exists already";
        return null;
    }

    public String disableCreatePartitionItemForReconciliation(){
        if (getBudget().getPartitioningForReconciliation()==null) return "No partitioning of type AUDITED found";
        return isAssignedForTypeReason(BudgetCalculationType.AUDITED);
    }

    @Programmatic
    public BudgetItem createCopyFor(final Budget budget) {
        // only copies of budgeted values are made
        BudgetItem newBudgetItemCopy = budget.newBudgetItem(getBudgetedValue(), getCharge());
        newBudgetItemCopy.setCalculationDescription(getCalculationDescription());
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
    public BudgetItem upsertValue(final BigDecimal value, final LocalDate date, final BudgetCalculationType type){
        if (isAssignedForType(type)) return this;
        if (value!=null) {
            budgetItemValueRepository.upsert( this, value, date, type);
        }
        if (value == null && type == BudgetCalculationType.AUDITED){
            final BudgetItemValue auditedValueIfAny = budgetItemValueRepository.findUnique(this, date, type);
            if (auditedValueIfAny!=null) budgetItemValueRepository.remove(auditedValueIfAny);
        }
        return this;
    }

    @Programmatic
    public PartitionItem updateOrCreatePartitionItem(final BudgetCalculationType budgetCalculationType, final Charge charge, final PartitioningTable partitioningTable, final BigDecimal percentage, final BigDecimal fixedBudgetedAmount, final BigDecimal fixedAuditedAmount){
        Partitioning partitioning;
        switch (budgetCalculationType){
        case BUDGETED:
            partitioning = getBudget().getPartitioningForBudgeting();
            break;
        case AUDITED:
            partitioning = getBudget().findOrCreatePartitioningForReconciliation();
            break;
        default:
            partitioning = getBudget().getPartitioningForBudgeting();
        }
        return partitionItemRepository.updateOrCreatePartitionItem(partitioning, this, charge, partitioningTable, percentage, fixedBudgetedAmount, fixedAuditedAmount);
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
            return isAssignedForType(budgetCalculationType) ? "Budget item has been assigned" : null;

        case AUDITED:
            return isAssignedForType(budgetCalculationType) ? "Budget item has been reconciled" : null;
        }
        return null;
    }

    @Programmatic
    public List<PartitionItem> getPartitionItemsForType(final BudgetCalculationType type) {
        return getPartitionItems().stream().filter(x->x.getPartitioning().getType() == type).collect(Collectors.toList());
    }

    @Inject
    private PartitionItemRepository partitionItemRepository;

    @Inject
    PartitioningTableRepository partitioningTableRepository;

    @Inject
    BudgetItemValueRepository budgetItemValueRepository;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    BudgetCalculationRepository budgetCalculationRepository;
}