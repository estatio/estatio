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
import java.math.MathContext;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.Status;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.charge.dom.Charge;

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
                name = "findByBudgetItem", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.partioning.PartitionItem " +
                        "WHERE budgetItem == :budgetItem "),
        @Query(
                name = "findByPartitioningTable", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.partioning.PartitionItem " +
                        "WHERE partitioningTable == :partitioningTable "),
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.partioning.PartitionItem " +
                        "WHERE partitioning == :partitioning && charge == :charge && budgetItem == :budgetItem && partitioningTable == :partitioningTable ")
})
@Unique(name = "PartitionItem_partitioning_charge_budgetItem_partitioningTable_UNQ", members = {"partitioning", "charge", "budgetItem", "partitioningTable"})
@DomainObject(
        auditing = Auditing.DISABLED,
        objectType = "org.estatio.dom.budgeting.partioning.PartitionItem"
)
public class PartitionItem extends UdoDomainObject2<PartitionItem> implements WithApplicationTenancyProperty {

    public PartitionItem() {
        super("partitioning, budgetItem, charge, partitioningTable");
    }

    //region > identificatiom
    public String title() {

        return "From "
                .concat(getBudgetItem().getCharge().getName()
                .concat(" to "))
                .concat(getCharge().getName());
    }
    //endregion

    @Column(allowsNull = "false", name = "partitioningId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Partitioning partitioning;

    @Column(allowsNull = "false", name = "chargeId")
    @Getter @Setter
    private Charge charge;

    @Column(name="partitioningTableId", allowsNull = "false")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private PartitioningTable partitioningTable;

    @Column(allowsNull = "false", name = "budgetItemId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private BudgetItem budgetItem;

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal fixedBudgetedAmount;

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal fixedAuditedAmount;

    @Column(allowsNull = "false", scale = 6)
    @Getter @Setter
    private BigDecimal percentage;

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public PartitionItem updatePercentage(final BigDecimal percentage) {
        setPercentage(percentage.setScale(6, BigDecimal.ROUND_HALF_UP));
        return this;
    }

    public BigDecimal default0UpdatePercentage(final BigDecimal percentage) {
        return getPercentage();
    }

    public String validateUpdatePercentage(final BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(new BigDecimal(100)) > 0) {
            return "percentage should be in range 0 - 100";
        }
        return null;
    }

    public String disableUpdatePercentage(){
        BudgetCalculationType type = getPartitioning().getType();
        return getBudgetItem().isAssignedForTypeReason(type);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public BudgetCalculationType getType(){
        return getPartitioning().getType();
    }

    @Persistent(mappedBy = "partitionItem", dependentElement = "true")
    @Getter @Setter
    private SortedSet<BudgetCalculation> calculations = new TreeSet<>();

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.ALL_TABLES)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Budget getBudget(){
        return getBudgetItem().getBudget();
    }

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudgetItem().getBudget().getApplicationTenancy();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public BudgetItem remove() {
        BudgetItem budgetItem = getBudgetItem();
        repositoryService.removeAndFlush(this);
        return budgetItem;
    }

    public String disableRemove(){
        if (isAssigned()) return "This item has assigned calculations";
        return null;
    }

    private boolean isAssigned(){
        BudgetCalculation firstAssigned = Lists.newArrayList(getCalculations()).stream().filter(x->x.getStatus()==Status.ASSIGNED).findFirst().orElse(null);
        return firstAssigned != null;
    }

    @Programmatic
    public BigDecimal getBudgetedValue(){
        return getFixedBudgetedAmount() !=null ? getFixedBudgetedAmount() : percentageOf(getBudgetItem().getBudgetedValue(), getPercentage());
    }

    @Programmatic
    public BigDecimal getAuditedValue(){
        final BigDecimal auditedValue = getBudgetItem().getAuditedValue();
        return getFixedAuditedAmount() !=null ? getFixedAuditedAmount() :  auditedValue!= null ? percentageOf(auditedValue, getPercentage()) : null;
    }

    BigDecimal percentageOf(final BigDecimal value, final BigDecimal percentage) {
        return value
                .multiply(percentage)
                .divide(new BigDecimal("100"), MathContext.DECIMAL64);
    }

    @Inject
    private RepositoryService repositoryService;

}